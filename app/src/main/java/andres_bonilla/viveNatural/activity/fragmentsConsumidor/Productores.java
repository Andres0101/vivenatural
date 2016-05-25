package andres_bonilla.viveNatural.activity.fragmentsConsumidor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import andres_bonilla.viveNatural.activity.VerDetalleProductor;
import andres_bonilla.viveNatural.activity.classes.Rate;
import andres_bonilla.viveNatural.activity.classes.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Productores extends Fragment {

    private View rootView;
    private Firebase myRef;
    private Firebase usuarios;
    private Firebase rates;

    private SwipeRefreshLayout swipeContainer;

    private String nombreDelConsumidor;

    private Typeface text;
    private Typeface editText;

    MyListAdapter adapter;

    private TextView textoNoHay;

    private User clickedProducer;
    private List<User> productores = new ArrayList<>();

    private ArrayList<String> myUsers = new ArrayList<>();
    private ArrayList<Integer> myRatesCantidad = new ArrayList<>();
    private ArrayList<Integer> myRatesCantidadTotal = new ArrayList<>();
    private ArrayList<Integer> userArray = new ArrayList<>();

    private ProgressBar progress;

    private Boolean pinto;
    private Boolean yaAgrego;

    public Productores() {
        // Required empty public constructor

        myRef = new Firebase("https://vivenatural.firebaseio.com/");
        usuarios = myRef.child("users");
        rates = myRef.child("rates");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_productores, container, false);
        setHasOptionsMenu(true);

        // Obtiene el nombre de la persona que inicia sesión.
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

        editText = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        text = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");

        textoNoHay = (TextView) rootView.findViewById(R.id.textoInfoProductores);
        textoNoHay.setTypeface(editText);
        textoNoHay.setVisibility(View.GONE);

        progress = (ProgressBar) rootView.findViewById(R.id.progress);

        pinto = false;
        yaAgrego = false;

        listaBaseDatos();

        listView();
        clickSobreItem();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void listaBaseDatos(){
        // Lee los datos de los productores
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);

                    //Si el nombre del productor coincide con el que inicio sesión entonces...
                    if (user.getRol().equals("Productor")) {
                        progress.setVisibility(View.GONE);
                        productores.add(postSnapshot.getValue(User.class));
                        pinto = true;

                        myUsers.add(user.getNombre());

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

                for (int i = 0; i < myUsers.size(); i++) {
                    // Lee los datos de las reservas
                    final int finalI = i;
                    rates.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Integer sumaCantidad = 0;
                            Integer productor = 0;
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Rate rate = postSnapshot.getValue(Rate.class);

                                if (rate.getCalificoA().equals(myUsers.get(finalI))) {
                                    sumaCantidad = sumaCantidad + rate.getCalificacion();
                                    productor = productor + 1;
                                }
                            }
                            //Agregar al arraList
                            myRatesCantidad.add(sumaCantidad);
                            userArray.add(productor);

                            System.out.println(sumaCantidad);
                            System.out.println("#Productores: " + productor);
                            System.out.println("-----------------------------");

                            if (myRatesCantidad.size() == myUsers.size()) {
                                for (int i = 0; i < myRatesCantidad.size(); i++) {
                                    Integer calificaionTotal = 0;
                                    if (userArray.get(i) != 0) {
                                        calificaionTotal = calificaionTotal + (myRatesCantidad.get(i)/userArray.get(i));
                                    } else {
                                        calificaionTotal = calificaionTotal + myRatesCantidad.get(i);
                                    }
                                    //Agregar al arraList
                                    myRatesCantidadTotal.add(calificaionTotal);
                                    System.out.println("Estas son las calificaciones: " + myRatesCantidadTotal);
                                }

                                if (myRatesCantidadTotal.size() == myUsers.size()) {
                                    yaAgrego = true;
                                    listView();
                                }
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
    }

    private void listView() {
        adapter = new MyListAdapter();
        ListView list = (ListView) rootView.findViewById(R.id.producersListView);
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
        ListView list = (ListView) rootView.findViewById(R.id.producersListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedProducer = productores.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("nombreProductor", clickedProducer.getNombre());
                bundle.putString("nombreConsumidor", nombreDelConsumidor);
                Intent i = new Intent(getActivity(), VerDetalleProductor.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<User> {
        public MyListAdapter(){
            super(getActivity(), R.layout.producers_view, productores);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //Se asegura que existe un View con el que se pueda trabajar
            View producersView = convertView;
            if (producersView == null) {
                producersView = getActivity().getLayoutInflater().inflate(R.layout.producers_view, parent, false);
            }

            //Encontrar el producto
            User currentProducer = productores.get(position);

            //LLenar el View
            ImageView imageView = (ImageView) producersView.findViewById(R.id.imageProducer);
            if (!currentProducer.getImagen().equals("")) {
                String imageProduct = currentProducer.getImagen();
                Bitmap imagenProducto = StringToBitMap(imageProduct);
                imageView.setImageBitmap(imagenProducto);
            } else {
                imageView.setImageResource(R.drawable.no_image_profile);
            }

            //Nombre:
            TextView nombreProductor = (TextView) producersView.findViewById(R.id.textNameProducer);
            nombreProductor.setTypeface(text);
            nombreProductor.setText(currentProducer.getNombre());

            ImageView iconStar = (ImageView) producersView.findViewById(R.id.iconStar);

            //Calificación:
            TextView textViewCalificacion = (TextView) producersView.findViewById(R.id.textViewCalificacion);
            textViewCalificacion.setTypeface(editText);

            if (yaAgrego) {
                textViewCalificacion.setText(myRatesCantidadTotal.get(position) + "/5");
                if (myRatesCantidadTotal.get(position) <= 2) {
                    iconStar.setColorFilter(Color.parseColor("#B6B6B6"));
                } else if (myRatesCantidadTotal.get(position) == 3) {
                    iconStar.setColorFilter(Color.parseColor("#EBC775"));
                } else if (myRatesCantidadTotal.get(position) >= 4) {
                    iconStar.setColorFilter(Color.parseColor("#FFC107"));
                }
            }

            return producersView;
        }
    }
}
