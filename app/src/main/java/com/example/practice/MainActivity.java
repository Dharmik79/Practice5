package com.example.practice;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        check();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.example,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.id1)
        {  logout();
        }
        return super.onOptionsItemSelected(item);
    }

    public  void logout()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure?").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),login.class));
                finish();
            }
        }).setNegativeButton("Cancel",null);

        AlertDialog alert=builder.create();
        alert.show();

    }

   public void check()
   {
       ConnectivityManager cm=(ConnectivityManager)getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
       NetworkInfo ni=cm.getActiveNetworkInfo();
       if(ni!=null)
       {
           if(ni.getType()==ConnectivityManager.TYPE_MOBILE)
       {
           Toast.makeText(this,"Mobile Data is Connected",Toast.LENGTH_LONG).show();
       }
           if(ni.getType()==ConnectivityManager.TYPE_WIFI)
           {
               Toast.makeText(this,"Wifi is Connected",Toast.LENGTH_LONG).show();
           }

       }
       else
       {
           Toast.makeText(this,"No internet Connection",Toast.LENGTH_LONG).show();
       }

   }
}
