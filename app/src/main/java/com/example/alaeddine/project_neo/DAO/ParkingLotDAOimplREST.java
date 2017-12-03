package com.example.alaeddine.project_neo.DAO;

import android.util.Log;

import com.example.alaeddine.project_neo.Singletons.TheContext;
import com.example.alaeddine.project_neo.models.ParkingLot;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import com.koushikdutta.ion.Response;
import static com.example.alaeddine.project_neo.Constants.SERVER_IP;

/**
 * Created by Alaeddine on 23-Nov-17.
 */

public class ParkingLotDAOimplREST implements ParkingLotDAO {

    @Override
    public ArrayList<ParkingLot> getParkingsWithDistance(LatLng self, int kmLong) {
        String url = "http://"+SERVER_IP+":8000/pklsd/"+self.latitude+"&&"+self.longitude+"K"+kmLong;
        ArrayList<ParkingLot> arrayList = new ArrayList();
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
                Gson gson = new GsonBuilder().create();

                JSONArray mainObject = new JSONArray(response.getResult());
                for (int i = 0; i < mainObject.length(); i++) {
                    JSONObject obj = mainObject.getJSONObject(i);
                    arrayList.add(gson.fromJson(obj.toString(), ParkingLot.class));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    @Override
    public ArrayList<ParkingLot> getAllParkings() {
        String url = "http://"+SERVER_IP+":8000/pkls/";
        ArrayList<ParkingLot> arrayList = new ArrayList();
        Response<String> response =null;
        try {
            response = Ion.with(TheContext.getInstance()).load(url).asString().withResponse().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

       /* StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Gson gson = new GsonBuilder().create();

                            JSONArray mainObject = new JSONArray(response);
                            for (int i = 0; i < mainObject.length(); i++) {
                                JSONObject obj = mainObject.getJSONObject(i);
                                arrayList.add(gson.fromJson(obj.toString(), ParkingLot.class));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        // formWindow = new InfoWindow(marker2, markerSpec, new FormFragment());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        QueueToRest.sendRequest(stringRequest);*/
       if(response!=null) {
           try {
               Gson gson = new GsonBuilder().create();

               JSONArray mainObject = new JSONArray(response.getResult());
               for (int i = 0; i < mainObject.length(); i++) {
                   JSONObject obj = mainObject.getJSONObject(i);
                   arrayList.add(gson.fromJson(obj.toString(), ParkingLot.class));

               }
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
        return arrayList;
    }

    @Override
    public String update(ParkingLot parkingLot) {
        String url = "http://"+SERVER_IP+":8000/pkls/";
        Gson gson = new Gson();
        String json = gson.toJson(parkingLot);
        Response<String> response =null;
        try {
            response = Ion.with(TheContext.getInstance()).load("PUT",url).setJsonObjectBody((new JsonParser()).parse(json).getAsJsonObject()).asString().withResponse().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return response.getResult();
    }
}
