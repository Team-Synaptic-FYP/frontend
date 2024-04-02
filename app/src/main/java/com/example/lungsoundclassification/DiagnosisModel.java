package com.example.lungsoundclassification;

public class DiagnosisModel {
    private String disease;
    private String confidentLevel;

    public DiagnosisModel(String disease, String confidentLevel){
        this.disease = disease;
        this.confidentLevel = confidentLevel;
    }

    public String getDisease() {
        return disease;
    }

    public String getConfidentLevel() {
        return confidentLevel;
    }
}
