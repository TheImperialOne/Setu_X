package com.imperial.setux.patient;

import java.util.Date;

public class Patient {
    private String Name;
    private String DateOfBirth;
    private String Gender;
    private String BloodGroup;
    private String Address;
    private String Phone;
    private String Aadhaar;

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setDateOfBirth(String DateOfBirth) {
        this.DateOfBirth = DateOfBirth;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public void setBloodGroup(String BloodGroup) {
        this.BloodGroup = BloodGroup;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public void setAadhaarNo(String Aadhaar) {
        this.Aadhaar = Aadhaar;
    }


    public void setPhoneNo(String Phone) {
        this.Phone = Phone;
    }

    public Patient() {

    }

    public String getName() {
        return Name;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public String getGender() {
        return Gender;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public String getAddress() {
        return Address;
    }

    public String getPhoneNo() {
        return Phone;
    }

    public String getAadhaarNo() {
        return Aadhaar;
    }


    public Patient(String Aadhaar, String Address, String BloodGroup, String DateOfBirth, String Gender, String Name, String Phone) {
        this.Name = Name;
        this.DateOfBirth = DateOfBirth;
        this.Gender = Gender;
        this.BloodGroup = BloodGroup;
        this.Address = Address;
        this.Phone = Phone;
        this.Aadhaar = Aadhaar;
    }
    public Patient(String Aadhaar, String BloodGroup, String DateOfBirth, String Gender, String Name, String Phone) {
        this.Name = Name;
        this.DateOfBirth = DateOfBirth;
        this.Gender = Gender;
        this.BloodGroup = BloodGroup;
        this.Phone = Phone;
        this.Aadhaar = Aadhaar;
    }

}
