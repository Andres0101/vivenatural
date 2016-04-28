package com.example.andres_bonilla.ensayo.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Comment;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.example.andres_bonilla.ensayo.activity.fragmentsProductor.ProductosFragment;
import com.example.andres_bonilla.ensayo.activity.fragmentsProductor.VerDetalleProductoFragment;
import com.example.andres_bonilla.ensayo.activity.fragmentsProductor.VerProductoCheck;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class VerDetalleProducto extends AppCompatActivity {

    private Firebase myRef;
    private Firebase comments;

    private Typeface message;
    private Typeface button;

    private String nombreDelProductor;
    private String nombreDelProducto;
    private String nombreConsumidor;

    private ImageView imagenProducto;

    private VerProductoCheck fragmentCuatroCheck;

    private Boolean guardeProducto;

    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_detalle_producto);

        message = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Light.ttf");

        button = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Medium.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the padding to match the Status Bar height
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        myRef = new Firebase("https://vivenatural.firebaseio.com/");
        comments = myRef.child("comments");

        guardeProducto = false;

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelProductor = getIntent().getExtras().getString("nombreProductor");
        nombreDelProducto = getIntent().getExtras().getString("nombreProducto");
        setTitle(nombreDelProducto);

        // Inicia en el fragment de VerDetalleProductoFragment
        VerDetalleProductoFragment fragmentUno = new VerDetalleProductoFragment();
        android.support.v4.app.FragmentTransaction fragmentInicio = getSupportFragmentManager().beginTransaction();
        fragmentInicio.add(R.id.container_body, fragmentUno);

        Bundle bundle = new Bundle();
        bundle.putString("nombreDelProductor", nombreDelProductor);
        bundle.putString("nombreDelProducto", nombreDelProducto);
        // set Fragmentclass Arguments
        fragmentUno.setArguments(bundle);

        fragmentInicio.commit();

        imagenProducto = (ImageView) findViewById(R.id.imageProduct);
        progress = (ProgressBar) findViewById(R.id.imageProgress);

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

                if (!fragmentCuatroCheck.getTextDescripcion().equals("") && !fragmentCuatroCheck.getTextCantidad().equals("")){
                    //Si edita un producto vuelva el dibujar el icono de regresar.
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    guardeProducto = true; //Booleano para validar en la acción de goBack.

                    //Agrega el texto de descripción al productor(Base de datos)
                    Firebase textoDescripcion = myRef.child("products").child(nombreDelProductor+": "+nombreDelProducto);
                    Map<String, Object> descripcion = new HashMap<>();
                    descripcion.put("descripcionProducto", fragmentCuatroCheck.getTextDescripcion());
                    descripcion.put("cantidad", fragmentCuatroCheck.getTextCantidad());
                    textoDescripcion.updateChildren(descripcion);

                    // "Borra" imagen del producto.
                    imagenProducto.setVisibility(View.GONE);

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

            case R.id.action_delete:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(VerDetalleProducto.this);
                // Setting Dialog Title
                builder1.setTitle("Eliminar");
                builder1.setIcon(R.drawable.ic_delete);
                builder1.setMessage("¿Estás seguro que deseas elminar el producto " + nombreDelProducto + "?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Eliminar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                comments.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                            Comment comment = postSnapshot.getValue(Comment.class);

                                            if (comment.getDirigidoA().equals(nombreDelProductor) && comment.getProductoComentado().equals(nombreDelProducto)) {
                                                System.out.println("Si entro a buscar en la base de datos");
                                                nombreConsumidor = comment.getHechoPor();
                                                System.out.println("Y este fue el consumidor que hizo el comentario: " + nombreConsumidor);

                                                //Elimina los comentarios que tiene el producto a eliminar
                                                Firebase deleteComments = myRef.child("comments").child(nombreConsumidor + ": " + nombreDelProducto + " de " + nombreDelProductor);
                                                deleteComments.removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                    }
                                });

                                //Elimina el producto
                                Firebase deleteProduct = myRef.child("products").child(nombreDelProductor + ": " + nombreDelProducto);
                                deleteProduct.removeValue();

                                Toast.makeText(VerDetalleProducto.this, "El producto " + nombreDelProducto + " fue eliminado con éxito!", Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }
                        });

                builder1.setNegativeButton(
                        "Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                Dialog alert11 = builder1.create();
                alert11.show();

                //Change message text font
                TextView content = ((TextView) alert11.findViewById(android.R.id.message));
                content.setTypeface(message);
                //Change dialog button text font
                TextView textButtonUno = ((TextView) alert11.findViewById(android.R.id.button1));
                textButtonUno.setTypeface(button);
                TextView textButtonDos = ((TextView) alert11.findViewById(android.R.id.button2));
                textButtonDos.setTypeface(button);
                //Change dialog icon color
                ImageView icon = ((ImageView) alert11.findViewById(android.R.id.icon));
                int color = Color.parseColor("#B6B6B6");
                icon.setColorFilter(color);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
