package com.example.practice;

import java.util.List;

public interface IALLSALOON {

    void onAllSalonLoadSuccess(List<String> areaNameList);
    void onAllSalonFailed(String message);
}
