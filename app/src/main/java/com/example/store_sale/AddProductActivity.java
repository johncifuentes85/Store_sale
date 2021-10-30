package com.example.store_sale;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.store_sale.databinding.ActivityAddProductBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    Button btnImage, btnAddProduct;
    EditText etName,etDescription, etStock, etPrice, etCategory;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnImage = findViewById(R.id.btnImage);
        etName = findViewById(R.id.etProductName);
        etDescription = findViewById(R.id.etProductDescription);
        etStock = findViewById(R.id.etProducStock);
        etPrice = findViewById(R.id.etProductPrice);
        etCategory = findViewById(R.id.etProducCategory);
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

    public void addProduct(View view){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creando producto");
        progressDialog.show();

        Map<String, Object> userData = new HashMap<>();

        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        int stock = Integer.parseInt(etStock.getText().toString());
        double price = Double.parseDouble(etPrice.getText().toString());
        String category = etCategory.getText().toString();

        userData.put("name",name);
        userData.put("description",description);
        userData.put("stock",stock);
        userData.put("price",price); // se envia la informacion
        userData.put("category",category);

        db.collection("products")
                .add(userData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Producto Creado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });

        Intent intent = new Intent(getApplicationContext(),InicioActivity.class);
        startActivity(intent);
        finish();

    }
}