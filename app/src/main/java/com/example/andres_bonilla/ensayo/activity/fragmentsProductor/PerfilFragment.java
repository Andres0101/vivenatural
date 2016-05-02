package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class PerfilFragment extends Fragment {

    public Activity activity;

    private Firebase user;

    private String userString;
    private Boolean editText;
    private String textDescription;

    private ImageView imageProducer;
    private TextView userName;
    private EditText textoEditable;

    private String nombreDelProductor;

    public PerfilFragment() {
        // Required empty public constructor
        userString = "none";
        editText = false;
        textDescription = "No hay información.";

        Firebase myRef = new Firebase("https://vivenatural.firebaseio.com/");
        user = myRef.child("users");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil, container, false);
        setHasOptionsMenu(true);

        Typeface medium = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Medium.ttf");

        Typeface editTextD = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        // Obtiene el nombre de la persona que inicia sesión.
        nombreDelProductor = getArguments().getString("nombreDelProductor");

        imageProducer = (ImageView) rootView.findViewById(R.id.imageProducer);
        userName = (TextView) rootView.findViewById(R.id.nombreUsuario);
        userName.setTypeface(medium);
        textoEditable = (EditText) rootView.findViewById(R.id.textDescription);
        textoEditable.setTypeface(editTextD);
        textoEditable.setBackground(null);

        changeText(userString);
        textEditable(editText);

        changeDescription(textDescription);

        // Lee los datos de los usuarios del mercado para obtener su imagen de perfil.
        user.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);

                    if (user.getNombre().equals(nombreDelProductor)) {
                        if (!user.getImagen().equals("")) {
                            String imageFile = user.getImagen();
                            Bitmap imagenProducto = StringToBitMap(imageFile);

                            imageProducer.setImageBitmap(imagenProducto);
                        } else {
                            imageProducer.setImageResource(R.drawable.no_image_profile_profile);
                        }
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

    // Setea el texto con el nombre de usuario
    private void changeText(String text){
        userName.setText(text);
    }
    public void setUserString(String userString) {
        this.userString = userString;
    }

    // Setea la descripción con el texto que mandó PerilFragmentCheck
    private void changeDescription(String description){
        textoEditable.setText(description);
    }
    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
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

    // Vuelve el editText editable para que el usuario pueda escribir
    private void textEditable(Boolean editable){
        textoEditable.setEnabled(editable);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();    //remove all items
        getActivity().getMenuInflater().inflate(R.menu.menu_editar, menu);
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
