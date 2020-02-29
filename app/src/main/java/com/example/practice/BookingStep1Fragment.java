package com.example.practice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import static com.example.practice.R.layout.fragment_booking_step_1;

public class BookingStep1Fragment extends Fragment implements IALLSALOON, IBranchLoadListner {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        iallsaloon=this;
        iBranchLoadListner=this;

    }
    FirebaseFirestore data=FirebaseFirestore.getInstance();
    CollectionReference allSalonRef=data.collection("All Saloon");
    CollectionReference branchref;
    IALLSALOON iallsaloon;
    IBranchLoadListner iBranchLoadListner;

    static BookingStep1Fragment instance;
    public static BookingStep1Fragment getInstance()
    {
        if(instance==null)
            instance=new BookingStep1Fragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        View itemView= inflater.inflate(fragment_booking_step_1,container,false);
       spinner=(MaterialSpinner)itemView.findViewById(R.id.spinner);
        recycler_salon=(RecyclerView) itemView.findViewById(R.id.recycler_salon);
        initView();
        loadAllSaloon();

        return  itemView;
    }

    private void initView() {
        recycler_salon.setHasFixedSize(true);
        recycler_salon.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recycler_salon.addItemDecoration(new SpacesItemDecoration(4));

    }

    MaterialSpinner spinner;
    RecyclerView recycler_salon;

    private void loadAllSaloon() {

        allSalonRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<String> list =new ArrayList<>();
                    list.add("Please choose City");

                    for(QueryDocumentSnapshot documentSnapshot:task.getResult())
                    {
                        list.add( documentSnapshot.getId());
                    }
                    iallsaloon.onAllSalonLoadSuccess(list);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iallsaloon.onAllSalonFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllSalonLoadSuccess(List<String> areaNameList) {
        spinner.setItems(areaNameList);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(position>0)
                {

                    loadBranchofCity(item.toString());

                }
                else
                {
                    recycler_salon.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadBranchofCity(String cityName) {

        Common.city=cityName;
        branchref=FirebaseFirestore.getInstance().collection("All Saloon").document(cityName).collection("Branch");
        branchref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            List<Salon> list=new ArrayList<>();
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {

                    for(QueryDocumentSnapshot documentSnapshot:task.getResult())
                    {
                        Salon salon=documentSnapshot.toObject(Salon.class);
                        salon.setSalonId(documentSnapshot.getId());
                        list.add(salon);
                    }
                    iBranchLoadListner.onAllBranchLoadSuccess(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                iBranchLoadListner.onAllBranchLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllSalonFailed(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAllBranchLoadSuccess(List<Salon> salonList) {
        MySalonAdapter adapter=new MySalonAdapter(getActivity(),salonList);
        recycler_salon.setAdapter(adapter);
        recycler_salon.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAllBranchLoadFailed(String message) {

        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }
}
