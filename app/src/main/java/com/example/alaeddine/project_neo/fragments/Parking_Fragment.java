package com.example.alaeddine.project_neo.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alaeddine.project_neo.Constants;
import com.example.alaeddine.project_neo.DAO.BookingDAO;
import com.example.alaeddine.project_neo.DAO.BookingDAOimplREST;
import com.example.alaeddine.project_neo.DAO.ParkingLotDAO;
import com.example.alaeddine.project_neo.DAO.ParkingLotDAOimplREST;
import com.example.alaeddine.project_neo.DAO.RatedDAO;
import com.example.alaeddine.project_neo.DAO.RatedDAOimplREST;
import com.example.alaeddine.project_neo.LoginActivity;
import com.example.alaeddine.project_neo.NavigationActivity;
import com.example.alaeddine.project_neo.R;
import com.example.alaeddine.project_neo.Singletons.TheContext;
import com.example.alaeddine.project_neo.models.Booking;
import com.example.alaeddine.project_neo.models.ParkingLot;
import com.example.alaeddine.project_neo.models.Rated;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.utils.ReadTask;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.example.alaeddine.project_neo.Constants.LOGIN_CODE;
import static com.example.alaeddine.project_neo.Constants.SERVER_IP;
import static com.example.alaeddine.project_neo.Helper_Class.getMapsApiDirectionsUrl;
import static com.example.alaeddine.project_neo.Helper_Class.is_open;

/**
 * Created by Nanoa on 26-Nov-17.
 */

public class Parking_Fragment extends Fragment {
    private ParkingLot parkingLot ;
    SharedPreferences preferences;
    private GoogleMap googleMap;
    private Location currentLocation;
    private Menu menum;

    public Menu getMenum() {
        return menum;
    }

    public void setMenum(Menu menum) {
        this.menum = menum;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public void setParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }


    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.parkinglot_fragment, container, false);
        preferences = this.getActivity().getSharedPreferences(Constants.NEO_REFS, Context.MODE_PRIVATE);
        return view;
    }

    //onCreate Equivalent
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final BookingDAO bookingDAO = new BookingDAOimplREST();
        ImageView img = view.findViewById(R.id.pphoto);
        final TextView parkingName= view.findViewById(R.id.pname);
        ProgressBar progressBar = view.findViewById(R.id.pprogress);
        TextView remainingps = view.findViewById(R.id.remainingps);
        TextView poctime= view.findViewById(R.id.poctime);
        TextView pricetx = view.findViewById(R.id.pricedt);
        TextView info = view.findViewById(R.id.infoid);
        final FloatingActionButton floatingActionButton = view.findViewById(R.id.bookandpay);
        Picasso.with(TheContext.getInstance()).load("http://"+SERVER_IP+":8000/"+parkingLot.getPhoto() ).into(img);
        parkingName.setText(parkingLot.getName());
        int numberofplacebooked = 0;
        final ArrayList<Booking> bookings = bookingDAO.getAllBookings();
        for (int i = 0; i <bookings.size() ; i++) {
            if(bookings.get(i).getParking_lot()==parkingLot.getId())
                numberofplacebooked++;
        }
        if(numberofplacebooked==parkingLot.getMax_places()){
            floatingActionButton.setImageResource(R.drawable.car_red);
            floatingActionButton.setEnabled(false);
        }

        if(!is_open(parkingLot.getOpen_hour(),parkingLot.getClose_hour())) {
            floatingActionButton.setImageResource(R.drawable.car_grey);
        }
        progressBar.setMax(parkingLot.getMax_places());
        progressBar.setProgress(numberofplacebooked);
        remainingps.setText(numberofplacebooked+"/"+parkingLot.getMax_places());
        String opent ;
        String closet;
        if(parkingLot.getOpen_hour()>12){
            opent= parkingLot.getOpen_hour()%12+" pm";
        }else {
            if (parkingLot.getOpen_hour() == 12) {
                opent = parkingLot.getOpen_hour() + " pm";
            }else {
                if(parkingLot.getOpen_hour() == 0)
                    opent = parkingLot.getOpen_hour()+12 + " am";
                else
                    opent = parkingLot.getOpen_hour() + " am";
            }
        }
        if(parkingLot.getClose_hour()>12){
            closet= parkingLot.getClose_hour()%12+" pm";
        }else {
            if (parkingLot.getClose_hour() == 12) {
                closet = parkingLot.getClose_hour() + " pm";
            }else {
                if(parkingLot.getClose_hour() == 0)
                    closet = parkingLot.getClose_hour()+12 + " am";
                else
                    closet = parkingLot.getClose_hour() + " am";
            }
        }
        poctime.setText(opent+" - "+closet);
        pricetx.setText(parkingLot.getPrice()+" DT");
        info.setText(parkingLot.getInfo());
        final RatingBar pratingbar = view.findViewById(R.id.pratingbar);
        pratingbar.setRating(parkingLot.getRating());
        pratingbar.setOnTouchListener(new View.OnTouchListener() {
                                          @Override
                                          public boolean onTouch(final View v, MotionEvent event) {

                                              if (event.getAction() == MotionEvent.ACTION_UP) {

                                                  final String mat = preferences.getString("mat", null);
                                                  if (mat != null) {
                                                      final RatedDAO ratedDAO = new RatedDAOimplREST();
                                                      final ArrayList<Rated> arrayList = ratedDAO.getAllRatings();
                                                      Rated rated = null;
                                                      for (int i = 0; i < arrayList.size(); i++) {
                                                          if (arrayList.get(i).getMat().equals(mat) && arrayList.get(i).getParking_lot()==parkingLot.getId()) {
                                                              rated = arrayList.get(i);
                                                              break;
                                                          }
                                                      }
                                                      if (rated == null) {

                                                          LinearLayout layout = new LinearLayout(getContext());
                                                          TextView exitNote = new TextView(getContext());
                                                          exitNote.setTextColor(Color.WHITE);
                                                          ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.specialControlsinv);
                                                          final RatingBar ratingBar = new RatingBar(contextThemeWrapper);
                                                          ratingBar.setNumStars(5);
                                                          ratingBar.setMax(5);
                                                          ratingBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

                                                          exitNote.setText("Rate "+parkingLot.getName()+" parking lot ?");
                                                          layout.setOrientation(LinearLayout.VERTICAL);
                                                          exitNote.setPadding(10, 15, 10, 15);
                                                          layout.addView(exitNote);
                                                          layout.addView(ratingBar);
                                                          new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AppTheme_Dark_Dialog))
                                                                  .setTitle("Rating ")
                                                                  .setView(layout)
                                                                  .setIcon(R.drawable.favorite)
                                                                  .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                      public void onClick(DialogInterface dialog, int whichButton) {
                                                                          Rated rated1 = new Rated();
                                                                          rated1.setMat(mat);
                                                                          rated1.setParking_lot(parkingLot.getId());
                                                                          rated1.setRating(ratingBar.getRating());
                                                                          Log.d("Rating", "onClick: "+ratedDAO.addRating(rated1));
                                                                          ParkingLotDAO parkingLotDAO = new ParkingLotDAOimplREST();
                                                                          float numberofratings= 0;
                                                                          float totalRating = 0;
                                                                          ArrayList< Rated> rateds = ratedDAO.getAllRatings();
                                                                          for (int i = 0; i <rateds.size() ; i++) {
                                                                              if(rateds.get(i).getParking_lot()==parkingLot.getId()){
                                                                                  numberofratings++;
                                                                                  totalRating+=rateds.get(i).getRating();
                                                                              }
                                                                          }
                                                                          if(numberofratings!=0) {
                                                                              parkingLot.setRating(totalRating / numberofratings);

                                                                          }
                                                                          else {
                                                                              parkingLot.setRating(0);
                                                                          }
                                                                          Log.d("Rating", "onClick: "+parkingLotDAO.update(parkingLot));

                                                                          pratingbar.setRating(parkingLot.getRating());

                                                                      }
                                                                  })
                                                                  .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                      public void onClick(DialogInterface dialog, int whichButton) {
                                                                          dialog.cancel();
                                                                      }
                                                                  })
                                                                  .show();



                                                      } else {
                                                          Toast.makeText(getContext(), "already rated this parking", Toast.LENGTH_SHORT).show();
                                                      }
                                                  } else {
                                                      Intent i = new Intent(TheContext.getInstance(), LoginActivity.class);
                                                      startActivityForResult(i, LOGIN_CODE);
                                                  }
                                              }
                                              return true;
                                          }
                                      });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String mat =preferences.getString("mat",null);

                if(!is_open(parkingLot.getOpen_hour(),parkingLot.getClose_hour())) {
                    floatingActionButton.setImageResource(R.drawable.car_grey);
                    Toast.makeText(getContext(), "this parking is closed!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mat !=null){

                    BookingDAO bookingDAO1 = new BookingDAOimplREST();
                    ArrayList<Booking> bookings1 = bookingDAO1.getAllBookings();
                    boolean booked = false;
                    for (int i = 0; i <bookings1.size(); i++) {
                        if(bookings1.get(i).getMat().equals(mat) && bookings.get(i).getParking_lot()==parkingLot.getId()){
                            booked =true;
                            break;
                        }
                    }
                    if(booked){
                        floatingActionButton.setImageResource(R.drawable.car_grey);

                        Toast.makeText(getContext(), "you already booked in ths parking lot!", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        LinearLayout layout = new LinearLayout(getContext());
                        TextView exitNote = new TextView(getContext());
                        exitNote.setTextColor(Color.WHITE);

                        exitNote.setText("Book a place in "+parkingLot.getName()+" parking lot and pay "+parkingLot.getPrice()+" DT ?");
                        layout.setOrientation(LinearLayout.VERTICAL);
                        exitNote.setPadding(20, 25, 20, 25);
                        layout.addView(exitNote);
                        new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AppTheme_Dark_Dialog))
                                .setTitle("Rating ")
                                .setView(layout)
                                .setIcon(R.drawable.money_bag)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Booking booking = new Booking();
                                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.ENGLISH);
                                        Calendar cal = Calendar.getInstance();
                                        booking.setDate_time(new Date(cal.getTimeInMillis()));
                                        booking.setMat(mat);
                                        booking.setParking_lot(parkingLot.getId());
                                        booking.setPenalty(0);
                                        bookingDAO.add(booking);

                                        ((NavigationActivity)getActivity()).update_markers(((NavigationActivity) getActivity()).getKmprogress(),
                                                ((NavigationActivity) getActivity()).getDtprogress());

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                })
                                .show();

                    }
                }
                else
                {
                    Intent i = new Intent(TheContext.getInstance(), LoginActivity.class);
                    startActivityForResult(i,LOGIN_CODE);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOGIN_CODE:
                if (resultCode == RESULT_OK) {
                    ((NavigationActivity)getActivity()).update_markers(((NavigationActivity) getActivity()).getKmprogress(),
                            ((NavigationActivity) getActivity()).getDtprogress());
                   String regNum = preferences.getString("mat",null);
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
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
