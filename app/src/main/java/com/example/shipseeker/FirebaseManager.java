package com.example.shipseeker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shipseeker.model.CruiseRoute;
import com.example.shipseeker.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseManager {

    private static String LOG_TAG = FirebaseManager.class.getName();
    private static FirebaseManager instance;
    private FirebaseFirestore db;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private static FirebaseAuth auth;

    private FirebaseManager(){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseManager getInstance(){
        if(instance == null){
            synchronized (FirebaseManager.class){
                if(instance == null){
                    instance = new FirebaseManager();
                }
            }
        }
        return instance;
    }

    public LiveData<Bitmap> getBitmapOfRoute(Activity activity, CruiseRoute route){
        String[] split = route.getImageName().split("/");
        String filename = split[split.length-1];

        StorageReference ref = storage.getReference().child("images/" + filename);
        long MEGABYTE = 1024 * 1024;

        File cacheDir = activity.getCacheDir();
        File cachedFile = new File(cacheDir, filename);

        MutableLiveData<Bitmap> bitmap = new MutableLiveData<>();

        if(!cachedFile.exists()){
            ref.getBytes(MEGABYTE).addOnCompleteListener(task -> {

                if(task.isSuccessful()){
                    try{
                        FileOutputStream outputStream = new FileOutputStream(cachedFile);
                        outputStream.write(task.getResult());
                        outputStream.close();

                        bitmap.setValue(BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length));
                        Log.i(LOG_TAG, "Downloaded file: " + cachedFile);
                    }
                    catch (IOException e){
                        Log.e(LOG_TAG, "Couldnt download file: " + e.getMessage());
                        bitmap.setValue(BitmapFactory.decodeResource(activity.getResources(), R.drawable.montenegro));
                    }
                }
                else{
                    Log.e(LOG_TAG, "Couldnt download file: " + task.getException());
                    bitmap.setValue(BitmapFactory.decodeResource(activity.getResources(), R.drawable.montenegro));
                }
            });
        }
        else{
            bitmap.setValue(BitmapFactory.decodeFile(cachedFile.getAbsolutePath()));
            Log.i(LOG_TAG, "Using Cached file: " + cachedFile);
        }

        return bitmap;
    }

    public LiveData<CruiseRoute> queryHotDealRoute(Activity activity){
        MutableLiveData<CruiseRoute> res = new MutableLiveData<>();

        db.collection("routes")
                .orderBy("departTimestamp", Query.Direction.ASCENDING)
                .orderBy("slots", Query.Direction.ASCENDING)
                .whereGreaterThan("slots", 0)
                .limit(1)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CruiseRoute> routesList = task.getResult().toObjects(CruiseRoute.class);

                        if (routesList.isEmpty()) {
                            res.postValue(null);
                        }
                        else {
                            CruiseRoute route = routesList.get(0);
                            getBitmapOfRoute(activity, route).observe((LifecycleOwner) activity, bitmap -> {
                                route._setImage(bitmap);
                                res.postValue(route);
                            });
                        }
                    } else {
                        Log.e(LOG_TAG, "Error querying hot deal route: ", task.getException());
                    }
                });

        return res;
    }

    public LiveData<List<CruiseRoute>> queryRoutes(Activity activity, int slotLimit){
        MutableLiveData<List<CruiseRoute>> res = new MutableLiveData<>();
        res.setValue(new ArrayList<>());

        db.collection("routes")
                .orderBy("departTimestamp", Query.Direction.ASCENDING)
                .orderBy("slots", Query.Direction.ASCENDING)
                .whereGreaterThan("slots", slotLimit)
                .limit(30)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        List<CruiseRoute> routesList = task.getResult().toObjects(CruiseRoute.class);
                        int totalObservables = routesList.size();
                        AtomicInteger completedObservables = new AtomicInteger(0);

                        for(CruiseRoute route: routesList){
                            getBitmapOfRoute(activity, route).observe((LifecycleOwner) activity, bitmap -> {
                                route._setImage(bitmap);

                                if (completedObservables.incrementAndGet() == totalObservables) {
                                    res.postValue(routesList);
                                }
                            });
                        }

                        res.setValue(routesList);
                    } else {
                        Log.e(LOG_TAG, "Error querying hot deal route: ", task.getException());
                    }
                });

        return res;
    }

    public LiveData<List<CruiseRoute>> queryBookedRoutes(Activity activity){
        MutableLiveData<List<CruiseRoute>> res = new MutableLiveData<>();

        queryRoutes(activity, -10000).observe((LifecycleOwner) activity, routes -> {
            List<CruiseRoute> filtered = new ArrayList<>();

            for(CruiseRoute route: routes){
                if(route.getBookedBy().containsKey(auth.getUid())){
                    filtered.add(route);
                }
            }

            res.setValue(filtered);
        });

        return res;
    }

    public LiveData<CruiseRoute> queryRouteById(Activity activity, String id){
        MutableLiveData<CruiseRoute> res = new MutableLiveData<>();

        db.collection("routes")
                .document(id)
                .get()
                .addOnCompleteListener(activity, task -> {
                    if(task.isSuccessful()){
                        CruiseRoute route = task.getResult().toObject(CruiseRoute.class);
                        if(route != null){
                            getBitmapOfRoute(activity, route).observe((LifecycleOwner) activity, bitmap -> {
                                route._setImage(bitmap);
                                res.postValue(route);
                            });
                        }
                        else{
                            Log.e(LOG_TAG, "Couldn't find route by ID: route is null");
                        }
                    }else{
                        Log.e(LOG_TAG, "Couldn't query route by ID: " + task.getException());
                    }
                });

        return res;
    }

    public LiveData<Boolean> addRoute(Activity activity, @NonNull CruiseRoute route, @NonNull Uri imageUri){
        MutableLiveData<Boolean> res = new MutableLiveData<>();

        db.collection("routes").add(route).addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                String id = task.getResult().getId();
                route.setId(id);
                String filename = id + ".jpg";

                task.getResult().update("imageName", filename, "id", id).addOnCompleteListener(activity, task1 -> {

                    if(task.isSuccessful()){
                        uploadImage(activity, filename, imageUri)
                                .observe((LifecycleOwner) activity, res::setValue);
                    }
                    else{
                        res.setValue(false);
                        Log.e(LOG_TAG, "Couldn't set imageName: " + task.getException());
                    }
                });
            }
            else{
                res.setValue(false);
                Log.e(LOG_TAG, "Couldn't ADD route: " + task.getException());
            }
        });

        return res;
    }

    public LiveData<Boolean> deleteRouteById(String id){
        MutableLiveData<Boolean> res = new MutableLiveData<>();

        db.collection("routes").document(id).delete().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Log.e(LOG_TAG, "Couldn't delete route: " + id);
            }
            res.setValue(task.isSuccessful());
        });

        return res;
    }

    private void closeStream(InputStream stream){
        try {
            stream.close();
        } catch (IOException ioException) {
            Log.e(LOG_TAG, "Couldn't close stream - IoException");
        }
    }

    private LiveData<Boolean> uploadImage(Activity activity, String filename, Uri uri) {
        MutableLiveData<Boolean> res = new MutableLiveData<>();
        InputStream stream = null;

        try{
            stream = activity.getContentResolver().openInputStream(uri);

            if(stream == null) throw new Exception("Couldn't create InputStream!");

            final InputStream finalStream = stream;

            StorageReference ref = storage.getReference().child("images").child(filename);

            UploadTask uploadTask = ref.putStream(stream);
            uploadTask.addOnCompleteListener(activity, task -> {

                if(!task.isSuccessful()){
                    Log.e(LOG_TAG, "Couldn't upload image to storage: " + task.getException());
                }
                closeStream(finalStream);
                res.setValue(task.isSuccessful());
            });
        }
        catch (Exception e) {
            if(stream != null){
                closeStream(stream);
            }
            Log.e(LOG_TAG, "Couldn't upload image to storage: " + e.getMessage());
            res.setValue(false);
        }
        return res;
    }

    public LiveData<Boolean> bookRoute(Activity activity, CruiseRoute route){
        MutableLiveData<Boolean> res = new MutableLiveData<>();

        queryRouteById(activity, route.getId()).observe((LifecycleOwner)activity, upToDateRoute -> {
            int count = 1;
            if(upToDateRoute.getBookedBy().containsKey(auth.getUid())){
                count = upToDateRoute.getBookedBy().get(auth.getUid()) + 1;
            }

            upToDateRoute.setSlots(upToDateRoute.getSlots() - 1);
            upToDateRoute.getBookedBy().put(auth.getUid(), count);

            route.clone(upToDateRoute);

            db.collection("routes")
                    .document(route.getId())
                    .update("bookedBy", route.getBookedBy(), "slots", route.getSlots())
                    .addOnCompleteListener(activity, task -> {
                        if(!task.isSuccessful()) {
                            Log.e(LOG_TAG, "Couldn't book route! - " + task.getException());
                        }
                        res.setValue(task.isSuccessful());
                    });
        });

        return res;
    }

    public LiveData<Boolean> deleteBooking(Activity activity, CruiseRoute route){
        MutableLiveData<Boolean> res = new MutableLiveData<>();

        queryRouteById(activity, route.getId()).observe((LifecycleOwner)activity, upToDateRoute -> {
            int count = upToDateRoute.getBookedBy().get(auth.getUid()) - 1;

            upToDateRoute.getBookedBy().put(auth.getUid(), count);

            if(count <= 0){
                upToDateRoute.getBookedBy().remove(auth.getUid());
            }

            route.clone(upToDateRoute);

            db.collection("routes")
                    .document(route.getId())
                    .update("bookedBy", route.getBookedBy(), "slots", route.getSlots() + 1)
                    .addOnCompleteListener(activity, task -> {
                        if(!task.isSuccessful()) {
                            Log.e(LOG_TAG, "Couldn't delete booking! - " + task.getException());
                        }
                        res.setValue(task.isSuccessful());
                    });
        });

        return res;
    }

    public LiveData<Boolean> createUser(Activity activity, User user, String password){
        MutableLiveData<Boolean> res = new MutableLiveData<>();

        auth.createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(activity, task -> {
            if(!task.isSuccessful()){
                Log.e(LOG_TAG, "Couldn't create user | Exceptions: " + task.getException());
            }
            else{
                db.collection("user-data")
                        .document(Objects.requireNonNull(task.getResult().getUser()).getUid())
                        .set(user)
                        .addOnCompleteListener(t -> {
                            res.postValue(t.isSuccessful());

                            if(!t.isSuccessful()){
                                Log.e(LOG_TAG, "Couldn't add user-data | Exceptions: " + t.getException());
                            }
                        });
            }
        });

        return res;
    }

    public LiveData<Boolean> loginUser(Activity activity, String email, String password){
        MutableLiveData<Boolean> res = new MutableLiveData<>();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, t -> {
            res.postValue(t.isSuccessful());

            if(!t.isSuccessful()){
                Log.e(LOG_TAG, "Couldn't login user | Exceptions: " + t.getException());
            }
        });

        return res;
    }

    public LiveData<Boolean> isUserAdmin(Activity activity, String uid){
        MutableLiveData<Boolean> res = new MutableLiveData<>();

        db.collection("user-data").document(uid).get().addOnCompleteListener(activity, task -> {
            if(task.isSuccessful()){
                User userData = task.getResult().toObject(User.class);
                if(userData != null){
                    res.setValue(userData.isAdmin());
                }
                else{
                    res.setValue(false);
                    Log.e(LOG_TAG, "Couldn't check if user is admin | user doesn't exist");
                }
            }
            else{
                res.setValue(false);
                Log.e(LOG_TAG, "Couldn't check if user is admin | Exceptions: " + task.getException());
            }
        });

        return res;
    }
}
