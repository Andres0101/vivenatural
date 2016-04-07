package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;

/**
 * Created by ANDRES_BONILLA on 30/03/16.
 */
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
        View rootView = inflater.inflate(R.layout.ver_productos, container, false);
        setHasOptionsMenu(true);

        Typeface editTextCheck = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        Typeface textView = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Bold.ttf");

        ImageView imagenProducto = (ImageView) rootView.findViewById(R.id.imageProduct);
        TextView textDescription = (TextView) rootView.findViewById(R.id.textViewDescripcion);
        textDescription.setTypeface(textView);
        TextView textViewCantidadDisponible = (TextView) rootView.findViewById(R.id.textViewCantidadDisponible);
        textViewCantidadDisponible.setTypeface(textView);

        descripcionProducto = (EditText) rootView.findViewById(R.id.editTextDescriProduct);
        descripcionProducto.setTypeface(editTextCheck);
        cantidadDisponible = (EditText) rootView.findViewById(R.id.editTextCantidadDisponible);
        cantidadDisponible.setTypeface(editTextCheck);

        textEditable(editText);
        textEditableCantidad(editTextCant);

        // Lee los datos de los productos
        /*productos = myRef.child("products");
        productos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    //Si el nombre del productor coincide con el que inicio sesión entonces...
                    if (product.getNombreProducto().equals(nombreDelProducto)) {
                        imagenProducto.setImageResource(product.getImage());
                    } else {
                        //textoNoHay.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });*/

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
