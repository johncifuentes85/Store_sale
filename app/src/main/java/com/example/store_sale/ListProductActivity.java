package com.example.store_sale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.store_sale.Entities.Product;
import com.example.store_sale.ProductAdapter.ProductAdapter;
import com.example.store_sale.databinding.ActivityListProductBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListProductActivity extends AppCompatActivity {


    private ActivityListProductBinding mainBinding;
    private FirebaseFirestore db;

    ArrayList<Product> productArrayList;
    ProductAdapter productAdapter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        getSupportActionBar().hide();



        mainBinding = ActivityListProductBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);
        db = FirebaseFirestore.getInstance();
        productArrayList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productArrayList, db);
        mainBinding.rvProducts.setHasFixedSize(true);
        mainBinding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.rvProducts.setAdapter(productAdapter);



        getProducts();
        //Toast.makeText(getApplicationContext(), "tienda: "+tienda, Toast.LENGTH_SHORT).show();

    }

    public  void getProducts(){

        Context context = getApplicationContext();
        SharedPreferences sharedPref2 = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String tienda = sharedPref2.getString("tienda","");
        //Toast.makeText(getApplicationContext(), "tienda: "+tienda, Toast.LENGTH_SHORT).show();
        TextView nameStore = (TextView)findViewById(R.id.tvNameUser);
        nameStore.setText(tienda);

        db.collection("products").whereEqualTo("shop", tienda)
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
                        productAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void close(View v) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(ListProductActivity.this, "Sesion cerrada", Toast.LENGTH_SHORT).show();
        cerrarLogin();
    }

    public void newProduct(View view) {
        Intent intent = new Intent(this,AddProductActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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