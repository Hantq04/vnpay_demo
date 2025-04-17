package com.example.vnpay.controller;

import com.example.vnpay.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final PaymentService paymentService;

    //    Trả về sau khi thanh toán
    @GetMapping("/success_payment")
    public String confirmPayment(HttpServletRequest request) {
        return paymentService.orderReturn(request) == 1 ? "orderSuccess" : "orderFail";
    }
    // This is master-branch
}
