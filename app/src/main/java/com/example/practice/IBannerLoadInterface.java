package com.example.practice;

import java.util.List;

public interface IBannerLoadInterface {
    void onBannerLoadSucess(List<Banner> banners);
    void onBannerLoadFailed(String message);

}
