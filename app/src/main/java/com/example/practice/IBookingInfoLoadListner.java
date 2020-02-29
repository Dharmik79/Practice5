package com.example.practice;

public interface IBookingInfoLoadListner {
    void onBookingInfoLoadempty();
    void onBookingInfoLoadSuccess(BookingInformation bookingInformation);
    void onBookingInfoLoadFailed(String message);

}
