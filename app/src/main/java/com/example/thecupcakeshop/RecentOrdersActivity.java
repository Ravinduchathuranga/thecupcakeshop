package com.example.thecupcakeshop;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecupcakeshop.adapter.RecentOrdersAdapter;
import com.example.thecupcakeshop.model.RecentOrderModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecentOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecentOrders;
    private RecentOrdersAdapter recentOrdersAdapter;
    private List<RecentOrderModel> recentOrdersList = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recent_orders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerViewRecentOrders = findViewById(R.id.recyclerViewRecentOrders);
        recyclerViewRecentOrders.setLayoutManager(new LinearLayoutManager(this));

        recentOrdersAdapter = new RecentOrdersAdapter(recentOrdersList, this);
        recyclerViewRecentOrders.setAdapter(recentOrdersAdapter);

        // Load recent orders
        loadRecentOrders();
    }

    private void loadRecentOrders() {
        firestore.collection("orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recentOrdersList.clear();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            RecentOrderModel order = snapshot.toObject(RecentOrderModel.class);
                            recentOrdersList.add(order);
                        }
                        recentOrdersAdapter.notifyDataSetChanged();
                    }
                });
    }
}