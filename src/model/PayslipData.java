/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author singh
 */

package model;

public class PayslipData {
    private String period;
    private double gross, sss, philhealth, pagibig, tax, allowances, net;

    public PayslipData(String period, double gross, double sss, double philhealth, 
                       double pagibig, double tax, double allowances, double net) {
        this.period = period;
        this.gross = gross;
        this.sss = sss;
        this.philhealth = philhealth;
        this.pagibig = pagibig;
        this.tax = tax;
        this.allowances = allowances;
        this.net = net;
    }
    
    public String getPeriod() { return period; }

    public double getSss() {
        return sss;
    }

    public double getPhilhealth() {
        return philhealth;
    }

    public double getPagibig() {
        return pagibig;
    }

    public double getTax() {
        return tax;
    }

    // Getters for all fields (e.g., getGross(), getNet(), etc.)
    public double getAllowances() {
        return allowances;
    }

    public double getNet() {
        return net;
    }
    public double getGross() { return gross; }
   
}