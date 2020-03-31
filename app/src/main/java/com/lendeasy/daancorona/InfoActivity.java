package com.lendeasy.daancorona;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.IDNA;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoActivity extends AppCompatActivity {

    private EditText shop_name,first_name,last_name,shop_type,address;
    private Button proceed, location;
    private CircleImageView userImageView, shopImage;
    private static final int USER_IMAGE = 100;
    private static final int SHOP_IMAGE = 101;
    String shopName,firstName,lastName,shopType,latitude,longitude,shopAddress;
    float lat,lng;
    Uri userImageURI, shopImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initializeItems();
        declaration();

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                gallery.setType("image/*");
                startActivityForResult(gallery, USER_IMAGE);
            }
        });

        shopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                gallery.setType("image/*");
                startActivityForResult(gallery, SHOP_IMAGE);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        Intent intent1 = getIntent();
        intent1.getFloatExtra("lat",lat);
        intent1.getFloatExtra("lng",lng);
        latitude = Float.toString(lat);
        longitude = Float.toString(lng);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new sendDataTask().execute(firstName,lastName,shopName,shopType,latitude,longitude,shopAddress);
            }
        });

    }

    private void initializeItems() {
        location = findViewById(R.id.shopLocation);
        first_name = findViewById(R.id.firstname);
        last_name = findViewById(R.id.lastname);
        proceed = findViewById(R.id.signin);
        userImageView = findViewById(R.id.user_image);
        shopImage = findViewById(R.id.shop_image);
        shop_name = findViewById(R.id.shopName);
        shop_type = findViewById(R.id.shopType);
        address = findViewById(R.id.address);

        userImageView.setImageResource(R.drawable.ic_launcher_background);
        shopImage.setImageResource(R.drawable.ic_launcher_background);
    }

    private void declaration() {
        firstName = first_name.getText().toString();
        lastName = last_name.getText().toString();
        shopName = shop_name.getText().toString();
        shopType = shop_type.getText().toString();
        shopAddress = address.getText().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == USER_IMAGE) {
            userImageURI = data.getData();
            userImageView.setImageURI(userImageURI);
        }
        if (resultCode == RESULT_OK && requestCode == SHOP_IMAGE) {
            shopImageURI = data.getData();
            shopImage.setImageURI(shopImageURI);
        }
    }

    class sendDataTask extends AsyncTask<String,Void,String>{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {

            final OkHttpClient httpClient = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .addEncoded("first_name",strings[0])
                    .addEncoded("last_name",strings[1])
                    .addEncoded("business_name",strings[2])
                    .addEncoded("business_type",strings[3])
                    .addEncoded("lat",strings[4])
                    .addEncoded("lng",strings[5])
                    .addEncoded("address",strings[6])
                    .build();

            Request request = new Request.Builder()
                    .url("https://daancorona.pythonanywhere.com/api/recepient_profile/")
                    .post(formBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                Log.d("Tag",response.body()+"");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent intent = new Intent(InfoActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}