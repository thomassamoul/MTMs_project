package com.thomas.mtmsproject.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.thomas.mtmsproject.database.FirebaseRepo;
import com.thomas.mtmsproject.models.Drivers;
import com.thomas.mtmsproject.models.SourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<List<SourceLocation>> sourceMutableLiveData;
    private MutableLiveData<List<Drivers>> driversMutableLiveData;

    public LiveData<List<SourceLocation>> getSourceMutableLiveData() {
        if (sourceMutableLiveData == null) {
            sourceMutableLiveData = new MutableLiveData<>();
            loadSources();
        }
        return sourceMutableLiveData;
    }

    public LiveData<List<Drivers>> getDriversMutableLiveData() {
        if (driversMutableLiveData == null) {
            driversMutableLiveData = new MutableLiveData<>();
            loadDrivers();
        }
        return driversMutableLiveData;
    }

    private void loadDrivers() {
        FirebaseRepo.getInstance().getDrivers().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Drivers> driversArrayList = new ArrayList<>();
                for (QueryDocumentSnapshot dc : task.getResult()) {
                    Drivers sourceLocation = dc.toObject(Drivers.class);
                    driversArrayList.add(sourceLocation);
                }
                driversMutableLiveData.setValue(driversArrayList);
            }
        });
    }

    private void loadSources() {
        FirebaseRepo.getInstance().getSource().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<SourceLocation> sourceLocationList = new ArrayList<>();
                for (QueryDocumentSnapshot dc : task.getResult()) {
                    SourceLocation sourceLocation = dc.toObject(SourceLocation.class);
                    sourceLocationList.add(sourceLocation);
                }
                sourceMutableLiveData.setValue(sourceLocationList);
            }
        });
    }


}
