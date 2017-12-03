package com.example.alaeddine.project_neo.Singletons;

import android.content.Context;

/**
 * Created by Nanoa on 25-Nov-17.
 */

public class TheContext {
    private static Context context = null;
    public static void init(Context rcontext){
        context = rcontext;
    }

    public static Context getInstance(){
        return  context;
    }
}
