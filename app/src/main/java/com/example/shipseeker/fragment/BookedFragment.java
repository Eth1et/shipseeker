package com.example.shipseeker.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shipseeker.FirebaseManager;
import com.example.shipseeker.R;
import com.example.shipseeker.SharedViewModel;
import com.example.shipseeker.adapter.BookedRouteAdapter;
import com.example.shipseeker.adapter.CruiseRouteAdapter;
import com.example.shipseeker.decoration.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.Objects;

public class BookedFragment extends Fragment {

    public BookedFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booked, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        BookedRouteAdapter adapter = new BookedRouteAdapter(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        SharedViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) requireActivity()).get(SharedViewModel.class);

        viewModel.getRouteBookingCanceled().observe((LifecycleOwner) requireActivity(), adapter::handleBookingChange);
        viewModel.getRouteBooked().observe((LifecycleOwner) requireActivity(),  adapter::handleBookingChange);

        int leftSpace = getResources().getDimensionPixelSize(R.dimen.leftSpace);
        int rightSpace = getResources().getDimensionPixelSize(R.dimen.rightSpace);
        int topSpace = getResources().getDimensionPixelSize(R.dimen.topSpace);
        int bottomSpace = getResources().getDimensionPixelSize(R.dimen.bottomSpace);

        recyclerView.addItemDecoration(
                new SpaceItemDecoration(
                        getActivity(),
                        topSpace, rightSpace, bottomSpace, leftSpace)
        );

        FirebaseManager.getInstance().queryBookedRoutes(getActivity()).observe(getViewLifecycleOwner(), cruiseRoutes ->{
            ((BookedRouteAdapter) Objects.requireNonNull(recyclerView.getAdapter())).replaceRoutes(cruiseRoutes);
        });

        return view;
    }
}