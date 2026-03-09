/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.AuthorizationService to edit this template
 */
package service;

/**
 *
 * @author singh
 */
import model.Employee;
import service.IAuthorizationService;



// Implementation of RBAC logic for MotorPH.

public class AuthorizationService implements IAuthorizationService {

    @Override
    public boolean canManageSystem(Employee emp) {
        return "IT".equalsIgnoreCase(emp.getRole());
    }

    @Override
    public boolean canViewReports(Employee emp) {
        String role = emp.getRole();
        return "ADMIN".equalsIgnoreCase(role) || "FINANCE".equalsIgnoreCase(role);
    }

    @Override
    public boolean canViewMasterEmployeeInfo(Employee emp) {
        String role = emp.getRole();
        return "ADMIN".equalsIgnoreCase(role) || "HR".equalsIgnoreCase(role) || "FINANCE".equalsIgnoreCase(role);
    }

    @Override
    public boolean canViewOtherEmployeePayslip(Employee emp) {
        // Only Finance and Admin should see others' money
        String role = emp.getRole();
        return "ADMIN".equalsIgnoreCase(role) || "FINANCE".equalsIgnoreCase(role);
    }

    @Override
    public boolean canManageAttendanceRecords(Employee emp) {
        String role = emp.getRole();
        return "ADMIN".equalsIgnoreCase(role) || "HR".equalsIgnoreCase(role) || "FINANCE".equalsIgnoreCase(role);
    }

    @Override
    public boolean canSelectOtherEmployeeAttendance(Employee emp) {
        String role = emp.getRole();
        return "ADMIN".equalsIgnoreCase(role) || "FINANCE".equalsIgnoreCase(role);
    }

    @Override
    public boolean canSelectOtherEmployeeLeave(Employee emp) {
        String role = emp.getRole();
        return "ADMIN".equalsIgnoreCase(role) || "HR".equalsIgnoreCase(role);
    }

    @Override
    public boolean isReadOnlyInEmployeeEditor(Employee emp) {
        String role = emp.getRole();
        // Finance can see details but maybe shouldn't edit personal info like address
        return "FINANCE".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    @Override
    public boolean shouldUseMyProfileLabel(Employee emp) {
        String role = emp.getRole();
        return "IT".equalsIgnoreCase(role) 
                || "REGULAR".equalsIgnoreCase(role) 
                || "PROBATIONARY".equalsIgnoreCase(role);
    }
}
 
