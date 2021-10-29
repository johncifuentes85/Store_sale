package com.example.store_sale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.store_sale.Entities.Product;
import com.example.store_sale.ProductAdapter.ProductAdapter;
import com.example.store_sale.databinding.ActivityListProductBinding;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

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
    }

    public  void getProducts(){
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
                        productAdapter.notifyDataSetChanged();
                    }
                });
    }
}