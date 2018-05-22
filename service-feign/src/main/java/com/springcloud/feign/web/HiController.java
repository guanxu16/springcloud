package com.springcloud.feign.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xu.guan on 2018/5/16.
 */
@RestController
public class HiController {
    @Autowired
    private FeignServiceHi feignServiceHi;

    @RequestMapping("/hi")
    public String sayHi(@RequestParam String something){
        return "feign->"+feignServiceHi.saySomethingByFeign(something);
    }
}
