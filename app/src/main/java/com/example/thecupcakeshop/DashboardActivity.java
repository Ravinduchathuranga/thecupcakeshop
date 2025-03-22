package com.example.thecupcakeshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecupcakeshop.adapter.ProductAdapter;
import com.example.thecupcakeshop.adapter.RecentOrdersAdapter;
import com.example.thecupcakeshop.model.ProductModel;
import com.example.thecupcakeshop.model.RecentOrderModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private TextView totalUsers, totalOrders;
    private PieChart pieChart;
    private RecyclerView recyclerViewRecentOrders, recyclerViewProducts;
    private RecentOrdersAdapter recentOrdersAdapter;
    private ProductAdapter productAdapter;
    private List<RecentOrderModel> recentOrdersList = new ArrayList<>();
    private List<ProductModel> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize Views
        totalUsers = findViewById(R.id.totalUsers);
        totalOrders = findViewById(R.id.totalOrders);
        pieChart = findViewById(R.id.pieChart);

        // Initialize RecyclerViews
        recyclerViewRecentOrders = findViewById(R.id.recyclerViewRecentOrders);
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);

        recyclerViewRecentOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));

        recentOrdersAdapter = new RecentOrdersAdapter(recentOrdersList, this);
        productAdapter = new ProductAdapter(productList, this);

        recyclerViewRecentOrders.setAdapter(recentOrdersAdapter);
        recyclerViewProducts.setAdapter(productAdapter);

        // Load data
        loadTotalUsers();
        loadTotalOrders();
        loadRecentOrders();
        loadProductList();
        loadPieChartData();

        // Bottom Navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                // Already on Dashboard
                return true;
            } else if (itemId == R.id.nav_orders) {
                startActivity(new Intent(DashboardActivity.this, RecentOrdersActivity.class));
                return true;
            } else if (itemId == R.id.nav_products) {
                startActivity(new Intent(DashboardActivity.this, ProductListActivity.class));
                return true;
            }
            return false;
        });

        // Floating Action Button (FAB) for Add Product
        FloatingActionButton fabAddProduct = findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, AddProductActivity.class));
        });
    }

    private void loadTotalUsers() {
        firestore.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        totalUsers.setText(String.valueOf(count));
                    } else {
                        totalUsers.setText("0");
                    }
                });
    }

    private void loadTotalOrders() {
        firestore.collection("orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        totalOrders.setText(String.valueOf(count));
                    } else {
                        totalOrders.setText("0");
                    }
                });
    }

    private void loadRecentOrders() {
        firestore.collection("orders")
                .limit(5) // Load only 5 recent orders
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

    private void loadProductList() {
        firestore.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            ProductModel product = snapshot.toObject(ProductModel.class);
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadPieChartData() {
        firestore.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Integer> categoryCountMap = new HashMap<>();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            ProductModel product = snapshot.toObject(ProductModel.class);
                            String category = product.getCategory();
                            categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
                        }

                        List<PieEntry> entries = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
                            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                        }

                        PieDataSet dataSet = new PieDataSet(entries, "Product Categories");
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        dataSet.setValueTextSize(12f);

                        PieData pieData = new PieData(dataSet);
                        pieChart.setData(pieData);
                        pieChart.invalidate();
                    }
                });
    }
}