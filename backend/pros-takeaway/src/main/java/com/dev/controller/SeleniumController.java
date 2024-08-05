package com.dev.controller;

import com.dev.selenium.Selenium;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/selenium")
public class SeleniumController {

    @Autowired
    private Selenium selenium;

    @GetMapping
    public void selenium() {
        selenium.getSeleniumDriver();
    }

}
