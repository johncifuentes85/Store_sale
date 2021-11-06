package com.example.store_sale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.store_sale.Entities.Product;
import com.example.store_sale.ProductAdapterUser.ProductAdapterUser;
import com.example.store_sale.databinding.ActivityListProductUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListProductUserActivity extends AppCompatActivity {

    private ActivityListProductUserBinding mainBinding;
    private FirebaseFirestore db;

    ArrayList<Product> productArrayList;
    ProductAdapterUser productAdapterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product_user);

        getSupportActionBar().hide();

        mainBinding = ActivityListProductUserBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);
        db = FirebaseFirestore.getInstance();
        productArrayList = new ArrayList<>();
        productAdapterUser = new ProductAdapterUser(this, productArrayList, db);
        mainBinding.rvProducts.setHasFixedSize(true);
        mainBinding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.rvProducts.setAdapter(productAdapterUser);

        getProducts();
        //Toast.makeText(getApplicationContext(), "tienda: "+tienda, Toast.LENGTH_SHORT).show();

    }

    public  void getProducts(){

        Context context = getApplicationContext();
        SharedPreferences sharedPref2 = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String correo = sharedPref2.getString("correo","");
        //Toast.makeText(getApplicationContext(), "tienda: "+tienda, Toast.LENGTH_SHORT).show();
        TextView correoe = (TextView)findViewById(R.id.tvNameUser);
        correoe.setText(correo);

        db.collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Toast.makeText(getApplicationContext(), "Faile to retrive data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for(DocumentChange dc : value.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED){
                                productArrayList.add(dc.getDocument().toObject(Product.class));
                            }
                        }
                        productAdapterUser.notifyDataSetChanged();
                    }
                });
    }

    public void close(View v) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(ListProductUserActivity.this, "Sesion cerrada", Toast.LENGTH_SHORT).show();
        cerrarLogin();
    }


    private void cerrarLogin() {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name","");
        editor.putString("tipo","");
        editor.putString("tienda","");
        editor.putString("correo","");
        editor.putBoolean("session",false);
        editor.commit();

        Intent intent = new Intent(this,SessionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}