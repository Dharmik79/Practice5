package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forget extends AppCompatActivity {
    EditText email;
    Button pass;
    private FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        email=findViewById(R.id.editText6);
        pass=findViewById(R.id.button3);
        fAuth=FirebaseAuth.getInstance();
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memail=email.getText().toString().trim();
                if(memail.isEmpty())
                {
                    email.setError("Enter email address");
                    email.requestFocus();
                    return;

                }
                else
                {
                    fAuth.sendPasswordResetEmail(memail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"Password reset email sent ",Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(forget.this,login.class));

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Password reset email has not been sent ",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
                }
            }
        });

    }
}
