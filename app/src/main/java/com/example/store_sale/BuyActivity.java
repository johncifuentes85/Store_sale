package com.example.store_sale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.store_sale.Entities.Product;
import com.bumptech.glide.Glide;
import com.example.store_sale.databinding.ActivityBuyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.type.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BuyActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityBuyBinding buyBinding;
    private Product product;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    EditText etUnits_buy, etDireccionBuy, tetx;
    Button btnbuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_buy);

        buyBinding = ActivityBuyBinding.inflate(getLayoutInflater());
        View view = buyBinding.getRoot();
        setContentView(view);
        buyBinding.btnUpdateBuy.setOnClickListener(this);
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");
        db = FirebaseFirestore.getInstance();
        buyBinding.tvNamebuy.setText(product.getName());
        buyBinding.etTiendaBuy.setText(product.getShop());
        buyBinding.tvPriceBuy.setText(String.valueOf(product.getPrice()));
        Glide.with(getApplicationContext())
                .load(product.getUri())
                .placeholder(R.drawable.caja)
                .into(buyBinding.ivProductBuy);
    }

    @Override
    public void onClick(View v) {

        Context context = getApplicationContext();
        SharedPreferences sharedPref2 = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String correo = sharedPref2.getString("correo","");

        Date d = new Date();
        CharSequence fechaActual  = DateFormat.format("d-M-yyyy", d.getTime());

        etUnits_buy = findViewById(R.id.etUnitsBuy);
        etDireccionBuy = findViewById(R.id.etDireccion);
        String unidades = etUnits_buy.getText().toString();
        String direccion = etDireccionBuy.getText().toString();

            String id = product.getId();

            //db.collection("products").document(id).get()
            db.collection("products")
                    .document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //Long nombre = document.getLong("name");//Asi se obtienen los datos
                            Long dataStock = document.getLong("stock");
                            //int s = Integer.parseInt(String.valueOf(dataStock));
                            int units = Integer.parseInt(unidades);


                            if(dataStock >= units && units != 0 && direccion != ""){
                                int dato = (int) (dataStock - units);
                                Map<String, Object> dataProduct = new HashMap<>();
                                dataProduct.put("stock",dato);

                                db.collection("products")
                                        .document(product.getId())
                                        .update(dataProduct)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //Toast.makeText(getApplicationContext(), "Product stock", Toast.LENGTH_SHORT).show();
                                                //Intent intent = new Intent(getApplicationContext(),ListProductActivity.class);
                                                //startActivity(intent);
                                                //finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error stock", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                Map<String, Object> userData = new HashMap<>();

                                String name = product.getName();
                                String description = product.getDescription();
                                //int units = Integer.parseInt(unidades);
                                double price = product.getPrice();
                                String category = product.getCategory();
                                String shop = product.getShop();
                                String uri = product.getUri();
                                String user= correo;
                                String fecha = (String) fechaActual;

                                userData.put("name",name);
                                userData.put("description",description);
                                userData.put("units",units);
                                userData.put("price",price); // se envia la informacion
                                userData.put("category",category);
                                userData.put("shop",shop);
                                userData.put("uri",uri);
                                userData.put("user",user);
                                userData.put("fecha",fecha);
                                userData.put("direction", direccion);

                                db.collection("shopping")
                                        .add(userData)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                int total = (int) (units*price);
                                                DecimalFormat formato = new DecimalFormat("$#,###.###");
                                                String valorFormateado = formato.format(total);
                                                TextView confirmation = (TextView)findViewById(R.id.tvConfirmacion);
                                                TextView confirmation1 = (TextView)findViewById(R.id.tvConfirmacion2);
                                                confirmation.setText("Total: "+valorFormateado);
                                                confirmation1.setText("Compra exitosa, para mas detalles visite su lista de compras.");
                                                //Toast.makeText(getApplicationContext(), "Compra procesada con exito...", Toast.LENGTH_SHORT).show();
                                                TimerTask Star = new TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(getApplicationContext(),ListProductUserActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                };
                                                Timer time = new Timer();
                                                time.schedule(Star,2500);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Log.w(TAG, "Error adding document", e);
                                                Toast.makeText(getApplicationContext(), "Error en procesar compra", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                //Toast.makeText(getApplicationContext(), "DocumentSnapshot data: " + document.getData(), Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getApplicationContext(), "DocumentSnapshot data: " + units, Toast.LENGTH_SHORT).show();
                            }else if(units == 0 || direccion == ""){
                                Toast.makeText(getApplicationContext(), "Por favor verifique su dirreccion de entrega y cantidad.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "Lo sentimos el articulo tiene en existencias " +dataStock + " unidades", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "No such document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "get failed with", Toast.LENGTH_SHORT).show();
                    }
                }
            });



    }
}