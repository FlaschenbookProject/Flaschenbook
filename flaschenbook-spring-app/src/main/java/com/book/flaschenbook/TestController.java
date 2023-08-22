package com.book.flaschenbook;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
 
import java.util.Date;
 
@RestController
public class TestController {
 
    @GetMapping("/test")
    public String time(){
        return "안녕하세요. 현재 서버의 시간은 " + new Date() + " 입니다!";
    }
 
}