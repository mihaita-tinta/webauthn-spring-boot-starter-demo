package com.mih.webauthn.repository;

import com.mih.webauthn.domain.AppCredentials;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AppCredentialsRepository extends ReactiveCrudRepository<AppCredentials, Long> {

    Flux<AppCredentials> findAllByAppUserId(Long userId);

    Mono<AppCredentials> findByCredentialIdAndAppUserId(byte[] credentialId, Long userId);
    Flux<AppCredentials> findByCredentialId(byte[] credentialId);
}
