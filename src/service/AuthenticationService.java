//Implementation of Authentication operations.
//Consolidates all authentication logic including login, 2FA, password reset, and account lockout.

package service;

import repository.UserRepository;
import repository.EmployeeRepository;
import model.Employee;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationService implements IAuthenticationService {
    
    private final UserRepository userRepository = new UserRepository();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();
    private final TwoFactorAuthService twoFactorAuthService = new TwoFactorAuthService();
    
    // Track failed attempts and lockout times per username
    private final Map<String, Integer> failedAttempts = new HashMap<>();
    private final Map<String, Long> accountLockoutTimes = new HashMap<>();
    
    // Password validation constants
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCKOUT_DURATION_MS = 5 * 60 * 1000; // 5 minutes
    private static final long OTP_EXPIRY_MS = 5 * 60 * 1000; // 5 minutes
    
    private String lastError = null;
    
    @Override
    public Employee authenticate(String username, String password) {
        lastError = null;
        
        // Check if account is locked
        if (isAccountLocked(username)) {
            long secondsRemaining = (getAccountUnlockTime(username) - System.currentTimeMillis()) / 1000;
            lastError = "Account locked. Try again in " + secondsRemaining + " seconds.";
            return null;
        }
        
        // Attempt authentication using repository data access + service-side rules.
        Employee emp = authenticateFromCredentials(username, password);
        
        if (emp != null) {
            // Successful authentication - clear failed attempts
            clearFailedLoginAttempts(username);
            return emp;
        } else {
            // Failed authentication - record attempt
            recordFailedLoginAttempt(username);
            int attempts = getFailedLoginAttempts(username);
            
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                lockAccount(username, LOCKOUT_DURATION_MS);
                lastError = "Account locked after 3 failed attempts. Try again in 5 minutes.";
            } else {
                lastError = "Invalid username or password. Attempt " + attempts + " of " + MAX_FAILED_ATTEMPTS + ".";
            }
            return null;
        }
    }
    
    @Override
    public boolean verifySecurityAnswer(String empId, String answer) {
        if (empId == null || answer == null) {
            return false;
        }
        String stored = userRepository.getSecurityAnswer(empId.trim());
        return stored != null && stored.equalsIgnoreCase(answer.trim());
    }
    
    @Override
    public String getSecurityQuestion(String empId) {
        return userRepository.getSecurityQuestion(empId);
    }
    
    @Override
    public boolean updatePassword(String empId, String newPassword) {
        if (!validatePasswordFormat(newPassword)) {
            lastError = "Password must meet requirements: alphanumeric only, at least 1 uppercase, 1 digit.";
            return false;
        }
        return userRepository.updatePassword(empId, newPassword);
    }
    
    @Override
    public String generateOtp() {
        return twoFactorAuthService.generateOtp();
    }
    
    @Override
    public boolean sendOtpEmail(String email, String otp) {
        boolean sent = twoFactorAuthService.sendOtpEmail(email, otp);
        if (!sent) {
            lastError = twoFactorAuthService.getLastError();
        }
        return sent;
    }
    
    @Override
    public String getLastError() {
        return lastError;
    }
    
    @Override
    public boolean validatePasswordFormat(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // Only letters and numbers
        boolean isAlphanumeric = password.matches("^[a-zA-Z0-9]+$");
        
        // At least one uppercase letter
        boolean hasCapital = password.matches(".*[A-Z].*");
        
        // At least one digit
        boolean hasNumber = password.matches(".*\\d.*");
        
        return isAlphanumeric && hasCapital && hasNumber;
    }
    
    @Override
    public void recordFailedLoginAttempt(String username) {
        failedAttempts.put(username, getFailedLoginAttempts(username) + 1);
    }
    
    @Override
    public void clearFailedLoginAttempts(String username) {
        failedAttempts.remove(username);
    }
    
    @Override
    public int getFailedLoginAttempts(String username) {
        return failedAttempts.getOrDefault(username, 0);
    }
    
    @Override
    public boolean isAccountLocked(String username) {
        Long lockUntil = accountLockoutTimes.get(username);
        if (lockUntil == null) {
            return false;
        }
        
        if (System.currentTimeMillis() >= lockUntil) {
            // Lockout expired - remove it
            accountLockoutTimes.remove(username);
            clearFailedLoginAttempts(username);
            return false;
        }
        
        return true;
    }
    
    @Override
    public void lockAccount(String username, long durationMillis) {
        accountLockoutTimes.put(username, System.currentTimeMillis() + durationMillis);
    }
    
    @Override
    public long getAccountUnlockTime(String username) {
        return accountLockoutTimes.getOrDefault(username, 0L);
    }

    private Employee authenticateFromCredentials(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        java.util.Optional<String[]> rowOpt = userRepository.findLoginRowByUsername(username.trim());
        if (rowOpt.isEmpty()) {
            return null;
        }
        String[] row = rowOpt.get();
        if (row.length < 7) {
            return null;
        }
        String storedPassword = row[6] == null ? "" : row[6].trim();
        if (!storedPassword.equals(password)) {
            return null;
        }
        try {
            int empId = Integer.parseInt((row[0] == null ? "" : row[0]).trim());
            String accessRole = (row.length >= 10 && row[9] != null && !row[9].trim().isEmpty())
                    ? row[9].trim()
                    : "EMPLOYEE";
            return employeeRepository.findByIdWithRole(empId, accessRole).orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
