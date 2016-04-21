package com.example.andres_bonilla.ensayo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.example.andres_bonilla.ensayo.activity.classes.Reserve;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VerReservaNotification extends AppCompatActivity {

    private Firebase productosReservados;

    MyListAdapter adapter;

    private List<Reserve> myReserves = new ArrayList<>();

    private Product clickedProduct;

    private TextView textoNoHay;

    private String nombreDelProductor;

    private Typeface texto;
    private Typeface infoName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_reserva_notification);

        texto = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Light.ttf");

        infoName = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Medium.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.title_reservas);

        Firebase myRef = new Firebase("https://vivenatural.firebaseio.com/");
        productosReservados = myRef.child("reserves");

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelProductor = getIntent().getExtras().getString("nombreDelProductor");

        textoNoHay = (TextView) findViewById(R.id.textoInfoProductos);
        textoNoHay.setTypeface(texto);

        listaBaseDatos();
        listView();
        //clickSobreItem();

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
        // Lee los datos de los productos
        productosReservados.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Reserve reservedProducts = postSnapshot.getValue(Reserve.class);

                    //Si el consumidor que reservó el producto coincide con el que inicio sesión entonces...
                    if (reservedProducts.getReservadoA().equals(nombreDelProductor)) {
                        textoNoHay.setVisibility(View.GONE);

                        myReserves.add(postSnapshot.getValue(Reserve.class));

                        // We notify the data model is changed
                        adapter.notifyDataSetChanged();
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
        ListView list = (ListView) findViewById(R.id.productsListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /*private void clickSobreItem() {
        ListView list = (ListView) rootView.findViewById(R.id.productsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedProduct = myProducts.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("nombreProducto", clickedProduct.getNombreProducto());
                bundle.putString("nombreProductor", nombreDelProductor);
                Intent i = new Intent(getActivity(), VerDetalleProducto.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }*/

    private class MyListAdapter extends ArrayAdapter<Reserve> {
        public MyListAdapter(){
            super(VerReservaNotification.this, R.layout.products_reserved_producer_view, myReserves);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Se asegura que existe un View con el que se pueda trabajar
            View productsView = convertView;
            if (productsView == null) {
                productsView = VerReservaNotification.this.getLayoutInflater().inflate(R.layout.products_reserved_producer_view, parent, false);
            }

            //Encontrar productos reservados
            Reserve currentProductReserve = myReserves.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) productsView.findViewById(R.id.imageProduct);
            String imageProduct = currentProductReserve.getImagenProducto();
            Bitmap imagenProducto = StringToBitMap(imageProduct);
            imageView.setImageBitmap(imagenProducto);

            //Nombre:
            TextView nombreProducto = (TextView) productsView.findViewById(R.id.textNameProduct);
            nombreProducto.setTypeface(infoName);
            nombreProducto.setText(currentProductReserve.getProducto());

            //Cantidad reservada:
            TextView reservedQuantity = (TextView) productsView.findViewById(R.id.textCantidad);
            reservedQuantity.setTypeface(texto);
            reservedQuantity.setText(currentProductReserve.getCantidadReservada() + " lb");

            //Cantidad:
            TextView textViewReserve = (TextView) productsView.findViewById(R.id.textViewReserve);
            textViewReserve.setTypeface(texto);
            TextView reservedBy = (TextView) productsView.findViewById(R.id.textNameConsumer);
            reservedBy.setTypeface(texto);
            reservedBy.setText(" " + currentProductReserve.getReservadoPor());

            return productsView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
