package com.example.juniorf.granmapey;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.juniorf.granmapey.ADAPTER.MessageAdapter;
import com.example.juniorf.granmapey.CONFIG.AppController;
import com.example.juniorf.granmapey.DAO.MessageDAO;
import com.example.juniorf.granmapey.MODEL.Mensagem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetalhesActivity extends AppCompatActivity {

    private EditText message;
    private ListView listView;
    private Button btnSend;
    String emailDestino;
    String emailOrigem;
    private List<Mensagem> lista;

    @Override
    protected void onStart() {
        super.onStart();
    }

    public List<Mensagem> show(final Mensagem mensagem, final Context c){

        Log.i("NOME DO DESTINO", mensagem.getEmailDestino());
        Log.i("kkk", "antes do req");
        lista = new ArrayList<Mensagem>();

        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, "http://grainmapey.pe.hu/GranMapey/show_message.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("OOOOKKKK", response+"          a");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject person = (JSONObject) jsonArray.get(i);

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
                        Log.i("MENSAGEMZ", mensagemz.getTexto());
                        lista.add(mensagemz);

                    }
                    showList();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        },  new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERRO", error+"r");
            }
        })

        {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String , String>();
                parameters.put("emailDestino", mensagem.getEmailDestino());
                return parameters;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
        return this.lista;
    }

    private void showList() {
        MessageAdapter adapter = new MessageAdapter(this.lista, this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        //Dados da activity
        message = (EditText) findViewById(R.id.etMessage);
        listView = (ListView) findViewById(R.id.listView);
        btnSend = (Button) findViewById(R.id.btnSend);

        ///Do intent bundle da main
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        emailDestino = bundle.getString("emailDestino");
        emailOrigem = bundle.getString("emailOrigem");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Mensagem n = new Mensagem();
                String text = message.getText().toString();
                n.setTexto(text);
                n.setEmailOrigem(emailOrigem);
                n.setEmailDestino(emailDestino);
                MessageDAO messageDAO = new MessageDAO(getApplicationContext());
                messageDAO.insert(n, getApplicationContext());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        MessageDAO mDAO = new MessageDAO(this);
        Mensagem n = new Mensagem();
        n.setEmailDestino(emailDestino);
        show(n, this);

    }

    public void main(){
        startActivity(new Intent(this, MainActivity.class));
    }
}
