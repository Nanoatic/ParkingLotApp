package com.example.alaeddine.project_neo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alaeddine.project_neo.DAO.BookingDAO;
import com.example.alaeddine.project_neo.DAO.BookingDAOimplREST;
import com.example.alaeddine.project_neo.DAO.ParkingLotDAO;
import com.example.alaeddine.project_neo.DAO.ParkingLotDAOimplREST;
import com.example.alaeddine.project_neo.Singletons.TheContext;
import com.example.alaeddine.project_neo.fragments.Parking_Fragment;
import com.example.alaeddine.project_neo.models.Booking;
import com.example.alaeddine.project_neo.models.ParkingLot;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.utils.InfoWindow;
import com.utils.InfoWindowManager;
import com.utils.ReadTask;
import com.utils.ReadTaskUpdate;
import com.utils.fragment.MapInfoWindowFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.alaeddine.project_neo.Constants.LOGIN_CODE;
import static com.example.alaeddine.project_neo.Constants.NEO_REFS;
import static com.example.alaeddine.project_neo.Constants.SIGNUP_CODE;
import static com.example.alaeddine.project_neo.Helper_Class.getMapsApiDirectionsUrl;
import static com.example.alaeddine.project_neo.Helper_Class.is_open;

public class NavigationActivity extends AppCompatActivity implements

        GoogleMap.OnMarkerClickListener,
        InfoWindowManager.WindowShowListener ,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks ,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
      {
    public static HashMap<Integer,Polyline> polylineArrayList = new HashMap<>();
    private ArrayList<ParkingLot> parkingLotArrayList;
    private static final String RECYCLER_VIEW = "RECYCLER_VIEW_MARKER";
    private static final String FORM_VIEW = "FORM_VIEW_MARKER";
          SharedPreferences preferences;
    GoogleApiClient mGoogleApiClient;
    private String regNum =null;
    private InfoWindow[] recyclerWindow;
    private InfoWindow formWindow;
    private InfoWindowManager infoWindowManager;
    boolean updatedOnce =false;
    private GoogleMap gmap = null;
    ArrayList<Marker> markers = new ArrayList();
    Menu menum;
    private Spinner seekBarkm;
    private Spinner seekBarDT;
    private TextView kmTV;
    private TextView dtTV;
    int dtprogress = 10;
    int kmprogress =10;
    private LocationRequest mLocationRequest;
    private Location clientCurrentLocation = new Location("potato") ;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_);
        preferences = getSharedPreferences(NEO_REFS, MODE_PRIVATE);
        updatedOnce =false;
        seekBarkm = findViewById(R.id.kmfilt);
        seekBarDT = findViewById(R.id.dtfilt);
        Integer[] kmitems = new Integer[]{10,20,50,100};
        ArrayAdapter<Integer> kmadapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, kmitems);
        Integer[] dtitems = new Integer[]{10,15,20,30};
        ArrayAdapter<Integer> dtadapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, dtitems);
        seekBarkm.setAdapter(kmadapter);
        seekBarDT.setAdapter(dtadapter);
        kmTV = findViewById(R.id.kmftext);
        dtTV = findViewById(R.id.dtftext);
        dtTV.setText(" DT");
        kmTV.setText(" KM");
        seekBarDT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                dtprogress = (Integer) item;
                update_markers(kmprogress,dtprogress);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        seekBarkm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    Object item = adapterView.getItemAtPosition(i);
                                                    kmprogress = (Integer) item;
                                                    update_markers(kmprogress,dtprogress);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                }
                                            }
        );

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        TextView mTitle =  myToolbar.findViewById(R.id.toolbar_title);
        Typeface typeface = ResourcesCompat.getFont(getBaseContext(),R.font.euphoria_script_regular);
        mTitle.setTypeface(typeface);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TheContext.init(this);
        startLocationUpdates();
        final MapInfoWindowFragment mapInfoWindowFragment =
                (MapInfoWindowFragment) getSupportFragmentManager().findFragmentById(R.id.infoWindowMap);

        infoWindowManager = mapInfoWindowFragment.infoWindowManager();
        infoWindowManager.setHideOnFling(true);

        mapInfoWindowFragment.getMapAsync(this);

        infoWindowManager.setWindowShowListener(NavigationActivity.this);


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
       InfoWindow infoWindow;
        int i = Integer.parseInt(marker.getSnippet());
        infoWindow = recyclerWindow[i];




      /*  String urlx = getMapsApiDirectionsUrl(
                new LatLng(clientCurrentLocation.getLatitude(),
                        clientCurrentLocation.getLongitude()),
                markers.get(i).getPosition());
        Log.d("url : ",urlx);
        ReadTaskUpdate downloadTask = new ReadTaskUpdate();
        downloadTask.setGoogleMap(gmap);
        // Start downloading json data from Google Directions API
        downloadTask.execute(urlx);*/

        if (infoWindow != null) {
            infoWindowManager.toggle(infoWindow, true);
        }

        return true;
    }

    @Override
    public void onWindowShowStarted(@NonNull InfoWindow infoWindow) {
//        Log.d("debug", "onWindowShowStarted: " + infoWindow);
    }

    @Override
    public void onWindowShown(@NonNull InfoWindow infoWindow) {
//        Log.d("debug", "onWindowShown: " + infoWindow);
    }

    @Override
    public void onWindowHideStarted(@NonNull InfoWindow infoWindow) {
//        Log.d("debug", "onWindowHideStarted: " + infoWindow);
    }

    @Override
    public void onWindowHidden(@NonNull InfoWindow infoWindow) {
//        Log.d("debug", "onWindowHidden: " + infoWindow);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        ParkingLotDAO parkingLotDAO = new ParkingLotDAOimplREST();
        parkingLotArrayList =  parkingLotDAO.getAllParkings();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        buildGoogleApiClient();

        mGoogleApiClient.connect();

        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);




// Request a string response from the provided URL.


// Add the request to the RequestQueue.

       // final Marker marker1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(5, 5)).snippet(RECYCLER_VIEW));
        //final Marker marker2 = googleMap.addMarker(new MarkerOptions().position(new LatLng(1, 1)).snippet(FORM_VIEW));

        googleMap.setOnMarkerClickListener(NavigationActivity.this);
        gmap = googleMap;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gmap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }




    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            // do work here
                            onLocationChanged(locationResult.getLastLocation());
                        }
                    },
                    Looper.myLooper());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }
    public void onLocationChanged(Location location) {
        // New location has now been determined
     clientCurrentLocation = location;
     String mat = preferences.getString("mat",null);
     if(mat!=null && updatedOnce) {
         BookingDAO bookingDAO = new BookingDAOimplREST();
         ArrayList<Booking> bookings = bookingDAO.getAllBookings();
         ArrayList<ParkingLot> parkingLotsChosen = new ArrayList<>();
         for (int i = 0; i < parkingLotArrayList.size(); i++) {
             float kmb = kmPointToPoint(
                     clientCurrentLocation.getLatitude(),
                     clientCurrentLocation.getLongitude(), parkingLotArrayList.get(i).getLat()
                     , parkingLotArrayList.get(i).getLongt());
             if (parkingLotArrayList.get(i).getPrice() <= dtprogress && kmb <= kmprogress) {

                 for (int j = 0; j < bookings.size(); j++) {
                     if (bookings.get(j).getParking_lot() == parkingLotArrayList.get(i).getId()) {
                         if (bookings.get(j).getMat().equals(mat)) {

                             String urlx = getMapsApiDirectionsUrl(
                                     new LatLng(clientCurrentLocation.getLatitude(),
                                             clientCurrentLocation.getLongitude()),
                                     new LatLng(parkingLotArrayList.get(i).getLat(), parkingLotArrayList.get(i).getLongt()));
                             ReadTaskUpdate downloadTask = new ReadTaskUpdate();
                             downloadTask.setGoogleMap(gmap);

                             downloadTask.setIndex(parkingLotArrayList.get(i).getId());
                             // Start downloading json data from Google Directions API
                             downloadTask.execute(urlx);
                         }
                     }

                 }
             }
         }
     }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        clientCurrentLocation = mLastLocation;
        if (mLastLocation != null) {
            //place marker at current position
            gmap.clear();
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),15));
            update_markers(kmprogress,dtprogress);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

          @Override
          public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
              Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
          }
          @Override
          public boolean onOptionsItemSelected(MenuItem item) {
              switch (item.getItemId()) {
                  case R.id.syncing: {
                      update_markers(kmprogress,dtprogress);
                      return true ;
                  }
                  case R.id.action_login: {
                      Intent i = new Intent(getBaseContext(), LoginActivity.class);
                      startActivityForResult(i,LOGIN_CODE);

                      return true;
                  }
                  case R.id.action_logout:
                      SharedPreferences.Editor editor = preferences.edit();
                      editor.remove("mat");
                      editor.apply();
                      update_toolbar();
                      update_markers(kmprogress,dtprogress);
                      return true;
                  case R.id.action_signup: {
                      Intent i = new Intent(getBaseContext(), SignupActivity.class);
                      startActivityForResult(i,SIGNUP_CODE);
                      return true;
                  }

                  default:

                      return super.onOptionsItemSelected(item);

              }
          }
          public boolean onCreateOptionsMenu(Menu menu) {
              // Inflate the menu items for use in the action bar
                menum=menu;
              MenuInflater inflater = getMenuInflater();
              regNum = preferences.getString("mat",null);

              inflater.inflate(R.menu.menuelem, menu);
              if(regNum==null){
                  menu.findItem(R.id.action_login).setVisible(true);
                  menu.findItem(R.id.action_login).setEnabled(true);
                  menu.findItem(R.id.action_logout).setVisible(false);
                  menu.findItem(R.id.action_logout).setEnabled(false);
              }
              else
              {
                  menu.findItem(R.id.action_login).setVisible(false);
                  menu.findItem(R.id.action_login).setEnabled(false);
                  menu.findItem(R.id.action_logout).setVisible(true);
                  menu.findItem(R.id.action_logout).setEnabled(true);
              }
              return super.onCreateOptionsMenu(menu);
          }

        void update_toolbar(){
            regNum = preferences.getString("mat",null);
            if(regNum==null){
                menum.findItem(R.id.action_login).setVisible(true);
                menum.findItem(R.id.action_login).setEnabled(true);
                menum.findItem(R.id.action_logout).setVisible(false);
                menum.findItem(R.id.action_logout).setEnabled(false);
            }
            else
            {
                menum.findItem(R.id.action_login).setVisible(false);
                menum.findItem(R.id.action_login).setEnabled(false);
                menum.findItem(R.id.action_logout).setVisible(true);
                menum.findItem(R.id.action_logout).setEnabled(true);
            }
        }

          @Override
          protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode){
                case LOGIN_CODE:
                    if(resultCode==RESULT_OK){
                        update_toolbar();
                        update_markers(kmprogress,dtprogress);
                    }
                    break;
                case SIGNUP_CODE:
                    if(resultCode==RESULT_OK){
                        update_toolbar();
                        update_markers(kmprogress,dtprogress);
                    }
                    break;
            }
              super.onActivityResult(requestCode, resultCode, data);
          }

          public int getDtprogress() {
              return dtprogress;
          }

          public int getKmprogress() {
              return kmprogress;
          }

          public void update_markers(int km , int dt){
              if(markers!=null)
                  markers.clear();
            gmap.clear();

              BookingDAO bookingDAO = new BookingDAOimplREST();
              ArrayList<Booking> bookings = bookingDAO.getAllBookings();
            ParkingLotDAO parkingLotDAO = new ParkingLotDAOimplREST();
              parkingLotArrayList= parkingLotDAO.getAllParkings();
              String mat = preferences.getString("mat",null);
              if(mat==null) {
                  ArrayList<ParkingLot> parkingLotsChosen = new ArrayList<>();
                  markers = new ArrayList<>();
                  int k =0 ;
                  for (int i = 0; i < parkingLotArrayList.size(); i++) {
                      Log.d("TAG", "update_markers: first"+markers.size()+"/"+i);
                      float kmb = kmPointToPoint(
                              clientCurrentLocation.getLatitude(),
                              clientCurrentLocation.getLongitude(), parkingLotArrayList.get(i).getLat()
                              , parkingLotArrayList.get(i).getLongt());
                      if (parkingLotArrayList.get(i).getPrice() <= dt && kmb <= km) {

                          int numberofbookings=0;
                          for (int j = 0; j <bookings.size() ; j++) {
                              if( bookings.get(j).getParking_lot()==parkingLotArrayList.get(i).getId())
                              {


                                  numberofbookings++;
                              }

                          }
                          markers.add((gmap.addMarker(new MarkerOptions().position(new LatLng(parkingLotArrayList.get(i).getLat(), parkingLotArrayList.get(i).getLongt())).snippet(Integer.toString(k)))));

                          float rate = (float) numberofbookings/ parkingLotArrayList.get(i).getMax_places();
                          rate *= 100;

                          Log.d("TAG", "update_markers: "+markers.size()+"/"+i);
                          if(!is_open( parkingLotArrayList.get(i).getOpen_hour(), parkingLotArrayList.get(i).getClose_hour())){
                              markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_grey));

                          }
                          else {
                              if (rate >= 0 && rate < 20) {
                                  markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_0_20));
                              } else {

                                  if (rate >= 20 && rate < 40) {
                                      markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_20_40));
                                  } else {
                                      if (rate >= 40 && rate < 60) {
                                          markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_40_60));
                                      } else {
                                          if (rate >= 60 && rate < 80) {
                                              markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_60_80));

                                          } else {
                                              if (rate >= 80 && rate < 100) {
                                                  markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_80_100));
                                              } else if (rate == 100) {
                                                  markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_100));
                                              }
                                          }

                                      }
                                  }
                              }
                          }
                          k++;
                          parkingLotsChosen.add(parkingLotArrayList.get(i));
                      }


                  }
                  final int offsetX = (int) getResources().getDimension(R.dimen.marker_offset_x);
                  final int offsetY = (int) getResources().getDimension(R.dimen.marker_offset_y);
                  final InfoWindow.MarkerSpecification markerSpec =
                          new InfoWindow.MarkerSpecification(offsetX, offsetY);
                  recyclerWindow = new InfoWindow[markers.size()];
                  for (int i = 0; i < parkingLotsChosen.size(); i++) {
                      Parking_Fragment f = new Parking_Fragment();
                      f.setGoogleMap(gmap);
                      f.setCurrentLocation(clientCurrentLocation);
                      f.setMenum(menum);
                      f.setParkingLot(parkingLotsChosen.get(i));
                      recyclerWindow[i] = new InfoWindow(markers.get(i), markerSpec, f);
                  }
              }
              else {
                  polylineArrayList.clear();
                  ArrayList<ParkingLot> parkingLotsChosen = new ArrayList<>();
                  markers = new ArrayList<>();
                  int k= 0;
                  for (int i = 0; i < parkingLotArrayList.size(); i++) {
                      float kmb = kmPointToPoint(
                              clientCurrentLocation.getLatitude(),
                              clientCurrentLocation.getLongitude(), parkingLotArrayList.get(i).getLat()
                              , parkingLotArrayList.get(i).getLongt());
                      if (parkingLotArrayList.get(i).getPrice() <= dt && kmb <= km) {
                          boolean booked =false;
                          int numberofbookings=0;
                          for (int j = 0; j <bookings.size() ; j++) {
                              if( bookings.get(j).getParking_lot()==parkingLotArrayList.get(i).getId())
                              {
                                  if(bookings.get(j).getMat().equals(mat)) {
                                      booked = true;
                                      String urlx = getMapsApiDirectionsUrl(
                                              new LatLng(clientCurrentLocation.getLatitude(),
                                                      clientCurrentLocation.getLongitude()),
                                              new LatLng(parkingLotArrayList.get(i).getLat(),parkingLotArrayList.get(i).getLongt()));
                                      ReadTask downloadTask = new ReadTask();
                                      downloadTask.setGoogleMap(gmap);
                                      downloadTask.setIndex(parkingLotArrayList.get(i).getId());
                                        // Start downloading json data from Google Directions API
                                      downloadTask.execute(urlx);
                                  }
                                  numberofbookings++;
                              }

                          }
                          markers.add((gmap.addMarker(new MarkerOptions().position(new LatLng(parkingLotArrayList.get(i).getLat(), parkingLotArrayList.get(i).getLongt())).snippet(Integer.toString(k)))));

                          if(!is_open( parkingLotArrayList.get(i).getOpen_hour(), parkingLotArrayList.get(i).getClose_hour())){
                              markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_grey));

                          }else {
                          if(booked) {

                              markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.alert_circle_outline));

                          }else {
                              float rate = (float) numberofbookings / parkingLotArrayList.get(i).getMax_places();
                              rate *= 100;
                              if (rate >= 0 && rate < 20) {
                                  markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_0_20));
                              } else {
                                  if (rate >= 20 && rate < 40) {
                                      markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_20_40));
                                  } else {
                                      if (rate >= 40 && rate < 60) {
                                          markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_40_60));
                                      } else {
                                          if (rate >= 60 && rate < 80) {
                                              markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_60_80));

                                          } else {
                                              if (rate >= 80 && rate < 100) {
                                                  markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_80_100));
                                              } else if (rate == 100) {
                                                  markers.get(k).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_circle_100));
                                              }
                                          }

                                      }
                                  }
                              }
                          }

                          }
                          k++;
                          parkingLotsChosen.add(parkingLotArrayList.get(i));
                      }


                  }
                  final int offsetX = (int) getResources().getDimension(R.dimen.marker_offset_x);
                  final int offsetY = (int) getResources().getDimension(R.dimen.marker_offset_y);
                  final InfoWindow.MarkerSpecification markerSpec =
                          new InfoWindow.MarkerSpecification(offsetX, offsetY);
                  recyclerWindow = new InfoWindow[markers.size()];
                  for (int i = 0; i < parkingLotsChosen.size(); i++) {
                      Parking_Fragment f = new Parking_Fragment();
                      f.setGoogleMap(gmap);
                      f.setCurrentLocation(clientCurrentLocation);
                      f.setMenum(menum);
                      f.setParkingLot(parkingLotsChosen.get(i));
                      recyclerWindow[i] = new InfoWindow(markers.get(i), markerSpec, f);
                  }
              }
              updatedOnce=true;
          }

          float kmPointToPoint(double lat_a , double  lng_a , double lat_b , double lng_b){
              double earthRadius = 3958.75;
              double latDiff = Math.toRadians(lat_b-lat_a);
              double lngDiff = Math.toRadians(lng_b-lng_a);
              double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                      Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                              Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
              double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
              double distance = earthRadius * c;

              int meterConversion = 1609;

              return new Float(distance * meterConversion/1000.0).floatValue();
          }



      }

