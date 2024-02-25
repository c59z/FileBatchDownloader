package com.yuki.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost")
@RestController
@RequestMapping("/Test")
public class TestController {
    @Value("${user.filepath}")
    private String filePath;

    @GetMapping("/testPath")
    public String testPath(){
        return filePath;
    }

}
