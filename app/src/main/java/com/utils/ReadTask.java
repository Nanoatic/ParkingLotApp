package com.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

public  class ReadTask extends AsyncTask<String, Void , String> {
private GoogleMap googleMap;
int index ;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
protected String doInBackground(String... url) {
    // TODO Auto-generated method stub
    String data = "";
    try {
        MapHttpConnection http = new MapHttpConnection();
        data = http.readUr(url[0]);


    } catch (Exception e) {
        // TODO: handle exception
        Log.d("Background Task", e.toString());
    }
    return data;
}

@Override
protected void onPostExecute(String result) {
    super.onPostExecute(result);
    ParserTask parserTask = new ParserTask();
    parserTask.setGoogleMap(googleMap);
    parserTask.setIndex(index);
    parserTask.execute(result);
}

}