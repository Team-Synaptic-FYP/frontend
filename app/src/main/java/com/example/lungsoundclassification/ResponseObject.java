package com.example.lungsoundclassification;

import java.io.Serializable;
import java.util.List;

public class ResponseObject implements Serializable {

    private List<String> diseases;
    private List<Float> probabilities;
    private List<Integer> severities;

    public ResponseObject() {
    }

    public ResponseObject(List<String> diseases, List<Float> probabilities, List<Integer> severities) {
        this.diseases = diseases;
        this.probabilities = probabilities;
        this.severities = severities;
    }

    public List<String> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<String> diseases) {
        this.diseases = diseases;
    }

    public List<Float> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(List<Float> predictions) {
        this.probabilities = predictions;
    }

    public List<Integer> getSeverities() {
        return severities;
    }

    public void setSeverities(List<Integer> severities) {
        this.severities = severities;
    }
}
