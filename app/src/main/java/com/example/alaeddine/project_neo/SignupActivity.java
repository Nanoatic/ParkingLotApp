package com.example.alaeddine.project_neo;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alaeddine.project_neo.DAO.UserDAO;
import com.example.alaeddine.project_neo.DAO.UserDAOimplREST;
import com.example.alaeddine.project_neo.models.User;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.BindView;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    SharedPreferences preferences;
    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_password_repeat) EditText _passwordRText;
    @BindView(R.id.btn_signup) Button _signupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(Constants.NEO_REFS,MODE_PRIVATE);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.signup_toolbar);
        myToolbar.setNavigationIcon(R.drawable.back);

        setSupportActionBar(myToolbar);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

    }

    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String password_repeat = _passwordRText.getText().toString();
        String password = _passwordText.getText().toString();
        final User user = new User();
        user.setMat(name);
        user.setPassword(password);
        final UserDAO userDAO = new UserDAOimplREST();
        ArrayList<User> arrayList = userDAO.getAllUsers();
        boolean exist=false;
        for (int i = 0; i <arrayList.size() ; i++) {
            if(arrayList.get(i).getMat().equals(name)){
                exist =true;
                break;
            }
        }
        final boolean finalExist = exist;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(!finalExist) {

                            userDAO.add(user);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("mat",user.getMat());
                            editor.apply();
                            onSignupSuccess();
                        }
                        else
                        {
                            _nameText.setError("registration number exists!");
                            onSignupFailed();
                        }
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String password = _passwordText.getText().toString();
        String passwordR = _passwordRText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters!");
            valid = false;
        } else {
            _nameText.setError(null);
        }



        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters!");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        if (passwordR.isEmpty() ) {
            _passwordRText.setError("repeat password!");
            valid = false;

        } else {
            _passwordRText.setError(null);
        }
        if(!passwordR.equals(password))
        {
            _passwordRText.setError("password does not match!");
            valid = false;
        } else {
            _passwordRText.setError(null);
        }

        return valid;
    }
}