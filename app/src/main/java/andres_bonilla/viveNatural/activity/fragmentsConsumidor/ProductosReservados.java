package andres_bonilla.viveNatural.activity.fragmentsConsumidor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import andres_bonilla.viveNatural.activity.R;
import andres_bonilla.viveNatural.activity.classes.Reserve;
import andres_bonilla.viveNatural.activity.classes.SuccessReserve;
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

    private TextView textoNoHay;

    private String nombreDelConsumidor;

    private Typeface texto;
    private Typeface regular;

    private ProgressBar reserveProgress;

    private Boolean pinto;
    private Boolean puederReclamar;
    private Boolean deleteItems;

    private long fechaReserva;

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

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelConsumidor = getArguments().getString("nombreDelConsumidor");
        deleteItems = getArguments().getBoolean("delete");

        reserveProgress = (ProgressBar) rootView.findViewById(R.id.reserveProgress);

        textoNoHay = (TextView) rootView.findViewById(R.id.textoInfoProductos);
        textoNoHay.setTypeface(texto);
        textoNoHay.setVisibility(View.GONE);

        pinto = false;
        puederReclamar = false;

        listaBaseDatos();
        listView();

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
        if (!deleteItems) {
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
        } else {
            myReserves.clear();
            reserveProgress.setVisibility(View.GONE);
            textoNoHay.setVisibility(View.VISIBLE);
            deleteItems = false;

            // Lee los datos de las reservas exitosas
            successReserves.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        SuccessReserve successReserve = postSnapshot.getValue(SuccessReserve.class);

                        //Elimina el producto de la base de datos
                        Firebase deleteProduct = myRef.child("successReserves").child(nombreDelConsumidor + ": " + successReserve.getProducto() + " de " + successReserve.getReservadoA() + " la fecha " + successReserve.getFecha());
                        deleteProduct.removeValue();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });

            // Lee los datos de los productos reservados
            productosReservados.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Reserve reserve = postSnapshot.getValue(Reserve.class);

                        //Elimina el producto de la base de datos
                        Firebase deleteProductOfReserves = myRef.child("reserves").child(nombreDelConsumidor + ": " + reserve.getProducto() + " de " + reserve.getReservadoA());
                        deleteProductOfReserves.removeValue();
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
        ListView list = (ListView) rootView.findViewById(R.id.productsListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

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

            final CheckBox checkBox = (CheckBox) productsView.findViewById(R.id.checkBox);
            checkBox.setEnabled(false);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (puederReclamar) {
                        if (checkBox.isChecked()) {
                            fechaReserva = System.currentTimeMillis();
                            // Agrega producto reclamado a la base de datos
                            Firebase reservesRef = myRef.child("successReserves").child(nombreDelConsumidor + ": " + currentProductReserve.getProducto() + " de " + currentProductReserve.getReservadoA() + " la fecha " + fechaReserva);
                            SuccessReserve newSuccessReserve = new SuccessReserve(currentProductReserve.getProducto(), nombreDelConsumidor, currentProductReserve.getReservadoA(), currentProductReserve.getCantidadReservada(), currentProductReserve.getPrecio(), fechaReserva);
                            reservesRef.setValue(newSuccessReserve);
                        } else {
                            // Lee los datos de las reservas exitosas
                            successReserves.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                        SuccessReserve successReserve = postSnapshot.getValue(SuccessReserve.class);

                                        //Elimina el producto  de la base de datos
                                        Firebase deleteProduct = myRef.child("successReserves").child(nombreDelConsumidor + ": " + currentProductReserve.getProducto() + " de " + currentProductReserve.getReservadoA() + " la fecha " + successReserve.getFecha());
                                        deleteProduct.removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }
                            });
                        }
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
                                        checkBox.setChecked(true);
                                        checkBox.setEnabled(true);

                                        puederReclamar = true;
                                    } else {
                                        checkBox.setEnabled(true);
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
                        checkBox.setEnabled(true);
                        puederReclamar = true;
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();    //remove all items
        getActivity().getMenuInflater().inflate(R.menu.menu_borrar_lista_mercado, menu);
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
