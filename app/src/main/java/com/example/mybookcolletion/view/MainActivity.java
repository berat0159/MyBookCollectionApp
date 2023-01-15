package com.example.mybookcolletion.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mybookcolletion.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//RecyclerView Çalışma Rotası
//1-veritabanına textboxtaki verileri insert et
//2-veri tabanındaki verileri recyclerView classında(inflate edeceğimiz class) verileri get ederek bunları constroctur methoduna yaz constroctur u arrayList ekle yap
//3-adapter classından arraylisti göndererek details classından verileri alarak textboxlara yerleştir
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        if(user!=null){
            Intent intent=new Intent(this,FeedActivity.class);
            startActivity(intent);

        }
    }
    public void signUp(View view){
        String mail=binding.mailText.getText().toString();
        String password=binding.passwordText.getText().toString();

        auth.createUserWithEmailAndPassword(mail,password).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //when signUp success
                Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //when signUp fail
                Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }
    public void signIn(View view){
        String mail=binding.mailText.getText().toString();
        String password=binding.passwordText.getText().toString();

        if(mail.equals("")||password.equals("")){
            Toast.makeText(MainActivity.this,"Enter Your mail and Password",Toast.LENGTH_LONG).show();

        }else {
            auth.signInWithEmailAndPassword(mail,password).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    //when signIn success
                    Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //when signIn fail
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }


    }
}