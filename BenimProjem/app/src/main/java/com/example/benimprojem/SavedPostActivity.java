package com.example.benimprojem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.benimprojem.adapters.AdapterPosts;
import com.example.benimprojem.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SavedPostActivity extends AppCompatActivity {

    String  myUid, myEmail, myName, postId, pLikes;



    private List<String> mySaves;


    RecyclerView recyclerView_saves;
    List<ModelPost> postList_saves;

    AdapterPosts AdapterPosts_saves;//sonradan ekledim()sil

    ActionBar actionBar;

    FirebaseAuth firebaseAuth;
    //FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_post);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Kaydedilen Gönderiler");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);



        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        recyclerView_saves = findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);

        //  LinearLayoutManager layoutManager = new LinearLayoutManager(this);   bu ikisinden biri kullanılmalı
        // LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager linearLayoutManager_saves = new LinearLayoutManager(getApplicationContext());   //getBaseContext  bunun yerine getContext ya da this olmalı
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves = new ArrayList<>();

        //sonradan ekledim()sil
        //AdapterPosts_saves= new AdapterPosts(getApplicationContext(),postList_saves);
        //recyclerView_saves.setAdapter(AdapterPosts_saves);

        //myFotoAdapter kısmını yapmadım
        recyclerView_saves.setVisibility(View.VISIBLE);

        mysaves();
    }


    private void mysaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
               .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                }

                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList_saves.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelPost post = snapshot.getValue(ModelPost.class);

                    for (String id : mySaves){
                        if (post.getpId().equals(id)){
                            postList_saves.add(post);
                        }
                    }
                }
                //AdapterPosts_saves.notifyDataSetChanged();//sonradan ekledim()sil
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            myEmail = user.getEmail();
            myUid = user.getUid();//currently signed in users id


        }
        else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_saved).setVisible(false);
        menu.findItem(R.id.action_diyetisyen).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id ==R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

}
