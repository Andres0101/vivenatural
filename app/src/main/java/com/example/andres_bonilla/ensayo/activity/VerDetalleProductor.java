package com.example.andres_bonilla.ensayo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.example.andres_bonilla.ensayo.activity.classes.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VerDetalleProductor extends AppCompatActivity {

    private Firebase products;

    private Product clickedProduct;

    private String nombreDelProductor;
    private String nombreDelConsumidor;

    private ImageView imagenProductor;
    private EditText descripcionProductor;
    private TextView cantidadProducto;
    private TextView nohayProductos;

    MyListAdapter adapter;

    private Typeface editText;
    private Typeface infoName;

    private List<Product> myProducts = new ArrayList<>();

    private ProgressBar imageProgress;
    private ProgressBar descriptionProgress;
    private ProgressBar productProgress;

    private Boolean pinto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_detalle_productor);

        editText = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Light.ttf");

        Typeface textCantidad = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Regular.ttf");

        infoName = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Medium.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Firebase myRef = new Firebase("https://vivenatural.firebaseio.com/");
        products = myRef.child("products");

        productProgress = (ProgressBar) findViewById(R.id.productProgress);
        descriptionProgress = (ProgressBar) findViewById(R.id.descriptionProgress);
        imageProgress = (ProgressBar) findViewById(R.id.imageProgress);

        pinto = false;

        listaBaseDatos();
        listView();
        clickSobreItem();

        // Obtiene el nombre de la persona que inicia sesión y la del productor del
        // producto a quien el usuario le dio click.
        nombreDelConsumidor = getIntent().getExtras().getString("nombreConsumidor");
        nombreDelProductor = getIntent().getExtras().getString("nombreProductor");
        setTitle(nombreDelProductor);

        imagenProductor = (ImageView) findViewById(R.id.imageProducer);
        TextView textCantidadProductos = (TextView) findViewById(R.id.textViewProducts);
        textCantidadProductos.setTypeface(textCantidad);
        cantidadProducto = (TextView) findViewById(R.id.textViewCantidadProductos);
        cantidadProducto.setTypeface(textCantidad);
        nohayProductos = (TextView) findViewById(R.id.textoInfoProductos);
        nohayProductos.setTypeface(editText);
        nohayProductos.setVisibility(View.GONE);

        descripcionProductor = (EditText) findViewById(R.id.editTextDescriProducer);
        descripcionProductor.setTypeface(editText);
        descripcionProductor.setBackground(null);

        // Lee los datos de los productos
        Firebase users = myRef.child("users");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);

                    if (user.getNombre().equals(nombreDelProductor)) {
                        if (!user.getImagen().equals("")) {String imageProduct = user.getImagen();
                            Bitmap imagenBitmap = StringToBitMap(imageProduct);
                            imagenProductor.setImageBitmap(imagenBitmap);

                            imageProgress.setVisibility(View.GONE);
                        } else {
                            imageProgress.setVisibility(View.GONE);
                            imagenProductor.setImageResource(R.drawable.ic_no_profile_image);
                        }

                        descriptionProgress.setVisibility(View.GONE);

                        if (!user.getDescripcion().equals("")) {
                            descripcionProductor.setText(user.getDescripcion());
                        } else {
                            descripcionProductor.setText(nombreDelProductor + " no tiene descripción.");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
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

    private void listaBaseDatos(){
        // Lee los datos de los productos
        products.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    if (product.getProductor().equals(nombreDelProductor)) {
                        productProgress.setVisibility(View.GONE);

                        myProducts.add(postSnapshot.getValue(Product.class));
                        cantidadProducto.setText(" " + myProducts.size());

                        pinto = true;

                        // We notify the data model is changed
                        adapter.notifyDataSetChanged();
                    } else {
                        productProgress.setVisibility(View.GONE);
                    }

                    if (pinto) {
                        nohayProductos.setVisibility(View.GONE);
                    } else {
                        nohayProductos.setVisibility(View.VISIBLE);
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

    private void clickSobreItem() {
        ListView list = (ListView) findViewById(R.id.productsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedProduct = myProducts.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("nombreProducto", clickedProduct.getNombreProducto());
                bundle.putString("nombreProductor", nombreDelProductor);
                bundle.putString("nombreConsumidor", nombreDelConsumidor);
                Intent i = new Intent(VerDetalleProductor.this, VerDetalleProductoProductor.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<Product> {
        public MyListAdapter(){
            super(VerDetalleProductor.this, R.layout.products_view, myProducts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Se asegura que existe un View con el que se pueda trabajar
            View productsView = convertView;
            if (productsView == null) {
                productsView = VerDetalleProductor.this.getLayoutInflater().inflate(R.layout.products_view, parent, false);
            }

            //Encontrar el producto
            Product currentProduct = myProducts.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) productsView.findViewById(R.id.imageProduct);
            String imageProduct = currentProduct.getImagen();
            Bitmap imagenProducto = StringToBitMap(imageProduct);
            imageView.setImageBitmap(imagenProducto);

            //Nombre:
            TextView nombreProducto = (TextView) productsView.findViewById(R.id.textNameProduct);
            nombreProducto.setTypeface(infoName);
            nombreProducto.setText(currentProduct.getNombreProducto());

            //Precio:
            TextView priceProducto = (TextView) productsView.findViewById(R.id.textPrecio);
            priceProducto.setTypeface(editText);
            priceProducto.setText("$" + currentProduct.getPrecio());

            //Cantidad:
            TextView textViewCantidad = (TextView) productsView.findViewById(R.id.textViewCantidad);
            textViewCantidad.setTypeface(editText);
            TextView cantidadProducto = (TextView) productsView.findViewById(R.id.textCantidad);
            cantidadProducto.setTypeface(editText);
            priceProducto.setTypeface(editText);
            cantidadProducto.setText(currentProduct.getCantidad() + " lb");

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
