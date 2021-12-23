package com.allenyll.sw.sso.controller;

import com.allenyll.sw.common.util.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @GetMapping("/loginHtml")
    public String login() {
        return "login";
    }

    @ResponseBody
    @GetMapping("loginTest")
    public Result loginTest() {
        return new Result();
    }
}
