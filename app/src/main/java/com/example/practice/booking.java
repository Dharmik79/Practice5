package com.example.practice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;




public class booking extends AppCompatActivity {

LocalBroadcastManager localBroadcastManager;
CollectionReference barberRef;
StepView stepView;

    Button btn_next_step;

    NonSwipeViewPager viewPager;

    Button btn_previous_step;



    private BroadcastReceiver buttonNextReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step=intent.getIntExtra(Common.KEY_STEP,0);

            if(step==1)
                Common.currentSalon=intent.getParcelableExtra(Common.KEY_SALON_STORE);
            else if (step==2)
                Common.currentBarber=intent.getParcelableExtra(Common.KEY_BARBER_SELECTED);
            else if (step==3)
                Common.currentTimeSlot=intent.getIntExtra(Common.KEY_TIME_SLOT,-1);


            btn_next_step.setEnabled(true);
            setColorButton();
        }

    };



    @Override
    protected void onDestroy() {
      localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

localBroadcastManager =LocalBroadcastManager.getInstance(this);
localBroadcastManager.registerReceiver(buttonNextReceiver,new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));


    stepView=findViewById(R.id.step_view);
    btn_next_step=findViewById(R.id.btn_next_step);
    btn_previous_step=findViewById(R.id.btn_previous_step);
    viewPager=findViewById(R.id.view_pager);
        setupStepView();
        setColorButton();

       viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
       viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {
                stepView.go(i,true);

                if(i==0)
                        {
                            btn_previous_step.setEnabled(false);
                        }
                        else
                        {
                            btn_previous_step.setEnabled(true);
                        }
                        btn_next_step.setEnabled(false);
                        setColorButton();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void setColorButton() {
        if(btn_next_step.isEnabled())
        {
            btn_next_step.setBackgroundResource(R.color.dark);
        }
        else
        {
            btn_next_step.setBackgroundResource(R.color.grey);
        }
        if(btn_previous_step.isEnabled())
        {
            btn_previous_step.setBackgroundResource(R.color.dark);
        }
        else
        {
            btn_previous_step.setBackgroundResource(R.color.grey);
        }



    }

    private void setupStepView() {

    List<String> stepList=new ArrayList<>();
    stepList.add("Salon");
    stepList.add("Barber");
    stepList.add("Time");
    stepList.add("Confirm");
    stepView.setSteps(stepList);

    }



    public void nextClick(View view) {

        if(Common.step<3 || Common.step==0)
        {
            Common.step++;
                 if(Common.step==1)
                 {
                     if(Common.currentSalon!=null)
                     {
                         loadBarberBySalon(Common.currentSalon.getSalonId());
                     }
                 }
                 else if (Common.step==2)
                 {
                     if(Common.currentBarber!=null)
                     {
                         loadTimeSlotofBarber(Common.currentBarber.getBarberId());
                     }
                 }
                 else if (Common.step==3)
                 {
                     if(Common.currentTimeSlot!= -1)
                     {
                        confirmBooking();
                     }
                 }

                 viewPager.setCurrentItem(Common.step);

        }
    }

    private void confirmBooking() {

        Intent intent=new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSlotofBarber(String barberId) {
        Intent intent=new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);

    }


    private void loadBarberBySalon(String salonId) {

        if(!TextUtils.isEmpty(Common.city)) {
            barberRef = FirebaseFirestore.getInstance().collection("All Saloon").document(Common.city).collection("Branch").document(salonId).collection("Barber");

            barberRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    ArrayList<Barber> barbers=new ArrayList<>();

                    for (QueryDocumentSnapshot barberSnapShot:task.getResult())
                    {
                         Barber barber=barberSnapShot.toObject(Barber.class);
                        barber.setBarberId(barberSnapShot.getId());
                        barbers.add(barber);
                    }
                    Intent intent=new Intent(Common.KEY_BARBER_LOAD_DONE);
                    intent.putParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE,barbers);
                    localBroadcastManager.sendBroadcast(intent);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }
    }

    public void previousStep(View view) {

        if(Common.step==3||Common.step>0)
        {
            Common.step--;
            viewPager.setCurrentItem(Common.step);
        }
    }


}
