package com.zwj.consumer.controller;

import com.zwj.api.service.HelloWorldService;
import com.zwj.client.annotation.RpcAutowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/5 15:40
 **/
@Controller
public class HelloWorldController {
    @RpcAutowired(version = "1.0")
    private HelloWorldService helloWorldService;
    @GetMapping("/hello/world")
    public ResponseEntity<String> pullServiceInfo(@RequestParam("name") String name){
        return ResponseEntity.ok(helloWorldService.sayHello(name));
    }

}
