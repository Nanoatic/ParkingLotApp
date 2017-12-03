package com.example.alaeddine.project_neo;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.content.Intent;
import android.view.MenuItem;
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

import static com.example.alaeddine.project_neo.Helper_Class.md5;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    SharedPreferences preferences;
    @BindView(R.id.input_rn) EditText _rnText;
    @BindView(R.id.input_password_login) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences(Constants.NEO_REFS,MODE_PRIVATE);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        myToolbar.setNavigationIcon(R.drawable.back);

        setSupportActionBar(myToolbar);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });


    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home:
                finish();
               return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void login() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String rn = _rnText.getText().toString();
        final String password = _passwordText.getText().toString();

        UserDAO userDAO = new UserDAOimplREST();
        ArrayList<User> arrayList = userDAO.getAllUsers();
        User user = null;
        boolean passGood = false;
        for (int i = 0; i <arrayList.size() ; i++) {
            if(arrayList.get(i).getMat().equals(rn)){
                user = arrayList.get(i);
                break;
            }
        }
        final User finalUser = user;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(finalUser !=null){
                            if(md5(password).equals(finalUser.getPassword())){
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("mat",finalUser.getMat());
                                editor.apply();
                                onLoginSuccess();
                            }else {
                                Toast.makeText(LoginActivity.this, "wrong password!", Toast.LENGTH_SHORT).show();
                                onLoginFailed();
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, "user is not in database!", Toast.LENGTH_SHORT).show();
                            onLoginFailed();
                        }
                        //onLoginSuccess();

                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        setResult(RESULT_OK);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String rnString = _rnText.getText().toString();
        String password = _passwordText.getText().toString();

        if (rnString.isEmpty()) {
            _rnText.setError("enter register number");
            valid = false;
        } else {
            _rnText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
