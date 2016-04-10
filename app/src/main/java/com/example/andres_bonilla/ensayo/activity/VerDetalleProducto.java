package com.example.andres_bonilla.ensayo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.example.andres_bonilla.ensayo.activity.fragmentsProductor.ProductosFragment;
import com.example.andres_bonilla.ensayo.activity.fragmentsProductor.VerProductoCheck;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ANDRES_BONILLA on 09/04/2016.
 */
public class VerDetalleProducto extends AppCompatActivity {

    private Firebase myRef;

    private String nombreDelProductor;
    private String nombreDelProducto;

    private ImageView imagenProducto;
    private EditText descripcionProducto;
    private EditText cantidadDisponible;

    private VerProductoCheck fragmentCuatroCheck;

    private Boolean guardeProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_detalle_producto);

        Typeface editText = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Light.ttf");

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
        nombreDelProducto = getIntent().getExtras().getString("nombreProducto");
        setTitle(nombreDelProducto);

        imagenProducto = (ImageView) findViewById(R.id.imageProduct);
        TextView textDescription = (TextView) findViewById(R.id.textViewDescripcion);
        textDescription.setTypeface(textView);
        TextView textViewCantidadDisponible = (TextView) findViewById(R.id.textViewCantidadDisponible);
        textViewCantidadDisponible.setTypeface(textView);

        descripcionProducto = (EditText) findViewById(R.id.editTextDescriProduct);
        descripcionProducto.setTypeface(editText);
        descripcionProducto.setBackground(null);
        cantidadDisponible = (EditText) findViewById(R.id.editTextCantidadDisponible);
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

                        String imageProduct = product.getImagen();
                        Bitmap imagenBitmap = StringToBitMap(imageProduct);
                        imagenProducto.setImageBitmap(imagenBitmap);

                        descripcionProducto.setText(product.getDescripcionProducto());
                        cantidadDisponible.setText(" " + product.getCantidad() + " lb");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editar_producto, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VerDetalleProducto.this, HomeProductor.class);
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

            case R.id.action_edit_product:
                // User chose the "Settings" item, show the app settings UI...
                fragmentCuatroCheck = new VerProductoCheck();
                fragmentCuatroCheck.setEditText(true);
                fragmentCuatroCheck.setEditTextCant(true);
                android.support.v4.app.FragmentTransaction fragmentTransactionCuatroCheck = getSupportFragmentManager().beginTransaction();
                fragmentTransactionCuatroCheck.replace(R.id.container_body, fragmentCuatroCheck);

                fragmentTransactionCuatroCheck.commit();
                return true;

            case R.id.action_save_product:
                //Si edita un producto vuelva el dibujar el icono de regresar.
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                guardeProducto = true; //Booleano para validar en la acción de goBack.

                if (!fragmentCuatroCheck.getTextDescripcion().equals("") && !fragmentCuatroCheck.getTextCantidad().equals("")){

                    //Agrega el texto de descripción al productor(Base de datos)
                    Firebase textoDescripcion = myRef.child("products").child(nombreDelProductor+": "+nombreDelProducto);
                    Map<String, Object> descripcion = new HashMap<>();
                    descripcion.put("descripcionProducto", fragmentCuatroCheck.getTextDescripcion());
                    descripcion.put("cantidad", fragmentCuatroCheck.getTextCantidad());
                    textoDescripcion.updateChildren(descripcion);

                    ProductosFragment fragmentCuatro = new ProductosFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransactionCuatro = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionCuatro.replace(R.id.container_body, fragmentCuatro);

                    Bundle bundle = new Bundle();
                    bundle.putString("nombreDelProductor", nombreDelProductor);
                    // set Fragmentclass Arguments
                    fragmentCuatro.setArguments(bundle);

                    //Setea el nombre del label
                    setTitle(R.string.title_products);

                    fragmentTransactionCuatro.commit();
                    Toast.makeText(getApplicationContext(), "Cambios realizados con éxito!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Debes llenar el campo.", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
