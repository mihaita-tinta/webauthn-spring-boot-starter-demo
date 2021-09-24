package com.mih.webauthn.demo.domain;

import org.springframework.data.repository.CrudRepository;

public interface AccountRepo extends CrudRepository<Account, Long> {

    Iterable<Account> findAllByUsername(String username);
}
