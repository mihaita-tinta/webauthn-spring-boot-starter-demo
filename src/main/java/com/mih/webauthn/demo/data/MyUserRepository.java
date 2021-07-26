package com.mih.webauthn.demo.data;
import com.mih.webauthn.domain.WebAuthnUserRepository;
import org.springframework.data.repository.CrudRepository;

public interface MyUserRepository extends CrudRepository<MyUser, Long>, WebAuthnUserRepository<MyUser> {

}
