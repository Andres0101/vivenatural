package com.example.andres_bonilla.ensayo.activity.fragmentsConsumidor;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;

public class Comercializacion extends Fragment {

    public Comercializacion() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.comercializacion, container, false);

        Typeface light = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        Typeface regular = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");

        TextView title = (TextView) rootView.findViewById(R.id.title);
        title.setTypeface(regular);

        TextView content = (TextView) rootView.findViewById(R.id.content);
        content.setTypeface(light);

        // Inflate the layout for this fragment
        return rootView;
    }
}
