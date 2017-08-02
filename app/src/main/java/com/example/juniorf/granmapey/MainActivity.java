package com.example.juniorf.granmapey;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import com.example.juniorf.granmapey.LOGIN.LogInActivity;
import com.example.juniorf.granmapey.MAP.MapsActivity;
import com.example.juniorf.granmapey.TUTORIAL.TutorialActivity;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.juniorf.granmapey.CONSTANTS.Codes.url;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {


    ////// PARA O LOGIN GOOGLE
    private ImageView imageView;
    private TextView nome;
    private TextView email2;


    /////////////////////////////////////////////////

    private static final int REQUEST_PLACE_PICKER = 1;
    private  int PLACE_REQUEST = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private static String Tag = "AKII";

    // json array response url
    private String urlJsonArry = "http://"+url+"index.php";
    private String urlJsonInsert = "http://"+url+"insert.php";

    private static String TAG = MainActivity.class.getSimpleName();
    private Button btnMakeObjectRequest, btnMakeArrayRequest;

    // Progress dialog
    private ProgressDialog pDialog;

    private List<String> lista;
    private ListView listView;
    private EditText textoEditText;
    private TextView tx;
    private Button insertButton;
    private GoogleApiClient googleApiClient;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //////////////////LOGIN OGOGLE////////////////////////////////

        imageView = (ImageView) findViewById(R.id.foto);
        nome = (TextView) findViewById(R.id.nome);
        email2 = (TextView) findViewById(R.id.email);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //////////////////////////////////////////////////////////////

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API).build();
        lista = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.listView);
        textoEditText = (EditText) findViewById(R.id.textoEditText);
        insertButton = (Button) findViewById(R.id.inserirButton);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDataJson();
            }
        });

        btnMakeArrayRequest = (Button) findViewById(R.id.btnArrayRequest);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        btnMakeArrayRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            // making json array request
                makeJsonArrayRequest();
            }
        });

    }


    public void logout(View view){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess()){
                    goLoginScreen();
                }else{
                    Toast.makeText(getApplicationContext(), "N encerrou seção", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void findPlace(View view){
        try {
                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
                Intent intent = intentBuilder.build(this);
                // Start the intent by requesting a result,
                // identified by a request code.
                startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }
            tx = (TextView) findViewById(R.id.texto);
            tx.setText(name);


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

   /* protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PLACE_REQUEST){
            if(resultCode==RESULT_OK){
                Place place = PlacePicker.getPlace(MainActivity.this, data);
                String address = String.format("Place: %s", place.getAddress());
                if(place == null){
                    Log.i("null", "place");
                }
                else{
                    web.loadData(place.getAttributions().toString(), "text/html: charset=utf-8", "UFT-8");

                }
            }
        }
    }*/

    private void insertDataJson() {
        showpDialog();
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, urlJsonInsert, new Response.Listener<String>() {
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
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> parameters = new HashMap<String , String>();
                parameters.put("texto", textoEditText.getText().toString());
                hidepDialog();
                return parameters;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
    }

    /**
     * Method to make json array request where response starts with [
     */
    private void makeJsonArrayRequest(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://maps.googleapis.com/maps/api/place/radarsearch/json?location=51.503186,-0.126446&radius=5000&type=museum&key=AIzaSyAH90Hns3rgCVc6yGSGyJgP5T8uDNr5FDw";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject jo = new JSONObject(response);
                            String c = jo.getString("results");
                            JSONArray jsonArray = new JSONArray(c);
                            Log.i("TAG JSONARRAY", String.valueOf(jsonArray));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject person = (JSONObject) jsonArray.get(i);
                                Log.i("TAG DO RESULT", person.getString("place_id"));
                                Log.i("TAG DO RESULT", person.getString("id"));
                                lista.add(person.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                                lista.add(person.getString("place_id"));
                            }
                            showList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Log.i("LOGGG", "Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERRO","That didn't work!");
            }
        });
        queue.add(stringRequest);
    }

    private void showList() {
        if(lista.size()>0){
            final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, lista);
            listView.setAdapter(adapter);
        }else{
            Toast.makeText(this, "Sem Conexão", Toast.LENGTH_SHORT).show();
        }

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
           Intent i = new Intent(this, TutorialActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(this, LogInActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_share) {
            Intent i = new Intent(this, DetalhesActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("emailOrigem", (this.email));
            bundle.putString("emailDestino", String.valueOf("Email@Destino.com"));
            i.putExtras(bundle);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initMap(View view) {
        Intent i = new Intent(this, MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("emailOrigem", (this.email));
        i.putExtras(bundle);
        startActivity(i);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());*/
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            nome.setText(account.getDisplayName());
            email = (account.getEmail());
           // Log.d("MIAP", account.getPhotoUrl().toString());

        }else{
        }
    }

    private void goLoginScreen() {
        Intent intent = new Intent (this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
