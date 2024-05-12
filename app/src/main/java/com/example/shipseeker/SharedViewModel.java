package com.example.shipseeker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shipseeker.model.CruiseRoute;
import com.google.firebase.auth.FirebaseAuth;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<CruiseRoute> routeBookingCanceled = new MutableLiveData<>();
    private MutableLiveData<CruiseRoute> routeBooked = new MutableLiveData<>();
    private MutableLiveData<String> routeDeleted = new MutableLiveData<>();
    private MutableLiveData<String> routeCreated = new MutableLiveData<>();

    public LiveData<CruiseRoute> getRouteBookingCanceled() {
        return routeBookingCanceled;
    }

    public void notifyRouteBookingCanceled(CruiseRoute changed) {
        routeBookingCanceled.setValue(changed);
    }

    public LiveData<CruiseRoute> getRouteBooked() {
        return routeBooked;
    }

    public void notifyRouteBooked(CruiseRoute changed) {
        routeBooked.setValue(changed);
    }

    public LiveData<String> getRouteDeleted() {
        return routeDeleted;
    }

    public void notifyRouteDeleted(String changed) {
        routeDeleted.setValue(changed);
    }

    public LiveData<String> getRouteCreated() {
        return routeCreated;
    }

    public void notifyRouteCreated(String changed) {
        routeCreated.setValue(changed);
    }
}
