package com.example.alaeddine.project_neo.DAO;

import com.example.alaeddine.project_neo.Singletons.TheContext;
import com.example.alaeddine.project_neo.models.Rated;
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

/**
 * Created by Nanoa on 26-Nov-17.
 */

public class RatedDAOimplREST implements RatedDAO {
    @Override
    public ArrayList<Rated> getAllRatings() {
        String url = "http://"+SERVER_IP+":8000/rateds/";
        ArrayList<Rated> arrayList = new ArrayList();
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
                    arrayList.add(gson.fromJson(obj.toString(), Rated.class));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  arrayList;
    }

    @Override
    public String addRating(Rated rated) {
        String url = "http://"+SERVER_IP+":8000/rateds/";
        Response<String> response= null;
        Gson gson = new GsonBuilder().create();

        try {
            response= Ion.with(TheContext.getInstance()).load("POST",url).setJsonObjectBody((new JsonParser()).parse(gson.toJson(rated)).getAsJsonObject()).asString().withResponse().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return response.getResult();
    }
}
