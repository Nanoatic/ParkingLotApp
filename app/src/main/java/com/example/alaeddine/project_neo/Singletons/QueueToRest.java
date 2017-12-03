package com.example.alaeddine.project_neo.Singletons;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Alaeddine on 23-Nov-17.
 */

public class QueueToRest {
    private static RequestQueue queue;
    public static void Init(Context context){
        queue = Volley.newRequestQueue(context);
    }
    public  synchronized  static void  sendRequest(Request request){
        queue.add(request);
    }
}
