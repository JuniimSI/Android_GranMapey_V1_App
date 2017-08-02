package com.example.juniorf.granmapey.DAO;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
import com.example.juniorf.granmapey.MODEL.Mensagem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by juniorf on 09/06/17.
 */

public class MessageDAO extends  AbstractDAO<Mensagem> {

    private String urlJsonInsert = "http://grainmapey.pe.hu/GranMapey/insert.php";
    private String urlJsonInsertLocation = "http://grainmapey.pe.hu/GranMapey/insert_message.php";
    private String urlJsonArry = "http://grainmapey.pe.hu/GranMapey/show_message.php";
    private ProgressDialog pDialog;
    private String Tag = "Akii";
    private List<String> lista = new ArrayList<String>();
    private String jsonResponse;

    public MessageDAO(Context context) {
        super(context);
    }

    public void insert(final Mensagem location, final Context t) {
            //  showpDialog();
        RequestQueue mRequestQueue = Volley.newRequestQueue(t);
        StringRequest request = new StringRequest(Request.Method.POST, urlJsonInsertLocation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(Tag, response+"a");
                Toast.makeText(t, response, Toast.LENGTH_SHORT).show();
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
                parameters.put("emailOrigem", String.valueOf(location.getEmailOrigem()));
                parameters.put("emailDestino", String.valueOf(location.getEmailDestino()));
                parameters.put("message", location.getTexto());
                //  hidepDialog();
                return parameters;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
    }


    public List<String> show(final Mensagem mensagem, final Context c){
        Log.i("kkkkkk", "ANTES DO REQ");
        Map<String, String> params = new HashMap();
        params.put("emailDestino", mensagem.getEmailDestino());
        Log.i("NOME DO DESTINO", mensagem.getEmailDestino());
        JSONObject parameters = new JSONObject(params);
        Log.i("kkk", "antes do req");
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, "http://grainmapey.pe.hu/GranMapey/show_message.php", parameters, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.i("kkkkkk", "DENTRO DO ON RESPONSE");
                if(response.toString() == null){
                    Log.i("kkkkkk", "NULL");
                }else
                    Log.i("kkkkkk", response.toString());

                try {
                    jsonResponse = "";
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject person = (JSONObject) response.get(i);

                        Mensagem mensagemz = new Mensagem();
                        String id = person.getString("id");
                        String emailOrigem = person.getString("emailOrigem");
                        String emailDestino = person.getString("emailDestino");
                        String message = person.getString("message");
                        mensagemz.setId(Integer.parseInt(id));
                        mensagemz.setEmailOrigem(emailOrigem);
                        mensagemz.setEmailDestino(emailDestino);
                        mensagemz.setTexto(message);
                        mensagemz.setLocal(message);
                        //Log.i("O EMAIL QUE VEIO", "aaaaaaaaaaaaaaaaaaaa "+emailDestino);
                        Log.i("MENSAGEMZ", mensagemz.getTexto());
                        lista.add(message);
                        //Toast.makeText(c, "EMAIL DA LISTA 0"+lista.get(0).getEmailDestino(), Toast.LENGTH_SHORT).show();

                    }
                    Log.i("LISTA SIZE", lista.size()+"KD O SIZE()");
                    Log.i(Tag, "Antes do showList");
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
        Log.i("LISTA SIZE", lista.size()+"KD O SIZE()");
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        return this.lista;
    }

}

