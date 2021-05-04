package com.mih.webauthn.web;

import com.mih.webauthn.BytesUtil;
import com.mih.webauthn.domain.AppCredentials;
import com.mih.webauthn.domain.AppUser;
import com.mih.webauthn.repository.AppCredentialsRepository;
import com.mih.webauthn.repository.AppUserRepository;
import com.mih.webauthn.service.CredentialRepositoryService;
import com.mih.webauthn.web.dto.AssertionFinishRequest;
import com.mih.webauthn.web.dto.AssertionStartResponse;
import com.mih.webauthn.web.dto.RegistrationFinishRequest;
import com.mih.webauthn.web.dto.RegistrationStartResponse;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.exception.AssertionFailedException;
import com.yubico.webauthn.exception.RegistrationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
public class AuthController {

    private final Map<String, RegistrationStartResponse> registrationCache;

    private final Map<String, AssertionStartResponse> assertionCache;

    private final CredentialRepositoryService credentialRepositoryService;
    private final AppCredentialsRepository credentialRepository;
    private final AppUserRepository appUserRepository;

    private final RelyingParty relyingParty;

    private final SecureRandom random;

    public AuthController(CredentialRepositoryService credentialRepositoryService, AppCredentialsRepository credentialRepository,
                          AppUserRepository appUserRepository, RelyingParty relyingParty) {
        this.credentialRepository = credentialRepository;
        this.appUserRepository = appUserRepository;
        this.credentialRepositoryService = credentialRepositoryService;
        this.relyingParty = relyingParty;
        this.registrationCache = new HashMap<>();
        this.assertionCache = new HashMap<>();
        this.random = new SecureRandom();
    }

    @GetMapping("/authenticate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void authenticate() {
        // nothing here
    }

    @GetMapping("/registration-add")
    public byte[] registrationAdd(@AuthenticationPrincipal AppUserDetail user) {
        byte[] addToken = new byte[16];
        this.random.nextBytes(addToken);

        this.appUserRepository.findById(user.getAppUserId())
                .doOnNext(u -> {
                    u.setAddToken(addToken);
                })
                .flatMap(appUserRepository::save)
                .block();

        return Base64.getEncoder().encode(addToken);
    }

    @PostMapping("/registration/start")
    public RegistrationStartResponse registrationStart(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "registrationAddToken",
                    required = false) String registrationAddToken,
            @RequestParam(name = "recoveryToken", required = false) String recoveryToken) {

        long userId = -1;
        String name = null;
        RegistrationStartResponse.Mode mode = null;

        if (username != null && !username.isEmpty()) {
            // cancel if the user is already registered
            boolean isPresent = this.appUserRepository.findByUsername(username)
                    .blockOptional()
                    .isPresent();
            if (isPresent) {
                return new RegistrationStartResponse(
                        RegistrationStartResponse.Status.USERNAME_TAKEN);
            }

            AppUser user = new AppUser();
            user.setUsername(username);
            userId = this.appUserRepository.save(user)
                    .block()
                    .getId();
            name = username;
            mode = RegistrationStartResponse.Mode.NEW;
        } else if (registrationAddToken != null && !registrationAddToken.isEmpty()) {
            byte[] registrationAddTokenDecoded = null;
            try {
                registrationAddTokenDecoded = Base64.getDecoder().decode(registrationAddToken);
            } catch (Exception e) {
                return new RegistrationStartResponse(
                        RegistrationStartResponse.Status.TOKEN_INVALID);
            }


//            var record = this.dsl.select(APP_USER.ID, APP_USER.USERNAME).from(APP_USER)
//                    .where(APP_USER.REGISTRATION_ADD_TOKEN.eq(registrationAddTokenDecoded).and(
//                            APP_USER.REGISTRATION_ADD_START.gt(LocalDateTime.now().minusMinutes(10))))
//                    .fetchOne();

//            if (record == null) {
//                return new RegistrationStartResponse(
//                        RegistrationStartResponse.Status.TOKEN_INVALID);
//            }
//
//            userId = record.get(APP_USER.ID);
//            name = record.get(APP_USER.USERNAME);
//            mode = RegistrationStartResponse.Mode.ADD;
        } else if (recoveryToken != null && !recoveryToken.isEmpty()) {
            byte[] recoveryTokenDecoded = null;

//            try {
//                recoveryTokenDecoded = Base58.decode(recoveryToken);
//            } catch (Exception e) {
//                return new RegistrationStartResponse(
//                        RegistrationStartResponse.Status.TOKEN_INVALID);
//            }
//
//            var record = this.dsl.select(APP_USER.ID, APP_USER.USERNAME).from(APP_USER)
//                    .where(APP_USER.RECOVERY_TOKEN.eq(recoveryTokenDecoded)).fetchOne();
//
//            if (record == null) {
//                return new RegistrationStartResponse(
//                        RegistrationStartResponse.Status.TOKEN_INVALID);
//            }
//
//            userId = record.get(APP_USER.ID);
//            name = record.get(APP_USER.USERNAME);
//            mode = RegistrationStartResponse.Mode.RECOVERY;
//
//            this.dsl.deleteFrom(CREDENTIALS).where(CREDENTIALS.APP_USER_ID.eq(userId))
//                    .execute();
        }

        if (mode != null) {
            PublicKeyCredentialCreationOptions credentialCreation = this.relyingParty
                    .startRegistration(StartRegistrationOptions.builder()
                            .user(UserIdentity.builder().name(name).displayName(name)
                                    .id(new ByteArray(BytesUtil.longToBytes(userId))).build())
                            .build());

            byte[] registrationId = new byte[16];
            this.random.nextBytes(registrationId);
            RegistrationStartResponse startResponse = new RegistrationStartResponse(mode,
                    Base64.getEncoder().encodeToString(registrationId), credentialCreation);

            this.registrationCache.put(startResponse.getRegistrationId(), startResponse);

            return startResponse;
        }

        return null;
    }

    @PostMapping("/registration/finish")
    public String registrationFinish(@RequestBody RegistrationFinishRequest finishRequest) throws RegistrationFailedException {

        RegistrationStartResponse startResponse = this.registrationCache
                .get(finishRequest.getRegistrationId());
        this.registrationCache.remove(finishRequest.getRegistrationId());

        if (startResponse == null) {
            throw new IllegalStateException("call start before this");

        }
        RegistrationResult registrationResult = this.relyingParty
                .finishRegistration(FinishRegistrationOptions.builder()
                        .request(startResponse.getPublicKeyCredentialCreationOptions())
                        .response(finishRequest.getCredential()).build());

        UserIdentity userIdentity = startResponse.getPublicKeyCredentialCreationOptions()
                .getUser();

        long userId = BytesUtil.bytesToLong(userIdentity.getId().getBytes());

        AppCredentials credentials = new AppCredentials(registrationResult.getKeyId().getId().getBytes(),
                userId, finishRequest.getCredential().getResponse().getParsedAuthenticatorData()
                .getSignatureCounter(),
                registrationResult.getPublicKeyCose().getBytes()
        );
        this.credentialRepository.save(credentials)
                .block();

        if (startResponse.getMode() == RegistrationStartResponse.Mode.NEW
                || startResponse.getMode() == RegistrationStartResponse.Mode.RECOVERY) {
            byte[] recoveryToken = new byte[16];
            this.random.nextBytes(recoveryToken);

            this.appUserRepository.findById(userId)
                    .doOnNext(u -> u.setRecoveryToken(recoveryToken))
                    .block();

            return String.valueOf(Base64.getEncoder().encode(recoveryToken));
        }

        // TODO reset registration flow
//            this.dsl.update(APP_USER)
//                    .set(APP_USER.REGISTRATION_ADD_START, (LocalDateTime) null)
//                    .set(APP_USER.REGISTRATION_ADD_TOKEN, (byte[]) null)
//                    .where(APP_USER.ID.eq(userId)).execute();
        return "OK";
    }

    @PostMapping("/assertion/start")
    public AssertionStartResponse start(@RequestBody String username) {
        byte[] assertionId = new byte[16];
        this.random.nextBytes(assertionId);

        String assertionIdBase64 = Base64.getEncoder().encodeToString(assertionId);
        AssertionRequest assertionRequest = this.relyingParty
                .startAssertion(StartAssertionOptions.builder().username(username).build());

        AssertionStartResponse response = new AssertionStartResponse(assertionIdBase64,
                assertionRequest);

        this.assertionCache.put(response.getAssertionId(), response);
        return response;
    }

    @PostMapping("/assertion/finish")
    public boolean finish(@RequestBody AssertionFinishRequest finishRequest) throws AssertionFailedException {

        AssertionStartResponse startResponse = this.assertionCache
                .get(finishRequest.getAssertionId());
        this.assertionCache.remove(finishRequest.getAssertionId());

        AssertionResult result = this.relyingParty.finishAssertion(
                FinishAssertionOptions.builder().request(startResponse.getAssertionRequest())
                        .response(finishRequest.getCredential()).build());

        if (result.isSuccess()) {
            AppCredentials credentials = this.credentialRepositoryService.updateSignatureCount(result);

            long userId = BytesUtil.bytesToLong(result.getUserHandle().getBytes());
            var appUserRecord = this.appUserRepository.findById(userId).block();

            if (appUserRecord != null) {
                AppUserDetail userDetail = new AppUserDetail(appUserRecord,
                        new SimpleGrantedAuthority("USER"));
                AppUserAuthentication auth = new AppUserAuthentication(userDetail);
                SecurityContextHolder.getContext().setAuthentication(auth);
                return true;
            }
        }

        return false;
    }

}
