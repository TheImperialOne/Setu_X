package com.imperial.setux.patient;

import android.view.View;

import com.imperial.setux.patient.PatientDiagnosis;

public interface SelectListener {
    void onItemClicked(PatientDiagnosis rowModel, View view);

}
