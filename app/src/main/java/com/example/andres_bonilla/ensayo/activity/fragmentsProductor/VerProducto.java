package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by vivianabepa on 3/28/16.
 */
public class VerProducto extends Fragment {

    private View rootView;

    private Firebase myRef;
    private Firebase productos;

    private String nombreDelProducto;

    private ImageView imagenProducto;
    private EditText descripcionProducto;
    private EditText cantidadDisponible;
    private TextView textDescription;
    private TextView textViewCantidadDisponible;

    private Typeface editText;
    private Typeface textView;

    public VerProducto() {
        // Required empty public constructor

        myRef = new Firebase("https://vivenatural.firebaseio.com/");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.ver_productos, container, false);
        setHasOptionsMenu(true);

        editText = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        textView = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Bold.ttf");

        /*Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        //for crate home button
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelProducto = getArguments().getString("nombreProducto");
        getActivity().setTitle(nombreDelProducto);

        imagenProducto = (ImageView) rootView.findViewById(R.id.imageProduct);
        textDescription = (TextView) rootView.findViewById(R.id.textViewDescripcion);
        textDescription.setTypeface(textView);
        textViewCantidadDisponible = (TextView) rootView.findViewById(R.id.textViewCantidadDisponible);
        textViewCantidadDisponible.setTypeface(textView);

        descripcionProducto = (EditText) rootView.findViewById(R.id.editTextDescriProduct);
        descripcionProducto.setTypeface(editText);
        descripcionProducto.setBackground(null);
        cantidadDisponible = (EditText) rootView.findViewById(R.id.editTextCantidadDisponible);
        cantidadDisponible.setTypeface(editText);
        cantidadDisponible.setBackground(null);

        // Lee los datos de los productos
        productos = myRef.child("products");
        productos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    //Si el nombre del productor coincide con el que inicio sesión entonces...
                    if (product.getNombreProducto().equals(nombreDelProducto)) {
                        imagenProducto.setImageResource(product.getImage());
                        descripcionProducto.setText(product.getDescripcionProducto());
                        cantidadDisponible.setText(" " + product.getCantidad() + " lb");
                    } else {
                        //textoNoHay.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();    //remove all items
        getActivity().getMenuInflater().inflate(R.menu.menu_editar_producto, menu);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
