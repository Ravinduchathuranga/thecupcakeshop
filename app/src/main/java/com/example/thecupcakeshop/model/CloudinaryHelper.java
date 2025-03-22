package com.example.thecupcakeshop.model;

import android.net.Uri;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;

public class CloudinaryHelper {

    public static void uploadImage(Uri uri, String field, OnUploadCompleteListener listener) {
        MediaManager.get().upload(uri).unsigned("cupcakeshop").callback(new UploadCallback() {
            @Override
            public void onSuccess(String requestId, Map resultData) {
                String url = (String) resultData.get("secure_url");
                listener.onUploadComplete(url);
                Log.e("TEST CODE", "Success");
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.e("TEST CODE", error.getDescription());
            }

            @Override
            public void onStart(String requestId) {
                Log.e("TEST CODE", "Start");
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                Log.e("TEST CODE", "progress");
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Log.e("TEST CODE", "reschdule");
            }
        }).dispatch();
    }

    public interface OnUploadCompleteListener {
        void onUploadComplete(String url);
    }
}
