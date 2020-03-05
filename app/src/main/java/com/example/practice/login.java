package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    EditText email,pass;
    Button bt;
    private TextView tx,t,txt1;
    ProgressBar pr;
    FirebaseAuth fauth;

    RadioButton radioButton1,radioButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=(EditText)findViewById(R.id.e2);
        pass=(EditText)findViewById(R.id.e3);
        bt=(Button)findViewById(R.id.b);
        tx=(TextView)findViewById(R.id.tef2);
        t=findViewById(R.id.e5);

        fauth=FirebaseAuth.getInstance();
        txt1=(TextView)findViewById(R.id.e1);
        if(fauth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),loginm.class));
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String memail=email.getText().toString().trim();
               final String mpass=pass.getText().toString().trim();
                if(TextUtils.isEmpty(memail))
                {
                   email.setError("Email should not be empty");
                   email.requestFocus();
                   return;
                }
                if(TextUtils.isEmpty(mpass))
                {
                    pass.setError("Email should not be empty");
                    pass.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(memail).matches())
                {
                    email.setError("Enter valid Email address");
                    email.requestFocus();
                    return;
                }

                fauth.signInWithEmailAndPassword(memail,mpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Common.currentUser=memail;
                            Common.currentPass=mpass;
                            checkemail();

                        }
                        else
                            Toast.makeText(login.this,"login  is not  successful",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),signup.class));
            }
        });
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),forget.class));
            }
        });
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),loginm.class));
            }
        });

    }
    private void checkemail()
    {
        FirebaseUser firebaseUser = fauth.getInstance().getCurrentUser();
        Boolean emailflag=firebaseUser.isEmailVerified();
        if(emailflag)
        {
            startActivity(new Intent(getApplicationContext(),managerMain.class));
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Verify your email",Toast.LENGTH_LONG).show();
            fauth.signOut();
        }
    }


}
