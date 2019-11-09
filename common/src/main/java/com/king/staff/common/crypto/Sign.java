package com.king.staff.common.crypto;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.king.staff.common.error.ServiceException;
import io.micrometer.core.instrument.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Sign {


    public static final String CLAIM_EMAIL="email";
    public static final String CLAIM_USER_ID="userId";
    public static final String CLAIM_SUPPORT="support";

    private static Map<String, JWTVerifier> verifierMap =new HashMap<>();
    private static Map<String, Algorithm> algorithmMap=new HashMap<>();

    /**
     * @param signingToken
     * @return Algorithm
     * @description
     * (1)通过signingToken通过获取加密后的数据对象
     * (2)采用单例模式中的懒汉模式+加锁的模式
     */
    private static Algorithm getAlgorithm(String signingToken){
        Algorithm  algorithm=algorithmMap.get(signingToken);
        if(algorithm==null){
            synchronized (algorithmMap){
                algorithm=algorithmMap.get(signingToken);
                if(algorithm==null){
                    algorithm=Algorithm.HMAC256(signingToken);
                    algorithmMap.put(signingToken,algorithm);
                }
            }
        }
        return algorithm;
    }

    /**
     * @description 生成邮件的验证token
     * @param userId 用户id
     * @param email 邮件
     * @param signingToken 用于生成token的密钥
     * @return
     */
    public static String generateEmailConfirmationToken(String userId,String email,String signingToken){
        Algorithm algorithm=getAlgorithm(signingToken);
        String token= JWT.create()
                .withClaim(CLAIM_EMAIL,email)
                .withClaim(CLAIM_USER_ID,userId)
                .withExpiresAt(new Date(System.currentTimeMillis()+ TimeUnit.HOURS.toMillis(2)))
                .sign(algorithm);
        return token;
    }

    /**
     * @description 解码session中的加密token为DecodedJWT
     * @param tokenString 需要解码的字符串
     * @param signingToken 密钥
     * @return
     */
    public static DecodedJWT verifySessionToken(String tokenString,String signingToken){
        return verifyToken(tokenString,signingToken);
    }

    /**
     * @description 解码邮件中的加密的token为DecodedJWT
     * @param tokenString 需要解码的字符串
     * @param signingToken 密钥
     * @return
     */
    public static DecodedJWT verifyEmailConfirmationToken(String tokenString,String signingToken){
        return verifyToken(tokenString,signingToken);
    }

    /**
     * @description 解码JWT生成DecodedJWT +单例模式+懒汉模式
     * @param tokenString 加密的消息
     * @param signingToken 密钥
     * @return
     */
    static DecodedJWT verifyToken(String tokenString,String signingToken){
        JWTVerifier verifier=verifierMap.get(signingToken);
        if(verifier==null){
            synchronized (verifierMap){
                verifier=verifierMap.get(signingToken);
                if(verifier==null){
                    Algorithm algorithm=Algorithm.HMAC256(signingToken);
                    verifier=JWT.require(algorithm).build();
                    verifierMap.put(signingToken,verifier);
                }
            }
        }
        DecodedJWT jwt=verifier.verify(tokenString);
        return jwt;
    }

    /**
     * 生成session 的token
     * @param userId 用户id
     * @param signingToken 密钥
     * @param support
     * @param duration
     * @return
     */
    public static String generateSessionToken(String userId,String signingToken,boolean support,long duration){
        if(StringUtils.isEmpty(signingToken)){
            throw new ServiceException("No signing token present");
        }
        Algorithm algorithm=getAlgorithm(signingToken);
        String token=JWT.create()
                .withClaim(CLAIM_USER_ID,userId)
                .withClaim(CLAIM_SUPPORT,support)
                .withExpiresAt(new Date(System.currentTimeMillis()+duration))
                .sign(algorithm);
        return token;
    }
}
