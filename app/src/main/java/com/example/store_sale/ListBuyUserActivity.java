package com.example.store_sale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.store_sale.Entities.Shopping;
import com.example.store_sale.ProductAdapter.ListProductAdapter;
import com.example.store_sale.ProductAdapterUser.ProductListAdapterUser;
import com.example.store_sale.databinding.ActivityListBuyBinding;
import com.example.store_sale.databinding.ActivityListBuyUserBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListBuyUserActivity extends AppCompatActivity {

    private ActivityListBuyUserBinding mainBinding;
    private FirebaseFirestore db;

    ArrayList<Shopping> productListArrayList;
    ProductListAdapterUser productListAdapterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_buy_user);

        mainBinding = ActivityListBuyUserBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);
        db = FirebaseFirestore.getInstance();
        productListArrayList = new ArrayList<>();
        productListAdapterUser = new ProductListAdapterUser(this, productListArrayList, db);
        mainBinding.rvListProductUser.setHasFixedSize(true);
        mainBinding.rvListProductUser.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.rvListProductUser.setAdapter(productListAdapterUser);

        getProducts();
    }

    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.overflowuser, menu);
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
        } else if (id == R.id.item_list) {
            Intent intent = new Intent(this,ListBuyUserActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //Toast.makeText(getApplicationContext(), "lista", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.item_listProduct) {
            Intent intent = new Intent(this,ListProductUserActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //Toast.makeText(getApplicationContext(), "lista", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public  void getProducts(){

        Context context = getApplicationContext();
        SharedPreferences sharedPref2 = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String correo = sharedPref2.getString("correo","");
        //Toast.makeText(getApplicationContext(), "tienda: "+tienda, Toast.LENGTH_SHORT).show();
        this.setTitle(correo);

        db.collection("shopping").whereEqualTo("user", correo)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Toast.makeText(getApplicationContext(), "Faile to retrive data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for(DocumentChange dc : value.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED){
                                productListArrayList.add(dc.getDocument().toObject(Shopping.class));
                            }
                        }
                        productListAdapterUser.notifyDataSetChanged();
                    }
                });
    }
}