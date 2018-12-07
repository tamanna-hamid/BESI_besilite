package com.uva.inertia.besilite;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by ben on 19/02/16.
 */
// ref: https://developer.android.com/training/volley/requestqueue.html

public class NetworkSingleton {
    private static NetworkSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context ctx;

    private NetworkSingleton(Context context) {
        ctx = context;
        mRequestQueue = getRequestQueue();
    }

    public static NetworkSingleton getInstance() {
        return mInstance;
    }

    public static synchronized NetworkSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkSingleton(context);
        }
        return mInstance;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
