package com.example.andres_bonilla.ensayo.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

    private Boolean agregandoComentario;
    private Boolean puedeReservar;

    private long fechaReserva;

    private ProgressBar imageProgress;
    private ProgressBar detailsProgress;
    private ProgressBar commentProgress;

    private Boolean pinto;

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

        imageProgress = (ProgressBar) findViewById(R.id.imageProgress);
        detailsProgress = (ProgressBar) findViewById(R.id.detailsProgress);
        commentProgress = (ProgressBar) findViewById(R.id.commentProgress);

        pinto = false;

        agregandoComentario = false;
        puedeReservar = false;

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
        nohayComentarios.setVisibility(View.GONE);

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

                agregandoComentario = true;
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
                        if (product.getCantidad() > 0.0) {
                            puedeReservar = true;
                        } else {
                            puedeReservar = false;
                        }

                        String imageProduct = product.getImagen();
                        Bitmap imagenBitmap = StringToBitMap(imageProduct);
                        imagenProducto.setImageBitmap(imagenBitmap);

                        imageProgress.setVisibility(View.GONE);

                        descripcionProducto.setText(product.getDescripcionProducto());
                        cantidadDisponible.setText(" " + product.getCantidad() + " lb");

                        detailsProgress.setVisibility(View.GONE);
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
                            Bitmap imagenBitmapDefault = BitmapFactory.decodeResource(getResources(), R.drawable.no_image_profile);
                            imageConsumidor = BitMapToString(imagenBitmapDefault);
                            imageConsumer.setImageResource(R.drawable.no_image_profile);
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
        if (agregandoComentario) {
            // Dialogo de espera
            final ProgressDialog dlg = new ProgressDialog(VerDetalleProductoProductor.this);
            dlg.setMessage("Agregando comentario. Por favor espere.");
            dlg.show();
            // Lee los datos de los productos
            comments.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Comment comment = postSnapshot.getValue(Comment.class);

                        if (comment.getDirigidoA().equals(nombreDelProductor) && comment.getProductoComentado().equals(nombreDelProducto)) {
                            dlg.dismiss();
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
        } else {
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild("comments")) {
                        System.out.println("Si hay comentarios ®");
                        // Lee los datos de los comentarios
                        comments.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    Comment comment = postSnapshot.getValue(Comment.class);

                                    if (comment.getDirigidoA().equals(nombreDelProductor) && comment.getProductoComentado().equals(nombreDelProducto)) {
                                        commentProgress.setVisibility(View.GONE);

                                        myComments.add(postSnapshot.getValue(Comment.class));
                                        cantidadComentario.setText(" " + myComments.size());

                                        pinto = true;

                                        // We notify the data model is changed
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        commentProgress.setVisibility(View.GONE);
                                    }

                                    if (pinto) {
                                        nohayComentarios.setVisibility(View.GONE);
                                    } else {
                                        nohayComentarios.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    } else {
                        System.out.println("No hay comentarios ®");
                        commentProgress.setVisibility(View.GONE);
                        nohayComentarios.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
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
                imageView.setImageResource(R.drawable.no_image_profile);
            }

            //Nombre:
            TextView nombreConsumidor = (TextView) productsView.findViewById(R.id.textNameConsumer);
            nombreConsumidor.setTypeface(text);
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
                super.onBackPressed();
                return true;

            case R.id.action_reservar:
                if (puedeReservar) {// Si el producto tiene cantidad para ofrecer entonces deja reservar
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

                    fechaReserva = System.currentTimeMillis();

                    Button reservar = (Button) d.findViewById(R.id.done);
                    reservar.setTypeface(text);
                    reservar.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            final ProgressDialog dlg = new ProgressDialog(VerDetalleProductoProductor.this);

                            if (!productCant.getText().toString().equals("")) { //Verifica si el campo no está vacio
                                // Dialogo de espera
                                dlg.setMessage("Realizando reserva. Por favor espere.");
                                dlg.show();

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
                                                    Reserve newReserve = new Reserve(nombreDelProducto, nombreDelConsumidor, nombreDelProductor, stringImagenFirebase, cantidadDigitada, precioProducto, fechaReserva);
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

                                                    dlg.dismiss();

                                                    Toast.makeText(VerDetalleProductoProductor.this, "Has reservado " + cantidadDigitada + " lb de " + nombreDelProducto, Toast.LENGTH_SHORT).show();
                                                    d.dismiss();
                                                } else { //De lo contrario se le advierte
                                                    productCant.setText("");
                                                    dlg.dismiss();
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
                                dlg.dismiss();
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
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(VerDetalleProductoProductor.this);
                    // Setting Dialog Title
                    builder1.setTitle("Reserva no disponible");
                    builder1.setIcon(R.drawable.ic_no_available);
                    builder1.setMessage("El producto " + nombreDelProducto + " no tiene cantidad para ofrecer.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    Dialog alert11 = builder1.create();
                    alert11.show();

                    //Change message text font
                    TextView content = ((TextView) alert11.findViewById(android.R.id.message));
                    content.setTypeface(editText);
                    //Change dialog button text font
                    TextView textButtonUno = ((TextView) alert11.findViewById(android.R.id.button1));
                    textButtonUno.setTypeface(infoName);
                    //Change dialog icon color
                    ImageView icon = ((ImageView) alert11.findViewById(android.R.id.icon));
                    int color = Color.parseColor("#F44336");
                    icon.setColorFilter(color);
                }

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
