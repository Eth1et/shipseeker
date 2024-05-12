package com.example.shipseeker.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shipseeker.FirebaseManager;
import com.example.shipseeker.SharedViewModel;
import com.example.shipseeker.model.CruiseRoute;
import com.example.shipseeker.component.CruiseRouteView;
import java.util.List;

public class CruiseRouteAdapter extends RecyclerView.Adapter<CruiseRouteAdapter.CruiseRouteViewHolder> {
    private List<CruiseRoute> cruiseRoutes;
    Activity activity;
    SharedViewModel viewModel;
    FirebaseManager fm;

    public CruiseRouteAdapter(Activity activity, List<CruiseRoute> cruiseRoutes) {
        this.cruiseRoutes = cruiseRoutes;
        this.activity = activity;

        fm = FirebaseManager.getInstance();
        viewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(SharedViewModel.class);
    }

    @NonNull
    @Override
    public CruiseRouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CruiseRouteView cruiseRouteView = new CruiseRouteView(parent.getContext());
        return new CruiseRouteViewHolder(cruiseRouteView);
    }

    public void replaceRoutes(List<CruiseRoute> routes){
        this.cruiseRoutes = routes;
        notifyDataSetChanged();
    }

    public void handleBookingChange(CruiseRoute route){
        if(route == null) return;

        fm.queryRouteById(activity, route.getId()).observe((LifecycleOwner) activity, upToDateRoute -> {
            int pos = getIndexOfRoute(route.getId());

            if(upToDateRoute.getSlots() > 0){
                if(pos == -1){
                    cruiseRoutes.add(upToDateRoute);
                    notifyItemInserted(cruiseRoutes.size()-1);
                }
                else{
                    cruiseRoutes.get(pos).setSlots(upToDateRoute.getSlots());
                    notifyItemChanged(pos);
                }
            }
            else if(pos != -1){
                cruiseRoutes.remove(pos);
                notifyItemRemoved(pos);
            }
        });

    }

    private int getIndexOfRoute(String routeID){
        for(int i = 0; i < cruiseRoutes.size(); i++){
            if (cruiseRoutes.get(i).getId().equals(routeID)) return i;
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull CruiseRouteViewHolder holder, int position) {
        holder.itemView.init(cruiseRoutes.get(position));

        final String id = cruiseRoutes.get(position).getId();

        holder.itemView.button.setOnClickListener(view -> {
            final int pos = getIndexOfRoute(id);

            fm.bookRoute(activity, cruiseRoutes.get(pos))
                    .observe((LifecycleOwner) activity, success ->{
                if(success){
                    viewModel.notifyRouteBooked(cruiseRoutes.get(pos));
                    Log.i("CruiseRoute", "route booked!");
                }else{
                    Log.e("CruiseRoute", "Couldnt book cruise route!");
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return cruiseRoutes.size();
    }

    public static class CruiseRouteViewHolder extends RecyclerView.ViewHolder {
        CruiseRouteView itemView;

        public CruiseRouteViewHolder(@NonNull CruiseRouteView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
