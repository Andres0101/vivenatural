package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Comment;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VerDetalleProductoFragment extends Fragment {

    private Firebase myRef;
    private Firebase comments;
    private Firebase productos;

    private View rootView;

    private Typeface editText;
    private Typeface text;

    private String nombreDelProductor;
    private String nombreDelProducto;

    private TextView cantidadComentario;
    private TextView nohayComentarios;

    private EditText descripcionProducto;
    private EditText cantidadDisponible;

    MyListAdapter adapter;

    private List<Comment> myComments = new ArrayList<>();

    private ProgressBar progress;
    private ProgressBar commentProgress;

    private Boolean pinto;

    public VerDetalleProductoFragment() {
        // Required empty public constructor

        myRef = new Firebase("https://vivenatural.firebaseio.com/");
        comments = myRef.child("comments");
        productos = myRef.child("products");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.ver_detalle_producto_fragment, container, false);

        editText = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        text = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");

        nombreDelProductor = getArguments().getString("nombreDelProductor");
        nombreDelProducto = getArguments().getString("nombreDelProducto");

        TextView textCantidadComentarios = (TextView) rootView.findViewById(R.id.textViewComentarios);
        textCantidadComentarios.setTypeface(text);
        cantidadComentario = (TextView) rootView.findViewById(R.id.textViewCantidadComentarios);
        cantidadComentario.setTypeface(text);
        nohayComentarios = (TextView) rootView.findViewById(R.id.textoInfoComentarios);
        nohayComentarios.setTypeface(editText);

        descripcionProducto = (EditText) rootView.findViewById(R.id.editTextDescriProduct);
        descripcionProducto.setTypeface(editText);
        descripcionProducto.setBackground(null);
        cantidadDisponible = (EditText) rootView.findViewById(R.id.editTextCantidadDisponible);
        cantidadDisponible.setTypeface(editText);
        cantidadDisponible.setBackground(null);

        pinto = false;

        listaBaseDatos();
        listView();

        progress = (ProgressBar) rootView.findViewById(R.id.detailsProgress);
        commentProgress = (ProgressBar) rootView.findViewById(R.id.commentProgress);
        nohayComentarios.setVisibility(View.GONE);

        // Lee los datos de los productos
        productos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    if (product.getNombreProducto().equals(nombreDelProducto) && product.getProductor().equals(nombreDelProductor)) {
                        progress.setVisibility(View.GONE);

                        descripcionProducto.setText(product.getDescripcionProducto());
                        cantidadDisponible.setText(" " + product.getCantidad() + " lb");
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    private void listaBaseDatos(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild("comments")) {
                        System.out.println("Si hay comentarios ®");
                        // Lee los datos de los productos
                        comments.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    Comment comment = postSnapshot.getValue(Comment.class);

                                    if (comment.getDirigidoA().equals(nombreDelProductor) && comment.getProductoComentado().equals(nombreDelProducto)) {
                                        myComments.add(postSnapshot.getValue(Comment.class));
                                        cantidadComentario.setText(" " + myComments.size());
                                        commentProgress.setVisibility(View.GONE);

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

    private void listView() {
        adapter = new MyListAdapter();
        ListView list = (ListView) rootView.findViewById(R.id.commentsListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private class MyListAdapter extends ArrayAdapter<Comment> {
        public MyListAdapter(){
            super(getActivity(), R.layout.comment_view, myComments);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Se asegura que existe un View con el que se pueda trabajar
            View productsView = convertView;
            if (productsView == null) {
                productsView = getActivity().getLayoutInflater().inflate(R.layout.comment_view, parent, false);
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
            nombreConsumidor.setText(currentComment.getHechoPor());

            //Comentario:
            TextView priceProducto = (TextView) productsView.findViewById(R.id.textViewComentario);
            priceProducto.setTypeface(editText);
            priceProducto.setText(currentComment.getComentario());

            return productsView;
        }
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
}
