package com.example.store_sale.ProductAdapterUser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.store_sale.BuyActivity;
import com.example.store_sale.Entities.Product;

import com.example.store_sale.R;
import com.example.store_sale.databinding.ProductItemUserBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProductAdapterUser extends RecyclerView.Adapter<ProductAdapterUser.ProductViewHolder> {

    private Context context;
    private ProductItemUserBinding productItemUserBinding;
    private ArrayList<Product> productArrayList;
    private FirebaseFirestore db;

    public ProductAdapterUser(Context context, ArrayList<Product> productArrayList, FirebaseFirestore db) {
        this.context = context;
        this.productArrayList = productArrayList;
        this.db = db;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        productItemUserBinding = productItemUserBinding.inflate(LayoutInflater.from(context));
        return new ProductViewHolder(productItemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productArrayList.get(position);
        holder.itemBinding.tvName.setText(product.getName());
        holder.itemBinding.tvDescription.setText(product.getDescription());
        holder.itemBinding.tvStock.setText(String.valueOf(product.getStock()));
        holder.itemBinding.tvPrice.setText(String.valueOf(product.getPrice()));
        holder.itemBinding.tvCategory.setText(product.getCategory());
        holder.itemBinding.tvNameTienda.setText((product.getShop()));
        Glide.with(context)
                .load(product.getUri())
                .placeholder(R.drawable.caja)
                .error(R.drawable.caja)
                .into(productItemUserBinding.ivImageProduct1);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("products").document(product.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Data delete", Toast.LENGTH_SHORT).show();
                        productArrayList.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        /*holder.itemBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("Esta seguro que quiere eliminar el producto?");
                builder.create().show();
            }
        });*/
        holder.itemBinding.btnAddBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BuyActivity.class);
                intent.putExtra("product", product);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ProductItemUserBinding itemBinding;

        public ProductViewHolder(@NonNull ProductItemUserBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }
}
