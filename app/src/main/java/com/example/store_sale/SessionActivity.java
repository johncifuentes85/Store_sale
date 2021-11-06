package com.example.store_sale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.store_sale.Entities.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SessionActivity extends AppCompatActivity {

    Button btnEntrar, btnRegistrese;
    EditText etCorreosession, etContraseñasession;

    AwesomeValidation awesomeValidation;
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    ArrayList<Product> productArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        getSupportActionBar().hide();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser User = mAuth.getCurrentUser();
        if(User != null){
            //irahome();
    }
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this,R.id.etCorreo, Patterns.EMAIL_ADDRESS, R.string.invalid_mail);
        awesomeValidation.addValidation(this,R.id.etContraseña, ".{6,}", R.string.invalid_password);

        btnEntrar = findViewById(R.id.btnEntrar);
        btnRegistrese = findViewById(R.id.btnRegistrarse1);
        etCorreosession = findViewById(R.id.etCorreosession);
        etContraseñasession = findViewById(R.id.etContraseñasession);


        btnEntrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String correo = etCorreosession.getText().toString();
                String contraseña =etContraseñasession.getText().toString();


                //se hace el sharepreference
                db.collection("usser").whereEqualTo("correo", correo)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String name = (String) document.get("nombres");//asi se estraen los datos.
                                        String tipo = (String) document.get("tipo");//asi se estraen los datos.
                                        String tienda = (String) document.get("tienda");//asi se estraen los datos.
                                        //Toast.makeText(getApplicationContext(), "Exito...", Toast.LENGTH_SHORT).show();

                                        //se crean los datos para manejar la sesion
                                        Context context = getApplicationContext();
                                        SharedPreferences sharedPref = context.getSharedPreferences(
                                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("name",name);
                                        editor.putString("tipo",tipo);
                                        editor.putString("tienda",tienda);
                                        editor.putString("correo",correo);
                                        editor.putBoolean("session",true);
                                        editor.commit();

                                        //Asi se trae la infoamcion
                                        /*String nombres = sharedPref.getString("name","");
                                        String tupousuario = sharedPref.getString("tipo","");
                                        String nombretienda = sharedPref.getString("tienda","");
                                        String correoelect = sharedPref.getString("correo","");
                                        Toast.makeText(getApplicationContext(), "informacion..."+nombres+"-"+tupousuario+"-"+nombretienda+"-"+correoelect, Toast.LENGTH_SHORT).show();*/
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                if(correo.equals("")){
                    Toast.makeText(SessionActivity.this, "Ingrese su contraseña..!!", Toast.LENGTH_SHORT).show();
                    etCorreosession.setError("Ingrese su Correo electronico");
                    etCorreosession.requestFocus();
                    etCorreosession.setText("");
                }
                else if(contraseña.equals("")){
                    Toast.makeText(SessionActivity.this, "Ingrese su contraseña..!!", Toast.LENGTH_SHORT).show();
                    etContraseñasession.setError("Ingrese su Correo electronico");
                    etContraseñasession.requestFocus();
                    etContraseñasession.setText("");
                }
                else if(awesomeValidation.validate()){
                    firebaseAuth.signInWithEmailAndPassword(correo, contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //irahome();
                                Context context = getApplicationContext();
                                SharedPreferences sharedPref = context.getSharedPreferences(
                                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                String tipousuario = sharedPref.getString("tipo","");
                                //Toast.makeText(getApplicationContext(), "tipo: "+tipousuario, Toast.LENGTH_SHORT).show();
                                String t = "Vendedor";
                                if(tipousuario.equals("Usuario")){
                                    listUser();
                                }
                                else if(tipousuario.equals("Vendedor")){
                                    list();
                                }

                            }
                            else {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                dameToastdeerror(errorCode);
                            }
                        }

                    });
                }

            }


        });

    }

    private void irahome() {
        Intent intent = new Intent(this,InicioActivity.class);
        intent.putExtra("mail", etCorreosession.getText().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void listUser(){
        Intent intent = new Intent(this,ListProductUserActivity.class);
        startActivity(intent);
    }

    private void list(){
        Intent intent1 = new Intent(this,ListProductActivity.class);
        startActivity(intent1);
    }

    public void registrese(View view){
        Intent intent = new Intent(this,RegistreseActivity.class);
        startActivity(intent);
        finish();
    }

    private void dameToastdeerror(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(SessionActivity.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(SessionActivity.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(SessionActivity.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(SessionActivity.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                etCorreosession.setError("La dirección de correo electrónico está mal formateada.");
                etCorreosession.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(SessionActivity.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                etContraseñasession.setError("la contraseña es incorrecta ");
                etContraseñasession.requestFocus();
                etContraseñasession.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(SessionActivity.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(SessionActivity.this, "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(SessionActivity.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(SessionActivity.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                etCorreosession.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                etCorreosession.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(SessionActivity.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(SessionActivity.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(SessionActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(SessionActivity.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(SessionActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(SessionActivity.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(SessionActivity.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                etContraseñasession.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                etContraseñasession.requestFocus();
                break;

        }
    }

}