package com.example.lungsoundclassification;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public class MainActivity extends AppCompatActivity {

    private RelativeLayout progressBarOverlay;

    private static final int FILE_PICKER_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBarOverlay = findViewById(R.id.progress_bar_overlay);


        findViewById(R.id.up_upload_audio_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                openFilePicker();

            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/x-wav");
        Intent chooserIntent = Intent.createChooser(intent, "Select WAV audio");
        startActivityForResult(chooserIntent, FILE_PICKER_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the selected file's URI
            Uri fileUri = data.getData();

            Log.e("FileValidation", isFileAccessible(fileUri, this)?"Accessible":"Not Accessible");


            byte[] wavData = null;

            // Create an InputStream from the URI
            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);

                // Create a ByteArrayOutputStream
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                // Create a buffer for reading data
                byte[] buffer = new byte[1024];
                int bytesRead;

                // Read data from the InputStream and write it to the ByteArrayOutputStream
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                // Close the InputStream
                inputStream.close();

                // Get the byte array
                wavData = byteArrayOutputStream.toByteArray();

                // Close the ByteArrayOutputStream
                byteArrayOutputStream.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), wavData);


            RetrofitAPICall apiService = RetrofitClient.getRetrofitInstance().create(RetrofitAPICall.class);

            Call<ResponseObject> call = apiService.sendWav(requestFile);

            showProgressBar();


            byte[] finalWavData = wavData;
            // async call
            call.enqueue(new Callback<ResponseObject>() {
                @Override
                public void onResponse(Call<ResponseObject> call, retrofit2.Response<ResponseObject> response) {
                    if(response.isSuccessful()){

                        hideProgressBar();
                        Toast.makeText(MainActivity.this, "Request successful", Toast.LENGTH_SHORT).show();

                        ResponseObject responseObject = response.body();

                        Intent intent = new Intent(MainActivity.this, DiagnosisActivity.class);

                        intent.putExtra("response_object", responseObject);
                        intent.putExtra("wav_data", finalWavData);

                        startActivity(intent);

                    } else {

                        hideProgressBar();
                        Toast.makeText(MainActivity.this, "Request unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseObject> call, Throwable t) {
                    hideProgressBar();
                    Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
                }

            });



        }
    }

    public static boolean isFileAccessible(Uri uri, Context _context) {
        try {
            // Open the file using FileInputStream
            FileInputStream inputStream = new FileInputStream(_context.getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor());

            // Check if the file is open and ready for reading
            if (inputStream.available() > 0) {
                inputStream.close();
                return true;
            } else {
                inputStream.close();
                return false;
            }
        } catch (IOException e) {
            // Handle the IOException
            e.printStackTrace();
            return false;
        }
    }

    private void showProgressBar() {
        progressBarOverlay.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBarOverlay.setVisibility(View.GONE);
    }


//    private void diagnoseAudio(FileInputStream fileInputStream){
//        RequestQueue volleyQueue = Volley.newRequestQueue(MainActivity.this);
//
//        String url =  "https://mocki.io/v1/36a44058-cf79-47bb-bf83-ef8041ad4456";
//
//        JSONObject jsonData = new JSONObject();
//        try {
//            jsonData.put("audio_stream", encodeFileToBase64(fileInputStream));
//            jsonData.put("key2", "value2");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        showProgressBar();
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.GET,
//                url,
//                null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        hideProgressBar();
//                        Toast.makeText(MainActivity.this, "Request successful", Toast.LENGTH_SHORT).show();
//
//                        Intent intent = new Intent(MainActivity.this, DiagnosisActivity.class);
//
//                        startActivity(intent);
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        hideProgressBar();
//                        Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
//
//
//        volleyQueue.add(jsonObjectRequest);
//
//    }


//    private String encodeFileToBase64(FileInputStream fileInputStream) throws IOException {
//        byte[] fileBytes = new byte[0];
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//            fileBytes = fileInputStream.readAllBytes();
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            return Base64.getEncoder().encodeToString(fileBytes);
//        }
//    }


}