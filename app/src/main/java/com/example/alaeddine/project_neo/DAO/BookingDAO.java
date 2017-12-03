package com.example.alaeddine.project_neo.DAO;

import com.example.alaeddine.project_neo.models.Booking;

import java.util.ArrayList;

/**
 * Created by Alaeddine on 23-Nov-17.
 */

public interface BookingDAO {
    String add(Booking booking);
    int update(Booking booking);
    Booking findBooking(int id);
    ArrayList<Booking> getAllBookings();
}
