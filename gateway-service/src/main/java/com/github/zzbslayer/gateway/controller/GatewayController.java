package com.github.zzbslayer.gateway.controller;

import com.github.zzbslayer.gateway.service.ServiceInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayController {
    @Autowired
    ServiceInvoker serviceInvoker;

    @GetMapping("/gateway")
    public Object gateway(@RequestParam String service) {
        return serviceInvoker.invokeService(service);
    }
}
