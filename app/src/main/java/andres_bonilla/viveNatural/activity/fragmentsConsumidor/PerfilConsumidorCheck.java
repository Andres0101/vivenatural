package andres_bonilla.viveNatural.activity.fragmentsConsumidor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import andres_bonilla.viveNatural.activity.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

import at.markushi.ui.CircleButton;

public class PerfilConsumidorCheck extends Fragment {

    public Activity activity;

    private String userString;
    private Boolean editText;
    private String textoCapturadoDelEditText;

    private ImageView imageConsumer;
    private TextView userName;
    private EditText textoEditable;

    private String imageFile;

    private static int RESULT_LOAD_IMAGE = 1;
    private static final int SELECT_PICTURE = 1;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public PerfilConsumidorCheck() {
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
        View rootView = inflater.inflate(R.layout.fragment_perfil_consumidor, container, false);
        setHasOptionsMenu(true);

        Typeface medium = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Medium.ttf");

        Typeface editTextD = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        userName = (TextView) rootView.findViewById(R.id.nombreUsuario);
        userName.setTypeface(medium);
        textoEditable = (EditText) rootView.findViewById(R.id.textDescription);
        textoEditable.setTypeface(editTextD);

        imageConsumer = (ImageView) rootView.findViewById(R.id.imageConsumer);
        CircleButton addImage = (CircleButton) rootView.findViewById(R.id.iv_camera);
        addImage.setVisibility(View.VISIBLE);
        addImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                permissionToStorage();
            }
        });

        imageFile = "";

        changeText(userString);
        textEditable(editText);

        // Inflate the layout for this fragment
        return rootView;
    }

    //Dialogo que pide permiso para que la app tenga acceso al storage
    private void permissionToStorage() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showMessageOKCancel("Debes permitir el acceso al contenido multimedia de tu dispositivo",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        Uri data = Uri.parse(pictureDirectoryPath);

        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    String pictureDirectoryPath = pictureDirectory.getPath();

                    Uri data = Uri.parse(pictureDirectoryPath);

                    photoPickerIntent.setDataAndType(data, "image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Vive Natural no podrá acceder a tus fotos.", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                //OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                //MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);

                //DEBUG PURPOSE - you can delete this if you want
                if(selectedImagePath != null)
                    System.out.println(selectedImagePath);
                else System.out.println("selectedImagePath is null");
                if(filemanagerstring != null)
                    System.out.println(filemanagerstring);
                else System.out.println("filemanagerstring is null");

                //NOW WE HAVE OUR WANTED STRING
                if(selectedImagePath != null)
                    System.out.println("selectedImagePath is the right one for you!");
                else
                    System.out.println("filemanagerstring is the right one for you!");

                imageConsumer.setImageBitmap(decodeSampledBitmapFromResource(selectedImagePath, 100, 100));
                imageFile = BitMapToString(BitmapFactory.decodeFile(selectedImagePath));
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    //UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public String getImageFile(){
        return imageFile;
    }

    public String getTextoCapturadoDelEditText() {
        textoCapturadoDelEditText = textoEditable.getText().toString();
        System.out.println("EditText value: " + textoCapturadoDelEditText);
        return textoCapturadoDelEditText;
    }

    // Setea el texto con el nombre de usuario
    public void changeText(String text){
        userName.setText(text);
    }
    public void setUserString(String userString) {
        this.userString = userString;
    }


    // Vuelve el editText editable para que el usuario pueda escribir
    public void textEditable(Boolean editable){
        textoEditable.setEnabled(editable);
    }
    public void setEditText(Boolean editText) {
        this.editText = editText;
    }

    // Resize image selected in gallery
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resId, options);
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
