package com.healthcare.platform.model.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lab_tests")
public class LabTestDocument {

    @Id
    private String id;
    private String testName;
    private String category;
    private String sampleType;
    private String turnaroundTime;
    private Double price;
    private String preparationInstructions;
    private String labPartner;
    private boolean homeCollectionAvailable = true;

    public LabTestDocument() {}

    public LabTestDocument(String testName, String category, String sampleType, String turnaroundTime,
                           Double price, String preparationInstructions, String labPartner, boolean homeCollectionAvailable) {
        this.testName = testName;
        this.category = category;
        this.sampleType = sampleType;
        this.turnaroundTime = turnaroundTime;
        this.price = price;
        this.preparationInstructions = preparationInstructions;
        this.labPartner = labPartner;
        this.homeCollectionAvailable = homeCollectionAvailable;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSampleType() { return sampleType; }
    public void setSampleType(String sampleType) { this.sampleType = sampleType; }

    public String getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(String turnaroundTime) { this.turnaroundTime = turnaroundTime; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getPreparationInstructions() { return preparationInstructions; }
    public void setPreparationInstructions(String preparationInstructions) { this.preparationInstructions = preparationInstructions; }

    public String getLabPartner() { return labPartner; }
    public void setLabPartner(String labPartner) { this.labPartner = labPartner; }

    public boolean isHomeCollectionAvailable() { return homeCollectionAvailable; }
    public void setHomeCollectionAvailable(boolean homeCollectionAvailable) { this.homeCollectionAvailable = homeCollectionAvailable; }
}
