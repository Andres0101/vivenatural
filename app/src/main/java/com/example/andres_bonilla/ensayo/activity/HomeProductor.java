package com.example.andres_bonilla.ensayo.activity;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.Product;
import com.example.andres_bonilla.ensayo.activity.classes.Reserve;
import com.example.andres_bonilla.ensayo.activity.classes.User;
import com.example.andres_bonilla.ensayo.activity.fragmentsProductor.EstadisticasFragment;
import com.example.andres_bonilla.ensayo.activity.fragmentsProductor.PerfilFragment;
import com.example.andres_bonilla.ensayo.activity.fragmentsProductor.PerfilFragmentCheck;
import com.example.andres_bonilla.ensayo.activity.fragmentsProductor.ProductosFragment;
import com.example.andres_bonilla.ensayo.activity.fragmentsProductor.ReservasFragment;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class HomeProductor extends AppCompatActivity {

    private Firebase myRef;

    private DrawerLayout drawerLayout;

    private Bitmap largeIcon;

    private ImageView imageUserHeader;

    private PerfilFragment fragmentUno;
    private PerfilFragmentCheck fragmentUnoCheck;

    private ProductosFragment fragmentCuatro;

    private String dataNombre;
    private String datadescripcion;

    private Boolean save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_productor);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        save = false;

        myRef = new Firebase("https://vivenatural.firebaseio.com/");

        largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_vive_natural);

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View vi =  navigationView.getHeaderView(0);
        TextView tv = (TextView) vi.findViewById(R.id.usernameHeader);
        imageUserHeader = (ImageView) vi.findViewById(R.id.profile_image);

        //Setea el texto donde va el nombre de usuario en el header.xml por el puExtra que llega del MainActivity
        dataNombre = getIntent().getExtras().getString("NombreUsuario");
        tv.setText(dataNombre);

        // Lee los datos de las reservas para ver si tiene nuevas
        Firebase reservas = myRef.child("reserves");
        reservas.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Reserve newReserve = dataSnapshot.getValue(Reserve.class);
                //Si el productor tiene nuevas reservas, entonces le llega la notificación
                if (newReserve.getReservadoA().equals(dataNombre)) {
                    // Creates an explicit intent for an Activity in your app
                    Intent intent = new Intent(HomeProductor.this, VerReservaNotification.class);
                    intent.putExtra("nombreDelProductor", dataNombre);
                    // This somehow makes sure, there is only 1 CountDownTimer going if the notification is pressed:
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    // Make this unique ID to make sure there is not generated just a brand new intent with new extra values:
                    int requestID = (int) System.currentTimeMillis();

                    // Pass the unique ID to the resultPendingIntent:
                    PendingIntent pendingIntent = PendingIntent.getActivity(HomeProductor.this, requestID, intent, 0);
                    NotificationManager notificationManager =
                            (NotificationManager) HomeProductor.this.getSystemService(Context.NOTIFICATION_SERVICE);

                    // The stack builder object will contain an artificial back stack for the started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(HomeProductor.this);
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(intent);
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder notification = new NotificationCompat.Builder(HomeProductor.this)
                            .setSmallIcon(R.drawable.ic_launcher_vive_notification)
                            .setLargeIcon(largeIcon)
                            .setContentTitle("Nuevo pedido")
                            .setContentText(newReserve.getReservadoPor() + " te ha reservado " + newReserve.getCantidadReservada() + " de " + newReserve.getProducto())
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(newReserve.getReservadoPor() + " te ha reservado " + newReserve.getCantidadReservada() + " de " + newReserve.getProducto()))
                            .setContentIntent(pendingIntent);

                    // Make the notification play the default notification sound:
                    Uri alarmSound = RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    notification.setSound(alarmSound);
                    notification.setOngoing(true);

                    // mId allows you to update the notification later on.
                    notificationManager.notify(0, notification.build());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            //Elimina el producto de la lista en tiempo real.
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

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
                            imageUserHeader.setImageResource(R.drawable.ic_no_profile_image_header);
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

        Boolean accion = getIntent().getExtras().getBoolean("accion");

        //Empieza con el primer item del navigationView
        //Si se registró entonces va al fragment de perfil
        if (!accion) {
            fragmentUno = new PerfilFragment();
            fragmentUno.setUserString(dataNombre);

            Bundle bundlee = new Bundle();
            bundlee.putString("nombreDelProductor", dataNombre);
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
        } else {//Si inicio sesión entonces va al fragment de estadísticas
            EstadisticasFragment fragmentDos = new EstadisticasFragment();
            android.support.v4.app.FragmentTransaction fragmentTransactionDos = getSupportFragmentManager().beginTransaction();
            fragmentTransactionDos.replace(R.id.container_body, fragmentDos);
            navigationView.getMenu().getItem(1).setChecked(true);

            //Setea el nombre del label
            setTitle(R.string.title_statistics);

            fragmentTransactionDos.commit();
        }

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
                        fragmentUno = new PerfilFragment();
                        fragmentUno.setUserString(dataNombre);

                        Bundle bundlee = new Bundle();
                        bundlee.putString("nombreDelProductor", dataNombre);
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
                    case R.id.statistics:
                        EstadisticasFragment fragmentDos = new EstadisticasFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransactionDos = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionDos.replace(R.id.container_body, fragmentDos);

                        //Setea el nombre del label
                        setTitle(R.string.title_statistics);

                        fragmentTransactionDos.commit();
                        return true;
                    case R.id.reservas:
                        ReservasFragment fragmentTres = new ReservasFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransactionTres = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionTres.replace(R.id.container_body, fragmentTres);

                        Bundle bundleProducts = new Bundle();
                        bundleProducts.putString("nombreDelProductor", dataNombre);
                        // set Fragmentclass Arguments
                        fragmentTres.setArguments(bundleProducts);

                        //Setea el nombre del label
                        setTitle(R.string.title_reservas);

                        fragmentTransactionTres.commit();
                        return true;
                    case R.id.products:
                        fragmentCuatro = new ProductosFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransactionCuatro = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionCuatro.replace(R.id.container_body, fragmentCuatro);

                        Bundle bundle = new Bundle();
                        bundle.putString("nombreDelProductor", dataNombre);
                        // set Fragmentclass Arguments
                        fragmentCuatro.setArguments(bundle);

                        //Setea el nombre del label
                        setTitle(R.string.title_products);

                        fragmentTransactionCuatro.commit();
                        return true;
                    case R.id.log_out:
                        myRef.unauth();
                        Intent intent = new Intent(HomeProductor.this, MainActivity.class);
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
                fragmentUnoCheck = new PerfilFragmentCheck();
                fragmentUnoCheck.setUserString(dataNombre);
                fragmentUnoCheck.setEditText(true);
                android.support.v4.app.FragmentTransaction fragmentTransactionUnoCheck = getSupportFragmentManager().beginTransaction();
                fragmentTransactionUnoCheck.replace(R.id.container_body, fragmentUnoCheck);

                fragmentTransactionUnoCheck.commit();
                return true;

            case R.id.action_save:

                if (!fragmentUnoCheck.getTextoCapturadoDelEditText().equals("")){
                    save = true;

                    //Agrega el texto de descripción al productor(Base de datos)
                    Firebase textoDescripcion = myRef.child("users").child(dataNombre);
                    Map<String, Object> descripcion = new HashMap<>();
                    descripcion.put("descripcion", fragmentUnoCheck.getTextoCapturadoDelEditText());
                    descripcion.put("imagen", fragmentUnoCheck.getImageFile());
                    textoDescripcion.updateChildren(descripcion);

                    //Actualiza la imagen del productor en los productos con la que puso en su perfil.
                    Firebase products = myRef.child("products");
                    products.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                Product product = postSnapshot.getValue(Product.class);

                                if (product.getProductor().equals(dataNombre)) {
                                    Firebase imagenProductor = myRef.child("products").child(dataNombre + ": " + product.getNombreProducto());
                                    Map<String, Object> imagen = new HashMap<>();
                                    imagen.put("imagenProductor", fragmentUnoCheck.getImageFile());
                                    imagenProductor.updateChildren(imagen);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    fragmentUno = new PerfilFragment();
                    fragmentUno.setUserString(dataNombre);

                    Bundle bundlee = new Bundle();
                    bundlee.putString("nombreDelProductor", dataNombre);
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

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
