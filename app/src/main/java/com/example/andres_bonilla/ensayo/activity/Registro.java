package com.example.andres_bonilla.ensayo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.andres_bonilla.ensayo.R;
import com.example.andres_bonilla.ensayo.activity.classes.User;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class Registro extends AppCompatActivity {

    private Firebase myRef;

    private EditText username;
    private EditText password;
    private EditText email;

    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;

    private CheckBox checkProductor, checkConsumidor;

    private TextView rol;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);

        Typeface editText = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Light.ttf");

        Typeface textInput = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Regular.ttf");

        Typeface button = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/Roboto-Bold.ttf");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the padding to match the Status Bar height
        myToolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        setTitle("Registro");

        myRef = new Firebase("https://vivenatural.firebaseio.com/");

        // Formulario de registro
        username = (EditText) findViewById(R.id.username);
        username.setTypeface(editText);
        email = (EditText) findViewById(R.id.email);
        email.setTypeface(editText);
        password = (EditText) findViewById(R.id.contrasena);
        password.setTypeface(editText);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_username);
        inputLayoutName.setTypeface(textInput);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutEmail.setTypeface(textInput);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutPassword.setTypeface(textInput);

        username.addTextChangedListener(new MyTextWatcher(username));
        email.addTextChangedListener(new MyTextWatcher(email));
        password.addTextChangedListener(new MyTextWatcher(password));

        checkProductor = (CheckBox) findViewById(R.id.checkProductor);
        checkProductor.setTypeface(editText);
        checkConsumidor = (CheckBox) findViewById(R.id.checkConsumidor);
        checkConsumidor.setTypeface(editText);

        rol = (TextView) findViewById(R.id.rol);

        // Botón de registrar
        Button registroFormulario = (Button) findViewById(R.id.registrarFormulario);
        registroFormulario.setTypeface(button);
        registroFormulario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!validateName()) {
                    return;
                }

                if (!validateEmail()) {
                    return;
                }

                if (!validatePassword()) {
                    return;
                }

                // Dialogo de espera
                final ProgressDialog dlg = new ProgressDialog(Registro.this);
                dlg.setTitle("Registrando cuenta");
                dlg.setMessage("Por favor espere");
                dlg.show();

                // Crea usuario en base de datos
                myRef.createUser(email.getText().toString(), password.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {

                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        System.out.println("Successfully created user account with uid: " + result.get("uid"));

                        dlg.dismiss();

                        // Va al home del Productor/Consumidor
                        if (rol.getText().toString().equals("Productor")) {
                            Intent intent = new Intent(Registro.this, HomeProductor.class);
                            intent.putExtra("NombreUsuario", username.getText().toString());  // manda el nombre del usuario que se registra
                            intent.putExtra("accion", false);
                            startActivity(intent);
                        } else if (rol.getText().toString().equals("Consumidor")) {
                            Intent intent = new Intent(Registro.this, HomeConsumidor.class);
                            intent.putExtra("NombreUsuario", username.getText().toString());  // manda el nombre del usuario que se registra
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                    }
                });

                // Agrega usuario a la base de datos
                Firebase userRef = myRef.child("users").child(username.getText().toString());
                User newUser = new User(username.getText().toString(), "", email.getText().toString(), rol.getText().toString(), "");
                userRef.setValue(newUser);
            }
        });
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

    public void selectItem(View view){
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.checkConsumidor:
                if(checked){
                    System.out.println("Consumidor");
                    rol.setText("Consumidor");
                    checkProductor.setChecked(false);
                }
                break;
            case R.id.checkProductor:
                if (checked){
                    System.out.println("Productor");
                    rol.setText("Productor");
                    checkConsumidor.setChecked(false);
                }
                break;
        }
    }

    private boolean validateName() {
        if (username.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(username);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String emailtext = email.getText().toString().trim();

        if (emailtext.isEmpty() || !isValidEmail(emailtext)) {
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
                case R.id.username:
                    validateName();
                    break;
                case R.id.email:
                    validateEmail();
                    break;
                case R.id.contrasena:
                    validatePassword();
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
