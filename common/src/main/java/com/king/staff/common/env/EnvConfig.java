package com.king.staff.common.env;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shenwang
 * @description 环境配置
 * @date 2019-11-02
 */
@Data
@Builder
public class EnvConfig {

    private String name;
    private boolean debug;
    private String externalApex;
    private String internalApex;
    private String scheme;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Map<String,EnvConfig> map;

    static{
        map=new HashMap<String,EnvConfig>();

        //配置开发的环境
        EnvConfig envConfig=EnvConfig.builder().name(EnvConstant.ENV_DEV)
                .debug(true)
                .externalApex("staff-v2.local")
                .internalApex(EnvConstant.ENV_DEV)
                .scheme("http")
                .build();
        map.put(EnvConstant.ENV_DEV,envConfig);

        //配置测试的环境
        envConfig=EnvConfig.builder().name(EnvConstant.ENV_TEST)
                .debug(true)
                .externalApex("staff-v2.local")
                .internalApex(EnvConstant.ENV_DEV)
                .scheme("http")
                .build();
        map.put(EnvConstant.ENV_TEST,envConfig);

//        for aliyun k8s demo ,enable debug and use http and staff-uat.local
//        in real world,disable debug and use http and staff-uat.xyz UAT environment
        envConfig=EnvConfig.builder().name(EnvConstant.ENV_UAT)
                .debug(true)
                .externalApex("staff-uat.local")
                .internalApex(EnvConstant.ENV_UAT)
                .scheme("http")
                .build();
        map.put(EnvConstant.ENV_UAT,envConfig);

        envConfig=EnvConfig.builder().name(EnvConstant.ENV_PROD)
                .debug(false)
                .externalApex("staff.com")
                .internalApex(EnvConstant.ENV_PROD)
                .scheme("https")
                .build();
        map.put(EnvConstant.ENV_PROD,envConfig);
    }

//  获取相应的环境配置 单例模式 懒汉模式
    public static EnvConfig getEnvConfig(String env) {
        EnvConfig envConfig=map.get(env);
        if(envConfig==null){
            envConfig=map.get(EnvConstant.ENV_DEV);
        }
        return envConfig;
    }

}
