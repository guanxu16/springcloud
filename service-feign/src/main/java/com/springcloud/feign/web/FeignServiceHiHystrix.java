package com.springcloud.feign.web;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by xu.guan on 2018/5/16.
 */
@Component
public class FeignServiceHiHystrix implements FeignServiceHi {
    @Override
    public String saySomethingByFeign( String something) {
        return "feign hystrix->"+something+",its a error";
    }
}
