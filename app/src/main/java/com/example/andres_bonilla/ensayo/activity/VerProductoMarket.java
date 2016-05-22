package com.example.andres_bonilla.ensayo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.MarketProduct;
import com.example.andres_bonilla.ensayo.activity.fragmentsConsumidor.VerProductoMarketFragment;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class VerProductoMarket extends AppCompatActivity {

    private String nombreDelProducto;

    private ImageView imagenProducto;

    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_detalle_producto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the padding to match the Status Bar height
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        Firebase myRef = new Firebase("https://vivenatural.firebaseio.com/");

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelProducto = getIntent().getExtras().getString("nombreProducto");
        String nombreDelConsumidor = getIntent().getExtras().getString("nombreDelConsumidor");
        setTitle(nombreDelProducto);

        // Inicia en el fragment de VerProductoMarketFragment
        VerProductoMarketFragment fragmentUno = new VerProductoMarketFragment();
        android.support.v4.app.FragmentTransaction fragmentInicio = getSupportFragmentManager().beginTransaction();
        fragmentInicio.add(R.id.container_body, fragmentUno);

        Bundle bundle = new Bundle();
        bundle.putString("nombreProducto", nombreDelProducto);
        bundle.putString("nombreDelConsumidor", nombreDelConsumidor);
        // set Fragmentclass Arguments
        fragmentUno.setArguments(bundle);

        fragmentInicio.commit();

        imagenProducto = (ImageView) findViewById(R.id.imageProduct);
        progress = (ProgressBar) findViewById(R.id.imageProgress);

        // Lee los datos de los productos
        Firebase productosMercado = myRef.child("marketProducts");
        productosMercado.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MarketProduct marketProduct = postSnapshot.getValue(MarketProduct.class);

                    if (marketProduct.getNombre().equals(nombreDelProducto)) {
                        String imageProduct = marketProduct.getImagen();
                        Bitmap imagenBitmap = StringToBitMap(imageProduct);
                        imagenProducto.setImageBitmap(imagenBitmap);

                        progress.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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
