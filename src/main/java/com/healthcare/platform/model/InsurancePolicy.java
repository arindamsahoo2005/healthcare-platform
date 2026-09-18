package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "insurance_policies")
public class InsurancePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String policyNumber;

    @Column(nullable = false)
    private String providerName; // Star Health, HDFC ERGO, Care Health, Niva Bupa, ICICI Lombard

    private String policyHolderName;
    private String planName; // Family Floater Comprehensive, Senior Citizen Red Carpet, Optima Restore
    private Double sumInsured = 1000000.0; // ₹10,00,000
    private Double remainingBalance = 920000.0;
    private LocalDate validUntil = LocalDate.now().plusMonths(10);

    private Double copayPercentage = 0.0;
    private Double deductible = 0.0;
    private Integer waitingPeriodPreExistingMonths = 24;
    private boolean cashlessNetworkActive = true;

    @Column(length = 1000)
    private String coveredBenefits = "Inpatient Hospitalization, Pre/Post Hospitalization (60/180 days), Daycare Treatments, Ambulance Cover (up to ₹5,000), AYUSH Treatment";

    private String claimStatus = "ACTIVE"; // ACTIVE, CLAIM_IN_PROCESS, SETTLED
    private String tpaContactPhone = "1800-425-2255";
    private String policyDocument = "Health_Policy_2026.pdf";

    public InsurancePolicy() {}

    public InsurancePolicy(String policyNumber, String providerName, String policyHolderName, String planName, Double sumInsured, Double remainingBalance, LocalDate validUntil) {
        this.policyNumber = policyNumber;
        this.providerName = providerName;
        this.policyHolderName = policyHolderName;
        this.planName = planName;
        this.sumInsured = sumInsured;
        this.remainingBalance = remainingBalance;
        this.validUntil = validUntil;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getPolicyHolderName() { return policyHolderName; }
    public void setPolicyHolderName(String policyHolderName) { this.policyHolderName = policyHolderName; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public Double getSumInsured() { return sumInsured; }
    public void setSumInsured(Double sumInsured) { this.sumInsured = sumInsured; }

    public Double getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(Double remainingBalance) { this.remainingBalance = remainingBalance; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public Double getCopayPercentage() { return copayPercentage; }
    public void setCopayPercentage(Double copayPercentage) { this.copayPercentage = copayPercentage; }

    public Double getDeductible() { return deductible; }
    public void setDeductible(Double deductible) { this.deductible = deductible; }

    public Integer getWaitingPeriodPreExistingMonths() { return waitingPeriodPreExistingMonths; }
    public void setWaitingPeriodPreExistingMonths(Integer waitingPeriodPreExistingMonths) { this.waitingPeriodPreExistingMonths = waitingPeriodPreExistingMonths; }

    public boolean isCashlessNetworkActive() { return cashlessNetworkActive; }
    public void setCashlessNetworkActive(boolean cashlessNetworkActive) { this.cashlessNetworkActive = cashlessNetworkActive; }

    public String getCoveredBenefits() { return coveredBenefits; }
    public void setCoveredBenefits(String coveredBenefits) { this.coveredBenefits = coveredBenefits; }

    public String getClaimStatus() { return claimStatus; }
    public void setClaimStatus(String claimStatus) { this.claimStatus = claimStatus; }

    public String getTpaContactPhone() { return tpaContactPhone; }
    public void setTpaContactPhone(String tpaContactPhone) { this.tpaContactPhone = tpaContactPhone; }

    public String getPolicyDocument() { return policyDocument; }
    public void setPolicyDocument(String policyDocument) { this.policyDocument = policyDocument; }
}
