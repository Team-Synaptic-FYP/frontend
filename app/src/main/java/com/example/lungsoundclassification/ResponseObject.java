package com.example.lungsoundclassification;

import java.io.Serializable;
import java.util.List;

public class ResponseObject implements Serializable {

    private List<String> diseases;
    private List<Float> probabilities;
    private String xai_base64;

    public ResponseObject() {
    }

    public ResponseObject(List<String> diseases, List<Float> probabilities, String xai_base64) {
        this.diseases = diseases;
        this.probabilities = probabilities;
        this.xai_base64 = xai_base64;
    }

    public List<String> getDiseases() {
        return diseases;
    }


    public List<Float> getProbabilities() {
        return probabilities;
    }


    public String getXai_base64() {
        return xai_base64;
    }

    public void setXai_base64(String xai_base64) {
        this.xai_base64 = xai_base64;
    }
}
