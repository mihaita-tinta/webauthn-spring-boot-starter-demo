package com.mih.webauthn.config;

import com.mih.webauthn.BytesUtil;
import com.mih.webauthn.domain.AppCredentials;
import com.mih.webauthn.repository.AppUserRepository;
import com.mih.webauthn.service.CredentialRepositoryService;
import com.mih.webauthn.web.AppUserAuthentication;
import com.mih.webauthn.web.AppUserDetail;
import com.mih.webauthn.web.dto.AssertionFinishRequest;
import com.mih.webauthn.web.dto.AssertionStartResponse;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.exception.AssertionFailedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;

public class WebAuthnAssertionFinishStrategy {

    private final AppUserRepository appUserRepository;
    private final CredentialRepositoryService credentialRepositoryService;
    private final RelyingParty relyingParty;
    private final WebAuthnOperation<AssertionStartResponse> operation;

    public WebAuthnAssertionFinishStrategy(AppUserRepository appUserRepository, CredentialRepositoryService credentialRepository, RelyingParty relyingParty, WebAuthnOperation<AssertionStartResponse> operation) {
        this.appUserRepository = appUserRepository;
        this.credentialRepositoryService = credentialRepository;
        this.relyingParty = relyingParty;
        this.operation = operation;
    }

    public boolean finish(@RequestBody AssertionFinishRequest finishRequest) {

        AssertionStartResponse startResponse = this.operation
                .get(finishRequest.getAssertionId());
        this.operation.remove(finishRequest.getAssertionId());

        try {
            AssertionResult result = this.relyingParty.finishAssertion(
                    FinishAssertionOptions.builder().request(startResponse.getAssertionRequest())
                            .response(finishRequest.getCredential()).build());

            if (result.isSuccess()) {
                this.credentialRepositoryService.updateSignatureCount(result);

                long userId = BytesUtil.bytesToLong(result.getUserHandle().getBytes());
                var appUserRecord = this.appUserRepository.findById(userId).get();

                if (appUserRecord != null) {
                    AppUserDetail userDetail = new AppUserDetail(appUserRecord,
                            new SimpleGrantedAuthority("USER"));
                    AppUserAuthentication auth = new AppUserAuthentication(userDetail);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    return true;
                }
            }
        } catch (AssertionFailedException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }


        return false;
    }
}
