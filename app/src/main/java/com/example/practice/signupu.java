package com.example.practice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class signupu extends AppCompatActivity {
    public static final String TAG = "TAG";
    private   EditText name,email,ph,pass,city,salonn;
    private Button bt;
    private TextView txt;
    private ProgressBar pr;
    private FirebaseAuth fauth;
    private FirebaseFirestore fstore;
    private String userid,salonid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupu);
        name=(EditText)findViewById(R.id.workername);
        email=(EditText)findViewById(R.id.workeremail);
        pass=(EditText)findViewById(R.id.workerpass);
        ph=(EditText)findViewById(R.id.workerphone);
        bt=(Button)findViewById(R.id.workersignup);
        txt=(TextView)findViewById(R.id.workerAlready);
        pr=(ProgressBar)findViewById(R.id.proBar);
       city=findViewById(R.id.workercity);
        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        salonn=findViewById(R.id.salonname);
        if(fauth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String memai=email.getText().toString().trim();
                final String mpas=pass.getText().toString().trim();
                final String mp=ph.getText().toString().trim();
                final String mnam=name.getText().toString().trim();
                final String ucity=city.getText().toString().trim();
               final String salonname=salonn.getText().toString().toLowerCase().trim();
          /*      if(TextUtils.isEmpty(memai))
                {
                    email.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(mpas))
                {
                    pass.setError("password is required");
                    return;
                }
                if(mpas.length()<6)
                {
                    pass.setError("Password should be grater than 6");
                    return;
                }*/

                pr.setVisibility(View.VISIBLE);
                fauth.createUserWithEmailAndPassword(memai,mpas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        DocumentReference doc;
                        CollectionReference collectionReference;
                        userid = fauth.getCurrentUser().getUid();
                  /*      collectionReference=fstore.collection("All Saloon").document(ucity).collection("Branch");
                       collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                           @Override
                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                               if(task.isSuccessful())
                               {
                                   for(QueryDocumentSnapshot documentSnapshot:task.getResult())
                                   {
                                       Salon salon=documentSnapshot.toObject(Salon.class);
                                       salon.setSalonId(documentSnapshot.getId());
                                      if(salon.getName().toLowerCase().trim().equals(salonname))
                                      {
                                          salonid=salon.getSalonId();
                                      }
                                   }

                               }
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
                           }
                       });*/
                            doc=fstore.collection("All Saloon").document(userid);
                       // doc = fstore.collection("All Saloon").document(ucity).collection("Branch").document(salonid).collection("Barber").document(userid);
                        Toast.makeText(getApplicationContext(),"!",Toast.LENGTH_SHORT).show();
                        Map<String, Object> map = new HashMap<>();
                        map.put("Fname", mnam);
                        map.put("phone number", mp);
                        map.put("email", memai);
                        map.put("Password", mpas);
                     //   map.put("City",ucity);
                    //    map.put("Salon name",salonname);
                        Toast.makeText(getApplicationContext(),"2",Toast.LENGTH_SHORT).show();
                        doc.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "on sucess: user profile is created for " + userid);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "On failure :" + e.toString());
                            }
                        });

                        sendemail();
                     /*}
                        else
                            Toast.makeText(signup.this,"Register  not successfully",Toast.LENGTH_LONG).show();*/
                    }
                });


            }
        });
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),loginw.class));
            }
        });



    }
    private void sendemail()
    {
        FirebaseUser firebaseUser=fauth.getCurrentUser();
        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(signupu.this,"Register successfully ,Verification email is sent",Toast.LENGTH_LONG).show();
                        fauth.signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(),loginw.class));
                    }
                    else
                    {
                        Toast.makeText(signupu.this,"Verification emailhas not been sent",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }


    }

}
