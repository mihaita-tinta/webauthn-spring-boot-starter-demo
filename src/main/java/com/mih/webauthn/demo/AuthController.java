package com.mih.webauthn.demo;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class AuthController {

    @GetMapping("/authenticate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void authenticate() {
        // nothing here
    }

}
