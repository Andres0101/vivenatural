package andres_bonilla.viveNatural.activity.fragmentsProductor;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import andres_bonilla.viveNatural.activity.R;
import andres_bonilla.viveNatural.activity.VerDetalleProducto;
import andres_bonilla.viveNatural.activity.classes.MarketProduct;
import andres_bonilla.viveNatural.activity.classes.Product;
import andres_bonilla.viveNatural.activity.classes.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductosFragment extends Fragment {

    private View rootView;

    private Firebase myRef;
    private Firebase productos;
    private Firebase marketProducts;
    private Firebase users;

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
    private String stringImagenFirebase;
    private String stringImagenFirebaseProductor;

    private ProgressBar progress;

    private Boolean pinto;

    public ProductosFragment() {
        // Required empty public constructor

        myRef = new Firebase("https://vivenatural.firebaseio.com/");
        marketProducts = myRef.child("marketProducts");
        users = myRef.child("users");
        productos = myRef.child("products");
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

        progress = (ProgressBar) rootView.findViewById(R.id.listProgress);
        textoNoHay.setVisibility(View.GONE);

        pinto = false;

        listaBaseDatos();

        listView();
        clickSobreItem();

        // Lee los datos de los productos del mercado
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

        // Lee los datos de los productos del mercado
        users.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);

                    if (user.getNombre().equals(nombreDelProductor)) {
                        stringImagenFirebaseProductor = user.getImagen();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Button readyToAdd = (Button) d.findViewById(R.id.done);
        readyToAdd.setTypeface(textCantidad);
        readyToAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (!productInfo.getText().toString().equals("") && !productCant.getText().toString().equals("")) {
                    textoNoHay.setVisibility(View.GONE);
                    // Dialogo de espera
                    final ProgressDialog dlg = new ProgressDialog(getActivity());
                    dlg.setMessage("Agregando producto. Por favor espere.");
                    dlg.show();

                    // Lee los datos de los productos
                    productos.addListenerForSingleValueEvent(new ValueEventListener() {

                        String nombreProducto = String.valueOf(spinnerProduct.getSelectedItem());
                        Double cantidad = Double.parseDouble(productCant.getText().toString());
                        String info = productInfo.getText().toString();

                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            //Verifica si el producto está agregando un producto ya existente.
                            if (snapshot.hasChild(nombreDelProductor+": "+nombreProducto)) {
                                dlg.dismiss();
                                Toast.makeText(getContext(), "El producto que intenta agregar ya existe.", Toast.LENGTH_SHORT).show();
                            } else {
                                dlg.dismiss();
                                myProducts.add(new Product(nombreDelProductor, stringImagenFirebase, stringImagenFirebaseProductor, nombreProducto, cantidad, precioProducto, info));

                                // Agrega producto a la base de datos
                                Firebase productRef = myRef.child("products").child(nombreDelProductor+": "+nombreProducto);
                                Product newProduct = new Product(nombreDelProductor, stringImagenFirebase, stringImagenFirebaseProductor, nombreProducto, cantidad, precioProducto, info);
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
                    Snackbar snackbar = Snackbar
                        .make(v, "Por favor llena todos los campos", Snackbar.LENGTH_LONG);
                    snackbar.show();
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
        productos.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            //Elimina el producto de la lista en tiempo real.
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for (Product product : myProducts) {
                    if (key.equals(product.getProductor() + ": " + product.getNombreProducto())){
                        myProducts.remove(product);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        productos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    //Si el nombre del productor coincide con el que inicio sesión entonces...
                    if (product.getProductor().equals(nombreDelProductor)) {
                        progress.setVisibility(View.GONE);

                        myProducts.add(postSnapshot.getValue(Product.class));
                        pinto = true;

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
            nombreProducto.setTypeface(textCantidad);
            nombreProducto.setText(currentProduct.getNombreProducto());

            //Precio:
            TextView priceProducto = (TextView) productsView.findViewById(R.id.textPrecio);
            priceProducto.setTypeface(texto);
            priceProducto.setText("$" + currentProduct.getPrecio() + "/lb");

            //Cantidad:
            TextView cantidadProducto = (TextView) productsView.findViewById(R.id.textCantidad);
            cantidadProducto.setTypeface(texto);
            cantidadProducto.setText(currentProduct.getCantidad() + " lb");

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
