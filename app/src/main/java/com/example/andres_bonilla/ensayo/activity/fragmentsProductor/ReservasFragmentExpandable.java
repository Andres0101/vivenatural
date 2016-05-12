package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.example.andres_bonilla.ensayo.activity.classes.Reserve;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReservasFragmentExpandable extends Fragment {

    private View rootView;

    private Firebase myRef;
    private Firebase productosReservados;
    private Firebase productosProductor;

    private TextView textoNoHay;

    private String nombreDelProductor;

    private Typeface texto;
    private Typeface regular;

    private ProgressBar progress;

    private Boolean pinto;
    private Boolean yaAgrego;

    private ArrayList<String> myProductsName = new ArrayList<>();
    private ArrayList<Double> myProductCantidad = new ArrayList<>();

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<Product> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public ReservasFragmentExpandable() {
        // Required empty public constructor

        myRef = new Firebase("https://vivenatural.firebaseio.com/");
        productosReservados = myRef.child("reserves");
        productosProductor = myRef.child("products");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_reservas, container, false);
        setHasOptionsMenu(true);

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

        progress = (ProgressBar) rootView.findViewById(R.id.reserveProgress);
        textoNoHay.setVisibility(View.GONE);

        pinto = false;
        yaAgrego = false;

        listaBaseDatos();
        listView();

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getActivity(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getActivity(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getActivity(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    /*
     * Preparing the list data
     */
    private void listaBaseDatos() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("products")) {
                    System.out.println("Si hay pedidos ®");
                    // Lee los datos de las reservas
                    productosProductor.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Product productProducer = postSnapshot.getValue(Product.class);

                                //Si el productor tiene reservas entonces...
                                if (productProducer.getProductor().equals(nombreDelProductor)) {
                                    progress.setVisibility(View.GONE);

                                    // Adding child data
                                    listDataHeader.add(postSnapshot.getValue(Product.class));
                                    pinto = true;

                                    myProductsName.add(productProducer.getNombreProducto());

                                    // We notify the data model is changed
                                    listAdapter.notifyDataSetChanged();
                                } else {
                                    progress.setVisibility(View.GONE);
                                }

                                if (pinto) {
                                    textoNoHay.setVisibility(View.GONE);
                                } else {
                                    textoNoHay.setVisibility(View.VISIBLE);
                                }
                            }

                            for (int i = 0; i < myProductsName.size(); i++) {
                                // Lee los datos de las reservas
                                final int finalI = i;
                                productosReservados.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        double sumaCantidad = 0;
                                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                            Reserve reserve = postSnapshot.getValue(Reserve.class);

                                            if (reserve.getProducto().equals(myProductsName.get(finalI)) && reserve.getReservadoA().equals(nombreDelProductor)) {
                                                sumaCantidad = sumaCantidad + reserve.getCantidadReservada();
                                                System.out.println("Producto: " + reserve.getProducto() + " Cantidad: " + reserve.getCantidadReservada());
                                            }
                                        }
                                        System.out.println(sumaCantidad);
                                        //Agregar al arraList
                                        myProductCantidad.add(sumaCantidad);
                                        System.out.println("------------");

                                        System.out.println("Como van las apuestas " + myProductCantidad.size() + "/" + myProductsName.size());

                                        if (myProductCantidad.size() == myProductsName.size()) {
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
                    System.out.println("No hay pedidos ®");
                    progress.setVisibility(View.GONE);
                    textoNoHay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        // Adding child data
        /*List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        for (int i = 0; i < listDataHeader.size(); i++) {
            listDataChild.put(listDataHeader.get(i), top250);
        }

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);*/
    }

    private void listView() {
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView = (ExpandableListView) rootView.findViewById(R.id.productsListView);
        expListView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private List<Product> listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<String>> _listDataChild;

        public ExpandableListAdapter(Context context, List<Product> listDataHeader,
                                     HashMap<String, List<String>> listChildData) {
            this.listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this.listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.products_reserved_list_item, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.textNameConsumer);

            txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this.listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.products_reserved_list_group, null);
            }

            Product currentProduct = listDataHeader.get(groupPosition);

            ImageView imageProduct = (ImageView) convertView.findViewById(R.id.imageProduct);
            String image = currentProduct.getImagen();
            Bitmap imagenProducto = StringToBitMap(image);
            imageProduct.setImageBitmap(imagenProducto);

            TextView textNameProduct = (TextView) convertView.findViewById(R.id.textNameProduct);
            textNameProduct.setTypeface(regular);
            textNameProduct.setText(currentProduct.getNombreProducto());

            TextView textCantidad = (TextView) convertView.findViewById(R.id.textCantidad);
            textCantidad.setTypeface(texto);

            if (yaAgrego) {
                textCantidad.setText(myProductCantidad.get(groupPosition) + " lb");
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
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

