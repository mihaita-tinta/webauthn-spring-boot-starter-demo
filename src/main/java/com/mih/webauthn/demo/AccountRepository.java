package com.mih.webauthn.demo;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {

    List<Account> findAllByUserId(Long userId);
}
