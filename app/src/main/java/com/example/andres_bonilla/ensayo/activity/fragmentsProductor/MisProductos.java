package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.andres_bonilla.ensayo.R;

/**
 * Created by ANDRES_BONILLA on 11/03/2016.
 */
public class MisProductos extends Fragment {

    public MisProductos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.misproductos_fragment, container, false);
    }
}
