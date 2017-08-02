package com.example.juniorf.granmapey.DAO;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.juniorf.granmapey.CONFIG.AppController;
import com.example.juniorf.granmapey.MODEL.MyLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by juniorf on 17/12/16.
 */

public class MyLocationDAO extends AbstractDAO<MyLocation>{


    private String urlJsonInsert = "http://grainmapey.pe.hu/GranMapey/insert.php";
    private String urlJsonInsertLocation = "http://grainmapey.pe.hu/GranMapey/insert_location.php";
    private String urlJsonArry = "http://grainmapey.pe.hu/GranMapey/show_location.php";
    private ProgressDialog pDialog;
    private String Tag = "Akii";
    private ArrayList<MyLocation> lista;
    private String jsonResponse;

    public MyLocationDAO(Context context) {
        super(context);
    }

    public ArrayList<MyLocation> show(Context c){
        Log.i("kkkkkk", "ANTES DO REQ");

        JsonArrayRequest req = new JsonArrayRequest("http://grainmapey.pe.hu/GranMapey/show_location.php", new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.i("kkkkkk", "DENTRO DO ON RESPONSE");
                Log.i("kkkkkk", response.toString());

                try {
                    jsonResponse = "";
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject person = (JSONObject) response.get(i);

                        MyLocation myLocation = new MyLocation();
                        String id = person.getString("id");
                        String nome = person.getString("Nome");
                        String telefone = person.getString("Telefone");
                        String lat = person.getString("Lat");
                        String lng = person.getString("Lng");
                        String tipo = person.getString("Tipo");
                        myLocation.setId(Integer.parseInt(id));
                        myLocation.setTipo(Integer.parseInt(tipo));
                        myLocation.setNome(nome);
                        myLocation.setTelefone(telefone);
                        myLocation.setLat(Double.parseDouble(lat));
                        myLocation.setLng(Double.parseDouble(lng));

                        lista.add(myLocation);

                    }
                }
                catch (JSONException e) {
                    Log.i(Tag, "No Catch");

                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Akeii", "No errorListener");
                VolleyLog.d("TAG", "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        return lista;

    }

    public void insert(final MyLocation location, Context t) {
//        showpDialog();
        RequestQueue mRequestQueue = Volley.newRequestQueue(t);
        StringRequest request = new StringRequest(Request.Method.POST, urlJsonInsertLocation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(Tag, response+"a");
            }
        },  new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(Tag, error+"r");
            }
        })

        {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String , String>();
                parameters.put("nome", location.getNome());
                parameters.put("lat", String.valueOf(location.getLat()));
                parameters.put("lng", String.valueOf(location.getLng()));
                parameters.put("tipo", String.valueOf(location.getTipo()));
                parameters.put("telefone", location.getTelefone());
                parameters.put("email", location.getEmail());
              //  hidepDialog();
                return parameters;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
    }




}
