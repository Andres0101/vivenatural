package com.example.andres_bonilla.ensayo.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Comment;
import com.example.andres_bonilla.ensayo.activity.classes.MarketProduct;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.example.andres_bonilla.ensayo.activity.classes.Reserve;
import com.example.andres_bonilla.ensayo.activity.classes.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerDetalleProductoProductor extends AppCompatActivity {

    private Firebase myRef;
    private Firebase comments;
    private Firebase products;

    private Typeface editText;
    private Typeface infoName;
    private Typeface text;

    private String nombreDelConsumidor;
    private String nombreDelProductor;
    private String nombreDelProducto;
    private String imageConsumidor;

    private int precioProducto;
    private String stringImagenFirebase;

    private ImageView imagenProducto;
    private ImageView imageConsumer;
    private EditText descripcionProducto;
    private EditText cantidadDisponible;
    private TextView cantidadComentario;
    private TextView nohayComentarios;
    private EditText agregarComentario;

    private EditText productCant;

    private ImageView buttonSend;

    MyListAdapter adapter;

    private List<Comment> myComments = new ArrayList<>();

    private Boolean reservo;

    private String fechaReserva;
    private String hora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_detalle_producto_productor);

        editText = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Light.ttf");

        text = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Regular.ttf");

        infoName = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Medium.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRef = new Firebase("https://vivenatural.firebaseio.com/");
        Firebase usuarios = myRef.child("users");
        comments = myRef.child("comments");
        products = myRef.child("products");

        reservo = false;

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

                //Vacia el editText
                agregarComentario.setText("");
                //Esconde el teclado
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(buttonSend.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);

                listaBaseDatos();
            }
        });

        // Lee los datos de los productos
        products.addListenerForSingleValueEvent(new ValueEventListener() {
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
        //Pinta la imagen del consumidor en la parte de agregar comentario
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);

                    if (user.getNombre().equals(nombreDelConsumidor)) {
                        if (!user.getImagen().equals("")) {
                            imageConsumidor = user.getImagen();
                            Bitmap imagenBitmap = StringToBitMap(imageConsumidor);
                            imageConsumer.setImageBitmap(imagenBitmap);
                        } else {
                            Bitmap imagenBitmapDefault = BitmapFactory.decodeResource(getResources(), R.drawable.ic_no_profile_image);
                            imageConsumidor = BitMapToString(imagenBitmapDefault);
                            imageConsumer.setImageResource(R.drawable.ic_no_profile_image);
                        }
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
            Comment currentComment = myComments.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) productsView.findViewById(R.id.imageConsumer);
            if (!currentComment.getImagenConsumidor().equals("")) {
                String imagenConsumerComment = currentComment.getImagenConsumidor();
                Bitmap imagenConsumidor = StringToBitMap(imagenConsumerComment);
                imageView.setImageBitmap(imagenConsumidor);
            } else {
                imageView.setImageResource(R.drawable.ic_no_profile_image);
            }

            //Nombre:
            TextView nombreConsumidor = (TextView) productsView.findViewById(R.id.textNameConsumer);
            nombreConsumidor.setTypeface(infoName);
            //Si el consumidor que puso el comentario es el mismo que está en sesión, entonces...
            if (currentComment.getHechoPor().equals(nombreDelConsumidor)) {
                nombreConsumidor.setText("Tú");
            } else {
                nombreConsumidor.setText(currentComment.getHechoPor());
            }

            //Comentario:
            TextView textoComentario = (TextView) productsView.findViewById(R.id.textViewComentario);
            textoComentario.setTypeface(editText);
            textoComentario.setText(currentComment.getComentario());

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

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                /*if (reservo) {
                    Intent intent = new Intent(VerDetalleProductoProductor.this, VerDetalleProductor.class);
                    intent.putExtra("nombreProductor", nombreDelProductor);
                    intent.putExtra("nombreConsumidor", nombreDelConsumidor);
                    startActivity(intent);
                    finish();
                } else {*/
                    super.onBackPressed();
                //}
                return true;

            case R.id.action_reservar:

                final Dialog d = new Dialog(VerDetalleProductoProductor.this);

                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setContentView(R.layout.productos_reservar);
                d.setCancelable(true);

                TextView textViewDialog = (TextView) d.findViewById(R.id.textViewDialog);
                textViewDialog.setTypeface(infoName);
                productCant = (EditText) d.findViewById(R.id.editTextCant);
                productCant.setTypeface(editText);

                // Lee los datos de los productos del mercado
                Firebase marketProducts = myRef.child("marketProducts");
                marketProducts.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            MarketProduct marketProduct = postSnapshot.getValue(MarketProduct.class);

                            if (marketProduct.getNombre().equals(nombreDelProducto)) {
                                stringImagenFirebase = marketProduct.getImagen();
                                precioProducto = marketProduct.getPrecio();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                Calendar c = Calendar.getInstance();
                int day = c.get(Calendar.DATE);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minutes = c.get(Calendar.MINUTE);

                fechaReserva = month + "/" + day + "/" + year;
                hora = hour + ":" + minutes;

                Button reservar = (Button) d.findViewById(R.id.done);
                reservar.setTypeface(text);
                reservar.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // Dialogo de espera
                        final ProgressDialog dlg = new ProgressDialog(VerDetalleProductoProductor.this);
                        dlg.setMessage("Realizando reserva. Por favor espere.");
                        dlg.show();

                        if (!productCant.getText().toString().equals("")) { //Verifica si el campo no está vacio
                            products.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                        Product product = postSnapshot.getValue(Product.class);

                                        if (product.getProductor().equals(nombreDelProductor) && product.getNombreProducto().equals(nombreDelProducto)) {
                                            //Si la cantidad a reservar es menor o igual a la que tiene el producto en la base de datos
                                            // puede reservar
                                            if (Double.parseDouble(productCant.getText().toString()) <= product.getCantidad()) {
                                                //Resta lo que el usuario reserva con la cantidad que el productor tiene del producto
                                                Double cantidadDigitada = Double.parseDouble(productCant.getText().toString());
                                                Double cantidad = product.getCantidad()-cantidadDigitada;

                                                //Actualiza la cantidad del producto en la base de datos
                                                Firebase cantidadQueQueda = myRef.child("products").child(nombreDelProductor + ": " + nombreDelProducto);
                                                Map<String, Object> cantidadRestante = new HashMap<>();
                                                cantidadRestante.put("cantidad", cantidad);
                                                cantidadQueQueda.updateChildren(cantidadRestante);

                                                // Agrega reserva a la base de datos
                                                Firebase reserve = myRef.child("reserves").child(nombreDelConsumidor + ": " + nombreDelProducto + " de " + nombreDelProductor);
                                                Reserve newReserve = new Reserve(nombreDelProducto, nombreDelConsumidor, nombreDelProductor, stringImagenFirebase, cantidadDigitada, precioProducto, fechaReserva, hora);
                                                reserve.setValue(newReserve);

                                                // Lee los datos de los productos para actualizar cantidad
                                                products.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot snapshot) {
                                                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                            Product product = postSnapshot.getValue(Product.class);

                                                            if (product.getNombreProducto().equals(nombreDelProducto) && product.getProductor().equals(nombreDelProductor)) {
                                                                cantidadDisponible.setText(" " + product.getCantidad() + " lb");
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(FirebaseError firebaseError) {
                                                    }
                                                });

                                                reservo = true;
                                                dlg.dismiss();

                                                Toast.makeText(VerDetalleProductoProductor.this, "Has reservado " + cantidadDigitada + " lb de " + nombreDelProducto, Toast.LENGTH_SHORT).show();
                                                d.dismiss();
                                            } else { //De lo contrario se le advierte
                                                productCant.setText("");
                                                Toast.makeText(VerDetalleProductoProductor.this, "La cantidad máxima que puedes reservar es " + product.getCantidad() + " lb", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }
                            });
                        } else {
                            Toast.makeText(VerDetalleProductoProductor.this, "Por favor indica la cantidad que quieres reservar.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Button cancel = (Button) d.findViewById(R.id.cancel);
                cancel.setTypeface(text);
                cancel.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        d.dismiss();
                    }
                });

                d.show();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
