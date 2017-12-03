package com.example.alaeddine.project_neo.DAO;

import com.example.alaeddine.project_neo.models.Booking;
import com.example.alaeddine.project_neo.models.ParkingLot;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Alaeddine on 23-Nov-17.
 */

public interface ParkingLotDAO {
    ArrayList<ParkingLot> getParkingsWithDistance(LatLng self ,int kmLong);
    ArrayList<ParkingLot> getAllParkings();
    String update(ParkingLot booking);

}
