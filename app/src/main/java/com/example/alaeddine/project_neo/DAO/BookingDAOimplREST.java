package com.example.alaeddine.project_neo.DAO;

import com.example.alaeddine.project_neo.Singletons.TheContext;
import com.example.alaeddine.project_neo.models.Booking;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.alaeddine.project_neo.Constants.SERVER_IP;
import static com.example.alaeddine.project_neo.Helper_Class.md5;

/**
 * Created by Alaeddine on 23-Nov-17.
 */

public class BookingDAOimplREST implements BookingDAO {

    @Override
    public String add(Booking booking) {
        String url = "http://"+SERVER_IP+":8000/bookings/";
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

        Response<String> response= null;
        try {
            response= Ion.with(TheContext.getInstance()).load("POST",url).setJsonObjectBody((new JsonParser()).parse(gson.toJson(booking)).getAsJsonObject()).asString().withResponse().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return response.getResult();
    }

    @Override
    public int update(Booking booking) {
        return 0;
    }

    @Override
    public Booking findBooking(int id) {
        return null;
    }

    @Override
    public ArrayList<Booking> getAllBookings() {
        String url = "http://"+SERVER_IP+":8000/bookings/";
        ArrayList<Booking> arrayList = new ArrayList();
        Response<String> response =null;
        try {
            response = Ion.with(TheContext.getInstance()).load(url).asString().withResponse().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(response!=null) {
            try {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                JSONArray mainObject = new JSONArray(response.getResult());
                for (int i = 0; i < mainObject.length(); i++) {
                    JSONObject obj = mainObject.getJSONObject(i);
                    arrayList.add(gson.fromJson(obj.toString(), Booking.class));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }
}
