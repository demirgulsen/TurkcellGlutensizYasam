package com.example.benimprojem.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.benimprojem.AddPostActivity;
import com.example.benimprojem.DiyetisyenActivity;
import com.example.benimprojem.MainActivity;
import com.example.benimprojem.R;
import com.example.benimprojem.SavedPostActivity;
import com.example.benimprojem.ShopActivity;
import com.example.benimprojem.adapters.AdapterUser;
import com.example.benimprojem.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUser adapterUser;
    List<ModelUser> userList;

    FirebaseAuth firebaseAuth;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        firebaseAuth= FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.users_rec);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //init user list
        userList =new ArrayList<>();
        //getall user
        getAllUser();

        return view;
    }

    private void getAllUser() {
        //get current user
        final FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref. addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    if (!modelUser.getUid().equals(fUser.getUid())){
                        userList.add(modelUser);
                    }
                    // adapter

                    adapterUser= new AdapterUser(getActivity(), userList);
                    recyclerView.setAdapter(adapterUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchUsers(final String query) {

        //get current user
        final FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref. addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //get all searched users except currently signed in user
                    if (!modelUser.getUid().equals(fUser.getUid())){

                        if(modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getEmail().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);

                        }

                    }

                    // adapter
                    adapterUser= new AdapterUser(getActivity(), userList);
                    //refresh adpater
                    adapterUser.notifyDataSetChanged();
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //ProfilActivity Dashboard olarak değitirildi
            //mProfilP.setText(user.getEmail());

        }
        else{

            //Bir activity den veri çekileceği zaman yazılır(Burası Fragment)
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // to show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
     inflater.inflate(R.menu.menu_main, menu);

     //hide addpost icon from this fragment
//     menu.findItem(R.id.action_add_post).setVisible(false);
//
//     menu.findItem(R.id.action_saved).setVisible(false);
//     menu.findItem(R.id.action_diyetisyen).setVisible(false);
//     menu.findItem(R.id.action_alisveris).setVisible(false);
//     menu.findItem(R.id.action_kafe).setVisible(false);



        //search view
     MenuItem item = menu.findItem(R.id.action_search);
     SearchView searchView =(SearchView) MenuItemCompat.getActionView(item);

     //search listener
     searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
         @Override
         public boolean onQueryTextSubmit(String s) {
             //called when user press search press button from keyboard
             //if search query is not empty then search
             if(!TextUtils.isEmpty(s.trim())){
                 searchUsers(s);
             }
             else{
                 // search text empty, get all users
                 getAllUser();

             }
             return false;
         }

         @Override
         public boolean onQueryTextChange(String s) {
             //called whenever user press any single letter
             if(!TextUtils.isEmpty(s.trim())){
                 searchUsers(s);
             }
             else{
                 // search text empty, get all users
                 getAllUser();

             }

             return false;
         }
     });

     super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id ==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }


//        if (id ==R.id.action_add_post){
//            startActivity(new Intent(getActivity(), AddPostActivity.class));
//        }
//
//        if (id ==R.id.action_saved){
//            startActivity(new Intent(getActivity(), SavedPostActivity.class));
//        }
//
//        if (id ==R.id.action_diyetisyen){
//            startActivity(new Intent(getActivity(), DiyetisyenActivity.class));
//        }
//        if (id ==R.id.action_alisveris){
//            startActivity(new Intent(getActivity(), ShopActivity.class));
//        }
//        if (id ==R.id.action_kafe){
//            startActivity(new Intent(getActivity(), ShopActivity.class));
//        }
        return super.onOptionsItemSelected(item);
    }


}
