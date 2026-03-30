package com.king.staff.common.crypto;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.king.staff.common.error.ServiceException;
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

    @Test
    public void testSessionTokenGenerationAndVerification() {
        String userId = UUID.randomUUID().toString();
        String signingToken = "session_signing_token";
        boolean support = true;
        long duration = 3600000L; // 1 hour in milliseconds

        String sessionToken = Sign.generateSessionToken(userId, signingToken, support, duration);
        assertThat(sessionToken).isNotNull();

        DecodedJWT jwt = Sign.verifySessionToken(sessionToken, signingToken);
        assertThat(jwt.getClaim(Sign.CLAIM_USER_ID).asString()).isEqualTo(userId);
        assertThat(jwt.getClaim(Sign.CLAIM_SUPPORT).asBoolean()).isEqualTo(support);
    }

    @Test
    public void testSessionTokenWithEmptySigningToken() {
        String userId = UUID.randomUUID().toString();
        String signingToken = "";
        boolean support = false;
        long duration = 3600000L;

        expectedException.expect(ServiceException.class);
        expectedException.expectMessage("No signing token present");

        Sign.generateSessionToken(userId, signingToken, support, duration);
    }

    @Test
    public void testSessionTokenWithNullSigningToken() {
        String userId = UUID.randomUUID().toString();
        String signingToken = null;
        boolean support = false;
        long duration = 3600000L;

        expectedException.expect(ServiceException.class);
        expectedException.expectMessage("No signing token present");

        Sign.generateSessionToken(userId, signingToken, support, duration);
    }

    @Test
    public void testSessionTokenVerificationWithWrongToken() {
        String userId = UUID.randomUUID().toString();
        String signingToken = "correct_token";
        boolean support = true;
        long duration = 3600000L;

        String sessionToken = Sign.generateSessionToken(userId, signingToken, support, duration);
        assertThat(sessionToken).isNotNull();

        expectedException.expect(SignatureVerificationException.class);
        
        Sign.verifySessionToken(sessionToken, "wrong_token");
    }

    @Test
    public void testVerifySessionTokenWithEmailToken() {
        // Verify that verifySessionToken works with email tokens too
        // since both methods call verifyToken internally
        String userId = UUID.randomUUID().toString();
        String email = "test@example.com";
        String signingToken = "shared_signing_token";

        String emailToken = Sign.generateEmailConfirmationToken(userId, email, signingToken);
        
        // This should work because verifySessionToken calls verifyToken internally
        DecodedJWT jwt = Sign.verifySessionToken(emailToken, signingToken);
        assertThat(jwt.getClaim(Sign.CLAIM_USER_ID).asString()).isEqualTo(userId);
        assertThat(jwt.getClaim(Sign.CLAIM_EMAIL).asString()).isEqualTo(email);
    }
}

