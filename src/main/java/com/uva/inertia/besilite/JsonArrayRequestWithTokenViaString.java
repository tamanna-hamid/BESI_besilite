package com.uva.inertia.besilite;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ben on 3/16/2016.
 */
public class JsonArrayRequestWithTokenViaString extends JsonRequest<JSONArray> {

        String token;

        public JsonArrayRequestWithTokenViaString(int method, String url, String jsonRequest, String token, Response.Listener listener, Response.ErrorListener errorListener)
        {
            super(method, url, jsonRequest, listener, errorListener);
            this.token = token;
        }


        @Override
        public Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                return Response.success(new JSONArray(jsonString),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }


        @Override
        public Map getHeaders() throws AuthFailureError {
            Map headers = new HashMap();
            headers.put("Authorization", "Token "+token);
            Log.v("DATA", headers.toString());
            return headers;
        }

    }
