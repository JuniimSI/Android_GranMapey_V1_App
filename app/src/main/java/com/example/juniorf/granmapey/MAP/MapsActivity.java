package com.example.juniorf.granmapey.MAP;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.juniorf.granmapey.CONFIG.AppController;
import com.example.juniorf.granmapey.DAO.MyLocationDAO;
import com.example.juniorf.granmapey.DetalhesActivity;
import com.example.juniorf.granmapey.MODEL.MyLocation;
import com.example.juniorf.granmapey.MessageActivity;
import com.example.juniorf.granmapey.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.juniorf.granmapey.CONSTANTS.Codes.url;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private Location mLastLocation;
    private String Tag = "Akii";
    private ProgressDialog pDialog;
    private ArrayList<MyLocation> lista;
    private List<LatLng> rotas;
    private long distance;
    private Polyline polyline;
    public String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ///Do intent bundle da main
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        this.email = bundle.getString("emailOrigem");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        apiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
            apiClient.connect();
    }


    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        rotas = new ArrayList<LatLng>();
        lista = new ArrayList<MyLocation>();
        LatLng ll1 = new LatLng(-5.193068, -39.304487);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll1, 14));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mMap != null) {

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    geraRota(displayLocation(), marker.getPosition());
                }
            });
            mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
                @Override
                public void onInfoWindowLongClick(Marker marker) {
                    Intent i = new Intent(MapsActivity.this, DetalhesActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("emailOrigem", String.valueOf(email));
                    bundle.putString("emailDestino", String.valueOf(marker.getSnippet()));
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });


            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(final Marker marker) {
                    View view = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView txLocality = (TextView) view.findViewById(R.id.tvLocality);
                    TextView txLat = (TextView) view.findViewById(R.id.tvLat);
                    TextView txLng = (TextView) view.findViewById(R.id.tvLng);
                    TextView txSnippet = (TextView) view.findViewById(R.id.tvSnippet);

                    LatLng ll = marker.getPosition();
                    txLocality.setText(marker.getTitle());
                    txLat.setText("Latitude : " + ll.latitude);
                    txLng.setText("Longitude : " + ll.longitude);
                    txSnippet.setText(marker.getSnippet());
                    return view;
                }
            });
        }

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Geocoder gc = new Geocoder(MapsActivity.this);
                LatLng ll = marker.getPosition();
                List<Address> list = null;
                try {
                    list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address add = list.get(0);
                marker.setTitle(add.getLocality());
                marker.showInfoWindow();

                MyLocationDAO locationDAO = new MyLocationDAO(getApplicationContext());
                /////////////////////////////AJEITAR////////////////////////////////////
                ////criar no banco para update lat lng, where lat e lng = anterior.////////
                /////////////////////////////AJEITAR////////////////////////////////////
                //////////////////////ATUALIZAR O REGISTRO NO BANCO//////////////////////////////

            }
        });

        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                getMarkers();
            }
        });

        //______________________________________________________________________________//
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {

                //getLocation();
                LatLng ll11 = new LatLng(-5.193068, -39.304487);
                LatLng ll2 = new LatLng(-5.192763, -39.293833);
                geraRota(ll11, ll2);

                final String[] n = {"", ""};

                LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
                View pront = layoutInflater.inflate(R.layout.insert_location, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                alertDialogBuilder.setView(pront);

                final EditText nome = (EditText) pront.findViewById(R.id.nomeEditText);
                final EditText telefone = (EditText) pront.findViewById(R.id.telefoneEditText);
                final Spinner tipo = (Spinner) pront.findViewById(R.id.spinner);
                final EditText email = (EditText) pront.findViewById(R.id.etEmail);

                alertDialogBuilder.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        n[0] = nome.getText().toString();
                        n[1] = telefone.getText().toString();
                        n[2] = email.getText().toString();

                        MyLocation location = new MyLocation();
                        MyLocationDAO locationDAO = new MyLocationDAO(getApplicationContext());
                        location.setLat(latLng.latitude);
                        location.setLng(latLng.longitude);
                        location.setNome(n[0]);
                        location.setTelefone(n[1]);
                        location.setTipo(1);
                        location.setEmail(n[2]);
                        locationDAO.insert(location, getApplicationContext());
                        /////////////////////////////AJEITAR////////////////////////////////////
                        Toast.makeText(MapsActivity.this, "INSERIDO VIEW", Toast.LENGTH_SHORT).show();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        return;
                    }
                });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });
    }

    private void getMarkers() {
        List<MyLocation> locationsMarkers = new ArrayList<MyLocation>();
        MyLocationDAO locationDAO = new MyLocationDAO(getApplicationContext());
        locationsMarkers = show();
        for (int i = 0; i < lista.size(); i++) {
            LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
            mMap.addMarker(new MarkerOptions().position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getEmail()));
        }
    }

    public void geraRota(LatLng ll1, final LatLng ll2) {
        getRoute(ll1, ll2);
    }

    public void getRoute(final LatLng origin, final LatLng destination) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," +
                origin.longitude + "&destination=" + destination.latitude + "," + destination.longitude + "&key=AIzaSyAClTiQTuCSqZuQd-Yp4u87pqvVtvIqU8c";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("kkkkkk", response.toString() + "EH O response");

                try {
                    JSONObject result = new JSONObject(response.toString());
                    JSONArray routes = result.getJSONArray("routes");

                    distance = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");
                    JSONArray steps = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                    rotas = new ArrayList<LatLng>();

                    for (int i = 0; i < steps.length(); i++) {

                        String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");

                        for (LatLng ll : decodePolyline(polyline)) {
                            rotas.add(ll);
                        }

                    }
                    Log.i(Tag, String.valueOf(rotas.size()));
                    drawRoute();

                } catch (JSONException e) {
                    Log.i(Tag, "No Catch");

                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                Log.i(Tag, "Antes do hide Dialog, depois do catch");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Akeii", "da  rota");
                VolleyLog.d("Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Carregando..", Toast.LENGTH_SHORT).show();
            }

        });

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    public void drawRoute() {
        Log.i("ENTROU NO DRAW", "INICIO");
        PolylineOptions po;
        if (polyline == null) {
            Log.i("NO IF", "polyline nulo");
            po = new PolylineOptions();
            for (int i = 0; i < rotas.size(); i++) {
                Log.i("ROTAS I ", rotas.get(i).latitude + " " + rotas.get(i).longitude);
                Log.i("NO FOR", "------------");
                po.add(rotas.get(i));
            }
            po.color(Color.BLUE).width(8);
            polyline = mMap.addPolyline(po);

        } else {
            Log.i("NO else", "polyline nao nulo");

            polyline.setPoints(rotas);
        }

    }

    public List<LatLng> decodePolyline(String enconded) {
        List<LatLng> listPoints = new ArrayList<LatLng>();
        int index = 0, len = enconded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = enconded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = enconded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            listPoints.add(p);
        }
        return listPoints;
    }

    private void placeSearch() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location=51.503186,-0.126446&radius=5000&type=museum&key=AIzaSyAH90Hns3rgCVc6yGSGyJgP5T8uDNr5FDw";

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
                                MyLocation myLocation = new MyLocation();
                                myLocation.setLat(Double.parseDouble(person.getJSONObject("geometry").getJSONObject("location").getString("lat")));
                                myLocation.setLng(Double.parseDouble(person.getJSONObject("geometry").getJSONObject("location").getString("lng")));

                                lista.add(myLocation);
//                                lista.(person.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                                //                              lista.(person.getString("place_id"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Log.i("LOGGG", "Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERRO", "That didn't work!");
            }
        });
        queue.add(stringRequest);
    }

    public ArrayList<MyLocation> show() {
        placeSearch();
        JsonArrayRequest req = new JsonArrayRequest("http://grainmapey.pe.hu/GranMapey/show_location.php", new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject person = (JSONObject) response.get(i);

                        MyLocation myLocation = new MyLocation();
                        String id = person.getString("id");
                        String nome = person.getString("Nome");
                        String telefone = person.getString("Telefone");
                        String lat = person.getString("Lat");
                        String lng = person.getString("Lng");
                        String tipo = person.getString("Tipo");
                        String email = person.getString("Email");
                        myLocation.setId(Integer.parseInt(id));
                        myLocation.setTipo(Integer.parseInt(tipo));
                        myLocation.setNome(nome);
                        myLocation.setEmail(email);
                        myLocation.setTelefone(telefone);
                        myLocation.setLat(Double.parseDouble(lat));
                        myLocation.setLng(Double.parseDouble(lng));
                        lista.add(myLocation);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        return lista;

    }

    //////_____________________________________________________________________________________
    //PLACES API
    AddPlaceRequest place =
            new AddPlaceRequest(
                    "Manly Sea Life Sanctuary", // Name
                    new LatLng(-33.7991, 151.2813), // Latitude and longitude
                    "W Esplanade, Manly NSW 2095", // Address
                    Collections.singletonList(Place.TYPE_AQUARIUM), // Place types
                    "+61 1800 199 742", // Phone number
                    Uri.parse("http://www.manlysealifesanctuary.com.au/") // Website
            );

    private LatLng displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            return new LatLng(latitude, longitude);
        } else {
            /////////////////////AJEITAR//////////////////////////////
            Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        apiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        /////////////////////////////AJEITAR////////////////////////////////
        Toast.makeText(this, "CONECTION FAILED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {    }
}
