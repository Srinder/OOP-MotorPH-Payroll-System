package service;

import java.security.SecureRandom;

public class TwoFactorAuthService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private String lastError = "";

    public String generateOtp() {
        int value = RANDOM.nextInt(1_000_000);
        return String.format("%06d", value);
    }

    public boolean sendOtpEmail(String recipientEmail, String otp) {
        if (isBlank(otp) || !otp.matches("\\d{6}")) {
            lastError = "Invalid OTP format.";
            return false;
        }

        // Demo mode: no external API call, always succeed.
        lastError = "";
        return true;
    }

    public String getLastError() {
        return lastError;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
