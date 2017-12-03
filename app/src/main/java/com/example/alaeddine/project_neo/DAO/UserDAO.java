package com.example.alaeddine.project_neo.DAO;

import com.example.alaeddine.project_neo.models.User;

import java.util.ArrayList;

/**
 * Created by Alaeddine on 23-Nov-17.
 */

public interface UserDAO {
    String add(User user);
    ArrayList<User> getAllUsers();
    boolean update(User user);
}
