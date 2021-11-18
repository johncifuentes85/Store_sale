package com.example.store_sale.ProductAdapter;

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
import com.example.store_sale.EditProductActivity;
import com.example.store_sale.Entities.Product;
import com.example.store_sale.Entities.Shopping;
import com.example.store_sale.R;
import com.example.store_sale.databinding.ProductItemBinding;
import com.example.store_sale.databinding.ProductItemBuyBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ProductViewHolder> {

    private Context context;
    private ProductItemBuyBinding productItemBuyBinding;
    private ArrayList<Shopping> productListArrayList;
    private FirebaseFirestore db;

    public ListProductAdapter(Context context, ArrayList<Shopping> productListArrayList, FirebaseFirestore db) {
        this.context = context;
        this.productListArrayList = productListArrayList;
        this.db = db;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        productItemBuyBinding = productItemBuyBinding.inflate(LayoutInflater.from(context));
        return new ProductViewHolder(productItemBuyBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Shopping shopping = productListArrayList.get(position);
        holder.itemBinding.tvNameListUser.setText(shopping.getName());
        holder.itemBinding.tvCantidadListUser.setText(String.valueOf(shopping.getUnits()));
        DecimalFormat formato = new DecimalFormat("$#,###.###");
        String valorFormateado = formato.format((shopping.getPrice()*shopping.getUnits()));
        holder.itemBinding.tvTotalListUser.setText(valorFormateado);
        holder.itemBinding.tvDireccionListUser.setText(shopping.getDescription());
        holder.itemBinding.tvTiendaListUser.setText(shopping.getShop());
        Glide.with(context)
                .load(shopping.getUri())
                .placeholder(R.drawable.caja)
                .into(productItemBuyBinding.ivImageProductListUser);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("shopping").document(shopping.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Data delete", Toast.LENGTH_SHORT).show();
                        productListArrayList.remove(holder.getAdapterPosition());
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

    }

    @Override
    public int getItemCount() {
        return productListArrayList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ProductItemBuyBinding itemBinding;

        public ProductViewHolder(@NonNull ProductItemBuyBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }
}
