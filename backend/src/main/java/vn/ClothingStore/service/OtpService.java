package vn.ClothingStore.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.ClothingStore.domain.Otp;
import vn.ClothingStore.repository.OtpRepository;

@Service
public class OtpService {
    private final OtpRepository otpRepository;
    private final SecureRandom random = new SecureRandom();

    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public String generateOtp6Digits(String email) {
        String otpCode = String.format("%06d", random.nextInt(999999));
        Instant expiryTime = Instant.now().plusSeconds(300); // 2 phút

        Otp otp = new Otp(email, otpCode, expiryTime);
        otpRepository.save(otp);

        return otpCode;
    }

    public boolean verifyOtp(String email, String code) {
        Optional<Otp> otpOptional = otpRepository.findById(email);
        if (otpOptional.isEmpty())
            return false;

        Otp otp = otpOptional.get();

        // kiểm tra hết hạn
        if (Instant.now().isAfter(otp.getExpiryTime())) {
            otpRepository.delete(otp); // xoá luôn
            return false;
        }

        boolean match = otp.getCode().equals(code);
        if (match) {
            otpRepository.delete(otp); // xoá sau khi dùng
        }

        return match;
    }

}
