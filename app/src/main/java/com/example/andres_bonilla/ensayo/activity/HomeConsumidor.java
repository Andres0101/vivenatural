package com.example.andres_bonilla.ensayo.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Comment;
import com.example.andres_bonilla.ensayo.activity.classes.User;
import com.example.andres_bonilla.ensayo.activity.fragmentsConsumidor.PerfilConsumidor;
import com.example.andres_bonilla.ensayo.activity.fragmentsConsumidor.PerfilConsumidorCheck;
import com.example.andres_bonilla.ensayo.activity.fragmentsConsumidor.Productores;
import com.example.andres_bonilla.ensayo.activity.fragmentsConsumidor.ProductosReservados;
import com.example.andres_bonilla.ensayo.activity.fragmentsConsumidor.ProductsMarket;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class HomeConsumidor extends AppCompatActivity {

    private Firebase myRef;
    private Firebase comments;

    private Typeface light;
    private Typeface medium;

    private DrawerLayout drawerLayout;

    private ImageView imageUserHeader;

    private PerfilConsumidor fragmentUno;
    private PerfilConsumidorCheck fragmentUnoCheck;

    private Productores fragmentDos;
    private ProductosReservados fragmentTres;
    private ProductsMarket fragmentCuatro;

    private String dataNombre;
    private String datadescripcion;

    private Boolean save;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_consumidor);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set the padding to match the Status Bar height
        mToolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        light = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Light.ttf");

        medium = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Medium.ttf");

        save = false;

        myRef = new Firebase("https://vivenatural.firebaseio.com/");
        comments = myRef.child("comments");

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View vi =  navigationView.getHeaderView(0);
        TextView tv = (TextView)vi.findViewById(R.id.usernameHeader);
        tv.setTypeface(medium);
        imageUserHeader = (ImageView) vi.findViewById(R.id.profile_image);

        //Setea el texto donde va el nombre de usuario en el header.xml por el puExtra que llega del MainActivity
        dataNombre = getIntent().getExtras().getString("NombreUsuario");
        tv.setText(dataNombre);

        // Lee los datos de los usuarios del mercado para obtener su imagen de perfil.
        Firebase userImage = myRef.child("users");
        userImage.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);

                    if (user.getNombre().equals(dataNombre)) {
                        if (!user.getImagen().equals("")) {
                            String imageFile = user.getImagen();
                            Bitmap imagenProducto = StringToBitMap(imageFile);

                            imageUserHeader.setImageBitmap(imagenProducto);
                        } else {
                            imageUserHeader.setImageResource(R.drawable.no_image_profile_profile);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Setea el texto donde va el nombre de usuario en el header.xml por el puExtra que llega del MainActivity
        datadescripcion = getIntent().getExtras().getString("DescripcionUsuario");

        //Empieza con el primer item del navigationView
        fragmentUno = new PerfilConsumidor();
        fragmentUno.setUserString(dataNombre);

        Bundle bundlee = new Bundle();
        bundlee.putString("nombreDelConsumidor", dataNombre);
        // set Fragmentclass Arguments
        fragmentUno.setArguments(bundlee);

        if (save) {
            fragmentUno.setTextDescription(fragmentUnoCheck.getTextoCapturadoDelEditText());
        }

        // Setea el texto de descripción con la descripción que existe en la base de datos
        fragmentUno.setTextDescription(datadescripcion);

        android.support.v4.app.FragmentTransaction fragmentInicio = getSupportFragmentManager().beginTransaction();
        fragmentInicio.add(R.id.container_body, fragmentUno);
        navigationView.getMenu().getItem(0).setChecked(true);

        //Setea el nombre del label
        setTitle(R.string.title_perfil);

        fragmentInicio.commit();

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    //Replacing the main content with Fragment Which is our Inbox View;
                    case R.id.profile:
                        fragmentUno = new PerfilConsumidor();
                        fragmentUno.setUserString(dataNombre);

                        Bundle bundlee = new Bundle();
                        bundlee.putString("nombreDelConsumidor", dataNombre);
                        // set Fragmentclass Arguments
                        fragmentUno.setArguments(bundlee);

                        if (save) {
                            fragmentUno.setTextDescription(fragmentUnoCheck.getTextoCapturadoDelEditText());
                        }

                        // Setea el texto de descripción con la descripción que existe en la base de datos
                        fragmentUno.setTextDescription(datadescripcion);

                        android.support.v4.app.FragmentTransaction fragmentTransactionUno = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionUno.replace(R.id.container_body, fragmentUno);

                        //Setea el nombre del label
                        setTitle(R.string.title_perfil);

                        fragmentTransactionUno.commit();

                        return true;
                    case R.id.productores:
                        fragmentDos = new Productores();
                        android.support.v4.app.FragmentTransaction fragmentTransactionDos = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionDos.replace(R.id.container_body, fragmentDos);

                        Bundle bundle = new Bundle();
                        bundle.putString("nombreDelConsumidor", dataNombre);
                        // set Fragmentclass Arguments
                        fragmentDos.setArguments(bundle);

                        //Setea el nombre del label
                        setTitle(R.string.title_productores);

                        fragmentTransactionDos.commit();
                        return true;
                    case R.id.products:
                        fragmentCuatro = new ProductsMarket();
                        android.support.v4.app.FragmentTransaction fragmentTransactionCuatro = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionCuatro.replace(R.id.container_body, fragmentCuatro);

                        Bundle bundleProductMarket = new Bundle();
                        bundleProductMarket.putString("nombreDelConsumidor", dataNombre);
                        // set Fragmentclass Arguments
                        fragmentCuatro.setArguments(bundleProductMarket);

                        //Setea el nombre del label
                        setTitle(R.string.title_products_market);

                        fragmentTransactionCuatro.commit();
                        return true;
                    case R.id.listaMercado:
                        fragmentTres = new ProductosReservados();
                        android.support.v4.app.FragmentTransaction fragmentTransactionTres = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionTres.replace(R.id.container_body, fragmentTres);

                        Bundle bundleProducts = new Bundle();
                        bundleProducts.putString("nombreDelConsumidor", dataNombre);
                        bundleProducts.putBoolean("delete", false);
                        // set Fragmentclass Arguments
                        fragmentTres.setArguments(bundleProducts);

                        //Setea el nombre del label
                        setTitle(R.string.title_lista_mercado);

                        fragmentTransactionTres.commit();
                        return true;
                    case R.id.log_out:
                        myRef.unauth();
                        Intent intent = new Intent(HomeConsumidor.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, mToolbar,R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                //super.onBackPressed();
                return true;

            case R.id.action_edit:
                // User chose the "Settings" item, show the app settings UI...
                fragmentUnoCheck = new PerfilConsumidorCheck();
                fragmentUnoCheck.setUserString(dataNombre);
                fragmentUnoCheck.setEditText(true);
                android.support.v4.app.FragmentTransaction fragmentTransactionUnoCheck = getSupportFragmentManager().beginTransaction();
                fragmentTransactionUnoCheck.replace(R.id.container_body, fragmentUnoCheck);

                Bundle bundleNombre = new Bundle();
                bundleNombre.putString("nombreDelConsumidor", dataNombre);
                // set Fragmentclass Arguments
                fragmentUnoCheck.setArguments(bundleNombre);

                fragmentTransactionUnoCheck.commit();
                return true;

            case R.id.action_save:

                if (!fragmentUnoCheck.getTextoCapturadoDelEditText().equals("")){
                    save = true;

                    //Agrega el texto de descripción y la imagen al productor(Base de datos)
                    Firebase textoDescripcion = myRef.child("users").child(dataNombre);
                    Map<String, Object> descripcion = new HashMap<>();
                    descripcion.put("descripcion", fragmentUnoCheck.getTextoCapturadoDelEditText());
                    descripcion.put("imagen", fragmentUnoCheck.getImageFile());
                    textoDescripcion.updateChildren(descripcion);

                    //Actualiza la imagen del consumidor del comentario con la que puso en su perfil.
                    comments.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Comment comment = postSnapshot.getValue(Comment.class);

                                if (comment.getHechoPor().equals(dataNombre)) {
                                    Firebase imagenConsumidor = myRef.child("comments").child(dataNombre + ": " + comment.getProductoComentado() + " de " + comment.getDirigidoA());
                                    Map<String, Object> imagen = new HashMap<>();
                                    imagen.put("imagenConsumidor", fragmentUnoCheck.getImageFile());
                                    imagenConsumidor.updateChildren(imagen);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    fragmentUno = new PerfilConsumidor();
                    fragmentUno.setUserString(dataNombre);

                    Bundle bundlee = new Bundle();
                    bundlee.putString("nombreDelConsumidor", dataNombre);
                    // set Fragmentclass Arguments
                    fragmentUno.setArguments(bundlee);

                    fragmentUno.setTextDescription(fragmentUnoCheck.getTextoCapturadoDelEditText());
                    android.support.v4.app.FragmentTransaction fragmentTransactionUno = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionUno.replace(R.id.container_body, fragmentUno);

                    //Setea el nombre del label
                    setTitle(R.string.title_perfil);

                    fragmentTransactionUno.commit();
                    Toast.makeText(getApplicationContext(), "Cambio realizado con éxito!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Debes llenar el campo.", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_search:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                //openSearch();
                Toast.makeText(getApplicationContext(), "Buscar fue seleccionada!", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.delete_list_items:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeConsumidor.this);
                // Setting Dialog Title
                builder1.setTitle("Eliminar lista");
                builder1.setIcon(R.drawable.ic_delete);
                builder1.setMessage("Esta acción elimina todos los productos de tu lista de mercado");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Eliminar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                fragmentTres = new ProductosReservados();
                                android.support.v4.app.FragmentTransaction fragmentTransactionTres = getSupportFragmentManager().beginTransaction();
                                fragmentTransactionTres.replace(R.id.container_body, fragmentTres);

                                Bundle bundleProducts = new Bundle();
                                bundleProducts.putString("nombreDelConsumidor", dataNombre);
                                bundleProducts.putBoolean("delete", true);
                                // set Fragmentclass Arguments
                                fragmentTres.setArguments(bundleProducts);

                                //Setea el nombre del label
                                setTitle(R.string.title_lista_mercado);

                                fragmentTransactionTres.commit();
                            }
                        });

                builder1.setNegativeButton(
                        "Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                Dialog alert11 = builder1.create();
                alert11.show();

                //Change message text font
                TextView content = ((TextView) alert11.findViewById(android.R.id.message));
                content.setTypeface(light);
                //Change dialog button text font
                TextView textButtonUno = ((TextView) alert11.findViewById(android.R.id.button1));
                textButtonUno.setTypeface(medium);
                TextView textButtonDos = ((TextView) alert11.findViewById(android.R.id.button2));
                textButtonDos.setTypeface(medium);
                //Change dialog icon color
                ImageView icon = ((ImageView) alert11.findViewById(android.R.id.icon));
                int color = Color.parseColor("#B6B6B6");
                icon.setColorFilter(color);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
