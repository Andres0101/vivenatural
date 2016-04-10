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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.example.andres_bonilla.ensayo.activity.classes.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by ANDRES_BONILLA on 10/04/2016.
 */
public class VerDetalleProductor extends AppCompatActivity {

    private Firebase myRef;

    private String nombreDelProductor;

    private ImageView imagenProductor;
    private EditText descripcionProductor;

    private Boolean guardeProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_detalle_productor);

        Typeface editText = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Light.ttf");

        Typeface textCantidad = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Regular.ttf");

        Typeface textView = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Bold.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRef = new Firebase("https://vivenatural.firebaseio.com/");

        guardeProducto = false;

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelProductor = getIntent().getExtras().getString("nombreProductor");
        setTitle(nombreDelProductor);

        imagenProductor = (ImageView) findViewById(R.id.imageProducer);
        TextView textDescription = (TextView) findViewById(R.id.textViewDescripcion);
        textDescription.setTypeface(textView);
        TextView textCantidadProductos = (TextView) findViewById(R.id.textViewCantidadProductos);
        textCantidadProductos.setTypeface(textCantidad);

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

                        String imageProduct = user.getImagen();
                        Bitmap imagenBitmap = StringToBitMap(imageProduct);
                        imagenProductor.setImageBitmap(imagenBitmap);

                        descripcionProductor.setText(user.getDescripcion());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        Firebase products = myRef.child("products");
        products.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    if (snapshot.hasChild(nombreDelProductor + ": " + product.getNombreProducto())){
                        System.out.println(nombreDelProductor + " tiene: " + snapshot.getChildrenCount() + " productos");
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VerDetalleProductor.this, HomeConsumidor.class);
        intent.putExtra("NombreUsuario", nombreDelProductor);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                if (guardeProducto) { //Si editó y guardó los datos del producto, entonces se regresa al perfil
                    onBackPressed();
                } else { //De lo contrario se regresa al frament anterior.
                    super.onBackPressed();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
