package com.lendeasy.daancorona;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProfile extends AppCompatActivity {

    private static final int MY_GALLERY_REQUEST_CODE =102 ;
    private static final int STORAGE_PERMISSION_CODE = 103;
    private EditText shop_name,first_name,last_name,shop_type,address,maxcredit,buss_address;
    private Button proceed, location;
    private CircleImageView userImageView, shopImage;
    private static final int USER_IMAGE = 100;
    private static final int SHOP_IMAGE = 101;
    String shopName,firstName,lastName,shopType,latitude,longitude,shopAddress,MaxCredit,BussAddress;
    double lat,lng;
    Uri userImageURI, shopImageURI;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
        token=sharedPref.getString("Token","");

        initializeItems();

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}[0], MY_GALLERY_REQUEST_CODE);
                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("image/*");
                startActivityForResult(gallery, USER_IMAGE);
            }
        });

        shopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}[0], MY_GALLERY_REQUEST_CODE);
                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("image/*");
                startActivityForResult(gallery, SHOP_IMAGE);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent1 = getIntent();
        lat=intent1.getDoubleExtra("lat",0.0);
        lng=intent1.getDoubleExtra("lng",0.0);
        latitude = Double.toString(lat);
        longitude = Double.toString(lng);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declaration();
                new sendDataTask().execute(firstName,lastName,shopName,shopType,latitude,longitude,shopAddress,MaxCredit,BussAddress);
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
        maxcredit=findViewById(R.id.maxcredit);
        buss_address=findViewById(R.id.businessaddress);

        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://daancorona.pythonanywhere.com/api/recipient_profile/")
                .addHeader("Authorization","JWT "+token)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                else
                {
                    try {
                        JSONObject jsonObject=new JSONObject(response.body().string());
                        shopName=jsonObject.getString("business_name");
                        firstName=jsonObject.getString("first_name");
                        lastName=jsonObject.getString("last_name");
                        shopType=jsonObject.getString("business_type");
                        latitude=jsonObject.getString("lat");
                        longitude=jsonObject.getString("long");
                        shopAddress=jsonObject.getString("address");
                        MaxCredit=jsonObject.getString("max_credit");
                        BussAddress=jsonObject.getString("business_address");

                        setData(shopName,firstName,lastName,shopType,shopAddress,MaxCredit,BussAddress);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Request request1 = new Request.Builder()
                .url("http://daancorona.pythonanywhere.com/api/recipient_profile/recepient_photo")
                .addHeader("Authorization","JWT "+token)
                .build();

        httpClient.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                else
                {
                    try {
                        final Bitmap userBitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                userImageView.setImageBitmap(userBitmap);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Request request2 = new Request.Builder()
                .url("http://daancorona.pythonanywhere.com/api/recipient_profile/business_photo")
                .addHeader("Authorization","JWT "+token)
                .build();

        httpClient.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                else
                {
                    try {
                        final Bitmap shopBitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                shopImage.setImageBitmap(shopBitmap);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        userImageView.setImageResource(R.drawable.ic_launcher_background);
        shopImage.setImageResource(R.drawable.ic_launcher_background);
    }

    private void setData(String shopName, String firstName, String lastName, String shopType, String shopAddress, String maxCredit, String bussAddress) {
        shop_name.setText(shopName);
        first_name.setText(firstName);
        last_name.setText(lastName);
        shop_type.setText(shopType);
        address.setText(shopAddress);
        maxcredit.setText(maxCredit);
        buss_address.setText(bussAddress);
    }

    private void declaration() {

        firstName = first_name.getText().toString();
        lastName = last_name.getText().toString();
        shopName = shop_name.getText().toString();
        shopType = shop_type.getText().toString();
        shopAddress = address.getText().toString();
        MaxCredit=maxcredit.getText().toString();
        BussAddress=buss_address.getText().toString();
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

    class sendDataTask extends AsyncTask<String,Void,String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {

            final OkHttpClient httpClient = new OkHttpClient();

            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
            //File path = Environment.getExternalStoragePublicDirectory(
            //      Environment.DIRECTORY_PICTURES);

            Log.d("TAG",""+userImageURI);

            String userpath=getPath(userImageURI),shoppath=getPath(shopImageURI);
            Log.d("TAG",""+userpath);
            Log.d("TAG",""+shoppath);

            File user = new File(userpath);
            File shop = new File(shoppath);

            Log.d("TAG",""+user.getName());

            RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("first_name",strings[0])
                    .addFormDataPart("last_name",strings[1])
                    .addFormDataPart("business_name",strings[2])
                    .addFormDataPart("business_type",strings[3])
                    .addFormDataPart("lat",strings[4])
                    .addFormDataPart("long",strings[5])
                    .addFormDataPart("address",strings[6])
                    .addFormDataPart("max_credit",strings[7])
                    .addFormDataPart("business_address",strings[8])
                    .addFormDataPart("recipient_photo",user.getName(),RequestBody.create(MEDIA_TYPE_PNG,user))
                    .addFormDataPart("business_photo",shop.getName(),RequestBody.create(MEDIA_TYPE_PNG,shop))
                    .build();

            Request request = new Request.Builder()
                    .url("http://daancorona.pythonanywhere.com/api/recipient_profile/")
                    .addHeader("Authorization","JWT "+token)
                    .post(formBody)
                    .build();


            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                Log.d("Tag",response.body()+"");
                return "Done";

            } catch (IOException e ) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null) {

                Toast.makeText(EditProfile.this,s,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditProfile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else
                Toast.makeText(EditProfile.this,"Error",Toast.LENGTH_LONG).show();
        }
    }
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            this,
                            new String[] { permission },
                            requestCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);


        if (requestCode ==  STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


}
