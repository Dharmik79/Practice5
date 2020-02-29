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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class loginm extends AppCompatActivity {
    EditText email,pass;
     Button bt;
    TextView tx,t,txt1;
      ProgressBar pr;
     FirebaseAuth fauth;

    RadioButton radioButton1,radioButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginm);
        email=(EditText)findViewById(R.id.ed2);
        pass=(EditText)findViewById(R.id.ed3);
        bt=(Button)findViewById(R.id.bu3);
        tx=(TextView)findViewById(R.id.tett2);
        t=findViewById(R.id.ed5);
        pr=(ProgressBar)findViewById(R.id.prBar);
        fauth=FirebaseAuth.getInstance();
        txt1=(TextView)findViewById(R.id.ed11);
        if(fauth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),login.class));
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memail=email.getText().toString().trim();
                String mpass=pass.getText().toString().trim();
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
                pr.setVisibility(View.VISIBLE);
                fauth.signInWithEmailAndPassword(memail,mpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            checkemail();
                        }
                        else
                            Toast.makeText(loginm.this,"login  is not  successful",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),signupm.class));
            }
        });

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),forget.class));
            }
        });

    }
    private void checkemail()
    {
        FirebaseUser firebaseUser = fauth.getInstance().getCurrentUser();
        Boolean emailflag=firebaseUser.isEmailVerified();
        if(emailflag)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Verify your email",Toast.LENGTH_LONG).show();
            fauth.signOut();
        }
    }


}
