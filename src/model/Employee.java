//Represents an Employee object with relevant attributes and methods.
//Stores employee details such as employee number, name, phone number, status, position, supervisor, salary, and allowances.
//Abstract class representing the Employee template.

//Why did we do this?
//Abstraction: By making the class abstract, we can no longer do new Employee().
//We must create a specific type of employee (like HRStaff), which is more realistic.
/**
 *
 * @author singh
 */

package model;

public abstract class Employee implements ICalculatable {
    //Identity Information
    private int employeeNumber;
    private String lastName;
    private String firstName;
    private String birthday;
    private String email;
    
    //Contact and Admin
    private String phoneNumber;
    private String address;
    private String status;
    private String position;
    private String supervisor;
    
    //Government Id's
    private String sssNumber;
    private String philHealthNumber;
    private String tinNumber;
    private String pagIbigNumber;
    
    //Financials
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossSemiMonthlyRate;
    private double hourlyRate;
    private double withholdingTax;
   
    
    //short Constructor
    public Employee(int employeeNumber, String lastName, String firstName, String email) {
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
    }
    
    public String getRole() {
    if (this instanceof AdminStaff) {
        return "ADMIN";
    }
    if (this instanceof HRStaff) {
        return "HR";
    }
    if (this instanceof FinanceStaff) {
        return "FINANCE";
    }
    if (this instanceof ITStaff) {
        return "IT";
    }
    return "REGULAR";
}
        
    //main contstructor
    public Employee(int employeeNumber, String lastName, String firstName, String birthday, String phoneNumber, String address, String status, String position, String supervisor, String sssNumber, String philHealthNumber, String tinNumber, String pagIbigNumber, double basicSalary, double riceSubsidy, double phoneAllowance, double clothingAllowance, double grossSemiMonthlyRate, double hourlyRate, double withholdingTax) {
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = status;
        this.position = position;
        this.supervisor = supervisor;
        this.sssNumber = sssNumber;
        this.philHealthNumber = philHealthNumber;
        this.tinNumber = tinNumber;
        this.pagIbigNumber = pagIbigNumber;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        this.hourlyRate = hourlyRate;
        this.withholdingTax = withholdingTax;
    }
    
    
    //HELPER METHODS
    //instead of storing full name, compute it when needed
    public String getFullName(){
        return firstName + " " + lastName;
    }
    
    //Calculate Total Allowances
    public double getTotalAllowances(){
        return riceSubsidy + phoneAllowance + clothingAllowance;
    }
    
    //---------getters-------------

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getBirthday() {
        return birthday;
    }
    
    public String getEmail() { 
        return email; 
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }

    public String getPosition() {
        return position;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public String getSssNumber() {
        return sssNumber;
    }

    public String getPhilHealthNumber() {
        return philHealthNumber;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public String getPagIbigNumber() {
        return pagIbigNumber;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public double getRiceSubsidy() {
        return riceSubsidy;
    }

    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    public double getClothingAllowance() {
        return clothingAllowance;
    }

    public double getGrossSemiMonthlyRate() {
        return grossSemiMonthlyRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public double getWithholdingTax() {
        return withholdingTax;
    }
    
    //-----------Setters----------

    public void setEmployeeNumber(int employeeNumber) {   
        this.employeeNumber = employeeNumber;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public void setSssNumber(String sssNumber) {
        this.sssNumber = sssNumber;
    }

    public void setPhilHealthNumber(String philHealthNumber) {
        this.philHealthNumber = philHealthNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber;
    }

    public void setPagIbigNumber(String pagIbigNumber) {
        this.pagIbigNumber = pagIbigNumber;
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public void setRiceSubsidy(double riceSubsidy) {
        this.riceSubsidy = riceSubsidy;
    }

    public void setPhoneAllowance(double phoneAllowance) {
        this.phoneAllowance = phoneAllowance;
    }

    public void setClothingAllowance(double clothingAllowance) {
        this.clothingAllowance = clothingAllowance;
    }

    public void setGrossSemiMonthlyRate(double grossSemiMonthlyRate) {
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setWithholdingTax(double withholdingTax) {
        this.withholdingTax = withholdingTax;
    }
    
    //ABSTRACT METHODS (ICalculatable)
    @Override
    public abstract double calculateGrossIncome(double hoursWorked, double overtimeHours);
    
    @Override
    public abstract double calculateNetPay(double grossIncome, double totalDeductions);


    /**
     * Overridden toString() method provides a readable representation of the employee object.
     */
    @Override
    public String toString() {
        return "Employee{" +
                "EmpNum=" + employeeNumber +
                ", LastName='" + lastName + "'" +
                ", FirstName='" + firstName + "'" +
                ", PhoneNumber='" + phoneNumber + "'" +
                ", Status='" + status + "'" +
                ", Position='" + position + "'" +
                ", Supervisor='" + supervisor + "'" +
                ", Address='" + address + "'" +
                ", SSS='" + sssNumber + "'" +
                ", PHILHEALTH='" + philHealthNumber + "'" +
                ", TIN='" + tinNumber + "'" +
                ", PAGIBIG='" + pagIbigNumber + "'" +
                ", Basic Salary=" + basicSalary +
                ", Rice Subsidy=" + riceSubsidy +
                ", Phone Allowance=" + phoneAllowance +
                ", Clothing Allowance=" + clothingAllowance +
                ", Gross Semi-monthly Rate=" + grossSemiMonthlyRate +
                ", Hourly Rate=" + hourlyRate +
                ", Withholding Tax=" + withholdingTax +
                ", Total Allowances=" + getTotalAllowances() +
                "}";
    }
}
