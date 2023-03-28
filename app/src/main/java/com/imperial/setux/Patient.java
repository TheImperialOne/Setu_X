package com.imperial.setux;

import com.google.firebase.firestore.Exclude;

public class Patient {
    private String documentId;
    private String Date, Hospital, Details, Diagnosis, Prescription;

    public Patient() {
        //public no-arg constructor needed
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Patient(String Date, String Hospital, String Details, String Diagnosis, String Prescription) {
        this.Date = Date;
        this.Hospital = Hospital;
        this.Details = Details;
        this.Diagnosis = Diagnosis;
        this.Prescription = Prescription;
    }

    public String getDate() {
        return Date;
    }

    public String getHospital() {
        return Hospital;
    }

    public String getDetails() {
        return Details;
    }

    public String getDiagnosis() {
        return Diagnosis;
    }

    public String getPrescription() {
        return Prescription;
    }
}

