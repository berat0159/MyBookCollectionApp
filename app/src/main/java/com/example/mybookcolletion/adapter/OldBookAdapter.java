package com.example.mybookcolletion.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybookcolletion.databinding.BookRowBinding;
import com.example.mybookcolletion.databinding.OldbookRowBinding;
import com.example.mybookcolletion.modul.OldBook;
import com.example.mybookcolletion.view.ReadedActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OldBookAdapter extends RecyclerView.Adapter<OldBookAdapter.OldBookHolder> {
    private ArrayList<OldBook> oldBookArrayList;
    public OldBookAdapter(ArrayList<OldBook> oldBookArrayList){
        this.oldBookArrayList=oldBookArrayList;
    }

    @NonNull
    @Override
    public OldBookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BookRowBinding bookRowBinding=BookRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new OldBookHolder(bookRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OldBookHolder holder, int position) {
        Picasso.get().load(oldBookArrayList.get(holder.getAdapterPosition()).image).into(holder.bookRowBinding.imageRow);
        holder.bookRowBinding.done.setVisibility(View.INVISIBLE);

        holder.bookRowBinding.imageRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(holder.itemView.getContext(),ReadedActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("OldBook",oldBookArrayList.get(holder.getAdapterPosition()));
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return oldBookArrayList.size();
    }

    public class OldBookHolder extends RecyclerView.ViewHolder{
        private BookRowBinding bookRowBinding;
        public OldBookHolder(@NonNull BookRowBinding bookRowBinding) {
            super(bookRowBinding.getRoot());
            this.bookRowBinding=bookRowBinding;
        }
    }
}
