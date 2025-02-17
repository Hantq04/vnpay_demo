package com.example.vnpay.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class TestController {
    @GetMapping("/success_payment")
    public String successPayment(@RequestParam Map<String, String> params) {
        return "Thanh toán thành công! Chi tiết giao dịch: " + params.toString();
    }
}

