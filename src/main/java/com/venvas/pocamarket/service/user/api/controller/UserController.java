package com.venvas.pocamarket.service.user.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/test")
    public ResponseEntity<Object> test() {
        return new ResponseEntity<>("api/user/test !!!", HttpStatus.OK);
    }

}
