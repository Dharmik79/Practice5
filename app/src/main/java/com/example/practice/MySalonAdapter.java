package com.example.practice;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MySalonAdapter extends RecyclerView.Adapter<MySalonAdapter.MyViewHolder> {

    Context context;
    List<Salon> salonList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MySalonAdapter(Context context, List<Salon> salonList) {
        this.context = context;
        this.salonList = salonList;
        cardViewList=new ArrayList<>();
        localBroadcastManager =LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview= LayoutInflater.from(context).inflate(R.layout.layout_salon,parent,false);

        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.txt_salon_name.setText(salonList.get(position).getName());
        holder.txt_salon_address.setText(salonList.get(position).getAddress());

        if(!cardViewList.contains(holder.card_salon))
        {
            cardViewList.add(holder.card_salon);
        }
        holder.setiRecyclerItemSelectedListner(new IRecyclerItemSelectedListner() {
            @Override
            public void onItemSelectedListner(View view, int pos) {

                for (CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));

                holder.card_salon.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));

                Intent intent=new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_SALON_STORE,salonList.get(pos));
                intent.putExtra(Common.KEY_STEP,1);
                localBroadcastManager.sendBroadcast(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return salonList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView txt_salon_name,txt_salon_address;
        CardView card_salon;

        IRecyclerItemSelectedListner iRecyclerItemSelectedListner;

        public void setiRecyclerItemSelectedListner(IRecyclerItemSelectedListner iRecyclerItemSelectedListner) {
            this.iRecyclerItemSelectedListner = iRecyclerItemSelectedListner;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_salon=(CardView)itemView.findViewById(R.id.card_salon);
            txt_salon_address=(TextView)itemView.findViewById(R.id.txt_salon_address);
            txt_salon_name=(TextView)itemView.findViewById(R.id.txt_salon_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListner.onItemSelectedListner(v,getAdapterPosition());
        }
    }

}
