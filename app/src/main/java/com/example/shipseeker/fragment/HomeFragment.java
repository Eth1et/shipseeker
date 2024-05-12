package com.example.shipseeker.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shipseeker.FirebaseManager;
import com.example.shipseeker.R;
import com.example.shipseeker.SharedViewModel;
import com.example.shipseeker.component.CruiseRouteView;
import com.example.shipseeker.model.CruiseRoute;
import com.google.firebase.Timestamp;

public class HomeFragment extends Fragment {

    private CruiseRoute hotDealRoute;
    private CruiseRouteView crv;
    private FirebaseManager fm;
    private SharedViewModel viewModel;

    public HomeFragment(){

    }

    private CruiseRoute getExampleRoute(){
        CruiseRoute route = new CruiseRoute();
        route.setName("Tehere are no active routes! :(");
        route.setDescription("A beautiful trip.");
        route.setDepartPlace("Makó");
        route.setPrice(150000);
        route.setSlots(0);

        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        long futureTimeSeconds = currentTimeSeconds + (2 * 24 * 60 * 60);
        route.setDepartTimestamp(new Timestamp(futureTimeSeconds, 0));

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.montenegro);
        route._setImage(bitmap);

        return route;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        crv = rootView.findViewById(R.id.hotDealCruiseRoute);
        fm = FirebaseManager.getInstance();

        if(hotDealRoute == null){
            getHotDeal();
        }else{
            initCRVWithRoute(hotDealRoute);
        }

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getRouteBooked().observe(getViewLifecycleOwner(), this::handleBookingChanged);
        viewModel.getRouteBookingCanceled().observe(getViewLifecycleOwner(), this::handleBookingChanged);

        return rootView;
    }

    private void handleBookingChanged(CruiseRoute route){
        if(route != null && hotDealRoute != null && route.getId().equals(hotDealRoute.getId())){
            if(route.getSlots() <= 0){
                getHotDeal();
            }
            else{
                fm.queryRouteById(getActivity(), route.getId()).observe(getViewLifecycleOwner(), updatedRoute -> {
                    hotDealRoute = updatedRoute;
                    crv.slotsTV.setText(String.valueOf(updatedRoute.getSlots()));
                });
            }
        }
    }

    private void getHotDeal(){
        fm.queryHotDealRoute(getActivity()).observe(getViewLifecycleOwner(), this::initCRVWithRoute);
    }

    private void initCRVWithRoute(CruiseRoute route){
        if(route != null){
            hotDealRoute = route;
        }else{
            hotDealRoute = getExampleRoute();
        }
        crv.init(hotDealRoute);

        crv.button.setOnClickListener(view -> {
            fm.bookRoute(requireActivity(), hotDealRoute).observe((LifecycleOwner) requireActivity(), success -> {

                if(success){
                    viewModel.notifyRouteBooked(hotDealRoute);
                    Log.i("CruiseRoute", "route booked!");
                }
                else{
                    Log.e("CruiseRoute", "Couldnt book cruise route!");
                }
            });
        });
    }
}