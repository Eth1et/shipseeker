package com.example.shipseeker.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String email;
    private boolean isAdmin;
    private Map<CruiseRoute, Integer> bookedRoutes;

    public User() {
        isAdmin = false;
        this.bookedRoutes = new HashMap<>();
    }

    public User(String username, String email, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
        this.bookedRoutes = new HashMap<>();
    }

    public void addBookedRoute(CruiseRoute route){
        if(bookedRoutes.containsKey(route)){
            bookedRoutes.put(route, bookedRoutes.get(route)+1);
        }else{
            bookedRoutes.put(route, 1);
        }
    }

    public boolean removeBookedRoute(CruiseRoute route){
        if(bookedRoutes.containsKey(route)){
            bookedRoutes.put(route, bookedRoutes.get(route)-1);
            if(bookedRoutes.get(route) == 0){
                bookedRoutes.remove(route);
            }
            return true;
        }
        return false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
