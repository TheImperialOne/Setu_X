package com.imperial.setux.patient;

import com.google.firebase.firestore.Exclude;

public class PatientDiagnosis {
    private String DocumentID;
    private String Date;
    private String HospitalName;
    private String Details;
    private String Diagnosis;
    private String Prescription;
    private String Treatment;

    public String getDoctor() {
        return Doctor;
    }

    private String Doctor;

    public PatientDiagnosis() {
        //public no-arg constructor needed
    }

    @Exclude
    public String getDocumentID() {
        return DocumentID;
    }

    public void setDocumentID(String documentID) {
        this.DocumentID = documentID;
    }

    public PatientDiagnosis(String Date, String HospitalName, String Details, String Diagnosis, String Prescription, String DocumentID, String Doctor, String Treatment) {
        this.Date = Date;
        this.HospitalName = HospitalName;
        this.Details = Details;
        this.Diagnosis = Diagnosis;
        this.Prescription = Prescription;
        this.DocumentID = DocumentID;
        this.Doctor = Doctor;
        this.Treatment = Treatment;
    }

    public String getDate() {
        return Date;
    }

    public String getHospitalName() {
        return HospitalName;
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

    public String getTreatment() {
        return Treatment;
    }
}

