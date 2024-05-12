package com.example.shipseeker.component;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.shipseeker.R;
import com.example.shipseeker.SharedViewModel;
import com.example.shipseeker.model.CruiseRoute;
import com.google.firebase.auth.FirebaseAuth;

public class BookedRouteView extends ConstraintLayout {

    public TextView nameTV, descTV, departPlaceTV, departTimeTV, priceTV, ticketsTV;
    public ImageView imageView;
    public Button button;

    public BookedRouteView(@NonNull Context context) {
        super(context);
    }

    public BookedRouteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BookedRouteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BookedRouteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(@NonNull CruiseRoute route) {
        if(nameTV == null){
            LayoutInflater.from(getContext()).inflate(R.layout.booked_route_view, this, true);
            nameTV = findViewById(R.id.routeName);
            descTV = findViewById(R.id.routeDesc);
            departPlaceTV = findViewById(R.id.routeDepartPlace);
            departTimeTV = findViewById(R.id.routeDepartTime);
            priceTV = findViewById(R.id.routePrice);
            imageView = findViewById(R.id.routeImage);
            ticketsTV = findViewById(R.id.ticketNumber);
        }

        nameTV.setText(route.getName());
        descTV.setText(route.getDescription());
        departPlaceTV.setText(route.getDepartPlace());
        departTimeTV.setText(route._getDepartureTimestampString());
        ticketsTV.setText(String.valueOf(route.getBookedBy().get(FirebaseAuth.getInstance().getUid())));
        imageView.setImageBitmap(route._getImage());

        String priceText = route.getPrice() + " Ft";
        priceTV.setText(priceText);

        button = findViewById(R.id.buttonBook);
    }
}
