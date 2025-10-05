package vn.ClothingStore.controller;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.http44.api.Response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import vn.ClothingStore.domain.request.payment.reqVnpayDTO;
import vn.ClothingStore.service.PaymentService;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class PaymentController {
    private final PaymentService paymentService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public ResponseEntity<String> createPayment(@RequestBody reqVnpayDTO paymentRequest,
            HttpServletRequest request) {
        try {
            String paymentUrl = paymentService.createPayment(paymentRequest, request);
            return ResponseEntity.ok(paymentUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi tạo thanh toán!");
        }
    }

    @GetMapping("/payment/return")
    public ResponseEntity<Void> returnPayment(@RequestParam Map<String, String> allParams) {
        String responseCode = allParams.get("vnp_ResponseCode");
        String redirectUrl;

        if ("00".equals(responseCode)) {
            // Thanh toán thành công → tạo đơn hàng
            this.paymentService.handlePaymentSuccess(allParams);
            redirectUrl = frontendUrl + "/payment-result?status=SUCCESS";
            log.info("VNPAY responseCode={}, redirectUrl={}", responseCode, redirectUrl);
        } else {
            redirectUrl = frontendUrl + "/payment-result?status=FAILED";
            log.warn("VNPAY responseCode={}, redirectUrl={}", responseCode, redirectUrl);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 redirect
    }

}
