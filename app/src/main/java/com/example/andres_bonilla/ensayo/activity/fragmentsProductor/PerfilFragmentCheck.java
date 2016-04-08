package com.example.andres_bonilla.ensayo.activity.fragmentsProductor;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import com.example.andres_bonilla.ensayo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

import at.markushi.ui.CircleButton;

/**
 * Created by ANDRES_BONILLA on 3/14/16.
 */
public class PerfilFragmentCheck extends Fragment {

    public Activity activity;

    private String userString;
    private Boolean editText;
    private String textoCapturadoDelEditText;

    private ImageView imageProducer;
    private TextView userName;
    private EditText textoEditable;

    private String imageFile;

    private static int RESULT_LOAD_IMAGE = 1;

    public PerfilFragmentCheck() {
        // Required empty public constructor
        userString = "none";
        editText = false;

        textoCapturadoDelEditText = "No hay información.";
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

        userName = (TextView) rootView.findViewById(R.id.nombreUsuario);
        userName.setTypeface(textView);
        TextView textDescriptionTittle = (TextView) rootView.findViewById(R.id.descriptionTittle);
        textDescriptionTittle.setTypeface(textView);
        textoEditable = (EditText) rootView.findViewById(R.id.textDescription);
        textoEditable.setTypeface(editTextD);

        imageProducer = (ImageView) rootView.findViewById(R.id.imageProducer);
        CircleButton addImage = (CircleButton) rootView.findViewById(R.id.iv_camera);
        addImage.setVisibility(View.VISIBLE);
        addImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();

                Uri data = Uri.parse(pictureDirectoryPath);

                photoPickerIntent.setDataAndType(data, "image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
            }
        });

        changeText(userString);
        textEditable(editText);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageProducer.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            imageFile = BitMapToString(BitmapFactory.decodeFile(picturePath));
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public String getImageFile(){
        return imageFile;
    }
    public Bitmap getImageBitmap(){
        Bitmap imageBitmap = StringToBitMap(imageFile);
        return imageBitmap;
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

    public String getTextoCapturadoDelEditText() {
        textoCapturadoDelEditText = textoEditable.getText().toString();
        System.out.println("EditText value: " + textoCapturadoDelEditText);
        return textoCapturadoDelEditText;
    }

    // Setea el texto con el nombre de usuario
    private void changeText(String text){
        userName.setText(text);
    }
    public void setUserString(String userString) {
        this.userString = userString;
    }


    // Vuelve el editText editable para que el usuario pueda escribir
    private void textEditable(Boolean editable){
        textoEditable.setEnabled(editable);
    }
    public void setEditText(Boolean editText) {
        this.editText = editText;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();    //remove all items
        getActivity().getMenuInflater().inflate(R.menu.menu_guardar, menu);
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
