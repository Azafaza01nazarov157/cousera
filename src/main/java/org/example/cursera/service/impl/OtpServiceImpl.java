package org.example.cursera.service.impl;

import org.example.cursera.service.gmail.OtpService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {
    private static final String OTP_CHARACTERS = "0123456789";

    @Override
    public String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(OTP_CHARACTERS.length());
            char character = OTP_CHARACTERS.charAt(index);
            otp.append(character);
        }

        return otp.toString();
    }
}
