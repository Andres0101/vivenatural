package com.example.andres_bonilla.ensayo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Comment;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.example.andres_bonilla.ensayo.activity.classes.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ANDRES_BONILLA on 11/04/2016.
 */
public class VerDetalleProductoProductor extends AppCompatActivity {

    private Firebase myRef;
    private Firebase comments;
    private Firebase usuarios;

    private Typeface editText;
    private Typeface infoName;

    private String nombreDelConsumidor;
    private String nombreDelProductor;
    private String nombreDelProducto;
    private String imageConsumidor;

    private ImageView imagenProducto;
    private ImageView imageConsumer;
    private EditText descripcionProducto;
    private EditText cantidadDisponible;
    private TextView cantidadComentario;
    private TextView nohayComentarios;
    private EditText agregarComentario;

    private ImageView buttonSend;

    MyListAdapter adapter;

    private List<Comment> myComments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_detalle_producto_productor);

        editText = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Light.ttf");

        Typeface text = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Regular.ttf");

        infoName = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Medium.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRef = new Firebase("https://vivenatural.firebaseio.com/");
        usuarios = myRef.child("users");
        comments = myRef.child("comments");

        listaBaseDatos();
        listView();

        // Obtiene el nombre de la persona que inicia sesión, la del productor a
        // quien el usuario entro a ver sus productos y la del producto del productor
        // al que el usuario le dio click.
        nombreDelConsumidor = getIntent().getExtras().getString("nombreConsumidor");
        nombreDelProductor = getIntent().getExtras().getString("nombreProductor");
        nombreDelProducto = getIntent().getExtras().getString("nombreProducto");
        setTitle(nombreDelProducto);

        buttonSend = (ImageView) findViewById(R.id.addButton);

        imagenProducto = (ImageView) findViewById(R.id.imageProduct);
        imageConsumer = (ImageView) findViewById(R.id.profile_image);
        TextView textCantidadComentarios = (TextView) findViewById(R.id.textViewComentarios);
        textCantidadComentarios.setTypeface(text);
        cantidadComentario = (TextView) findViewById(R.id.textViewCantidadComentarios);
        cantidadComentario.setTypeface(text);

        descripcionProducto = (EditText) findViewById(R.id.editTextDescriProduct);
        descripcionProducto.setTypeface(editText);
        descripcionProducto.setBackground(null);
        cantidadDisponible = (EditText) findViewById(R.id.editTextCantidadDisponible);
        cantidadDisponible.setTypeface(editText);
        cantidadDisponible.setBackground(null);
        agregarComentario = (EditText) findViewById(R.id.addComment);
        agregarComentario.setTypeface(editText);
        nohayComentarios = (TextView) findViewById(R.id.textoInfoComentarios);
        nohayComentarios.setTypeface(editText);

        agregarComentario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //Cuando el editText es presionado muestra la imagen del botón send.
                if (hasFocus) {
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    buttonSend.setVisibility(View.GONE);
                }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Agrega comentario a la base de datos
                //Firebase productComment = myRef.child("products").child(nombreDelProductor + ": " + nombreDelProducto).child("comments").child(nombreDelConsumidor);
                Firebase productComment = myRef.child("comments").child(nombreDelConsumidor + ": " + nombreDelProducto + " de " + nombreDelProductor);
                Comment comment = new Comment(nombreDelProductor, nombreDelConsumidor, agregarComentario.getText().toString(), nombreDelProducto, imageConsumidor);
                productComment.setValue(comment);

                agregarComentario.setText("");
            }
        });

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

        // Lee los datos de los usuarios
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);

                    if (user.getNombre().equals(nombreDelConsumidor)) {
                        imageConsumidor = user.getImagen();
                        Bitmap imagenBitmap = StringToBitMap(imageConsumidor);
                        imageConsumer.setImageBitmap(imagenBitmap);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void listaBaseDatos(){
        // Lee los datos de los productos
        comments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Comment comment = postSnapshot.getValue(Comment.class);

                    if (comment.getDirigidoA().equals(nombreDelProductor) && comment.getProductoComentado().equals(nombreDelProducto)) {
                        myComments.add(postSnapshot.getValue(Comment.class));
                        cantidadComentario.setText(" " + myComments.size());
                        nohayComentarios.setVisibility(View.GONE);

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
        ListView list = (ListView) findViewById(R.id.commentsListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private class MyListAdapter extends ArrayAdapter<Comment> {
        public MyListAdapter(){
            super(VerDetalleProductoProductor.this, R.layout.comment_view, myComments);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Se asegura que existe un View con el que se pueda trabajar
            View productsView = convertView;
            if (productsView == null) {
                productsView = VerDetalleProductoProductor.this.getLayoutInflater().inflate(R.layout.comment_view, parent, false);
            }

            //Encontrar el comentario
            Comment currentProduct = myComments.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) productsView.findViewById(R.id.imageConsumer);
            String imagenConsumerComment = currentProduct.getImagenConsumidor();
            Bitmap imagenConsumidor = StringToBitMap(imagenConsumerComment);
            imageView.setImageBitmap(imagenConsumidor);

            //Nombre:
            TextView nombreConsumidor = (TextView) productsView.findViewById(R.id.textNameConsumer);
            nombreConsumidor.setTypeface(infoName);
            //Si el consumidor que puso el comentario es el mismo que está en sesión, entonces...
            if (currentProduct.getHechoPor().equals(nombreDelConsumidor)) {
                nombreConsumidor.setText("Tú");
            } else {
                nombreConsumidor.setText(currentProduct.getHechoPor());
            }

            //Comentario:
            TextView priceProducto = (TextView) productsView.findViewById(R.id.textViewComentario);
            priceProducto.setTypeface(editText);
            priceProducto.setText(currentProduct.getComentario());

            return productsView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reservar, menu);
        return true;
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
