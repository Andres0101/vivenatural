package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.MarketProduct;
import com.example.andres_bonilla.ensayo.activity.classes.Reserve;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TodosProductos extends Fragment {

    private View rootView;

    private Firebase marketProducts;
    private Firebase reserves;

    MyListAdapter adapter;

    private List<MarketProduct> marketProductList = new ArrayList<>();

    private ArrayList<String> myListMarketProduct = new ArrayList<>();
    private ArrayList<Double> myListMarketProductCantidad = new ArrayList<>();

    private TextView textoNoHay;

    private Typeface texto;
    private Typeface regular;

    private ProgressBar progress;

    private Boolean pinto;
    private Boolean yaAgrego;

    public TodosProductos() {
        // Required empty public constructor

        Firebase myRef = new Firebase("https://vivenatural.firebaseio.com/");
        marketProducts = myRef.child("marketProducts");
        reserves = myRef.child("reserves");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.misproductos_fragment, container, false);

        texto = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        regular = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");

        textoNoHay = (TextView) rootView.findViewById(R.id.textoInfoProductos);
        textoNoHay.setTypeface(texto);

        progress = (ProgressBar) rootView.findViewById(R.id.productProgress);
        textoNoHay.setVisibility(View.GONE);

        pinto = false;
        yaAgrego = false;

        listaBaseDatos();
        listView();

        // Inflate the layout for this fragment
        return rootView;
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

    private void listaBaseDatos(){
        // Lee los datos de los productos del mercado
        marketProducts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MarketProduct marketProduct = postSnapshot.getValue(MarketProduct.class);
                    progress.setVisibility(View.GONE);

                    marketProductList.add(postSnapshot.getValue(MarketProduct.class));
                    pinto = true;

                    myListMarketProduct.add(marketProduct.getNombre());

                    // We notify the data model is changed
                    adapter.notifyDataSetChanged();

                    if (pinto) {
                        textoNoHay.setVisibility(View.GONE);
                    } else {
                        textoNoHay.setVisibility(View.VISIBLE);
                    }
                }

                for (int i = 0; i < myListMarketProduct.size(); i++) {
                    // Lee los datos de las reservas
                    final int finalI = i;
                    reserves.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            double sumaCantidad = 0;
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Reserve reserve = postSnapshot.getValue(Reserve.class);

                                if (reserve.getProducto().equals(myListMarketProduct.get(finalI))) {
                                    sumaCantidad = sumaCantidad + reserve.getCantidadReservada();
                                    System.out.println("Producto: " + reserve.getProducto() + " Cantidad: " + reserve.getCantidadReservada());
                                }
                            }
                            System.out.println(sumaCantidad);
                            //Agregar al arraList
                            myListMarketProductCantidad.add(sumaCantidad);
                            System.out.println("------------");

                            System.out.println("Como van las apuestas " + myListMarketProductCantidad.size() + "/" + myListMarketProduct.size());

                            if (myListMarketProductCantidad.size() == myListMarketProduct.size()) {
                                yaAgrego = true;
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void listView() {
        adapter = new MyListAdapter();
        ListView list = (ListView) rootView.findViewById(R.id.productsListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private class MyListAdapter extends ArrayAdapter<MarketProduct> {
        public MyListAdapter(){
            super(getActivity(), R.layout.mis_productos_view, marketProductList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Se asegura que existe un View con el que se pueda trabajar
            View productsView = convertView;
            if (productsView == null) {
                productsView = getActivity().getLayoutInflater().inflate(R.layout.mis_productos_view, parent, false);
            }

            //Encontrar productos reservados
            final MarketProduct currentMarketProduct = marketProductList.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) productsView.findViewById(R.id.imageProduct);
            String imageProduct = currentMarketProduct.getImagen();
            Bitmap imagenProducto = StringToBitMap(imageProduct);
            imageView.setImageBitmap(imagenProducto);

            //Nombre:
            TextView nombreProducto = (TextView) productsView.findViewById(R.id.textNameProduct);
            nombreProducto.setTypeface(regular);
            nombreProducto.setText(currentMarketProduct.getNombre());

            //Cantidad:
            TextView textCantidad = (TextView) productsView.findViewById(R.id.textCantidad);
            textCantidad.setTypeface(texto);

            if (yaAgrego) {
                System.out.println("Que paso?");
                System.out.println("El size: " + myListMarketProductCantidad.size());
                textCantidad.setText(myListMarketProductCantidad.get(position) + " lb");
            }

            return productsView;
        }
    }
}
