package andres_bonilla.viveNatural.activity.fragmentsConsumidor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import andres_bonilla.viveNatural.activity.R;
import andres_bonilla.viveNatural.activity.VerProductoMarket;
import andres_bonilla.viveNatural.activity.classes.MarketProduct;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductsMarket extends Fragment {

    private Firebase marketProducts;
    private View rootView;

    private SwipeRefreshLayout swipeContainer;

    private Typeface editText;
    private Typeface text;

    private MarketProduct clickedProduct;

    MyListAdapter adapter;

    private List<MarketProduct> marketProductList = new ArrayList<>();

    private String nombreDelConsumidor;

    private ProgressBar productsProgress;

    public ProductsMarket() {
        // Required empty public constructor

        Firebase myRef = new Firebase("https://vivenatural.firebaseio.com/");
        marketProducts = myRef.child("marketProducts");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.products_market, container, false);
        setHasOptionsMenu(true);

        editText = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        text = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");

        nombreDelConsumidor = getArguments().getString("nombreDelConsumidor");

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                listaBaseDatos();

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_green_light);

        productsProgress = (ProgressBar) rootView.findViewById(R.id.productsProgress);

        listaBaseDatos();

        listView();
        clickSobreItem();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void listaBaseDatos(){
        // Lee los datos de los productos del mercado
        marketProducts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    productsProgress.setVisibility(View.GONE);
                    marketProductList.add(postSnapshot.getValue(MarketProduct.class));

                    // We notify the data model is changed
                    adapter.notifyDataSetChanged();
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

    private void clickSobreItem() {
        ListView list = (ListView) rootView.findViewById(R.id.productsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedProduct = marketProductList.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("nombreProducto", clickedProduct.getNombre());
                bundle.putString("nombreDelConsumidor", nombreDelConsumidor);
                Intent i = new Intent(getActivity(), VerProductoMarket.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<MarketProduct> {
        public MyListAdapter(){
            super(getActivity(), R.layout.products_market_view, marketProductList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Se asegura que existe un View con el que se pueda trabajar
            View producersView = convertView;
            if (producersView == null) {
                producersView = getActivity().getLayoutInflater().inflate(R.layout.products_market_view, parent, false);
            }

            //Encontrar el producto
            MarketProduct currentProduct = marketProductList.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) producersView.findViewById(R.id.imageProduct);
            String imageProduct = currentProduct.getImagen();
            Bitmap imagenProducto = StringToBitMap(imageProduct);
            imageView.setImageBitmap(imagenProducto);

            //Nombre:
            TextView nombreProducto = (TextView) producersView.findViewById(R.id.textNameProduct);
            nombreProducto.setTypeface(text);
            nombreProducto.setText(currentProduct.getNombre());

            //precio:
            TextView precioProducto = (TextView) producersView.findViewById(R.id.textPrecio);
            precioProducto.setTypeface(editText);
            precioProducto.setText("$" + currentProduct.getPrecio() + "/lb");

            return producersView;
        }
    }
}
