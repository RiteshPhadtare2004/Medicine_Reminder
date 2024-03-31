package com.example.medicinereminder.ui.addMedicine;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddMedicineViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AddMedicineViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}