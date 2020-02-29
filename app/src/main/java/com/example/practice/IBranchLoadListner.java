package com.example.practice;

import java.util.List;

public interface IBranchLoadListner {

    void onAllBranchLoadSuccess(List<Salon> salonList);
    void onAllBranchLoadFailed(String message);

}
