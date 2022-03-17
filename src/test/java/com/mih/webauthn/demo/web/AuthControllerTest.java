package com.mih.webauthn.demo.web;

import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.UserIdentity;
import io.github.webauthn.BytesUtil;
import io.github.webauthn.domain.WebAuthnUser;
import io.github.webauthn.domain.WebAuthnUserRepository;
import io.github.webauthn.jpa.JpaWebAuthnUser;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;


@SpringBootTest
class AuthControllerTest {
    private static final Logger log = LoggerFactory.getLogger(AuthControllerTest.class);

    @Autowired
    RelyingParty relyingParty;
    //    @MockBean
//    WebAuthnCredentialsRepository credentialsRepository;
    @Autowired
    WebAuthnUserRepository<JpaWebAuthnUser> appUserRepository;

    @Test
    public void testGetChallenge() {
        JpaWebAuthnUser user = new JpaWebAuthnUser();
        user.setUsername("test");
        appUserRepository.save(user);

        PublicKeyCredentialCreationOptions credentialCreation = this.relyingParty
                .startRegistration(StartRegistrationOptions.builder()
                        .user(UserIdentity.builder().name(user.getUsername()).displayName(user.getUsername())
                                .id(new ByteArray(BytesUtil.longToBytes(user.getId()))).build())
                        .build());

        log.info("test: " + credentialCreation);
    }

    @Test
    public void testEncoding() {
        byte[] recoveryToken = new byte[16];
        new SecureRandom().nextBytes(recoveryToken);

        byte[] recoveryTokenDecoded = null;
        recoveryTokenDecoded = Base64.getDecoder().decode(
                Base64.getEncoder().encodeToString(recoveryToken)
        );

        assertArrayEquals(recoveryToken, recoveryTokenDecoded);
    }

}
