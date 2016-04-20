package com.example.andres_bonilla.ensayo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.batch.android.Batch;
import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.User;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Firebase myRef;

    private EditText email, password;

    private TextInputLayout inputLayoutEmail, inputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_login);

        Typeface editText = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Light.ttf");

        Typeface textInput = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Regular.ttf");

        Typeface button = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Bold.ttf");

        // Formulario inicio sesión
        email = (EditText) findViewById(R.id.email);
        email.setTypeface(editText);
        password = (EditText) findViewById(R.id.userpassword);
        password.setTypeface(editText);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutEmail.setTypeface(textInput);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutPassword.setTypeface(textInput);

        email.addTextChangedListener(new MyTextWatcher(email));
        password.addTextChangedListener(new MyTextWatcher(password));

        Firebase.setAndroidContext(this);
        myRef = new Firebase("https://vivenatural.firebaseio.com/");

        // Iniciar sesión
        Button iniciarSesion = (Button) findViewById(R.id.iniciar);
        iniciarSesion.setTypeface(button);
        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!validateEmail()) {
                    return;
                }

                if (!validatePassword()) {
                    return;
                }

                // Dialogo de espera
                final ProgressDialog dlg = new ProgressDialog(MainActivity.this);
                dlg.setTitle("Por favor espere.");
                dlg.setMessage("Iniciando sesión. Por favor espere.");
                dlg.show();

                myRef.authWithPassword(email.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());

                        dlg.dismiss();

                        // Lee los datos de los usuarios
                        Firebase usuarios = myRef.child("users");
                        usuarios.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                System.out.println("Hay " + snapshot.getChildrenCount() + " usuarios");
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    User user = postSnapshot.getValue(User.class);

                                    // Imprime en consola el Nombre y el Rol del usuario que inicia sesión
                                    if (user.getCorreo().equals(email.getText().toString())) {
                                        System.out.println("Usuario que inicio sesión: " + user.getNombre() + " - " + user.getRol());

                                        // Va al home de la app
                                        if (user.getRol().equals("Productor")) {
                                            Intent intent = new Intent(MainActivity.this, HomeProductor.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("NombreUsuario", user.getNombre());  // manda el nombre del usuario que inicia sesión
                                            intent.putExtra("DescripcionUsuario", user.getDescripcion());  // manda el nombre del usuario que inicia sesión
                                            intent.putExtra("accion", true);
                                            startActivity(intent);
                                        } else if (user.getRol().equals("Consumidor")){
                                            Intent intent = new Intent(MainActivity.this, HomeConsumidor.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("NombreUsuario", user.getNombre());  // manda el nombre del usuario que inicia sesión
                                            intent.putExtra("DescripcionUsuario", user.getDescripcion());  // manda el nombre del usuario que inicia sesión
                                            startActivity(intent);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                System.out.println("Falló la lectura de datos: " + firebaseError.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        // Hubo un error
                        dlg.dismiss();

                        switch (firebaseError.getCode()) {
                            case FirebaseError.USER_DOES_NOT_EXIST:
                                // handle a non existing user
                                Toast.makeText(MainActivity.this, "Usuario no existe.", Toast.LENGTH_LONG).show();
                                break;
                            case FirebaseError.INVALID_PASSWORD:
                                // handle an invalid password
                                Toast.makeText(MainActivity.this, "Contraseña incorrecta.", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                // handle other errors
                                break;
                        }

                    }
                });
            }
        });

        // Registro
        Button registro = (Button) findViewById(R.id.registrarse);
        registro.setTypeface(button);
        registro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Registro.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateEmail() {
        String emailText = email.getText().toString().trim();

        if (emailText.isEmpty() || !isValidEmail(emailText)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(email);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (password.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_pass));
            requestFocus(password);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.email:
                    validateEmail();
                    break;
                case R.id.userpassword:
                    validatePassword();
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Batch.onStart(this);
    }

    @Override
    protected void onStop() {
        Batch.onStop(this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Batch.onDestroy(this);

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Batch.onNewIntent(this, intent);

        super.onNewIntent(intent);
    }
}
