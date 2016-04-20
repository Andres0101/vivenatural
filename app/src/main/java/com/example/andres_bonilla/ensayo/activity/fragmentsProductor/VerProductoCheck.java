package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.andres_bonilla.ensayo.R;

public class VerProductoCheck extends Fragment {

    private EditText descripcionProducto;
    private EditText cantidadDisponible;

    private Boolean editText;
    private Boolean editTextCant;

    public VerProductoCheck() {
        // Required empty public constructor
        editText = false;
        editTextCant = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ver_producto_check, container, false);
        setHasOptionsMenu(true);

        //Utilizado para remover el icono de regresar.
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Typeface editTextCheck = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        descripcionProducto = (EditText) rootView.findViewById(R.id.editTextDescriProduct);
        descripcionProducto.setTypeface(editTextCheck);
        cantidadDisponible = (EditText) rootView.findViewById(R.id.editTextCantidadDisponible);
        cantidadDisponible.setTypeface(editTextCheck);

        textEditable(editText);
        textEditableCantidad(editTextCant);

        // Inflate the layout for this fragment
        return rootView;
    }

    public String getTextDescripcion() {
        String textDescripcion = descripcionProducto.getText().toString();
        return textDescripcion;
    }


    public String getTextCantidad() {
        String textCantidad = cantidadDisponible.getText().toString();
        return textCantidad;
    }

    // Vuelve el editText de la descripción editable para que el usuario pueda escribir
    public void textEditable(Boolean editable){
        descripcionProducto.setEnabled(editable);
    }
    public void setEditText(Boolean editText) {
        this.editText = editText;
    }

    // Vuelve el editText de la cantidad editable para que el usuario pueda escribir
    public void textEditableCantidad(Boolean editable){
        cantidadDisponible.setEnabled(editable);
    }
    public void setEditTextCant(Boolean editTextCant) {
        this.editTextCant = editTextCant;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();    //remove all items
        getActivity().getMenuInflater().inflate(R.menu.menu_guardar_producto, menu);
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
