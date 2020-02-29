package com.example.practice;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {


    Context context;
    List<TimeSlot> timeSlotList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.localBroadcastManager=LocalBroadcastManager.getInstance(context);
        cardViewList=new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context) {
        this.context=context;
        this.timeSlotList=new ArrayList<>();
        this.localBroadcastManager=LocalBroadcastManager.getInstance(context);
        cardViewList=new ArrayList<>();

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.layout_time_slot,viewGroup,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.txt_time_slot.setText(new StringBuilder(Common.ConvertTimeSlotToString(position)).toString());

        if(timeSlotList.size()==0)
        {
            holder.txt_time_slot_description.setText("Available");
            holder.txt_time_slot_description.setTextColor(context.getResources().getColor(R.color.dark));
            holder.txt_time_slot.setTextColor(context.getResources().getColor(R.color.dark));
            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));


        }
        else
        {
            for (TimeSlot slotValue:timeSlotList)
            {
                int slot=Integer.parseInt(slotValue.getSlot().toString());
                if (slot==position)
                {
                    holder.card_time_slot.setTag(Common.DISABLE_TAG);
                    holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(R.color.grey));
                    holder.txt_time_slot_description.setText("Full");
                    holder.txt_time_slot_description.setTextColor(context.getResources().getColor(R.color.whiteColor));
                    holder.txt_time_slot.setTextColor(context.getResources().getColor(R.color.whiteColor));

                }


            }

        }
        if (!cardViewList.contains(holder.card_time_slot))
        {
            cardViewList.add(holder.card_time_slot);
        }
        holder.setiRecyclerItemSelectedListner(new IRecyclerItemSelectedListner() {
            @Override
            public void onItemSelectedListner(View view, int pos) {
                for (CardView cardView:cardViewList)
                {
                    if(cardView.getTag()==null)
                    {
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
                    }
                }
                holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                Intent intent =new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_TIME_SLOT,position);
                intent.putExtra(Common.KEY_STEP,3);
                localBroadcastManager.sendBroadcast(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_time_slot,txt_time_slot_description;
        CardView card_time_slot;

        IRecyclerItemSelectedListner iRecyclerItemSelectedListner;

        public void setiRecyclerItemSelectedListner(IRecyclerItemSelectedListner iRecyclerItemSelectedListner) {
            this.iRecyclerItemSelectedListner = iRecyclerItemSelectedListner;
        }

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            card_time_slot=(CardView)itemView.findViewById(R.id.card_time_slot);
            txt_time_slot=(TextView)itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description=(TextView)itemView.findViewById(R.id.txt_time_slot_description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListner.onItemSelectedListner(v,getAdapterPosition());
        }
    }
}
