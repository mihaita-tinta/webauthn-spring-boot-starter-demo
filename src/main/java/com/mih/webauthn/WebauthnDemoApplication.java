package com.mih.webauthn;

import com.yubico.webauthn.CredentialRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;

import java.util.Optional;

@SpringBootApplication
public class WebauthnDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebauthnDemoApplication.class, args);
	}

	@Bean
	public RelyingParty relyingParty(CredentialRepository credentialRepository,
									 AppProperties appProperties) {

		RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
				.id(appProperties.getRelyingPartyId()).name(appProperties.getRelyingPartyName())
				.icon(Optional.ofNullable(appProperties.getRelyingPartyIcon())).build();

		return RelyingParty.builder().identity(rpIdentity)
				.credentialRepository(credentialRepository)
				.origins(appProperties.getRelyingPartyOrigins()).build();
	}
}
