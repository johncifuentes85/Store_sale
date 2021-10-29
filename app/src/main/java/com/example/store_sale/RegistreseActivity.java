package com.example.store_sale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistreseActivity extends AppCompatActivity {

    Button btnRegistrarse, btnInicio;
    Spinner spinnerPais, spinnerRol;
    EditText etNombres, etCorreo, etContraseña, etCiudad, etTienda;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth;
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrese);

        getSupportActionBar().hide();
        firebaseAuth = FirebaseAuth.getInstance();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this,R.id.etCorreo, Patterns.EMAIL_ADDRESS, R.string.invalid_mail);
        awesomeValidation.addValidation(this,R.id.etContraseña, ".{6,}", R.string.invalid_password);

        btnRegistrarse = findViewById(R.id.btnRegistrarse1);
        btnInicio = findViewById(R.id.btnInicio);
        spinnerPais = findViewById(R.id.spinnerPais);
        spinnerRol = findViewById(R.id.spinnerRol);
        etNombres = findViewById(R.id.etNombres);
        etCorreo = findViewById(R.id.etCorreo);
        etContraseña = findViewById(R.id.etContraseña);
        etCiudad = findViewById(R.id.etCiudad);
        etTienda = findViewById(R.id.etTienda);

        String [] opciones = {"Seleccione su pais", "Afganistán","Albania","Alemania","Andorra","Angola","Antigua y Barbuda","Arabia Saudita","Argelia","Argentina","Armenia","Australia","Austria","Azerbaiyán","Bahamas","Bangladés","Barbados","Baréin","Bélgica","Belice","Benín","Bielorrusia","Birmania","Bolivia","Bosnia y Herzegovina","Botsuana","Brasil","Brunéi","Bulgaria","Burkina Faso","Burundi","Bután","Cabo Verde","Camboya","Camerún","Canadá","Catar","Chad","Chile","China","Chipre","Ciudad del Vaticano","Colombia","Comoras","Corea del Norte","Corea del Sur","Costa de Marfil","Costa Rica","Croacia","Cuba","Dinamarca","Dominica","Ecuador","Egipto","El Salvador","Emiratos Árabes Unidos","Eritrea","Eslovaquia","Eslovenia","España","Estados Unidos","Estonia","Etiopía","Filipinas","Finlandia","Fiyi","Francia","Gabón","Gambia","Georgia","Ghana","Granada","Grecia","Guatemala","Guyana","Guinea","Guinea ecuatorial","Guinea-Bisáu","Haití","Honduras","Hungría","India","Indonesia","Irak","Irán","Irlanda","Islandia","Islas Marshall","Islas Salomón","Israel","Italia","Jamaica","Japón","Jordania","Kazajistán","Kenia","Kirguistán","Kiribati","Kuwait","Laos","Lesoto","Letonia","Líbano","Liberia","Libia","Liechtenstein","Lituania","Luxemburgo","Madagascar","Malasia","Malaui","Maldivas","Malí","Malta","Marruecos","Mauricio","Mauritania","México","Micronesia","Moldavia","Mónaco","Mongolia","Montenegro","Mozambique","Namibia","Nauru","Nepal","Nicaragua","Níger","Nigeria","Noruega","Nueva Zelanda","Omán","Países Bajos","Pakistán","Palaos","Palestina","Panamá","Papúa Nueva Guinea","Paraguay","Perú","Polonia","Portugal","Reino Unido","República Centroafricana","República Checa","República de Macedonia","República del Congo","República Democrática del Congo","República Dominicana","República Sudafricana","Ruanda","Rumanía","Rusia","Samoa","San Cristóbal y Nieves","San Marino","San Vicente y las Granadinas","Santa Lucía","Santo Tomé y Príncipe","Senegal","Serbia","Seychelles","Sierra Leona","Singapur","Siria","Somalia","Sri Lanka","Suazilandia","Sudán","Sudán del Sur","Suecia","Suiza","Surinam","Tailandia","Tanzania","Tayikistán","Timor Oriental","Togo","Tonga","Trinidad y Tobago","Túnez","Turkmenistán","Turquía","Tuvalu","Ucrania","Uganda","Uruguay","Uzbekistán","Vanuatu","Venezuela","Vietnam","Yemen","Yibuti","Zambia","Zimbabue"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_paises, opciones);
        spinnerPais.setAdapter(adapter);

        String [] rol = {"Seleccione su tipo de usuario","Usuario","Vendedor"};

        ArrayAdapter<String> adapterRol = new ArrayAdapter<String>(this, R.layout.spinner_item_paises, rol);
        spinnerRol.setAdapter(adapterRol);

        btnRegistrarse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                String correo = etCorreo.getText().toString();
                String contraseña = etContraseña.getText().toString();
                String tipoUsuario = spinnerRol.getSelectedItem().toString();
                String nombres = etNombres.getText().toString();
                String pais = spinnerPais.getSelectedItem().toString();
                String ciudad = etCiudad.getText().toString();
                String tienda = etTienda.getText().toString();


                if(nombres.equals("")){
                    Toast.makeText(RegistreseActivity.this, "Ingrese su nombre..!!", Toast.LENGTH_SHORT).show();
                    etNombres.setError("ingerse su nombre");
                    etNombres.requestFocus();
                    etNombres.setText("");
                }
                else if(pais == "Seleccione su pais"){
                    Toast.makeText(RegistreseActivity.this, "Seleccione su pais..!!", Toast.LENGTH_SHORT).show();

                }
                else if(ciudad.equals("")){
                    Toast.makeText(RegistreseActivity.this, "Ingrese su ciudad..!!", Toast.LENGTH_SHORT).show();
                    etCiudad.setError("Seleccione su ciudad");
                    etCiudad.requestFocus();
                    etCiudad.setText("");
                }
                else if(tipoUsuario == "Seleccione su tipo de usuario"){
                    Toast.makeText(RegistreseActivity.this, "Seleccione su tipo de usuario..!!", Toast.LENGTH_SHORT).show();
                }
                else if (tienda.equals("") && tipoUsuario == "Vendedor"){
                    Toast.makeText(RegistreseActivity.this, "Ingrese el nombre de su tienda..!!", Toast.LENGTH_SHORT).show();
                    etTienda.setError("Ingrese el nombre de su tienda");
                    etTienda.requestFocus();
                    etTienda.setText("");
                }
                else{
                    if(awesomeValidation.validate()){
                        firebaseAuth.createUserWithEmailAndPassword(correo,contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    regitrarInfo();
                                    Toast.makeText(RegistreseActivity.this, "Usuario creado con exito", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegistreseActivity.this,SessionActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                    dameToastdeerror(errorCode);
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(RegistreseActivity.this, "Complete todos los campos..!!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    public void regitrarInfo(){
        Map<String, Object> userData = new HashMap<>();

        String correo = etCorreo.getText().toString();
        String contraseña = etContraseña.getText().toString();
        String tipoUsuario = spinnerRol.getSelectedItem().toString();
        String nombres = etNombres.getText().toString();
        String pais = spinnerPais.getSelectedItem().toString();
        String ciudad = etCiudad.getText().toString();
        String tienda = etTienda.getText().toString();

        userData.put("correo",correo);
        userData.put("ciudad",ciudad);
        userData.put("contraseña",contraseña);
        userData.put("nombres",nombres); // se envia la informacion
        userData.put("pais",pais);
        userData.put("tienda",tienda);
        userData.put("tipo",tipoUsuario);

        db.collection("usser")
                .add(userData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Usuario agregado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void dameToastdeerror(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(RegistreseActivity.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(RegistreseActivity.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(RegistreseActivity.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(RegistreseActivity.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                etCorreo.setError("La dirección de correo electrónico está mal formateada.");
                etCorreo.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(RegistreseActivity.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                etContraseña.setError("la contraseña es incorrecta ");
                etContraseña.requestFocus();
                etContraseña.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(RegistreseActivity.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(RegistreseActivity.this, "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(RegistreseActivity.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(RegistreseActivity.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                etCorreo.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                etCorreo.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(RegistreseActivity.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(RegistreseActivity.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(RegistreseActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(RegistreseActivity.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(RegistreseActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(RegistreseActivity.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(RegistreseActivity.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                etContraseña.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                etContraseña.requestFocus();
                break;

        }
    }

    public void inicio(View view){
        Intent intent = new Intent(this,SessionActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validar(){
        boolean retorno = true;
        String nombres = etNombres.getText().toString();
        if(nombres.isEmpty()){
            etNombres.setError("ingerse su nombre");
        }
        return retorno;
    }

}