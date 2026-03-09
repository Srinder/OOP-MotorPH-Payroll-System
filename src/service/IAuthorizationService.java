/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

// Interface defining the Role-Based Access Control (RBAC) contract.
//Separates "What a user can do" from "Who a user is."

import model.Employee;
/**
 *
 * @author singh
 */
public interface IAuthorizationService {

    
    boolean canManageSystem(Employee emp);
    boolean canViewReports(Employee emp);
    boolean canViewOtherEmployeePayslip(Employee emp);
    boolean canViewMasterEmployeeInfo(Employee emp);
    boolean isReadOnlyInEmployeeEditor(Employee emp);
    boolean canManageAttendanceRecords(Employee emp);
    boolean canSelectOtherEmployeeAttendance(Employee emp);
    boolean canSelectOtherEmployeeLeave(Employee emp);
    boolean shouldUseMyProfileLabel(Employee emp);
}
    
    
