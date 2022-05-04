package com.aopexample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class TestController {

    @GetMapping("/request")
    public String getRequest(HttpServletRequest request) {
        return "Request";
    }

    @GetMapping("/response")
    public String getResponse(HttpServletResponse response) {
        return "Response";
    }

    @GetMapping("/exc")
    public void getException(@RequestParam(defaultValue = "default") String str) {
        throw new IllegalArgumentException("Something went wrong");
    }

    @GetMapping("/nothing")
    public void doNothing() {

    }

}
