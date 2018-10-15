package com.laxi.data.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Created by Chendk on 2018/10/15
 */
@RestController
@RequestMapping("/test")
class TestController {

    @GetMapping("/hello")
    fun test(): Mono<String> {
        return Mono.just("hello world")
    }
}