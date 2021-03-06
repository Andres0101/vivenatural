package andres_bonilla.viveNatural.activity.fragmentsProductor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import andres_bonilla.viveNatural.activity.R;
import andres_bonilla.viveNatural.activity.classes.Product;
import andres_bonilla.viveNatural.activity.classes.Reserve;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MisProductos extends Fragment {

    private View rootView;

    private Firebase myRef;
    private Firebase productos;
    private Firebase reserves;

    MyListAdapter adapter;

    private List<Product> myProducts = new ArrayList<>();

    private ArrayList<String> myOwnProduct = new ArrayList<>();
    private ArrayList<Double> myOwnProductCantidad = new ArrayList<>();

    private TextView textoNoHay;

    private String nombreDelProductor;

    private Typeface texto;
    private Typeface regular;

    private ProgressBar progress;

    private Boolean pinto;
    private Boolean yaAgrego;

    public MisProductos() {
        // Required empty public constructor

        myRef = new Firebase("https://vivenatural.firebaseio.com/");
        productos = myRef.child("products");
        reserves = myRef.child("reserves");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.misproductos_fragment, container, false);

        texto = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        regular = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelProductor = getArguments().getString("nombreDelProductor");

        textoNoHay = (TextView) rootView.findViewById(R.id.textoInfoProductos);
        textoNoHay.setTypeface(texto);

        progress = (ProgressBar) rootView.findViewById(R.id.productProgress);
        textoNoHay.setVisibility(View.GONE);

        pinto = false;
        yaAgrego = false;

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
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("products")) {
                    System.out.println("Si hay productos ®");
                    // Lee los datos de los productos
                    productos.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Product product = postSnapshot.getValue(Product.class);

                                //Si el productor tiene productos entonces...
                                if (product.getProductor().equals(nombreDelProductor)) {
                                    progress.setVisibility(View.GONE);

                                    myProducts.add(postSnapshot.getValue(Product.class));
                                    pinto = true;

                                    myOwnProduct.add(product.getNombreProducto());

                                    // We notify the data model is changed
                                    adapter.notifyDataSetChanged();
                                } else {
                                    progress.setVisibility(View.GONE);
                                }

                                if (pinto) {
                                    textoNoHay.setVisibility(View.GONE);
                                } else {
                                    textoNoHay.setVisibility(View.VISIBLE);
                                }
                            }

                            for (int i = 0; i < myOwnProduct.size(); i++) {
                                // Lee los datos de las reservas
                                final int finalI = i;
                                reserves.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        double sumaCantidad = 0;
                                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                            Reserve reserve = postSnapshot.getValue(Reserve.class);

                                            if (reserve.getProducto().equals(myOwnProduct.get(finalI))) {
                                                sumaCantidad = sumaCantidad + reserve.getCantidadReservada();
                                                System.out.println("Producto: " + reserve.getProducto() + " Cantidad: " + reserve.getCantidadReservada());
                                            }
                                        }
                                        System.out.println(sumaCantidad);
                                        //Agregar al arraList
                                        myOwnProductCantidad.add(sumaCantidad);
                                        System.out.println("------------");

                                        System.out.println("Como van las apuestas " + myOwnProductCantidad.size() + "/" + myOwnProduct.size());

                                        if (myOwnProductCantidad.size() == myOwnProduct.size()) {
                                            yaAgrego = true;
                                            listView();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                } else {
                    System.out.println("No hay productos ®");
                    progress.setVisibility(View.GONE);
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

    private class MyListAdapter extends ArrayAdapter<Product> {
        public MyListAdapter(){
            super(getActivity(), R.layout.mis_productos_view, myProducts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Se asegura que existe un View con el que se pueda trabajar
            View productsView = convertView;
            if (productsView == null) {
                productsView = getActivity().getLayoutInflater().inflate(R.layout.mis_productos_view, parent, false);
            }

            //Encontrar productos reservados
            Product currentProduct = myProducts.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) productsView.findViewById(R.id.imageProduct);
            String imageProduct = currentProduct.getImagen();
            Bitmap imagenProducto = StringToBitMap(imageProduct);
            imageView.setImageBitmap(imagenProducto);

            //Nombre:
            TextView nombreProducto = (TextView) productsView.findViewById(R.id.textNameProduct);
            nombreProducto.setTypeface(regular);
            nombreProducto.setText(currentProduct.getNombreProducto());

            //Cantidad:
            TextView textCantidad = (TextView) productsView.findViewById(R.id.textCantidad);
            textCantidad.setTypeface(texto);

            if (yaAgrego) {
                textCantidad.setText(myOwnProductCantidad.get(position) + " lb");
            }

            return productsView;
        }
    }
}
