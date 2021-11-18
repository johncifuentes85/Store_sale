package com.example.store_sale;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.store_sale.databinding.ActivityAddProductBinding;
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

public class AddProductActivity extends AppCompatActivity {

    Button btnImage, btnAddProduct;
    EditText etName,etDescription, etStock, etPrice, etCategory;
    String tvtienda;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ActivityAddProductBinding addProductBinding;
    Uri imageUri, downloadUrl;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addProductBinding = ActivityAddProductBinding.inflate(getLayoutInflater());

        //getSupportActionBar().hide();
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


        Context context = getApplicationContext();
        SharedPreferences sharedPref2 = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String tienda = sharedPref2.getString("tienda","");
        //Toast.makeText(getApplicationContext(), "tienda: "+tienda, Toast.LENGTH_SHORT).show();
        tvtienda=(tienda);

        this.setTitle(tienda);


    }

    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.overflow, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_close) {
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
        } else if (id == R.id.item_listProduct) {
            Intent intent = new Intent(this,ListProductActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.item_list) {
            Intent intent = new Intent(this,ListBuyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //Toast.makeText(getApplicationContext(), "lista", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.item_new) {
            Intent intent = new Intent(this,AddProductActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
                            addProductBinding.ivProduct.setImageURI(uri);
                            imageUri = uri;
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
        Intent intent = new Intent(this,ListProductActivity.class);
        startActivity(intent);
        finish();
    }

    public void addProduct(View view){

        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        String stock = etStock.getText().toString();
        String price = etPrice.getText().toString();
        String category = etCategory.getText().toString();

        if(imageUri==null){
            Toast.makeText(getApplicationContext(), "Ingrese la imagen de su producto..!!", Toast.LENGTH_SHORT).show();
        }
        else if(name.equals("")){
            Toast.makeText(getApplicationContext(), "Ingrese el nombre de su producto..!!", Toast.LENGTH_SHORT).show();
            etName.setError("Ingrese el nombre de su producto");
            etName.requestFocus();
            etName.setText("");
        }
        else if(description.equals("")){
            Toast.makeText(getApplicationContext(), "Ingrese la descripcion de su producto..!!", Toast.LENGTH_SHORT).show();
            etDescription.setError("Ingrese la descripcion de su producto");
            etDescription.requestFocus();
            etDescription.setText("");
        }
        else if(price.equals("")){
            Toast.makeText(getApplicationContext(), "Ingrese el precio de su producto..!!", Toast.LENGTH_SHORT).show();
            etPrice.setError("Ingrese el precio de su producto");
            etPrice.requestFocus();
            etPrice.setText("");
        }
        else if(stock.equals("")){
            Toast.makeText(getApplicationContext(), "Ingrese las unidades disponibles de su producto..!!", Toast.LENGTH_SHORT).show();
            etStock.setError("Ingrese las unidades disponibles de su producto");
            etStock.requestFocus();
            etStock.setText("");
        }
        else if(category.equals("")){
            Toast.makeText(getApplicationContext(), "Ingrese la categoria de su producto..!!", Toast.LENGTH_SHORT).show();
            etCategory.setError("Ingrese la categoria de su producto");
            etCategory.requestFocus();
            etCategory.setText("");
        }
        else {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);
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

                    Map<String, Object> userData = new HashMap<>();

                    String name = etName.getText().toString();
                    String description = etDescription.getText().toString();
                    int stock = Integer.parseInt(etStock.getText().toString());
                    double price = Double.parseDouble(etPrice.getText().toString());
                    String category = etCategory.getText().toString();
                    String shop = tvtienda;
                    String uri = downloadURL;

                    userData.put("name",name);
                    userData.put("description",description);
                    userData.put("stock",stock);
                    userData.put("price",price); // se envia la informacion
                    userData.put("category",category);
                    userData.put("shop",shop);
                    userData.put("uri",uri);

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
        }
    }
}