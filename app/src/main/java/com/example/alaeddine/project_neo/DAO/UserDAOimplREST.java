package com.example.alaeddine.project_neo.DAO;

import com.example.alaeddine.project_neo.Singletons.TheContext;
import com.example.alaeddine.project_neo.models.User;
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

public class UserDAOimplREST implements UserDAO {


    @Override
    public String add(User user) {
        String url = "http://"+SERVER_IP+":8000/users/";
        Gson gson = new GsonBuilder().create();
        user.setPassword(md5(user.getPassword()));
        Response<String> response= null;
        try {
           response= Ion.with(TheContext.getInstance()).load("POST",url).setJsonObjectBody((new JsonParser()).parse(gson.toJson(user)).getAsJsonObject()).asString().withResponse().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return response.getResult();
    }

    @Override
    public ArrayList<User> getAllUsers() {
        String url = "http://"+SERVER_IP+":8000/users/";
        ArrayList<User> arrayList = new ArrayList();
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
                    arrayList.add(gson.fromJson(obj.toString(), User.class));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    @Override
    public boolean update(User user) {
        return false;

    }
}
