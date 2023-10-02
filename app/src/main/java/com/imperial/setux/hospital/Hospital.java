package com.imperial.setux.hospital;

import com.google.firebase.firestore.Exclude;

public class Hospital {
    private String documentId;
    private String Name, Registration, Email;

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

    public Hospital(String Registration, String HospitalName, String Email) {
        this.Registration = Registration;
        this.Name = HospitalName;
        this.Email = Email;
    }

    public String getRegistration() {
        return Registration;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }
}

