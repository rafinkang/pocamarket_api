package com.venvas.pocamarket.service.pokemon.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/hello")
    public ResponseEntity<Object> hello() {
        return new ResponseEntity<>("hello world !!!123123", HttpStatus.OK);
    }
    
}
