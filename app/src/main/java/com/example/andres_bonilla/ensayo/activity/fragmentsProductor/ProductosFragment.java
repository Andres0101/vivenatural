package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ANDRES_BONILLA on 19/02/2016.
 */
public class ProductosFragment extends Fragment {

    private View rootView;

    private Firebase myRef;
    private Firebase productos;

    MyListAdapter adapter;

    private List<Product> myProducts = new ArrayList<Product>();
    private Product currentProduct;

    private Product clickedProduct;

    private FloatingActionButton add;

    private TextView nombreProducto;
    private TextView priceProducto;
    private TextView cantidadProducto;
    private String textProducto;
    private double textCantidad;

    private TextView textoNoHay;

    private Spinner spinnerProduct;
    private EditText productCant;
    private EditText productInfo;

    private String nombreDelProductor;
    private String nombreProductoBase;

    private VerProducto verProducto;

    private int[] arrayImagenProducto = {R.drawable.tomate, R.drawable.frijoles, R.drawable.cebolla, R.drawable.limon};
    private int[] arrayPrecioProducto = {500, 1000, 1000, 400};

    private int imagenProducto;
    private int precioProducto;

    public ProductosFragment() {
        // Required empty public constructor

        textProducto = "";
        textCantidad = 0;

        myRef = new Firebase("https://vivenatural.firebaseio.com/");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_productos, container, false);
        setHasOptionsMenu(true);

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelProductor = getArguments().getString("nombreDelProductor");

        textoNoHay = (TextView) rootView.findViewById(R.id.textoInfoProductos);

        listaBaseDatos();

        listView();
        clickSobreItem();

        //Botón flotante
        add = (FloatingActionButton) rootView.findViewById(R.id.addProducts);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct(view);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    public void addProduct(View view) {

        final Dialog d = new Dialog(getContext());

        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.productos_add);
        d.setCancelable(true);

        productCant = (EditText) d.findViewById(R.id.editTextCant);
        productInfo = (EditText) d.findViewById(R.id.editTextInfo);

        ImageView productColorIcon = (ImageView) d.findViewById(R.id.productsIcon);
        ImageView cantColorIcon = (ImageView) d.findViewById(R.id.productsCant);
        ImageView infoColorIcon = (ImageView) d.findViewById(R.id.productsInfo);
        //Cambia el color de los iconos
        int color = Color.parseColor("#B6B6B6");
        productColorIcon.setColorFilter(color);
        infoColorIcon.setColorFilter(color);
        cantColorIcon.setColorFilter(color);

        spinnerProduct = (Spinner) d.findViewById(R.id.spinnerProducts);

        spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String nombreProducto = String.valueOf(spinnerProduct.getSelectedItem());

                if (nombreProducto.equals("Tomate")) {
                    imagenProducto = arrayImagenProducto[0];
                    precioProducto = arrayPrecioProducto[0];
                } else if (nombreProducto.equals("Frijoles")) {
                    imagenProducto = arrayImagenProducto[1];
                    precioProducto = arrayPrecioProducto[1];
                } else if (nombreProducto.equals("Cebolla larga")) {
                    imagenProducto = arrayImagenProducto[2];
                    precioProducto = arrayPrecioProducto[2];
                } else if (nombreProducto.equals("Limones")) {
                    imagenProducto = arrayImagenProducto[3];
                    precioProducto = arrayPrecioProducto[3];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Button readyToAdd = (Button) d.findViewById(R.id.done);
        readyToAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (!productInfo.getText().toString().equals("") && !productCant.getText().toString().equals("")) {

                    textoNoHay.setVisibility(View.GONE);

                    // Lee los datos de los productos
                    productos = myRef.child("products");
                    productos.addListenerForSingleValueEvent(new ValueEventListener() {

                        String nombreProducto = String.valueOf(spinnerProduct.getSelectedItem());
                        Double cantidad = Double.parseDouble(productCant.getText().toString());
                        String info = productInfo.getText().toString();

                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            //Verifica si el producto está agregando un producto ya existente.
                            if (snapshot.hasChild(nombreDelProductor+": "+nombreProducto)) {
                                Toast.makeText(getContext(), "El producto que intenta agregar ya existe.", Toast.LENGTH_SHORT).show();
                            } else {
                                myProducts.add(new Product(nombreDelProductor, imagenProducto, nombreProducto, cantidad, precioProducto, info));

                                // Agrega producto a la base de datos
                                Firebase productRef = myRef.child("products").child(nombreDelProductor+": "+nombreProducto);
                                Product newProduct = new Product(nombreDelProductor, imagenProducto, nombreProducto, cantidad, precioProducto, info);
                                productRef.setValue(newProduct);

                                // We notify the data model is changed
                                adapter.notifyDataSetChanged();

                                d.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Por favor llena todos los campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button cancel = (Button) d.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
    }

    private void listaBaseDatos(){
        // Lee los datos de los productos
        productos = myRef.child("products");

        productos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    //Si el nombre del productor coincide con el que inicio sesión entonces...
                    if (product.getProductor().equals(nombreDelProductor)) {
                        textoNoHay.setVisibility(View.GONE);
                        myProducts.add(new Product(product.getProductor(), product.getImage(), product.getNombreProducto(), product.getCantidad(), product.getPrecio(), product.getDescripcionProducto()));

                        // We notify the data model is changed
                        adapter.notifyDataSetChanged();
                    } else {
                        //textoNoHay.setVisibility(View.VISIBLE);
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
        ListView list = (ListView) rootView.findViewById(R.id.productsListView);
        list.setAdapter(adapter);
        //list.setOnClickListener(onListClick);
        adapter.notifyDataSetChanged();
    }

    private void clickSobreItem() {
        ListView list = (ListView) rootView.findViewById(R.id.productsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedProduct = myProducts.get(position);

                verProducto = new VerProducto();
                android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container_body, verProducto);

                Bundle bundle = new Bundle();
                //Manda el nombre del producto seleccionado
                bundle.putString("nombreProducto", clickedProduct.getNombreProducto());
                // set Fragmentclass Arguments
                verProducto.setArguments(bundle);

                nombreProductoBase = clickedProduct.getNombreProducto();

                ft.commit();

                //Toast.makeText(getActivity(), "Seleccionaste: " + clickedProduct.getNombreProducto(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getNombreProductoBase() {
        return nombreProductoBase;
    }

    private class MyListAdapter extends ArrayAdapter<Product> {
        public MyListAdapter(){
            super(getActivity(), R.layout.products_view, myProducts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Se asegura que existe un View con el que se pueda trabajar
            View productsView = convertView;
            if (productsView == null) {
                productsView = getActivity().getLayoutInflater().inflate(R.layout.products_view, parent, false);
            }

            //Encontrar el producto
            currentProduct = myProducts.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) productsView.findViewById(R.id.imageProduct);
            imageView.setImageResource(currentProduct.getImage());

            //Nombre:
            nombreProducto = (TextView) productsView.findViewById(R.id.textNameProduct);
            nombreProducto.setText(currentProduct.getNombreProducto());
            //changeProducto(textProducto);

            //Precio:
            priceProducto = (TextView) productsView.findViewById(R.id.textPrecio);
            priceProducto.setText("$" + currentProduct.getPrecio());

            //Cantidad:
            cantidadProducto = (TextView) productsView.findViewById(R.id.textCantidad);
            cantidadProducto.setText(" " + currentProduct.getCantidad() + " lb");
            //changeCantidad(" " + textCantidad);

            return productsView;
        }
    }

    // Setea el nombre del producto con el texto que mandó
    public void changeProducto(String description){
        System.out.println("changeProducto");
        nombreProducto.setText(description);
    }
    public void setTextProducto(String textProducto) {
        this.textProducto = textProducto;
    }

    // Setea la cantidad del producto con el texto que mandó
    public void changeCantidad(String description){
        System.out.println("changeCantidad");
        cantidadProducto.setText(description);
    }
    public void setTextCantidad(Double textCantidad) {
        this.textCantidad = textCantidad;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();    //remove all items
        getActivity().getMenuInflater().inflate(R.menu.menu_busqueda, menu);
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
