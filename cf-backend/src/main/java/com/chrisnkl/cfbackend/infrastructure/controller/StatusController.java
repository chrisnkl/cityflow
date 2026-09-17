package com.chrisnkl.cfbackend.infrastructure.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/{v}/status")
@RestController
public class StatusController {

    @GetMapping
    public String getStatus() {
        return "Application is running successfully!";
    }

}
