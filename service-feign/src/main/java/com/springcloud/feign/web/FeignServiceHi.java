package com.springcloud.feign.web;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by xu.guan on 2018/5/16.
 */
@FeignClient(value = "service-sayHi",fallback = FeignServiceHiHystrix.class)
public interface FeignServiceHi {
    @RequestMapping("/say")
    String saySomethingByFeign(@RequestParam(value = "something") String something);
}
