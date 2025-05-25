package com.venvas.pocamarket.service.pokemon.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("")
    public ResponseEntity<Object> hello() {
        return new ResponseEntity<>("hello world !!!", HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<Object> test() {
        return new ResponseEntity<>("testtest hello world !!!", HttpStatus.OK);
    }
    
}
