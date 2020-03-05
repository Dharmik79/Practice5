package com.example.practice;

public interface IBookingInfoLoadListner {
    void onBookingInfoLoadempty();
    void onBookingInfoLoadSuccess(BookingInformation bookingInformation,String documentId);
    void onBookingInfoLoadFailed(String message);

}
