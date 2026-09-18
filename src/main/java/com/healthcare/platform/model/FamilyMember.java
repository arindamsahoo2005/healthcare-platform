package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "family_members")
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String relation; // SELF, SPOUSE, FATHER, MOTHER, SON, DAUGHTER, GRANDPARENT

    private Integer age;
    private String bloodGroup;
    private String allergies = "None reported";
    private String chronicConditions = "None";
    private String emergencyContactPhone;
    private boolean caregiverAccessGranted = true;
    private Integer activeMedicinesCount = 2;
    private LocalDate lastCheckupDate;

    public FamilyMember() {}

    public FamilyMember(String fullName, String relation, Integer age, String bloodGroup, String allergies, String chronicConditions, String emergencyContactPhone) {
        this.fullName = fullName;
        this.relation = relation;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.allergies = allergies;
        this.chronicConditions = chronicConditions;
        this.emergencyContactPhone = emergencyContactPhone;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getChronicConditions() { return chronicConditions; }
    public void setChronicConditions(String chronicConditions) { this.chronicConditions = chronicConditions; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public boolean isCaregiverAccessGranted() { return caregiverAccessGranted; }
    public void setCaregiverAccessGranted(boolean caregiverAccessGranted) { this.caregiverAccessGranted = caregiverAccessGranted; }

    public Integer getActiveMedicinesCount() { return activeMedicinesCount; }
    public void setActiveMedicinesCount(Integer activeMedicinesCount) { this.activeMedicinesCount = activeMedicinesCount; }

    public LocalDate getLastCheckupDate() { return lastCheckupDate; }
    public void setLastCheckupDate(LocalDate lastCheckupDate) { this.lastCheckupDate = lastCheckupDate; }
}
