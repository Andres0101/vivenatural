package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
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
import com.example.andres_bonilla.ensayo.activity.VerDetalleProducto;
import com.example.andres_bonilla.ensayo.activity.classes.MarketProduct;
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
    private Firebase marketProducts;

    MyListAdapter adapter;

    private List<Product> myProducts = new ArrayList<>();

    private Product clickedProduct;

    private TextView textoNoHay;

    private Spinner spinnerProduct;
    private EditText productCant;
    private EditText productInfo;

    private String nombreDelProductor;
    private String nombreProductoSpinner;

    private Typeface texto;
    private Typeface textCantidad;
    private Typeface infoName;

    private ArrayList<String> list = new ArrayList<>();

    private int precioProducto;
    String stringImagenFirebase;

    public ProductosFragment() {
        // Required empty public constructor

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

        texto = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        textCantidad = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");

        infoName = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Medium.ttf");

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelProductor = getArguments().getString("nombreDelProductor");

        textoNoHay = (TextView) rootView.findViewById(R.id.textoInfoProductos);
        textoNoHay.setTypeface(texto);

        listaBaseDatos();

        listView();
        clickSobreItem();

        // Lee los datos de los productos del mercado
        marketProducts = myRef.child("marketProducts");
        marketProducts.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MarketProduct marketProduct = postSnapshot.getValue(MarketProduct.class);

                    System.out.println("---------------" + marketProduct.getNombre());

                    //Obtiene el nombre de los productos y los agrega al arraylist para
                    //desplegarlo en el spinner.
                    list.add(marketProduct.getNombre());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Botón flotante
        FloatingActionButton add = (FloatingActionButton) rootView.findViewById(R.id.addProducts);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct(view);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void addProduct(View view) {

        final Dialog d = new Dialog(getContext());

        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.productos_add);
        d.setCancelable(true);

        TextView textViewDialog = (TextView) d.findViewById(R.id.textViewDialog);
        textViewDialog.setTypeface(infoName);
        productCant = (EditText) d.findViewById(R.id.editTextCant);
        productCant.setTypeface(texto);
        productInfo = (EditText) d.findViewById(R.id.editTextInfo);
        productInfo.setTypeface(texto);

        ImageView productColorIcon = (ImageView) d.findViewById(R.id.productsIcon);
        ImageView cantColorIcon = (ImageView) d.findViewById(R.id.productsCant);
        ImageView infoColorIcon = (ImageView) d.findViewById(R.id.productsInfo);
        //Cambia el color de los iconos
        int color = Color.parseColor("#B6B6B6");
        productColorIcon.setColorFilter(color);
        infoColorIcon.setColorFilter(color);
        cantColorIcon.setColorFilter(color);

        spinnerProduct = (Spinner) d.findViewById(R.id.spinnerProducts);
        //Agrega al spinner los nombres de los productos en la base de datos.
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, list)
        {
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(texto); //Cambia la tipografía del texto seleccionado
                return v;
            }

            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(texto); //Cambia la tipografía de los textos que contiene el Spinner
                return v;
            }
        };

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(adapter1);

        spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                nombreProductoSpinner = String.valueOf(spinnerProduct.getSelectedItem());

                // Lee los datos de los productos del mercado
                marketProducts = myRef.child("marketProducts");
                marketProducts.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            MarketProduct marketProduct = postSnapshot.getValue(MarketProduct.class);

                            if (marketProduct.getNombre().equals(nombreProductoSpinner)) {
                                stringImagenFirebase = marketProduct.getImagen();
                                precioProducto = marketProduct.getPrecio();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Button readyToAdd = (Button) d.findViewById(R.id.done);
        readyToAdd.setTypeface(textCantidad);
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
                                myProducts.add(new Product(nombreDelProductor, stringImagenFirebase, nombreProducto, cantidad, precioProducto, info));

                                // Agrega producto a la base de datos
                                Firebase productRef = myRef.child("products").child(nombreDelProductor+": "+nombreProducto);
                                Product newProduct = new Product(nombreDelProductor, stringImagenFirebase, nombreProducto, cantidad, precioProducto, info);
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
        cancel.setTypeface(textCantidad);
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

                        myProducts.add(new Product(product.getProductor(), product.getImagen(), product.getNombreProducto(), product.getCantidad(), product.getPrecio(), product.getDescripcionProducto()));

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
        ListView list = (ListView) rootView.findViewById(R.id.productsListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void clickSobreItem() {
        ListView list = (ListView) rootView.findViewById(R.id.productsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedProduct = myProducts.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("nombreProducto", clickedProduct.getNombreProducto());
                bundle.putString("nombreProductor", nombreDelProductor);
                Intent i = new Intent(getActivity(), VerDetalleProducto.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
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
            Product currentProduct = myProducts.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) productsView.findViewById(R.id.imageProduct);
            String imageProduct = currentProduct.getImagen();
            Bitmap imagenProducto = StringToBitMap(imageProduct);
            imageView.setImageBitmap(imagenProducto);

            //Nombre:
            TextView nombreProducto = (TextView) productsView.findViewById(R.id.textNameProduct);
            nombreProducto.setTypeface(infoName);
            nombreProducto.setText(currentProduct.getNombreProducto());

            //Precio:
            TextView priceProducto = (TextView) productsView.findViewById(R.id.textPrecio);
            priceProducto.setTypeface(texto);
            priceProducto.setText("$" + currentProduct.getPrecio());

            //Cantidad:
            TextView textViewCantidad = (TextView) productsView.findViewById(R.id.textViewCantidad);
            textViewCantidad.setTypeface(texto);
            TextView cantidadProducto = (TextView) productsView.findViewById(R.id.textCantidad);
            cantidadProducto.setTypeface(texto);
            priceProducto.setTypeface(texto);
            cantidadProducto.setText(currentProduct.getCantidad() + " lb");

            return productsView;
        }
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
