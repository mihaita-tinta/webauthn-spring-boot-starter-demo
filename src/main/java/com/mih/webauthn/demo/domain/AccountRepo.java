package com.mih.webauthn.demo.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepo extends CrudRepository<Account, Long> {

    List<Account> findAllByUsername(String username);
}
