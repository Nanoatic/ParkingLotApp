package com.example.alaeddine.project_neo.DAO;

import android.provider.Telephony;

import com.example.alaeddine.project_neo.models.Rated;

import java.util.ArrayList;

/**
 * Created by Nanoa on 26-Nov-17.
 */

public interface RatedDAO {
    ArrayList<Rated> getAllRatings();
    String addRating(Rated rated);
}
