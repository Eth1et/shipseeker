package com.example.shipseeker.component;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.shipseeker.R;
import com.example.shipseeker.model.CruiseRoute;

public class CruiseRouteView extends ConstraintLayout {

    public TextView nameTV, descTV, departPlaceTV, departTimeTV, priceTV, slotsTV;
    public ImageView imageView;
    public Button button;

    public CruiseRouteView(@NonNull Context context) {
        super(context);
    }

    public CruiseRouteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CruiseRouteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CruiseRouteView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(@NonNull CruiseRoute route) {
        if(nameTV == null){
            LayoutInflater.from(getContext()).inflate(R.layout.cruise_route_view, this, true);
            nameTV = findViewById(R.id.routeName);
            descTV = findViewById(R.id.routeDesc);
            departPlaceTV = findViewById(R.id.routeDepartPlace);
            departTimeTV = findViewById(R.id.routeDepartTime);
            priceTV = findViewById(R.id.routePrice);
            imageView = findViewById(R.id.routeImage);
            slotsTV = findViewById(R.id.ticketNumber);
        }

        nameTV.setText(route.getName());
        descTV.setText(route.getDescription());
        departPlaceTV.setText(route.getDepartPlace());
        departTimeTV.setText(route._getDepartureTimestampString());
        slotsTV.setText(String.valueOf(route.getSlots()));
        imageView.setImageBitmap(route._getImage());

        String priceText = route.getPrice() + " Ft";
        priceTV.setText(priceText);

        button = findViewById(R.id.buttonBook);

        if(route.getSlots() <= 0){
            button.setEnabled(false);
        }
    }
}
