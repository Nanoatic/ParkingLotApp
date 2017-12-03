package com.utils;

import android.graphics.Color;
import android.os.AsyncTask;

import com.example.alaeddine.project_neo.NavigationActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.utils.PathJSONParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.alaeddine.project_neo.Helper_Class.randInt;

class ParserTaskUpdate extends AsyncTask<String,Integer, List<List<HashMap<String , String >>>> {
    private GoogleMap googleMap;

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
  protected List<List<HashMap<String, String>>> doInBackground(
          String... jsonData) {
      // TODO Auto-generated method stub
      JSONObject jObject;
      List<List<HashMap<String, String>>> routes = null;
      try {
          jObject = new JSONObject(jsonData[0]);
          PathJSONParser parser = new PathJSONParser();
          routes = parser.parse(jObject);


      } catch (Exception e) {
          e.printStackTrace();
      }
      return routes;
  }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }
    int index=0;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
  protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
      ArrayList<LatLng> points = null;
      PolylineOptions polyLineOptions = null;


      // traversing through routes
      for (int i = 0; i < routes.size(); i++) {
          points = new ArrayList<LatLng>();
          polyLineOptions = new PolylineOptions();
          List<HashMap<String, String>> path = routes.get(i);

          for (int j = 0; j < path.size(); j++) {
              HashMap<String, String> point = path.get(j);

              double lat = Double.parseDouble(point.get("lat"));
              double lng = Double.parseDouble(point.get("lng"));
              LatLng position = new LatLng(lat, lng);

              points.add(position);
          }

          polyLineOptions.addAll(points);
          polyLineOptions.width(4);


          polyLineOptions.color(Color.RED);
      }
      if(polyLineOptions!=null) {
          if(NavigationActivity.polylineArrayList.get(index)!=null)
          NavigationActivity.polylineArrayList.get(index).setPoints(polyLineOptions.getPoints());
      }
  }}