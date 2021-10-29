package com.example.store_sale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.store_sale.Entities.Product;
import com.example.store_sale.databinding.ActivityEditProductBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProductActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityEditProductBinding editProductBinding;
    private Product product;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editProductBinding = ActivityEditProductBinding.inflate(getLayoutInflater());
        View view = editProductBinding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == editProductBinding.btnUpdate.getId()){

        }
    }

    public void menu(View view){
        Intent intent = new Intent(this,ListProductActivity.class);
        startActivity(intent);
        finish();
    }
}