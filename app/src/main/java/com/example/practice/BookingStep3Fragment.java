package com.example.practice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class BookingStep3Fragment extends Fragment implements ITimeSlotLoadListner {

    DocumentReference barberDoc;
    ITimeSlotLoadListner iTimeSlotLoadListner;
    RecyclerView recycler_time_slot;
    HorizontalCalendarView calendarView;
    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;


    BroadcastReceiver displayTimeSlot =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar date=Calendar.getInstance();
            date.add(Calendar.DATE,0);
            loadAvailableTimeSlotofSalon(Common.currentBarber.getBarberId(),simpleDateFormat.format(date.getTime()));
        }
    };

    private void loadAvailableTimeSlotofSalon(String barberId, final String bookDate) {
        barberDoc= FirebaseFirestore.getInstance().collection("All Saloon").document(Common.city).collection("Branch").document(Common.currentSalon.getSalonId()).collection("Barber").document(Common.currentBarber.getBarberId());

        barberDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                 DocumentSnapshot documentSnapshot=task.getResult();
                 if (documentSnapshot.exists())
                 {
                     CollectionReference date=FirebaseFirestore.getInstance().collection("All Saloon").document(Common.city).collection("Branch").document(Common.currentSalon.getSalonId()).collection("Barber").document(Common.currentBarber.getBarberId()).collection(bookDate);
                     date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                 if (task.isSuccessful())
                                 {
                                     QuerySnapshot querySnapshot=task.getResult();
                                     if (querySnapshot.isEmpty())
                                     {
                                         iTimeSlotLoadListner.onTimeSlotLoadEmpty();
                                     }
                                     else
                                     {
                                         List<TimeSlot> timeSlots=new ArrayList<>();
                                         for (QueryDocumentSnapshot document:task.getResult())
                                             timeSlots.add(document.toObject(TimeSlot.class));
                                         iTimeSlotLoadListner.onTimeSlotLoadSuccess(timeSlots);
                                     }


                                 }
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             iTimeSlotLoadListner.onTimeSlotLoadFailed(e.getMessage());
                         }
                     });

                 }
                }
            }
        });


    }


    static BookingStep3Fragment instance;

    public static BookingStep3Fragment getInstance()
    {
        if(instance==null)
            instance=new BookingStep3Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iTimeSlotLoadListner = this;
        localBroadcastManager =LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(displayTimeSlot,new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));

        simpleDateFormat =new SimpleDateFormat("dd_MM_yyyy");


    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(displayTimeSlot);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        View itemView=inflater.inflate(R.layout.fragment_booking_step_3,container,false);
        recycler_time_slot=(RecyclerView)itemView.findViewById(R.id.recycler_time_slot);
        calendarView=(HorizontalCalendarView)itemView.findViewById(R.id.calenderView);
        init(itemView);
        
        return  itemView;
    }

    private void init(View itemView) {
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),3);
        recycler_time_slot.setLayoutManager(gridLayoutManager);
        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(8));

        Calendar startDate=Calendar.getInstance();
        startDate.add(Calendar.DATE,0);
        Calendar endDate=Calendar.getInstance();
        endDate.add(Calendar.DATE,2);

        HorizontalCalendar horizontalCalendar=new HorizontalCalendar.Builder(itemView,R.id.calenderView).range(startDate,endDate).datesNumberOnScreen(1).mode(HorizontalCalendar.Mode.DAYS).defaultSelectedDate(startDate).build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if(Common.bookingDate.getTimeInMillis()!=date.getTimeInMillis())
                {
                    Common.bookingDate=date;
                    loadAvailableTimeSlotofSalon(Common.currentBarber.getBarberId(),simpleDateFormat.format(date.getTime()));
                }

            }
        });
    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter adapter=new MyTimeSlotAdapter(getContext(),timeSlotList);
        recycler_time_slot.setAdapter(adapter);
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter=new MyTimeSlotAdapter(getContext());
        recycler_time_slot.setAdapter(adapter);

    }
}
