package com.imperial.setux.hospital;

import com.google.firebase.firestore.Exclude;

public class Hospital {
    private String documentId;
    private String HospitalName;
    private String Registration;
    private String Email;
    private String AdminNo;
    private String IsAdmin;

    public Hospital() {
        //public no-arg constructor needed
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Hospital(String Registration, String HospitalName, String Email, String IsAdmin) {
        this.Registration = Registration;
        this.HospitalName = HospitalName;
        this.Email = Email;
        this.IsAdmin = IsAdmin;
    }

    public Hospital(String Registration, String HospitalName, String Email, String IsAdmin, String AdminNo) {
        this.Registration = Registration;
        this.HospitalName = HospitalName;
        this.Email = Email;
        this.IsAdmin = IsAdmin;
        this.AdminNo = AdminNo;
    }

    public String getRegistration() {
        return Registration;
    }

    public String getHospitalName() {
        return HospitalName;
    }

    public String getEmail() {
        return Email;
    }

    public String getAdmin() {
        return IsAdmin;
    }

    public String getAdminNo() {
        return AdminNo;
    }
}

