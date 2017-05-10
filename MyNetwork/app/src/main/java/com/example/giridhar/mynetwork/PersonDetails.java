package com.example.giridhar.mynetwork;

/**
 * Created by giridhar on 4/3/17.
 */

public class PersonDetails
{
    String username;
    String password;
    String country;
    String state;
    String city;
    double latitude;
    double longitude;
    int    joiningYear;
    int idForPerson;



    String uniqueIdForFirebase;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getJoiningYear() {
        return joiningYear;
    }

    public void setJoiningYear(int joiningYear) {
        this.joiningYear = joiningYear;
    }

    public int getIdForPerson() {return idForPerson;}

    public void setIdForPerson(int idForPerson) {this.idForPerson = idForPerson;}
    public String getUniqueIdForFirebase() {
        return uniqueIdForFirebase;
    }

    public void setUniqueIdForFirebase(String uniqueIdForFirebase) {
        this.uniqueIdForFirebase = uniqueIdForFirebase;
    }

}
