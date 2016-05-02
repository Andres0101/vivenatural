package com.example.andres_bonilla.ensayo.activity.fragmentsConsumidor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.VerDetalleProductoProductor;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VerProductoMarketFragment extends Fragment {

    private Firebase products;

    private Product clickedProducer;

    private View rootView;

    private Typeface text;
    private Typeface editText;

    private String nombreDelProducto;
    private String nombreDelConsumidor;

    private TextView cantidadProductor;
    private TextView nohayProductores;

    MyListAdapter adapter;

    private List<Product> myProducers = new ArrayList<>();

    private ProgressBar producerProgress;

    private Boolean pinto;

    public VerProductoMarketFragment() {
        // Required empty public constructor

        Firebase myRef = new Firebase("https://vivenatural.firebaseio.com/");
        products = myRef.child("products");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.ver_producto_market_fragment, container, false);

        editText = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        text = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");

        nombreDelProducto = getArguments().getString("nombreProducto");
        nombreDelConsumidor = getArguments().getString("nombreDelConsumidor");

        TextView textViewProducers = (TextView) rootView.findViewById(R.id.textViewProducers);
        textViewProducers.setTypeface(text);
        cantidadProductor = (TextView) rootView.findViewById(R.id.textViewCantidadProductores);
        cantidadProductor.setTypeface(text);
        nohayProductores = (TextView) rootView.findViewById(R.id.textoInfoProductores);
        nohayProductores.setTypeface(editText);
        nohayProductores.setVisibility(View.GONE);

        producerProgress = (ProgressBar) rootView.findViewById(R.id.producerProgress);

        pinto = false;

        listaBaseDatos();
        listView();
        clickSobreItem();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void listaBaseDatos(){
        // Lee los datos de los productos para obtener los productores que ofrecen dicho producto
        products.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    if (product.getNombreProducto().equals(nombreDelProducto)) {
                        producerProgress.setVisibility(View.GONE);
                        myProducers.add(postSnapshot.getValue(Product.class));
                        cantidadProductor.setText(" " + myProducers.size());

                        pinto = true;

                        // We notify the data model is changed
                        adapter.notifyDataSetChanged();
                    } else {
                        producerProgress.setVisibility(View.GONE);
                    }

                    if (pinto) {
                        nohayProductores.setVisibility(View.GONE);
                    } else {
                        nohayProductores.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void listView() {
        adapter = new MyListAdapter();
        ListView list = (ListView) rootView.findViewById(R.id.producersListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void clickSobreItem() {
        ListView list = (ListView) rootView.findViewById(R.id.producersListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedProducer = myProducers.get(position);

                if (clickedProducer.getCantidad() == 0.0) {
                    Snackbar snackbar = Snackbar
                            .make(view, clickedProducer.getProductor() + " no tiene cantidad disponible de " + clickedProducer.getNombreProducto(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("nombreProducto", nombreDelProducto);
                    bundle.putString("nombreProductor", clickedProducer.getProductor());
                    bundle.putString("nombreConsumidor", nombreDelConsumidor);
                    Intent i = new Intent(getActivity(), VerDetalleProductoProductor.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<Product> {
        public MyListAdapter(){
            super(getActivity(), R.layout.producers_view, myProducers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Se asegura que existe un View con el que se pueda trabajar
            View productsView = convertView;
            if (productsView == null) {
                productsView = getActivity().getLayoutInflater().inflate(R.layout.producers_view, parent, false);
            }

            //Encontrar el productor que hace el producto
            Product currentProductProducer = myProducers.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) productsView.findViewById(R.id.imageProducer);
            if (!currentProductProducer.getImagenProductor().equals("")) {
                String imagenConsumerComment = currentProductProducer.getImagenProductor();
                Bitmap imagenConsumidor = StringToBitMap(imagenConsumerComment);
                imageView.setImageBitmap(imagenConsumidor);
            } else {
                imageView.setImageResource(R.drawable.no_image_profile);
            }

            //Nombre:
            TextView nombreProductor = (TextView) productsView.findViewById(R.id.textNameProducer);
            nombreProductor.setTypeface(text);
            nombreProductor.setText(currentProductProducer.getProductor());

            //Calificación:
            TextView textViewCalificacion = (TextView) productsView.findViewById(R.id.textViewCalificacion);
            textViewCalificacion.setTypeface(editText);
            textViewCalificacion.setText("0/5");

            ImageView iconStar = (ImageView) productsView.findViewById(R.id.iconStar);

            if (currentProductProducer.getCantidad() == 0.0) {
                nombreProductor.setTextColor(Color.parseColor("#B6B6B6"));
                textViewCalificacion.setTextColor(Color.parseColor("#B6B6B6"));
                iconStar.setColorFilter(Color.parseColor("#B6B6B6"));
            } else {
                nombreProductor.setTextColor(Color.parseColor("#000000"));
                textViewCalificacion.setTextColor(Color.parseColor("#4C4C4C"));
                iconStar.setColorFilter(Color.parseColor("#FFC107"));
            }

            return productsView;
        }
    }

    private Bitmap StringToBitMap(String encodedString) {
        try {
            System.out.println("Comenzando StringToBitMap");
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            System.out.println("Retornando: " + bitmap);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
