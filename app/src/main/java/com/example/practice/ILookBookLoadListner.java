package com.example.practice;

import java.util.List;

public interface ILookBookLoadListner {

    void onLookBookLoadSuccess(List<Banner> banners);
    void onLookBookLoadFailed(String message);
}
