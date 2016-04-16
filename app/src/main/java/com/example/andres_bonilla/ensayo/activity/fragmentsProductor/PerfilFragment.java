package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;

/**
 * Created by ANDRES_BONILLA on 19/02/2016.
 */
public class PerfilFragment extends Fragment {

    public Activity activity;

    private String userString;
    private Bitmap imageBitmap;
    private Boolean editText;
    private String textDescription;

    private int imageInt;

    private ImageView imageProducer;
    private TextView userName;
    private EditText textoEditable;

    public PerfilFragment() {
        // Required empty public constructor
        userString = "none";
        editText = false;
        textDescription = "No hay información.";
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

        Typeface textView = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Bold.ttf");

        Typeface editTextD = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        imageProducer = (ImageView) rootView.findViewById(R.id.imageProducer);
        userName = (TextView) rootView.findViewById(R.id.nombreUsuario);
        userName.setTypeface(textView);
        TextView textDescriptionTittle = (TextView) rootView.findViewById(R.id.descriptionTittle);
        textDescriptionTittle.setTypeface(textView);
        textoEditable = (EditText) rootView.findViewById(R.id.textDescription);
        textoEditable.setTypeface(editTextD);
        textoEditable.setBackground(null);

        changeText(userString);
        textEditable(editText);

        changeDescription(textDescription);
        changeImage(imageBitmap);
        changeImageNoImage(imageInt);

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

    // Setea la imagen de perfil
    private void changeImage(Bitmap image) {
        imageProducer.setImageBitmap(image);
    }
    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    // Setea la imagen de perfil cuando no tiene imagen
    private void changeImageNoImage(int image) {
        imageProducer.setImageResource(image);
    }
    public void setImageInt(int imageInt) {
        this.imageInt = imageInt;
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
