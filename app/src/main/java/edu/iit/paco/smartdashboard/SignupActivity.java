package edu.iit.paco.smartdashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_url) EditText _urlText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("not valid input field/s");
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Theme_AppCompat);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String homeurl = _urlText.getText().toString();

        DBHelper db = new DBHelper(this);
        boolean userCreated = db.createUser(name, email, password, homeurl);
        db.close();
        Runnable r;
        if (userCreated) {
            r = new Runnable() {
                public void run() {
                    onSignupSuccess(email);
                    progressDialog.dismiss();
                }
            };
        } else {
            r = new Runnable() {
                public void run() {
                    onSignupFailed("fields are invalid or email already exists");
                    progressDialog.dismiss();
                }
            };
        }
        new android.os.Handler().postDelayed(r, 3000);
    }


    public void onSignupSuccess(String email) {
        _signupButton.setEnabled(true);
        Intent data = new Intent();
        data.putExtra("email", email);
        setResult(RESULT_OK, data);
        finish();
    }

    public void onSignupFailed(String msg) {
        Toast.makeText(getBaseContext(), "Signup failed: " + msg, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String url = _urlText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        //todo UNCOMMENT
        if (email.isEmpty() ){//|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }
        if (url.isEmpty() || !Patterns.WEB_URL.matcher(url).matches()) {
            _urlText.setError("enter a valid home URL");
            valid = false;
        } else {
            _urlText.setError(null);
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
