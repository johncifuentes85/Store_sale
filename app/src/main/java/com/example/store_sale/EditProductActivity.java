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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.store_sale.Entities.Product;
import com.example.store_sale.databinding.ActivityEditProductBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityEditProductBinding editProductBinding;
    private Product product;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    EditText etName,etDescription, etStock, etPrice, etCategory;
    TextView tvUri;

    Uri imageUri, downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        editProductBinding = ActivityEditProductBinding.inflate(getLayoutInflater());
        View view = editProductBinding.getRoot();
        setContentView(view);
        editProductBinding.btnUpdate.setOnClickListener(this);
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");
        db = FirebaseFirestore.getInstance();
        editProductBinding.etName.setText(product.getName());
        editProductBinding.etDescription.setText(product.getDescription());
        editProductBinding.etStock.setText(String.valueOf(product.getStock()));
        editProductBinding.etPrice.setText(String.valueOf(product.getPrice()));
        editProductBinding.etCategory.setText(product.getCategory());
        Glide.with(getApplicationContext())
                .load(product.getUri())
                .placeholder(R.drawable.caja)
                .into(editProductBinding.ivProduct);
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
                    //obtenemos el resulado de seleccionar la imagen
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        if(uri != null){
                            editProductBinding.ivProduct.setImageURI(uri);
                            imageUri = uri;
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
                        imageUri = null;
                    }
                }
            }
    );

    @Override
    public void onClick(View v) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading File....");
            progressDialog.show();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
            Date now = new Date();
            String fileName = formatter.format(now);
            storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);
            if(imageUri!=null){
                UploadTask uploadTask = storageReference.putFile(imageUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                       Uri downloadUri = task.getResult();
                        String downloadURL = downloadUri.toString();

                        Map<String, Object> dataProduct = new HashMap<>();
                        dataProduct.put("name", editProductBinding.etName.getText().toString());
                        dataProduct.put("description", editProductBinding.etDescription.getText().toString());
                        dataProduct.put("stock", Integer.parseInt(editProductBinding.etStock.getText().toString()));
                        dataProduct.put("price", Double.parseDouble(editProductBinding.etPrice.getText().toString()));
                        dataProduct.put("category", editProductBinding.etCategory.getText().toString());
                        dataProduct.put("uri",downloadURL);

                        if(v.getId() == editProductBinding.btnUpdate.getId()){
                            db.collection("products")
                                    .document(product.getId())
                                    .update(dataProduct)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "Product update", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(),ListProductActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error updating", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(),ListProductActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        //addProductBinding.tvDownloadUrl.setText("" + downloadUri);

                        //Toast.makeText(getApplicationContext(), "uri: " + downloadURL, Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failures
                        // ...
                    }
                    }

            });
            }else{
                Map<String, Object> dataProduct = new HashMap<>();
                dataProduct.put("name", editProductBinding.etName.getText().toString());
                dataProduct.put("description", editProductBinding.etDescription.getText().toString());
                dataProduct.put("stock", Integer.parseInt(editProductBinding.etStock.getText().toString()));
                dataProduct.put("price", Double.parseDouble(editProductBinding.etPrice.getText().toString()));
                dataProduct.put("category", editProductBinding.etCategory.getText().toString());

                if(v.getId() == editProductBinding.btnUpdate.getId()){
                    db.collection("products")
                            .document(product.getId())
                            .update(dataProduct)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(), "Product update", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),ListProductActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error updating", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(),ListProductActivity.class);
                    startActivity(intent);
                    finish();
                }
            }



    }

    public void menu(View view){
        Intent intent = new Intent(this,ListProductActivity.class);
        startActivity(intent);
        finish();
    }
}