package com.example.benimprojem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.benimprojem.fragments.AlisVerisFragment;
import com.example.benimprojem.fragments.ChatListFragment;
import com.example.benimprojem.fragments.EkleFragment;
import com.example.benimprojem.fragments.HomeFragment;
import com.example.benimprojem.fragments.KafeRestorantFragment;
import com.example.benimprojem.fragments.ProfileFragment;
import com.example.benimprojem.fragments.UsersFragment;
import com.example.benimprojem.notifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    //ProfilActivity Dashboard olarak değitirildi
    //TextView  mProfilP;

    ActionBar actionBar;
    String mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        actionBar=getSupportActionBar();
        actionBar.setTitle("Profil");

        firebaseAuth = FirebaseAuth.getInstance();

        //bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);


        //home fragment transaction default, on start
        actionBar.setTitle("Home");
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1, "");
        ft1.commit();

        //ProfilActivity Dashboard olarak değitirildi
        //mProfilP= findViewById(R.id.profilP);

        checkUserStatus();

        //updateToken(FirebaseInstanceId.getInstance().getToken());    orjinal kodda yok

    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    private void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //handle item click
                    switch(menuItem.getItemId()){
                        case R.id.nav_home:
                            //home fragment transaction
                            actionBar.setTitle("Anasayfa");
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content,fragment1, "");
                            ft1.commit();
                            return true;
                        case R.id.nav_profile:
                            //profil fragment transaction
                            //home fragment transaction
                            actionBar.setTitle("Profil");
                            ProfileFragment fragment2 = new ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content,fragment2, "");
                            ft2.commit();
                            return true;
                        case R.id.nav_add:
                            //post ekleme fragment transaction
                            //home fragment transaction
                            actionBar.setTitle("Tarif veya Bilgi Ekle");
                            EkleFragment fragment3 = new EkleFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content,fragment3, "");
                            ft3.commit();
                            return true;
                        case R.id.nav_users:
                            //usershome fragment transaction
                            //home fragment transaction
                            actionBar.setTitle("Kullanıcılar");
                            UsersFragment fragment4 = new UsersFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content,fragment4, "");
                            ft4.commit();
                            return true;
                        case R.id.nav_chat:
                            //usershome fragment transaction
                            //home fragment transaction
                            actionBar.setTitle("Mesajlar");
                            ChatListFragment fragment5 = new ChatListFragment();
                            FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
                            ft5.replace(R.id.content,fragment5, "");
                            ft5.commit();
                            return true;
//                        case R.id.nav_kafeveRestorant:
//                            //usershome fragment transaction
//                            //home fragment transaction
//                            actionBar.setTitle("Restoran ve Kafeler");
//                            KafeRestorantFragment fragment6 = new KafeRestorantFragment();
//                            FragmentTransaction ft6 = getSupportFragmentManager().beginTransaction();
//                            ft6.replace(R.id.content,fragment6, "");
//                            ft6.commit();
//                            return true;
//                        case R.id.nav_alisVeris:
//                            //usershome fragment transaction
//                            //home fragment transaction
//                            actionBar.setTitle("Alış-Veriş Yap");
//                            AlisVerisFragment fragment7 = new AlisVerisFragment();
//                            FragmentTransaction ft7 = getSupportFragmentManager().beginTransaction();
//                            ft7.replace(R.id.content,fragment7, "");
//                            ft7.commit();
//                            return true;
                    }
                    return false;
                }
            };

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //ProfilActivity Dashboard olarak değitirildi
        //mProfilP.setText(user.getEmail());

        if (user != null){
            mUID = user.getUid();

            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();

            updateToken(FirebaseInstanceId.getInstance().getToken());

        }
        else{
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }


}
