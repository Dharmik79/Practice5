package com.example.practice;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.TimeZone;

public class BookingStep4Fragment extends Fragment {

    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;
    BroadcastReceiver confirmBookingReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                setData();
        }
    };

    private void setData() {
        txt_booking_barber_text.setText(Common.currentBarber.getName());
        txt_booking_time_text.setText(new StringBuilder(Common.ConvertTimeSlotToString(Common.currentTimeSlot)).append("at").append(simpleDateFormat.format(Common.bookingDate.getTime())));
        txt_salon_address.setText(Common.currentSalon.getAddress());
        txt_salon_phone.setText(Common.currentSalon.getPhone());
        txt_salon_open_hours.setText(Common.currentSalon.getOpenHours());
    }

    TextView txt_booking_barber_text,txt_booking_time_text,txt_salon_address,txt_salon_open_hours,txt_salon_phone,txt_salon_name;
    Button btn_confirm;
    static BookingStep4Fragment instance;
    public static BookingStep4Fragment getInstance()
    {
        if(instance==null)
            instance=new BookingStep4Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat =new SimpleDateFormat("dd/MM/yyyy");
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver,new IntentFilter(Common.KEY_CONFIRM_BOOKING));
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);

        super.onDestroy();
    }

    public void confirmBooking(View view) {


        String startTime=Common.ConvertTimeSlotToString(Common.currentTimeSlot);
        String [] convertTime=startTime.split("-");
        String[] startTimeConvert=convertTime[0].split(":");
        int startHoutInt=Integer.parseInt(startTimeConvert[0].trim());
        int startMinInt=Integer.parseInt(startTimeConvert[1].trim());

        Calendar bookingDateWithourHours=Calendar.getInstance();
        bookingDateWithourHours.setTimeInMillis(Common.bookingDate.getTimeInMillis());
        bookingDateWithourHours.set(Calendar.HOUR_OF_DAY,startHoutInt);
        bookingDateWithourHours.set(Calendar.MINUTE,startMinInt);

        Timestamp timestamp=new Timestamp(bookingDateWithourHours.getTime());
        final BookingInformation bookingInformation=new BookingInformation();

        bookingInformation.setTimestamp(timestamp);
        bookingInformation.setDone(false);
        bookingInformation.setBarberId(Common.currentBarber.getBarberId());
        bookingInformation.setBarberName(Common.currentBarber.getName());
        bookingInformation.setSalonName(Common.currentSalon.getName());
        bookingInformation.setSalonAddress(Common.currentSalon.getAddress());
        bookingInformation.setSalonId(Common.currentSalon.getSalonId());
        bookingInformation.setCityBook(Common.city);
        bookingInformation.setTime(new StringBuilder(Common.ConvertTimeSlotToString(Common.currentTimeSlot)).append("at").append(simpleDateFormat.format(bookingDateWithourHours.getTime())).toString());
        bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));

        DocumentReference bookingdate= FirebaseFirestore.getInstance().collection("All Saloon").document(Common.city).collection("Branch").document(Common.currentSalon.getSalonId()).collection("Barber").document(Common.currentBarber.getBarberId()).collection(Common.simpleDateFormat.format(Common.bookingDate.getTime())).document(String.valueOf(Common.currentTimeSlot));

        bookingdate.set(bookingInformation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            addToUserBooking(bookingInformation);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void addToUserBooking(final BookingInformation bookingInformation) {

        final CollectionReference userBooking=FirebaseFirestore.getInstance().collection("User").document(Common.currentUser).collection("Booking");

        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        Timestamp todayTimeStamp=new Timestamp(calendar.getTime());
        userBooking.whereGreaterThanOrEqualTo("timestamp",todayTimeStamp).whereEqualTo("done",false).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty())
                {

                    userBooking.document().set(bookingInformation).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            resetStaticData();
                          getActivity().finish();
                            Toast.makeText(getContext(),"Success",Toast.LENGTH_LONG).show();
                        // addtoCalendar(Common.bookingDate,Common.ConvertTimeSlotToString(Common.currentTimeSlot));
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else
                {
                    resetStaticData();
                    getActivity().finish();
                    Toast.makeText(getContext(),"Success",Toast.LENGTH_LONG).show();

                }

            }
        });
    }

  /*  private void addtoCalendar(Calendar bookingDate, String startDate) {
        String startTime=Common.ConvertTimeSlotToString(Common.currentTimeSlot);
        String [] convertTime=startTime.split("-");
        String[] startTimeConvert=convertTime[0].split(":");
        int startHoutInt=Integer.parseInt(startTimeConvert[0].trim());
        int startMinInt=Integer.parseInt(startTimeConvert[1].trim());


        String[] endTimeConvert=convertTime[0].split(":");
        int endHoutInt=Integer.parseInt(endTimeConvert[0].trim());
        int endMinInt=Integer.parseInt(endTimeConvert[1].trim());

        Calendar startEvent=Calendar.getInstance();
        startEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        startEvent.set(Calendar.HOUR_OF_DAY,startHoutInt);
        startEvent.set(Calendar.MINUTE,startMinInt);
        Calendar endEvent=Calendar.getInstance();
        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        endEvent.set(Calendar.HOUR_OF_DAY,endHoutInt);
        endEvent.set(Calendar.MINUTE,endMinInt);

        SimpleDateFormat calendarDateFormat =new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String startEventTime=calendarDateFormat.format(startEvent.getTime());
        String endEventTime=calendarDateFormat.format(endEvent.getTime());

        addToDeviceCalendar(startEventTime,endEventTime,"Hair cut Booking",new StringBuilder("Haircut from").append(startTime).append("with").append(Common.currentBarber.getName()).append("at").append(Common.currentSalon.getName()).toString(),new StringBuilder("Address").append(Common.currentSalon.getAddress().toString()));

    }

    private void addToDeviceCalendar(String startEventTime, String endEventTime, String title, String description, StringBuilder location) {
        SimpleDateFormat calendardateFormat=new SimpleDateFormat("dd-MM-yyyy");

        try{

            Date start=calendardateFormat.parse(startEventTime);
            Date end=calendardateFormat.parse(endEventTime);

            ContentValues event=new ContentValues();

            event.put(CalendarContract.Events.CALENDAR_ID,getCalendar(getContext()));
            event.put(CalendarContract.Events.TITLE,title);
            event.put(CalendarContract.Events.DESCRIPTION,description);
            event.put(CalendarContract.Events.EVENT_LOCATION, String.valueOf(location));

            event.put(CalendarContract.Events.DTSTART,start.getTime());
            event.put(CalendarContract.Events.DTEND,end.getTime());
            event.put(CalendarContract.Events.ALL_DAY,0);
            event.put(CalendarContract.Events.HAS_ALARM,1);
            String timeZone= TimeZone.getDefault().getID();

            event.put(CalendarContract.Events.EVENT_TIMEZONE,timeZone);
            Uri calendars;
            if(Build.VERSION.SDK_INT>=8)
                calendars=Uri.parse("content.//com.android.calendar/calendars");
            else
                calendars=Uri.parse("content.//calendar/events");
            getActivity().getContentResolver().insert(calendars,event);



        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private String getCalendar(Context context) {

        String gmailIdCalendar="";
        String projection[]={"_id","calendar_displayName"};
        Uri calendars=Uri.parse("content.//com.android,calendar/calendars");

        ContentResolver contentResolver=context.getContentResolver();

        Cursor managedCursor=contentResolver.query(calendars,projection,null,null,null);
        if (managedCursor.moveToFirst())
        {

            String calName;
            int nameCo1=managedCursor.getColumnIndex(projection[1]);
            int idCo1=managedCursor.getColumnIndex(projection[0]);

            do{
                calName =managedCursor.getString(nameCo1);
                if (calName.contains("@gmail.com"))
                {
                    gmailIdCalendar=managedCursor.getString(idCo1);
                    break;

                }
            }while (managedCursor.moveToNext());
            managedCursor.close();
        }

        return null;
    }
*/
    private void resetStaticData() {
            Common.step=0;
            Common.currentTimeSlot=-1;
            Common.currentBarber=null;
            Common.currentSalon=null;
            Common.bookingDate.add(Calendar.DATE,0);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

         View itemView=inflater.inflate(R.layout.fragment_booking_step_4,container,false);
        txt_booking_barber_text=(TextView)itemView.findViewById(R.id.txt_booking_barber_text);
        txt_booking_time_text=(TextView)itemView.findViewById(R.id.txt_booking_time_text);
        txt_salon_address=(TextView)itemView.findViewById(R.id.txt_salon_address);
        txt_salon_name=(TextView)itemView.findViewById(R.id.txt_salon_name);
        txt_salon_open_hours=(TextView)itemView.findViewById(R.id.txt_salon_open_hours);
        txt_salon_phone=(TextView)itemView.findViewById(R.id.txt_salon_phone);
        btn_confirm=(Button)itemView.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmBooking(v);
            }
        });
        return  itemView;
    }
}
