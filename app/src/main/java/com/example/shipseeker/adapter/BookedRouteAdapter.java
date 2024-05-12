package com.example.shipseeker.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shipseeker.FirebaseManager;
import com.example.shipseeker.SharedViewModel;
import com.example.shipseeker.component.BookedRouteView;
import com.example.shipseeker.model.CruiseRoute;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class BookedRouteAdapter extends RecyclerView.Adapter<BookedRouteAdapter.BookedRouteViewHolder> {
    private List<CruiseRoute> cruiseRoutes;
    private Activity activity;
    private FirebaseManager fm;
    private FirebaseAuth auth;
    private SharedViewModel viewModel;

    public BookedRouteAdapter(Activity activity, List<CruiseRoute> cruiseRoutes) {
        this.cruiseRoutes = cruiseRoutes;
        this.activity = activity;

        fm = FirebaseManager.getInstance();
        auth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(SharedViewModel.class);
    }

    @NonNull
    @Override
    public BookedRouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BookedRouteView bookedRouteView = new BookedRouteView(parent.getContext());
        return new BookedRouteViewHolder(bookedRouteView);
    }

    public void replaceRoutes(List<CruiseRoute> routes){
        this.cruiseRoutes = routes;
        notifyDataSetChanged();
    }

    public void handleBookingChange(CruiseRoute route){
        if(route == null) return;

        final String id = auth.getUid();

        fm.queryRouteById(activity, route.getId()).observe((LifecycleOwner) activity, upToDateRoute -> {
            if(upToDateRoute == null) return;

            int pos = getIndexOfRoute(route.getId());

            if(upToDateRoute.getBookedBy().containsKey(id) && upToDateRoute.getBookedBy().get(id) > 0){
                if(pos == -1){
                    cruiseRoutes.add(upToDateRoute);
                    notifyItemInserted(cruiseRoutes.size()-1);
                }
                else{
                    cruiseRoutes.get(pos).getBookedBy().put(id, upToDateRoute.getBookedBy().get(id));
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
    public void onBindViewHolder(@NonNull BookedRouteViewHolder holder, int position) {
        holder.itemView.init(cruiseRoutes.get(position));

        final String id = cruiseRoutes.get(position).getId();

        holder.itemView.button.setOnClickListener(view -> {
            final int pos = getIndexOfRoute(id);

            fm.deleteBooking(activity, cruiseRoutes.get(pos))
                    .observe((LifecycleOwner) activity, success ->{
                if(success){
                    viewModel.notifyRouteBookingCanceled(cruiseRoutes.get(pos));
                    Log.i("BookedRoute", "route canceled!");
                }else{
                    Log.e("BookedRoute", "Couldn't cancel booking!");
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return cruiseRoutes.size();
    }

    public static class BookedRouteViewHolder extends RecyclerView.ViewHolder {
        BookedRouteView itemView;

        public BookedRouteViewHolder(@NonNull BookedRouteView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
