package com.imperial.setux.medicalRecord;

public class MedicalRecord {
    private String date, doctor, hospital, details, diagnosis, treatment, prescription, fileUrl;

    public MedicalRecord() {
    } // Required for Firebase

    public MedicalRecord(String date, String hospitalName, String doctor, String diagnosis, String details, String treatment, String prescription, String fileUrl) {
        this.date = date;
        this.doctor = doctor;
        this.hospital = hospitalName;
        this.details = details;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.prescription = prescription;
        this.fileUrl = fileUrl;
    }


    public String getDate() {
        return date;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getHospital() {
        return hospital;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getPrescription() {
        return prescription;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getDetails() {
        return details;
    }
}
