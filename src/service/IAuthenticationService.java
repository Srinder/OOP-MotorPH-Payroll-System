//Interface defining the contract for Authentication operations.

package service;

import model.Employee;

public interface IAuthenticationService {
    
    Employee authenticate(String username, String password);
    
    boolean verifySecurityAnswer(String empId, String answer);
    
    String getSecurityQuestion(String empId);
    
    boolean updatePassword(String empId, String newPassword);
    
    String generateOtp();
    
    boolean sendOtpEmail(String email, String otp);
    
    String getLastError();
    
    boolean validatePasswordFormat(String password);
    
    void recordFailedLoginAttempt(String username);
    
    void clearFailedLoginAttempts(String username);
    
    int getFailedLoginAttempts(String username);
    
    boolean isAccountLocked(String username);
    
    void lockAccount(String username, long durationMillis);
    
    long getAccountUnlockTime(String username);
}
