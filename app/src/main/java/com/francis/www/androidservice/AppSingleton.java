package com.francis.www.androidservice;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by edwige on 2/14/17.
 */

public class AppSingleton {

    private static AppSingleton appSingletonInstance;
    private RequestQueue requestQueue;
    private static Context context;

    private AppSingleton(Context context1){
        context = context1;
        requestQueue = getRequestQueue();

    }

    public static synchronized AppSingleton getInstance(Context context){
        if(appSingletonInstance == null){
            appSingletonInstance = new AppSingleton(context);
        }
        return appSingletonInstance;
    }

    private RequestQueue getRequestQueue() {
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag ){
        req.setTag(tag);
        getRequestQueue().add(req);
    }
}
