package com.lien.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WrongAnswerGenerationController {
    @GetMapping
    public String helloWorld() {
        return "Hello";
    }
}
