package com.example.thecupcakeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecupcakeshop.R;
import com.example.thecupcakeshop.model.RecentOrderModel;

import java.util.List;


public class RecentOrdersAdapter extends RecyclerView.Adapter<RecentOrdersAdapter.ViewHolder> {

    private List<RecentOrderModel> recentOrdersList;
    private Context context;

    public RecentOrdersAdapter(List<RecentOrderModel> recentOrdersList, Context context) {
        this.recentOrdersList = recentOrdersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentOrderModel order = recentOrdersList.get(position);
        holder.customerName.setText(order.getCustomerName());
        holder.productName.setText(order.getProductName());
        holder.totalPrice.setText(String.format("$%.2f", order.getTotalPrice()));
        holder.orderDate.setText(order.getOrderDate());
    }

    @Override
    public int getItemCount() {
        return recentOrdersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, productName, totalPrice, orderDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.textCustomerName);
            productName = itemView.findViewById(R.id.textProductName);
            totalPrice = itemView.findViewById(R.id.textTotalPrice);
            orderDate = itemView.findViewById(R.id.textOrderDate);
        }
    }
}