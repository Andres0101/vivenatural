package com.example.andres_bonilla.ensayo.activity.fragmentsConsumidor;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Reserve;
import com.example.andres_bonilla.ensayo.activity.classes.SuccessReserve;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductosReservados extends Fragment {

    private View rootView;

    private Firebase myRef;
    private Firebase productosReservados;
    private Firebase successReserves;

    MyListAdapter adapter;

    private List<Reserve> myReserves = new ArrayList<>();

    private Reserve clickedProduct;

    private TextView textoNoHay;

    private String nombreDelConsumidor;

    private Typeface texto;
    private Typeface regular;
    private Typeface medium;

    private ProgressBar reserveProgress;

    private Boolean pinto;
    private Boolean puederReclamar;

    public ProductosReservados() {
        // Required empty public constructor

        myRef = new Firebase("https://vivenatural.firebaseio.com/");
        productosReservados = myRef.child("reserves");
        successReserves = myRef.child("successReserves");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.productos_reservados, container, false);
        setHasOptionsMenu(true);

        texto = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        regular = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");

        medium = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Medium.ttf");

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelConsumidor = getArguments().getString("nombreDelConsumidor");

        reserveProgress = (ProgressBar) rootView.findViewById(R.id.reserveProgress);

        textoNoHay = (TextView) rootView.findViewById(R.id.textoInfoProductos);
        textoNoHay.setTypeface(texto);
        textoNoHay.setVisibility(View.GONE);

        pinto = false;
        puederReclamar = false;

        listaBaseDatos();
        listView();
        //clickSobreItem();

        // Inflate the layout for this fragment
        return rootView;
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
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("reserves")) {
                    System.out.println("Si hay reservas ®");
                    // Lee los datos de los productos
                    productosReservados.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Reserve reservedProducts = postSnapshot.getValue(Reserve.class);

                                //Si el consumidor que reservó el producto coincide con el que inicio sesión entonces...
                                if (reservedProducts.getReservadoPor().equals(nombreDelConsumidor)) {
                                    reserveProgress.setVisibility(View.GONE);

                                    myReserves.add(postSnapshot.getValue(Reserve.class));

                                    pinto = true;

                                    // We notify the data model is changed
                                    adapter.notifyDataSetChanged();
                                } else {
                                    reserveProgress.setVisibility(View.GONE);
                                }

                                if (pinto) {
                                    textoNoHay.setVisibility(View.GONE);
                                } else {
                                    textoNoHay.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                } else {
                    System.out.println("No hay reservas ®");
                    reserveProgress.setVisibility(View.GONE);
                    textoNoHay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void listView() {
        adapter = new MyListAdapter();
        ListView list = (ListView) rootView.findViewById(R.id.productsListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /*private void clickSobreItem() {
        final ListView list = (ListView) rootView.findViewById(R.id.productsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedProduct = myReserves.get(position);

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
            super(getActivity(), R.layout.products_reserve_view, myReserves);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Se asegura que existe un View con el que se pueda trabajar
            View productsView = convertView;
            if (productsView == null) {
                productsView = getActivity().getLayoutInflater().inflate(R.layout.products_reserve_view, parent, false);
            }

            //Encontrar productos reservados
            final Reserve currentProductReserve = myReserves.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) productsView.findViewById(R.id.imageProduct);
            String imageProduct = currentProductReserve.getImagenProducto();
            Bitmap imagenProducto = StringToBitMap(imageProduct);
            imageView.setImageBitmap(imagenProducto);

            //Nombre:
            TextView nombreProducto = (TextView) productsView.findViewById(R.id.textNameProduct);
            nombreProducto.setTypeface(regular);
            nombreProducto.setText(currentProductReserve.getProducto());

            //Precio:
            TextView priceProducto = (TextView) productsView.findViewById(R.id.textPrecio);
            priceProducto.setTypeface(texto);
            priceProducto.setText("$" + currentProductReserve.getPrecio() + "/lb");

            //Cantidad:
            TextView cantidadProducto = (TextView) productsView.findViewById(R.id.textCantidad);
            cantidadProducto.setTypeface(texto);
            cantidadProducto.setText(currentProductReserve.getCantidadReservada() + " lb");

            //Reservado a:
            TextView textNameProducer = (TextView) productsView.findViewById(R.id.textNameProducer);
            textNameProducer.setTypeface(texto);
            textNameProducer.setText(currentProductReserve.getReservadoA());

            final ImageView check = (ImageView) productsView.findViewById(R.id.check);
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (puederReclamar) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        // Setting Dialog Content
                        builder1.setMessage("Has reclamado el producto " + currentProductReserve.getProducto() + " de " + currentProductReserve.getReservadoA());
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Confirmar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int color = Color.parseColor("#378F43");
                                        check.setColorFilter(color);
                                        check.setEnabled(false);

                                        // Agrega producto reclamado a la base de datos
                                        Firebase productRef = myRef.child("successReserves");
                                        SuccessReserve newSuccessReserve = new SuccessReserve(currentProductReserve.getProducto(), currentProductReserve.getReservadoPor(), currentProductReserve.getReservadoA(), currentProductReserve.getCantidadReservada(), currentProductReserve.getPrecio());
                                        productRef.push().setValue(newSuccessReserve);
                                    }
                                });

                        builder1.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        Dialog alert11 = builder1.create();
                        alert11.show();

                        //Change dialog button text font
                        TextView textButtonUno = ((TextView) alert11.findViewById(android.R.id.button1));
                        textButtonUno.setTypeface(medium);
                        TextView textButtonDos = ((TextView) alert11.findViewById(android.R.id.button2));
                        textButtonDos.setTypeface(medium);
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(v, "Procesando datos...", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            });

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild("successReserves")) {
                        System.out.println("Si hay pedidos reclamados con éxito ®");
                        // Lee los datos de las reservas exitosas
                        successReserves.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    SuccessReserve successReserve = postSnapshot.getValue(SuccessReserve.class);

                                    //Valida si el consumidor ya ha marcado como reclamado un producto
                                    if (successReserve.getReservadoPor().equals(nombreDelConsumidor) && currentProductReserve.getProducto().equals(successReserve.getProducto()) && currentProductReserve.getReservadoA().equals(successReserve.getReservadoA())) {
                                        int color = Color.parseColor("#378F43");
                                        check.setColorFilter(color);
                                        check.setEnabled(false);

                                        puederReclamar = true;
                                    } else {
                                        puederReclamar = true;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    } else {
                        System.out.println("No hay pedidos reclamados con éxito ®");
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });

            return productsView;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
