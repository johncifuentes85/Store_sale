package com.example.store_sale;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.store_sale.databinding.ActivityAddProductBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding addProductBinding;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addProductBinding = ActivityAddProductBinding.inflate(getLayoutInflater());

        getSupportActionBar().hide();
        View v =addProductBinding.getRoot();
        setContentView(v);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public void selectImageFromGallery(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //obstenemos el resulado de seleccionar la imagen
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        if(uri != null){
                            addProductBinding.ivProduct.setImageURI(uri);
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    public void createProduct(View view){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creando producto");
        progressDialog.show();
    }

    public void menu(View view){
        Intent intent = new Intent(this,InicioActivity.class);
        startActivity(intent);
        finish();
    }
}