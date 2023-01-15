package com.example.mybookcolletion.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybookcolletion.R;
import com.example.mybookcolletion.databinding.BookRowBinding;
import com.example.mybookcolletion.modul.Book;
import com.example.mybookcolletion.view.ReadedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookHolder> {
        private ArrayList<Book> bookArrayList;


        public BookAdapter(){

        }
        public BookAdapter(ArrayList<Book> bookArrayList){
            this.bookArrayList=bookArrayList;

        }



    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            BookRowBinding bookRowBinding=BookRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new BookHolder(bookRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull  BookHolder holder, int position) {
        Picasso.get().load(bookArrayList.get(holder.getAdapterPosition()).image).into(holder.bookRowBinding.imageRow);
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_book));
        holder.bookRowBinding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(holder.itemView.getContext(), ReadedActivity.class);
                intent.putExtra("Book",bookArrayList.get(holder.getAdapterPosition()));
                intent.putExtra("info","new");
                holder.itemView.getContext().startActivity(intent);


            }
        });




    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    public class BookHolder extends RecyclerView.ViewHolder{
            private BookRowBinding bookRowBinding;
        public BookHolder(@NonNull BookRowBinding bookRowBinding) {
            super(bookRowBinding.getRoot());
            this.bookRowBinding =bookRowBinding;
        }
    }
}
