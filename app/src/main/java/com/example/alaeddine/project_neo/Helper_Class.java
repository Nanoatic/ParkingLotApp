package com.example.alaeddine.project_neo;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Alaeddine on 20-Nov-17.
 */

public class Helper_Class {
   static public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            String str;
            for (byte aMessageDigest : messageDigest) {
                str = Integer.toHexString(0xFF & aMessageDigest);
                str = (str.length() == 2) ? str : "0" + str;
                // or str=(messageDigest[i]<16)?"0"+str:str;
                hexString.append(str);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        //
        // In particular, do NOT do 'Random rand = new Random()' here or you
        // will not get very good / not very random results.
        Random rand=new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    static public String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;


    }
    static public boolean is_open(int openHour,int closeHour){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        Log.d("TAG", "is_open: "+hour+":"+minute);
        if(openHour > closeHour){
            if((openHour<= hour && hour <24) ||  (hour <= closeHour && hour >=0 ))
            {
                if(hour==closeHour && minute>0)
                    return false;
                return true;
            }
            return false;
        }
        if(openHour < closeHour){
            if((openHour<= hour ) &&  (hour <= closeHour ))
            {
                if(hour==closeHour && minute>0)
                    return false;
                return true;
            }
            return false;

        }
        return false;
    }
}