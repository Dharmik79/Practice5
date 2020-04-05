package com.example.practice;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice.Adapter.HomeSliderAdapter;
import com.example.practice.Adapter.lookbookAdapter;
import com.example.practice.Service.PicassoImageLoadingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ss.com.bannerslider.Slider;

public class managerMain extends AppCompatActivity implements IBannerLoadInterface, ILookBookLoadListner, IBookingInfoLoadListner, IBookingInformationChangeListner {
    CardView c1,c2,c3,c4,card_booking_info;
    Button b1;
    ImageButton im;
    TextView txt_user_name,txt_salon_address,txt_salon_barber,txt_time;
    Slider banner_slider;
    LinearLayout layout_user_information;
    RecyclerView recycler_look_book;
    CollectionReference banerRef,lookbookRef;
    IBookingInfoLoadListner iBookingInfoLoadListner;
    IBannerLoadInterface iBannerLoadInterface;
    ILookBookLoadListner iLookBookLoadListner;
    IBookingInformationChangeListner iBookingInformationChangeListner;
    public managerMain()
    {
        banerRef= FirebaseFirestore.getInstance().collection("Banner");
       lookbookRef=FirebaseFirestore.getInstance().collection("Lookbook");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);

        banner_slider=findViewById(R.id.banner_slider);
        recycler_look_book=findViewById(R.id.recycler_look_book);
        card_booking_info=findViewById(R.id.card_booking_info);
        txt_salon_address=findViewById(R.id.txt_salon_address);
        txt_salon_barber=findViewById(R.id.txt_salon_barber);
        txt_time=findViewById(R.id.txt_time);
        b1=findViewById(R.id.btn_change_booking);
        c1=findViewById(R.id.c1);
        c2=findViewById(R.id.c2);
        c3=findViewById(R.id.c3);
        c4=findViewById(R.id.c4);


        iBannerLoadInterface=this;
        iLookBookLoadListner=this;
        iBookingInfoLoadListner=this;
        iBookingInformationChangeListner=this;


        Slider.init(new PicassoImageLoadingService());
        loadBanner();
        loadlookbook();

        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),profile.class));
            }
        });
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HistoryActivity.class));
            }
        });

        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),booking.class));
            }
        });

    }

    private void loadlookbook() {
        lookbookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Banner> lookbooks=new ArrayList<>();
                 if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot bannerSnapShot:task.getResult())
                    {
                        Banner banner = bannerSnapShot.toObject(Banner.class);
                        lookbooks.add(banner);
                    }
                    iLookBookLoadListner.onLookBookLoadSuccess(lookbooks);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iLookBookLoadListner.onLookBookLoadFailed(e.getMessage());
            }
        });

    }

    private void loadBanner() {

        banerRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Banner> banners=new ArrayList<>();
                 if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot bannerSnapShot:task.getResult())
                    {
                        Banner banner = bannerSnapShot.toObject(Banner.class);
                        banners.add(banner);

                    }
                    iBannerLoadInterface.onBannerLoadSucess(banners);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBannerLoadInterface.onBannerLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserBooking();
    }

    private void loadUserBooking() {
CollectionReference userBooking=FirebaseFirestore.getInstance().collection("User").document(Common.currentUser).collection("Booking");
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        Timestamp todayTimeStamp=new Timestamp(calendar.getTime());
        userBooking.whereGreaterThanOrEqualTo("timestamp",todayTimeStamp).whereEqualTo("done",false).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful())
                {
                    if(!task.getResult().isEmpty())
                    {
                        for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                        {
                            BookingInformation bookingInformation=queryDocumentSnapshot.toObject(BookingInformation.class);
                            iBookingInfoLoadListner.onBookingInfoLoadSuccess(bookingInformation,queryDocumentSnapshot.getId());
                            break;
                        }
                    }
                    else
                        iBookingInfoLoadListner.onBookingInfoLoadempty();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBookingInfoLoadListner.onBookingInfoLoadFailed(e.getMessage());
            }
        });


    }

    @Override
    public void onBannerLoadSucess(List<Banner> banners) {

          banner_slider.setAdapter(new HomeSliderAdapter(banners));
    }

    @Override
    public void onBannerLoadFailed(String message) {
        Toast.makeText(getApplicationContext(), "message", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLookBookLoadSuccess(List<Banner> banners) {
        recycler_look_book.setHasFixedSize(true);
        recycler_look_book.setLayoutManager(new LinearLayoutManager(this));
        recycler_look_book.setAdapter(new lookbookAdapter(getApplication(),banners));
    }

    @Override
    public void onLookBookLoadFailed(String message) {
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInfoLoadempty() {
        card_booking_info.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation,String bookingId) {
        txt_salon_address.setText(bookingInformation.getSalonAddress());
        txt_salon_barber.setText(bookingInformation.getBarberName());
        txt_time.setText(bookingInformation.getTime());

        Common.currentBooking=bookingInformation;
        Common.currentBookingId=bookingId;

        card_booking_info.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBookingInfoLoadFailed(String message) {
Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
    }

    public void changeBooking(View view) {
        changeBookingFromUser();

    }

    private void changeBookingFromUser() {
        AlertDialog.Builder confirmDialog=new AlertDialog.Builder(managerMain.this).setCancelable(false).setTitle("Hey").setMessage("DO you really want to change the booking information ?").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteBookingFromBarber(true);
            }
        });
        confirmDialog.show();

    }

    public void deleteBooking(View view) {

        deleteBookingFromBarber(false);

    }

    private void deleteBookingFromBarber(final boolean isChange) {

      if(Common.currentBooking!=null)
      {
          DocumentReference barberBookingInfo=FirebaseFirestore.getInstance().collection("All Saloon").document(Common.currentBooking.getCityBook()).collection("Branch").document(Common.currentBooking.getSalonId()).collection("Barber").document(Common.currentBooking.getBarberId()).collection(Common.convertTimeStampToSTringKey(Common.currentBooking.getTimestamp())).document(Common.currentBooking.getSlot().toString());
          barberBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
              }
          }).addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void aVoid) {
                  deleteBookingFromUser(isChange);
              }
          });


      }
      else
      {
          Toast.makeText(getApplicationContext(),"Current Booking must not be null",Toast.LENGTH_SHORT).show();
      }
    }

    private void deleteBookingFromUser(final boolean isChange) {
        if(!TextUtils.isEmpty((Common.currentBookingId)))
        {
                DocumentReference userBookingInfo=FirebaseFirestore.getInstance().collection("User").document(Common.currentUser).collection("Booking").document(Common.currentBookingId);

            userBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"Successfully deleted Booking",Toast.LENGTH_SHORT).show();
                loadUserBooking();

                if(isChange)
                {
                    iBookingInformationChangeListner.onBookingInformationChange();
                }
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Booking Information ID must not be empty",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBookingInformationChange() {

        startActivity(new Intent(getApplicationContext(),booking.class));
    }
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Please confirm");
        builder.setMessage("Are you sure you want to exit the app and logout?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),login.class));
                managerMain.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
