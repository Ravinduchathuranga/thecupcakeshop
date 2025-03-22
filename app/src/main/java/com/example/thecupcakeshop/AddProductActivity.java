package com.example.thecupcakeshop;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private EditText productNameText, productDescText, productPriceText;
    private Spinner categorySpinner;
    private ImageView imageView;
    private Button addImageBtn;

    private Uri imageUri;
    private FirebaseFirestore firestore;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        imageView = findViewById(R.id.imageViewPreview);
        addImageBtn = findViewById(R.id.addImageButton);
        productNameText = findViewById(R.id.editTextProductName);
        productDescText = findViewById(R.id.editTextProductDescription);
        productPriceText = findViewById(R.id.editTextPrice);
        categorySpinner = findViewById(R.id.spinnerCategory);

        // Set up category spinner
        setupCategorySpinner();

        // Initialize Cloudinary
        Map config = new HashMap();
        config.put("cloud_name", "your_cloud_name"); // Replace with your Cloudinary cloud name
        config.put("api_key", "your_api_key"); // Replace with your Cloudinary API key
        config.put("api_secret", "your_api_secret"); // Replace with your Cloudinary API secret
        config.put("upload_preset", "your_upload_preset"); // Replace with your Cloudinary upload preset
        try {
            MediaManager.get();
        } catch (IllegalStateException e) {
            MediaManager.init(this, config);
        }

        // Set up image picker
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                imageUri = uri;
                imageView.setImageURI(uri);
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        });

        addImageBtn.setOnClickListener(v -> openImagePicker());
        imageView.setOnClickListener(v -> openImagePicker());

        // Confirm button
        Button confirmButton = findViewById(R.id.buttonConfirm);
        confirmButton.setOnClickListener(v -> addProduct());

        // Cancel button
        Button cancelButton = findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void openImagePicker() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void setupCategorySpinner() {
        String[] categories = getResources().getStringArray(R.array.cupcake_categories); // Update array in strings.xml
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_text, categories);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void addProduct() {
        String productName = productNameText.getText().toString().trim();
        String productDesc = productDescText.getText().toString().trim();
        String productCategory = categorySpinner.getSelectedItem().toString();
        String productPrice = productPriceText.getText().toString().trim();

        if (!validateInputs(productName, productDesc, productCategory, productPrice, imageUri)) {
            return;
        }

        saveProductToFirestore(productName, productDesc, productCategory, productPrice);
    }

    private boolean validateInputs(String productName, String productDesc, String productCategory, String productPrice, Uri uri) {
        if (uri == null) {
            showToast("Please add a product image");
            return false;
        } else if (productName.isEmpty()) {
            showToast("Please enter product name");
            return false;
        } else if (productDesc.isEmpty()) {
            showToast("Please enter product description");
            return false;
        } else if (productCategory.equals("Select Category")) {
            showToast("Please select a category");
            return false;
        } else if (productPrice.isEmpty()) {
            showToast("Please enter product price");
            return false;
        } else if (!isDouble(productPrice)) {
            showToast("Please enter a valid price");
            return false;
        }
        return true;
    }

    private boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void saveProductToFirestore(String productName, String productDesc, String productCategory, String productPrice) {
        // Upload image to Cloudinary
        MediaManager.get().upload(imageUri)
                .unsigned("your_upload_preset") // Replace with your upload preset
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        // Upload started
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Upload in progress
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("url");

                        // Save product details to Firestore
                        Map<String, Object> product = new HashMap<>();
                        product.put("name", productName);
                        product.put("description", productDesc);
                        product.put("category", productCategory);
                        product.put("price", Double.parseDouble(productPrice));
                        product.put("imageUrl", imageUrl);

                        firestore.collection("products")
                                .add(product)
                                .addOnSuccessListener(documentReference -> {
                                    showToast("Product added successfully");
                                    finish(); // Close the activity after adding the product
                                })
                                .addOnFailureListener(e -> {
                                    showToast("Error: " + e.getMessage());
                                });
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        showToast("Image upload failed: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // Upload rescheduled
                    }
                })
                .dispatch();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}