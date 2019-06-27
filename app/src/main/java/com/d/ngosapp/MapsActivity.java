package com.d.ngosapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private static final int MY_REQUEST_LOCATION_CODE = 101;
    private boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    String ngoname, ngoaddress, ngonumber, ngopassword, donor_phone_no, textbody, result;
    String[] donor_location;
    String donor_latitude, donor_longitude, number;
    LatLng donor, ngo;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Location location;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    float[] results = new float[1];
//    LatLng ngo = new LatLng(18.941482,72.823679);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        donor_phone_no = getIntent().getStringExtra("number");
        textbody = getIntent().getStringExtra("body");
        ngoname = getIntent().getStringExtra("ngoname");
        ngoaddress = getIntent().getStringExtra("ngoaddress");
        ngonumber = getIntent().getStringExtra("ngonumber");
        ngopassword = getIntent().getStringExtra("ngopassword");
//        getDeviceLocation();
        number = donor_phone_no.substring(3);
        BackgroundWorker1 backgroundWorker5 = new BackgroundWorker1(this);
        backgroundWorker5.execute("getdonoraddress", number);
        try {
            result = backgroundWorker5.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        donor_location = result.split(":");
        donor_latitude = donor_location[0];
        donor_longitude = donor_location[1];
        donor = new LatLng(Double.valueOf(donor_latitude), Double.valueOf(donor_longitude));
//        moveCamera(donor, DEFAULT_ZOOM);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(donor, DEFAULT_ZOOM));
        MarkerOptions options = new MarkerOptions().position(donor).title("Donor");
        mMap.addMarker(options);
//        String url = getRequestUrl(ngo, donor);
//        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
//        taskRequestDirections.execute(url);

    }
//
//    private String getRequestUrl(LatLng ngo, LatLng donor) {
//        String str_org = "origin=" + ngo.latitude + "," + ngo.longitude;
//        String str_dest = "destination=" + donor.latitude + "," + donor.longitude;
//        String sensor = "sensor=false";
//        String mode = "mode=driving";
//        String key = "key=AIzaSyDkgSVbIAT4Ny5yOJx4X8cULosiMU5x-N4";
//        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;
//        String output = "json";
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
//        return url;
//    }

//    private String requestDirection(String reqUrl) throws IOException {
//        String responseString = "";
//        InputStream inputStream = null;
//        HttpURLConnection httpURLConnection = null;
//        try {
//            URL url = new URL(reqUrl);
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.connect();
//            inputStream = httpURLConnection.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            StringBuffer stringBuffer = new StringBuffer();
//            String line = "";
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//            responseString = stringBuffer.toString();
//            bufferedReader.close();
//            inputStreamReader.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//                httpURLConnection.disconnect();
//            }
//            return responseString;
//        }
//    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                System.out.println("Permission granted");
                Log.e("Permission", "Gotpermission");
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        mLocationPermissionGranted = false;
//
//        switch (requestCode) {
//            case LOCATION_PERMISSION_REQUEST_CODE: {
//                if (grantResults.length > 0) {
//                    for (int i = 0; i < grantResults.length; i++) {
//                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                            mLocationPermissionGranted = false;
//                            return;
//                        }
//                    }
//                    mLocationPermissionGranted = true;
//                }
//            }
//        }
//    }
//
//    private void agetDeviceLocation() {
//        System.out.println("in getdeviceloc");
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        System.out.println("hh" + mLocationPermissionGranted);
//        try {
//            if (mLocationPermissionGranted) {
//                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
//                location.addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null) {
//                            System.out.println("in imp place");
//                            ngo = new LatLng(location.getLatitude(), location.getLongitude());
//                            moveCamera(ngo, DEFAULT_ZOOM);
//                            System.out.println(donor_phone_no + ngoname + String.valueOf(location.getLatitude()) + String.valueOf(location.getLongitude()));
//                            BackgroundWorker1 backgroundWorker3 = new BackgroundWorker1(getApplicationContext());
//                            backgroundWorker3.execute("intracking", donor_phone_no, ngoname, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
//                            System.out.println("Camera is moved");
//                        } else {
//                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
////                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
////                location.addOnCompleteListener(MapsActivity.this, new OnCompleteListener<Location>() {
////                    @Override
////                    public void onComplete(@NonNull Task task) {
////                        System.out.println("ll"+task.isSuccessful());
////                        if (task.isSuccessful()) {
////                            System.out.println("in imp place");
////                            Location currentLocation = (Location) task.getResult();
////                            System.out.println("ngolat" + currentLocation.getLatitude()+"lng"+currentLocation.getLongitude());
////                            ngo = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
////                            moveCamera(ngo, DEFAULT_ZOOM);
////                            System.out.println(donor_phone_no+ngoname+String.valueOf(currentLocation.getLatitude())+String.valueOf(currentLocation.getLongitude()));
////                            BackgroundWorker1 backgroundWorker3 = new BackgroundWorker1(getApplicationContext());
////                            backgroundWorker3.execute("intracking", donor_phone_no, ngoname, String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()));
////                            System.out.println("Camera is moved");
////                        }
////                        else {
////                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
////                        }
////                    }
////                });
//            }
//
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//    }

//    private void getDeviceLocation() {
//        System.out.println("in here");
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            Toast.makeText(this, "You need to install Google Play Services to use the App properly", Toast.LENGTH_SHORT).show();
//            locationTv.setText("You need to install Google Play Services to use the App properly");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            ngo = new LatLng(location.getLatitude(), location.getLongitude());
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ngo, DEFAULT_ZOOM));
            MarkerOptions options = new MarkerOptions().position(ngo).title("NGO");
            mMap.addMarker(options);
            BackgroundWorker1 backgroundWorker3 = new BackgroundWorker1(getApplicationContext());
            backgroundWorker3.execute("intracking", donor_phone_no, ngoname, String.valueOf(ngo.latitude), String.valueOf(ngo.longitude));
            Location.distanceBetween(Double.valueOf(ngo.latitude), Double.valueOf(ngo.longitude), Double.valueOf(donor.latitude), Double.valueOf(donor.longitude), results);
            if(results[0]<100) {
                Toast.makeText(this, "Reached", Toast.LENGTH_SHORT).show();
                googleApiClient.disconnect();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("ngoname", ngoname);
                intent.putExtra("ngoaddress", ngoaddress);
                intent.putExtra("ngonumber", ngonumber);
                intent.putExtra("ngopassword", ngopassword);
                startActivity(intent);
            }

        }

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            ngo = new LatLng(location.getLatitude(), location.getLongitude());
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ngo, DEFAULT_ZOOM));
            MarkerOptions options = new MarkerOptions().position(ngo).title("NGO");
            mMap.addMarker(options);
            BackgroundWorker1 backgroundWorker3 = new BackgroundWorker1(getApplicationContext());
            backgroundWorker3.execute("intracking", donor_phone_no, ngoname, String.valueOf(ngo.latitude), String.valueOf(ngo.longitude));
            Location.distanceBetween(Double.valueOf(ngo.latitude), Double.valueOf(ngo.longitude), Double.valueOf(donor.latitude), Double.valueOf(donor.longitude), results);
            if(results[0]<100) {
                Toast.makeText(this, "Reached", Toast.LENGTH_SHORT).show();
                googleApiClient.disconnect();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("ngoname", ngoname);
                intent.putExtra("ngoaddress", ngoaddress);
                intent.putExtra("ngonumber", ngonumber);
                intent.putExtra("ngopassword", ngopassword);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(MapsActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }

//    private void getDeviceLocation() {
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        try {
//            if(mLocationPermissionGranted) {
//                Task location = mFusedLocationProviderClient.getLastLocation();
//                location.addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if (task.isSuccessful()) {
//                            Location currentLocation = (Location) task.getResult();
//                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
////                            moveCamera(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)), DEFAULT_ZOOM);
//                            System.out.println("Camera is moved");
//                        }
//                        else {
//                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                location.addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(MapsActivity.this, "This is bad", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//        }catch (SecurityException e){
//            e.printStackTrace();
//        }
//    }

//    private  void aagetDeviceLocation() {
//       mLocationRequest = new LocationRequest();
//       mLocationRequest.setInterval(1000);
//       mLocationRequest.setFastestInterval(5000);
//       mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//       mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//           // permission is granted
//           // and we can now use getLastLocation method
//           mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//               @Override
//               public void onSuccess(Location location) {
//                   if (location != null) {
//                       ngo = new LatLng(location.getLatitude(), location.getLongitude());
//
//                       mMap.moveCamera(CameraUpdateFactory.newLatLng(ngo));
//                       mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//
////                       Log.d(TAG, "onSuccess: Latitude = " + location.getLatitude());
////                       Log.d(TAG, "onSuccess: Longitude = " + location.getLongitude());
//                   } else {
//                       Toast.makeText(MapsActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
//                   }
//               }
//           });
//
//       } else {
//
//           // permission is not granted.
//           // we have to request the user to grant the the ACCESS_FINE_LOCATION permission.
//           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//               requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_LOCATION_CODE);
//           }
//       }
//
//       mLocationCallback = new LocationCallback() {
//           @Override
//           public void onLocationResult(LocationResult locationResult) {
//               super.onLocationResult(locationResult);
//
//               for (Location location : locationResult.getLocations()) {
//
//                   if (location != null) {
//                       ngo = new LatLng(location.getLatitude(), location.getLongitude());
//
//                       mMap.moveCamera(CameraUpdateFactory.newLatLng(ngo));
//                       mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//
////                       Log.d(TAG, "onSuccess: Latitude = " + location.getLatitude());
////                       Log.d(TAG, "onSuccess: Longitude = " + location.getLongitude());
//                   } else {
//                       Toast.makeText(MapsActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
//                   }
//               }
//
//           }
//       };
//   }


//    private void moveCamera(LatLng latLng, float zoom) {
//        Log.e("MoveCamera", "in move camera");
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
//        MarkerOptions options = new MarkerOptions().position(latLng);
//        mMap.addMarker(options);
//    }

//    public class TaskRequestDirections extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String responseString = " ";
//            try {
//                responseString = requestDirection(strings[0]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return  responseString;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            TaskParser taskParser = new TaskParser();
//            taskParser.execute(s);
//        }
//    }

//    public class TaskParser extends  AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
//
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
//            JSONObject jsonObject = null;
//            List<List<HashMap<String, String>>> routes = null;
//            try {
//                jsonObject = new JSONObject(strings[0]);
//                DirectionsParser directionsParser = new DirectionsParser();
//                routes = directionsParser.parse(jsonObject);
//            }catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return  routes;
//        }
//
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
//            ArrayList points = null;
//            PolylineOptions polylineOptions = null;
//            for (List<HashMap<String, String>> path : lists) {
//                points = new ArrayList();
//                polylineOptions = new PolylineOptions();
//
//                for (HashMap<String, String> point : path) {
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lon = Double.parseDouble(point.get("lon"));
//                    points.add(new LatLng(lat, lon));
//                }
//                polylineOptions.addAll(points);
//                polylineOptions.width(15);
//                polylineOptions.color(Color.BLUE);
//                polylineOptions.geodesic(true);
//            }
//            if (polylineOptions != null) {
//                mMap.addPolyline(polylineOptions);
//            } else {
//                Toast.makeText(MapsActivity.this, "Direction not found", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

}
