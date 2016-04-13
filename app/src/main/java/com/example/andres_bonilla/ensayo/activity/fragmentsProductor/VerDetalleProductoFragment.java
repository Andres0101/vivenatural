package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by ANDRES_BONILLA on 12/04/2016.
 */
public class VerDetalleProductoFragment extends Fragment {

    private String nombreDelProductor;
    private String nombreDelProducto;

    private EditText descripcionProducto;
    private EditText cantidadDisponible;

    public VerDetalleProductoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ver_detalle_producto_fragment, container, false);

        Typeface editText = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        Typeface text = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");

        Firebase myRef = new Firebase("https://vivenatural.firebaseio.com/");

        nombreDelProductor = getArguments().getString("nombreDelProductor");
        nombreDelProducto = getArguments().getString("nombreDelProducto");

        TextView textCantidadComentarios = (TextView) rootView.findViewById(R.id.textViewComentarios);
        textCantidadComentarios.setTypeface(text);
        TextView cantidadComentario = (TextView) rootView.findViewById(R.id.textViewCantidadComentarios);
        cantidadComentario.setTypeface(text);

        descripcionProducto = (EditText) rootView.findViewById(R.id.editTextDescriProduct);
        descripcionProducto.setTypeface(editText);
        descripcionProducto.setBackground(null);
        cantidadDisponible = (EditText) rootView.findViewById(R.id.editTextCantidadDisponible);
        cantidadDisponible.setTypeface(editText);
        cantidadDisponible.setBackground(null);

        // Lee los datos de los productos
        Firebase productos = myRef.child("products");
        productos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    if (product.getNombreProducto().equals(nombreDelProducto) && product.getProductor().equals(nombreDelProductor)) {

                        descripcionProducto.setText(product.getDescripcionProducto());
                        cantidadDisponible.setText(" " + product.getCantidad() + " lb");
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
}
