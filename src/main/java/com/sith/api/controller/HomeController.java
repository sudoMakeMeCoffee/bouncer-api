package com.sith.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String hello(){
        return "Bouncer Api";
    }

    @GetMapping("/docs")
    public String docs(){
        return "Docs";
    }
}
