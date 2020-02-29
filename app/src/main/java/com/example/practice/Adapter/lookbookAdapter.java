package com.example.practice.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice.Banner;
import com.example.practice.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class lookbookAdapter extends RecyclerView.Adapter<lookbookAdapter.MyViewHolder> {

    Context context;
    List<Banner> lookbook;

    public lookbookAdapter(Context context, List<Banner> lookbook) {
        this.context = context;
        this.lookbook = lookbook;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemView= LayoutInflater.from(context).inflate(R.layout.lookbook,parent,false);



        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(lookbook.get(position).getImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return lookbook.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
      ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.image_look_book);
        }
    }
}
