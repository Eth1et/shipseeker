package com.example.shipseeker.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.shipseeker.FirebaseManager;
import com.example.shipseeker.R;
import com.example.shipseeker.SharedViewModel;
import com.example.shipseeker.model.CruiseRoute;
import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.List;


public class AdminFragment extends Fragment {

    private static final int CREATE_ADD_IMAGE_CODE = 1;
    private static final String LOG_TAG = AdminFragment.class.getName();

    // c = create
    private Uri cImageUri;
    private Calendar calendar;
    private Timestamp cTimestamp;
    private ImageView cPreviewImage;
    private EditText cNameET, cDescET, cDepartPlaceET, cPriceET, cSlotsET;
    private EditText[] inputFields;
    private TextView cTimestampTV;

    private TextView dNameTV, dDescTV, dDepartTimeTV, dDepartPlaceTV, dPriceTV, dSlotsTV;
    private ImageView dImageView;
    private List<CruiseRoute> routes;
    private int current = 0;

    private View view;

    private SharedViewModel viewModel;

    private FirebaseManager fm;

    public AdminFragment(){
        fm = FirebaseManager.getInstance();
        calendar = Calendar.getInstance();
    }

    private void resetParams(){
        cImageUri = null;
        cTimestamp = null;
        cPreviewImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.montenegro));
        cTimestampTV.setText(R.string.unset_date_and_time);

        for(EditText field: inputFields){
            field.setText("");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin, container, false);

        // CREATE
        cPreviewImage = view.findViewById(R.id.createImagePreview);
        cNameET = view.findViewById(R.id.createRouteName);
        cDescET = view.findViewById(R.id.createRouteDesc);
        cDepartPlaceET = view.findViewById(R.id.createRouteDepartPlace);
        cPriceET = view.findViewById(R.id.createRoutePrice);
        cSlotsET = view.findViewById(R.id.createRouteSlots);
        cTimestampTV = view.findViewById(R.id.createRouteTimestamp);

        inputFields = new EditText[] {cNameET, cDescET, cDepartPlaceET, cSlotsET, cPriceET};

        Button createB, timestampB, uploadB;
        createB = view.findViewById(R.id.createButton);
        timestampB = view.findViewById(R.id.createTimestampButton);
        uploadB = view.findViewById(R.id.createAddImage);

        createB.setOnClickListener(this::create);
        timestampB.setOnClickListener(this::pickTimestamp);
        uploadB.setOnClickListener(this::uploadImage);

        // DELETE
        dImageView = view.findViewById(R.id.deleteRouteImage);
        dNameTV = view.findViewById(R.id.deleteRouteName);
        dDescTV = view.findViewById(R.id.deleteRouteDesc);
        dDepartTimeTV = view.findViewById(R.id.deleteRouteDepartTime);
        dDepartPlaceTV = view.findViewById(R.id.deleteRouteDepartPlace);
        dPriceTV = view.findViewById(R.id.deleteRoutePrice);
        dSlotsTV = view.findViewById(R.id.deleteTicketNumber);

        Button deleteB = view.findViewById(R.id.buttonDelete);
        ImageButton prevB = view.findViewById(R.id.buttonPrevious);
        ImageButton nextB = view.findViewById(R.id.buttonNext);

        deleteB.setOnClickListener(this::delete);
        prevB.setOnClickListener(this::prev);
        nextB.setOnClickListener(this::next);

        fm.queryRoutes(getActivity(), -1515151).observe(getViewLifecycleOwner(), cruiseRoutes -> {
            this.routes = cruiseRoutes;

            if(cruiseRoutes != null && !cruiseRoutes.isEmpty()){
                current = 0;
                setDataOfDelete();
            }
        });

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getRouteCreated().observe(getViewLifecycleOwner(), this::handleRouteCreation);
        viewModel.getRouteDeleted().observe(getViewLifecycleOwner(), this::handleRouteDeletion);
        return view;
    }

    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CREATE_ADD_IMAGE_CODE);
    }

    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    showTimePicker();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    cTimestamp = new Timestamp(calendar.getTime());

                    CruiseRoute tmp = new CruiseRoute();
                    tmp.setDepartTimestamp(cTimestamp);
                    cTimestampTV.setText(tmp._getDepartureTimestampString());

                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_ADD_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            cImageUri = data.getData();
            cPreviewImage.setImageURI(cImageUri);
        }
    }

    public void create(View view) {
        if(cTimestamp == null || cImageUri == null) return;

        for(EditText field: inputFields){
            if(field.getText().toString().trim().isEmpty()){
                Log.e(LOG_TAG, "Invalid input at field");
                return;
            }
        }

        String name = cNameET.getText().toString();
        String desc = cDescET.getText().toString();
        String place = cDepartPlaceET.getText().toString();
        int slots, price;

        try{
            slots = Integer.parseInt(cSlotsET.getText().toString());
            price = Integer.parseInt(cPriceET.getText().toString());
        }catch (Exception e){
            Log.e(LOG_TAG, "invalid number format: " + e.getMessage());
            return;
        }

        CruiseRoute newRoute = new CruiseRoute();
        newRoute.setName(name);
        newRoute.setDescription(desc);
        newRoute.setSlots(slots);
        newRoute.setPrice(price);
        newRoute.setDepartPlace(place);
        newRoute.setDepartTimestamp(cTimestamp);

        fm.addRoute(requireActivity(), newRoute, cImageUri).observe(getViewLifecycleOwner(), success -> {
            if(success){
                Log.i(LOG_TAG, "Successfully added route!");
                viewModel.notifyRouteCreated(newRoute.getId());
                resetParams();
            }else{
                Log.e(LOG_TAG, "Couldn't add route!");
            }
        });
    }

    public void uploadImage(View view) {
        openFilePicker();
    }

    public void pickTimestamp(View view) {
        showDateTimePicker();
    }

    private void setDataOfDelete(){
        if(dPriceTV == null || routes == null || routes.isEmpty()){
            return;
        }

        CruiseRoute route = routes.get(current);

        dImageView.setImageBitmap(route._getImage());
        dNameTV.setText(route.getName());
        dDescTV.setText(route.getDescription());
        dDepartTimeTV.setText(route._getDepartureTimestampString());
        dDepartPlaceTV.setText(route.getDepartPlace());
        dPriceTV.setText(String.valueOf(route.getPrice()));
        dSlotsTV.setText(String.valueOf(route.getSlots()));
    }

    private void delete(View view){
        if(routes.isEmpty()) return;

        fm.deleteRouteById(routes.get(current).getId()).observe(getViewLifecycleOwner(), success -> {
            if(success){
                viewModel.notifyRouteDeleted(routes.get(current).getId());
            }else{
                Log.e(LOG_TAG, "couldn't delete route: " + routes.get(current).getId());
            }
        });
    }

    private void prev(View view){
        if(routes.isEmpty()) return;

        current = current - 1;
        if(current < 0){
            current += routes.size();
        }

        setDataOfDelete();
    }

    private void next(View view){
        if(routes.isEmpty()) return;

        current = (current + 1) % routes.size();

        setDataOfDelete();
    }

    private void handleRouteDeletion(String id){
        if(id == null) return;

        for(int i = 0; i < routes.size(); i++){
            if(routes.get(i).getId().equals(id)){
                routes.remove(i);
                break;
            }
        }

        current %= routes.size();
        setDataOfDelete();
    }

    private void handleRouteCreation(String id){
        if(id == null) return;

        fm.queryRouteById(getActivity(), id).observe(getViewLifecycleOwner(), newRoute -> {
            routes.add(newRoute);
        });
        setDataOfDelete();
    }
}