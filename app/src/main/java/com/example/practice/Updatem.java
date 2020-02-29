package com.example.practice;

import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class Updatem extends AppCompatActivity {

    Button b1;
    EditText a,b,c,d,e,f;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
     String userid;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatem);
        b1=findViewById(R.id.umb);
        a=findViewById(R.id.a);
        b=findViewById(R.id.b);
        c=findViewById(R.id.c);
        d=findViewById(R.id.d);
        e=findViewById(R.id.e);
        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        userid=fauth.getCurrentUser().getUid();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String aa=a.getText().toString().trim();
                final String bb=b.getText().toString().trim();
                final String cc=c.getText().toString().trim();
                final String dd=d.getText().toString().trim();
                final String ee=e.getText().toString().trim();
                if(TextUtils.isEmpty(aa))
                {
                    a.setError("Name is required");
                    return;
                }
                if(TextUtils.isEmpty(bb))
                {
                    b.setError("Number of worker is required");
                    return;
                }
                if(TextUtils.isEmpty(cc))
                {
                    c.setError("Address is required");
                    return;
                }
                if(TextUtils.isEmpty(dd))
                {
                    d.setError("City is required");
                    return;
                }
                if(TextUtils.isEmpty(ee))
                {
                    e.setError("Pincode is required");
                    return;
                }

                DocumentReference doc = fstore.collection("Manager").document(userid);
                Map<String, Object> map = new HashMap<>();
                map.put("No of worker",bb);
                map.put("City", dd);
                map.put("Pincode", ee);
                map.put("Name of Salon", aa);
                map.put("Address of Salon",cc);
                doc.set(map);

                DocumentReference doc1 = fstore.collection("All Saloon").document(dd).collection("Branch").document();
                Map<String, Object> map1 = new HashMap<>();
                map1.put("name", aa);
                map1.put("address",cc);
                map1.put("userid",userid);
                doc1.set(map1);
                DocumentReference doc2 = fstore.collection("All Saloon").document(dd);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("City",dd);
                doc2.set(map2);






            }
        });


    }
}
