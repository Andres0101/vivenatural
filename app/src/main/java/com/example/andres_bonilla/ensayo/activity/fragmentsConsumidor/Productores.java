package com.example.andres_bonilla.ensayo.activity.fragmentsConsumidor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.example.andres_bonilla.ensayo.activity.VerDetalleProductor;
import com.example.andres_bonilla.ensayo.activity.classes.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Productores extends Fragment {

    private View rootView;
    private Firebase myRef;

    private String nombreDelConsumidor;

    MyListAdapter adapter;

    private TextView textoNoHay;

    private User clickedProducer;
    private List<User> productores = new ArrayList<>();

    private ProgressBar progress;

    private Boolean pinto;

    public Productores() {
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
        rootView = inflater.inflate(R.layout.fragment_productores, container, false);
        setHasOptionsMenu(true);

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelConsumidor = getArguments().getString("nombreDelConsumidor");

        textoNoHay = (TextView) rootView.findViewById(R.id.textoInfoProductores);
        textoNoHay.setVisibility(View.GONE);

        progress = (ProgressBar) rootView.findViewById(R.id.progress);

        pinto = false;

        listaBaseDatos();

        listView();
        clickSobreItem();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void listaBaseDatos(){
        // Lee los datos de los productos
        Firebase usuarios = myRef.child("users");
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);

                    //Si el nombre del productor coincide con el que inicio sesión entonces...
                    if (user.getRol().equals("Productor")) {
                        progress.setVisibility(View.GONE);
                        productores.add(postSnapshot.getValue(User.class));
                        pinto = true;

                        // We notify the data model is changed
                        adapter.notifyDataSetChanged();
                    } else {
                        progress.setVisibility(View.GONE);
                    }

                    if (pinto) {
                        textoNoHay.setVisibility(View.GONE);
                    } else {
                        textoNoHay.setVisibility(View.VISIBLE);
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

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void clickSobreItem() {
        ListView list = (ListView) rootView.findViewById(R.id.producersListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedProducer = productores.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("nombreProductor", clickedProducer.getNombre());
                bundle.putString("nombreConsumidor", nombreDelConsumidor);
                Intent i = new Intent(getActivity(), VerDetalleProductor.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<User> {
        public MyListAdapter(){
            super(getActivity(), R.layout.producers_view, productores);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Se asegura que existe un View con el que se pueda trabajar
            View producersView = convertView;
            if (producersView == null) {
                producersView = getActivity().getLayoutInflater().inflate(R.layout.producers_view, parent, false);
            }

            //Encontrar el producto
            User currentProducer = productores.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) producersView.findViewById(R.id.imageProducer);
            if (!currentProducer.getImagen().equals("")) {
                String imageProduct = currentProducer.getImagen();
                Bitmap imagenProducto = StringToBitMap(imageProduct);
                imageView.setImageBitmap(imagenProducto);
            } else {
                imageView.setImageResource(R.drawable.ic_no_profile_image);
            }

            //Nombre:
            TextView nombreProductor = (TextView) producersView.findViewById(R.id.textNameProducer);
            nombreProductor.setText(currentProducer.getNombre());

            return producersView;
        }
    }
}
