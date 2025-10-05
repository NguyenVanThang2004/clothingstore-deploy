package vn.ClothingStore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ClothingStore.service.EmailService;
import vn.ClothingStore.util.annotation.ApiMessage;

@RestController
@RequestMapping("api/v1")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    @ApiMessage("send simple email")
    public String sendSimpleEmail() {
        // this.emailService.sendSimpleEmail();
        // this.emailService.sendEmailSync("boyhatinh07@gmail.com", "test quên mật
        // khẩu", "<h1> <b>siuu</b></h1>", false,
        // true);
        // this.emailService.sendEmailFromTemplateSync("boyhatinh07@gmail.com", "test",
        // "forgot_password");
        return "ok";
    }

}
