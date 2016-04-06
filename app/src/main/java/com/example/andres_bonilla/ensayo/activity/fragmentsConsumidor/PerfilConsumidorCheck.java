package com.example.andres_bonilla.ensayo.activity.fragmentsConsumidor;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;

/**
 * Created by ANDRES_BONILLA on 4/04/16.
 */
public class PerfilConsumidorCheck extends Fragment {
    public Activity activity;

    private String userString;
    private Boolean editText;
    private String textoCapturadoDelEditText;

    private TextView userName;
    private EditText textoEditable;

    public PerfilConsumidorCheck() {
        // Required empty public constructor
        userString = "none";
        editText = false;

        textoCapturadoDelEditText = "No hay información.";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil_consumidor, container, false);
        setHasOptionsMenu(true);

        userName = (TextView) rootView.findViewById(R.id.nombreUsuario);
        textoEditable = (EditText) rootView.findViewById(R.id.textDescription);

        changeText(userString);
        textEditable(editText);

        // Inflate the layout for this fragment
        return rootView;
    }

    public String getTextoCapturadoDelEditText() {
        textoCapturadoDelEditText = textoEditable.getText().toString();
        System.out.println("EditText value: " + textoCapturadoDelEditText);
        return textoCapturadoDelEditText;
    }

    // Setea el texto con el nombre de usuario
    public void changeText(String text){
        userName.setText(text);
    }
    public void setUserString(String userString) {
        this.userString = userString;
    }


    // Vuelve el editText editable para que el usuario pueda escribir
    public void textEditable(Boolean editable){
        textoEditable.setEnabled(editable);
    }
    public void setEditText(Boolean editText) {
        this.editText = editText;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();    //remove all items
        getActivity().getMenuInflater().inflate(R.menu.menu_guardar, menu);
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
