package com.king.staff.common.crypto;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class SignTest {

    @Rule
    public ExpectedException expectedException=ExpectedException.none();

    @Test
    public void testEmailToken(){
        String userId= UUID.randomUUID().toString();
        String email="test@163.com";
        String signingToken="test_signing_Token";

        String emailToken =Sign.generateEmailConfirmationToken(userId,email,signingToken);
        assertThat(emailToken).isNotNull();

        DecodedJWT jwt=Sign.verifyEmailConfirmationToken(emailToken,signingToken);
        System.out.println(jwt.getClaim(Sign.CLAIM_USER_ID).asString());
        assertThat(jwt.getClaim(Sign.CLAIM_USER_ID).asString()).isEqualTo(userId);
        System.out.println(jwt.getClaim(Sign.CLAIM_EMAIL).asString());
        assertThat(jwt.getClaim(Sign.CLAIM_EMAIL).asString()).isEqualTo(email);

        expectedException.expect(SignatureVerificationException.class);
        expectedException.expectMessage("The Token's Signature resulted invalid when verified using the Algorithm: HmacSHA512");

        jwt = Sign.verifyEmailConfirmationToken(emailToken, "wrong_signing_token");
    }

}

