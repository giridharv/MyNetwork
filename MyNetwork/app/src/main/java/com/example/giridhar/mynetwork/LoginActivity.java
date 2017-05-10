package com.example.giridhar.mynetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
EditText etmailid,etpassword;
    TextView tvsignup;
    Button btlogin;
    private FirebaseAuth firebaseAuth;
    //private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();
        etmailid=(EditText)findViewById(R.id.editText);
        etpassword=(EditText)findViewById(R.id.editText2);
        tvsignup=(TextView)findViewById(R.id.textView4);
        btlogin=(Button)findViewById(R.id.loginButton);
        btlogin.setOnClickListener(this);
        tvsignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
     switch (v.getId())
     {
         case R.id.loginButton:
              login(etmailid.getText().toString(),etpassword.getText().toString());
            // Intent i = new Intent(LoginActivity.this, NavDrawer.class);
            // startActivity(i);
             break;

         case R.id.textView4:
             Intent moveToNextActivity= new Intent(this,RegisterUser.class);
             startActivity(moveToNextActivity);
             break;

     }
    }
    public void login(String mail,String passwd) {
        if (mail.isEmpty()) {
            etmailid.setError("Enter mail id to proceed");
            etmailid.requestFocus();
        } else if (passwd.isEmpty()) {
            etpassword.setError("Please enter password to proceed");
            etpassword.requestFocus();
        } else {
            firebaseAuth.signInWithEmailAndPassword(mail, passwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_LONG).show();
                        //Bundle bdl = new Bundle();
                        //bdl.putString("currentuser",firebaseAuth.getCurrentUser().getDisplayName());
                        etmailid.setText("");
                        etpassword.setText("");
                        Intent i = new Intent(LoginActivity.this, NavDrawer.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unsuccessful", Toast.LENGTH_LONG).show();

                    }
                }
            });

        }
    }
}
