package com.example.myinventario;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    private Context context;
    private List<DataClase> dataList;

    public MyAdapter(Context context, List<DataClase> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @Nonnull
    @Override
    public MyViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@Nonnull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recTitle.setText(dataList.get(position).getDataNombre());
        holder.recDesc.setText(dataList.get(position).getDataDesc());
        holder.recLang.setText(dataList.get(position).getDataPrec());

        holder.recCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(context, Detalle.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDataDesc());
                intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getDataNombre());
                intent.putExtra("Key", dataList.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Languaje", dataList.get(holder.getAdapterPosition()).getDataPrec());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount(){
        return dataList.size();
    }
    public void searchDataList(ArrayList<DataClase> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView recImage;
        TextView recTitle, recDesc, recLang;
        CardView recCard;

        public MyViewHolder(@Nonnull View itemView){
            super(itemView);
            recImage = itemView.findViewById(R.id.recImage);
            recCard = itemView.findViewById(R.id.recCard);
            recDesc = itemView.findViewById(R.id.recDesc);
            recTitle = itemView.findViewById(R.id.recTitle);
        }
    }
}

