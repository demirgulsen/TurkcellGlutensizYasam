package com.example.benimprojem.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.benimprojem.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class KafeRestorantFragment extends Fragment {


    public KafeRestorantFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kafe_restorant, container, false);
    }

}
