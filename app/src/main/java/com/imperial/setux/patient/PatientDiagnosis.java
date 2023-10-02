package com.imperial.setux.patient;

import com.google.firebase.firestore.Exclude;

public class PatientDiagnosis {
    private String documentId;
    private String Date, Hospital, Details, Diagnosis, Prescription;

    public PatientDiagnosis() {
        //public no-arg constructor needed
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public PatientDiagnosis(String Date, String Hospital, String Details, String Diagnosis, String Prescription) {
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

