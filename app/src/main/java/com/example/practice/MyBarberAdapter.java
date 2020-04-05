package com.example.practice;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyBarberAdapter extends RecyclerView.Adapter<MyBarberAdapter.MyViewHolder> {

    Context context;
    List<Barber> barberList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyBarberAdapter(Context context, List<Barber> barberList) {
        this.context = context;
        this.barberList = barberList;
        cardViewList=new ArrayList<>();
        localBroadcastManager=LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.layout_barber,parent,false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.txt_barber_name.setText(barberList.get(position).getName());
      //  holder.ratingBar.setRating(barberList.get(position).getRating());

        if(!cardViewList.contains(holder.card_barber))
        {
            cardViewList.add(holder.card_barber);
        }
        holder.setiRecyclerItemSelectedListner(new IRecyclerItemSelectedListner() {
            @Override
            public void onItemSelectedListner(View view, int pos) {

                for (CardView cardView:cardViewList)
                {
                    cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
                }
                holder.card_barber.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));

                Intent intent=new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_BARBER_SELECTED,barberList.get(pos));
                intent.putExtra(Common.KEY_STEP,2);
                localBroadcastManager.sendBroadcast(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       TextView txt_barber_name;
       RatingBar ratingBar;
        CardView card_barber;

        IRecyclerItemSelectedListner iRecyclerItemSelectedListner;

        public void setiRecyclerItemSelectedListner(IRecyclerItemSelectedListner iRecyclerItemSelectedListner) {
            this.iRecyclerItemSelectedListner = iRecyclerItemSelectedListner;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_barber=(CardView)itemView.findViewById(R.id.card_barber);
            txt_barber_name=(TextView)itemView.findViewById(R.id.txt_barber_name);
            ratingBar=(RatingBar)itemView.findViewById(R.id.rtb_barber);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListner.onItemSelectedListner(v,getAdapterPosition());
        }
    }
}
