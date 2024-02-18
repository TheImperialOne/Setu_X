package com.imperial.setux.hospital;

import android.view.View;

import com.imperial.setux.patient.Patient;

public interface SelectListener {
    void onItemClicked(Patient patient, View view);
}
