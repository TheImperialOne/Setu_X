package com.imperial.setux.patient;

public class Patient {
    private String name;
    private String DOB;
    private String gender;
    private String bloodGroup;
    private String address;
    private String phoneNo;
    private String aadhaarNo;

    public void setName(String name) {
        this.name = name;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAadhaarNo(String aadhaarNo) {
        this.aadhaarNo = aadhaarNo;
    }


    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Patient() {

    }

    public Patient(String name, String DOB, String bloodGroup, String address, String phoneNo, String aadhaarNo, String gender) {
        this.name = name;
        this.DOB = DOB;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.address = address;
        this.phoneNo = phoneNo;
        this.aadhaarNo = aadhaarNo;
    }

}
