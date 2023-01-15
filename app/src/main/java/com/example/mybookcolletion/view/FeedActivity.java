package com.example.mybookcolletion.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mybookcolletion.R;
import com.example.mybookcolletion.adapter.BookAdapter;
import com.example.mybookcolletion.adapter.OldBookAdapter;
import com.example.mybookcolletion.databinding.ActivityFeedBinding;


import com.example.mybookcolletion.modul.Book;
import com.example.mybookcolletion.modul.OldBook;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivityFeedBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Book> bookArrayList;
    BookAdapter bookAdapter;
    private ArrayList<OldBook> oldBookArrayList;
    OldBookAdapter oldBookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFeedBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        bookArrayList=new ArrayList<>();
        binding.newRecyclerView.setLayoutManager(new LinearLayoutManager(FeedActivity.this,RecyclerView.HORIZONTAL,false));
        bookAdapter=new BookAdapter(bookArrayList);
        binding.newRecyclerView.setAdapter(bookAdapter);

        oldBookArrayList=new ArrayList<>();
        binding.oldRecyclerView.setLayoutManager(new LinearLayoutManager(FeedActivity.this,RecyclerView.HORIZONTAL,true));
        oldBookAdapter=new OldBookAdapter(oldBookArrayList);
        binding.oldRecyclerView.setAdapter(oldBookAdapter);

        oldGetData();
        getData();

    }

    public void oldGetData(){

        firebaseFirestore.collection("OldBooks").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){
                    Toast.makeText(FeedActivity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
                if(value!=null){

                    for (DocumentSnapshot documentSnapshot:value.getDocuments()){


                        Map<String, Object> data=documentSnapshot.getData();

                        String imageUrl=(String) data.get("downUrl");
                        String bookTxt=(String) data.get("bookTxt");
                        String writerTxt=(String) data.get("writerTxt");
                        String excreptTxt=(String) data.get("excreptTxt");

                        OldBook oldBook=new OldBook(imageUrl,bookTxt,writerTxt,excreptTxt);
                        oldBookArrayList.add(oldBook);

                    }
                    oldBookAdapter.notifyDataSetChanged();
                }


            }
        });








    }

    public void getData(){
        //get data from collection
        firebaseFirestore.collection("Books").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
                if(value!=null){

                    for(DocumentSnapshot documentSnapshot:value.getDocuments()){
                        Map<String,Object> data=documentSnapshot.getData();
                        //casting

                        String bookName=(String) data.get("bookName");
                        String writerName=(String) data.get("writerName");
                        String image=(String) data.get("image");

                        Book book=new Book(bookName,writerName,image);
                        bookArrayList.add(book);


                    }
                    //show data in recyclerView
                    bookAdapter.notifyDataSetChanged();

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=new MenuInflater(this);
        menuInflater.inflate(R.menu.signout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Sign Out User
        auth.signOut();
        Intent intent=new Intent(FeedActivity.this,MainActivity.class);
        startActivity(intent);
        finish();

        return super.onOptionsItemSelected(item);
    }
    public void addBook(View view){

        Intent intent=new Intent(FeedActivity.this,AddBookActivity.class);
        startActivity(intent);
        finish();

    }
}