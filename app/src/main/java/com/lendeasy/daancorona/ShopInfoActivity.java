package com.lendeasy.daancorona;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShopInfoActivity extends AppCompatActivity {

    private static final int MY_GALLERY_REQUEST_CODE =102 ;
    private static final int STORAGE_PERMISSION_CODE = 103;
    private static final int LOCATION_PERMISSION_REQUEST_CODE =1234;
    private EditText shop_name,shop_type,maxCredit,buss_address;
    private TextInputLayout shop_name1,shop_type1,maxCredit1,buss_address1;
    private Button proceed, location;
    private TextView buss_info;
    private CircleImageView shopImage;
    private static final int SHOP_IMAGE = 101;
    String shopName,shopType,latitude,longitude,MaxCredit,BussAddress;
    double lat,lng;
    Uri shopImageURI;
    String token;
    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
//        Toast.makeText(ShopInfoActivity.this,"Enter Shop location first",Toast.LENGTH_LONG).show();

        SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
        token=sharedPref.getString("Token","");

        initializeItems();

        if(sharedPref.getString("Lang","").equals("hin")){
            location.setText(getResources().getString(R.string.shop_location));
            proceed.setText(getResources().getString(R.string.proceed));
            buss_info.setText(getResources().getString(R.string.buss_info));
            shop_name1.setHint(getResources().getString(R.string.bussname));
            shop_type1.setHint(getResources().getString(R.string.busstype));
            buss_address1.setHint(getResources().getString(R.string.bussaddr));
            maxCredit1.setHint(getResources().getString(R.string.maxcredit));
        }

        shopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}[0], MY_GALLERY_REQUEST_CODE);

            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission(LOCATION_PERMISSION_REQUEST_CODE);
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
                if(shopName.isEmpty() || shopType.isEmpty() ||
                        latitude.isEmpty() || longitude.isEmpty() || MaxCredit.isEmpty()
                        || BussAddress.isEmpty() || shopImageURI==null)
                    Toast.makeText(ShopInfoActivity.this,"Enter all details",Toast.LENGTH_SHORT).show();
                else{
                    dialog.startloadingDialog();
                    new ShopInfoActivity.sendDataTask().execute(shopName,shopType,latitude,
                            longitude,MaxCredit,BussAddress);
//
//                    Intent i = new Intent(ShopInfoActivity.this, PaymentModeActivity.class);
//                    startActivity(i);

                }

            }
        });
    }

    private void initializeItems() {

        location = findViewById(R.id.shopLocation);
        proceed = findViewById(R.id.signin);
        shopImage = findViewById(R.id.shop_image);
        shop_name1=findViewById(R.id.shopName1);
        shop_name = findViewById(R.id.shopName);
        shop_type1=findViewById(R.id.shopType1);
        shop_type = findViewById(R.id.shopType);
        maxCredit=findViewById(R.id.maxcredit);
        maxCredit1=findViewById(R.id.maxcredit1);
        buss_address=findViewById(R.id.businessaddress);
        buss_address1=findViewById(R.id.businessaddress1);
        buss_info=findViewById(R.id.bussinfo);

        shopImage.setImageResource(R.drawable.ic_launcher_background);
        dialog=new LoadingDialog(this);
    }

    private void declaration() {
        shopName = shop_name.getText().toString();
        shopType = shop_type.getText().toString();
        MaxCredit=maxCredit.getText().toString();
        BussAddress=buss_address.getText().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

            String shopPath=getPath(shopImageURI);
            Log.d("TAG",""+shopPath);

            File shop = new File(shopPath);

            RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("business_name",strings[0])
                    .addFormDataPart("business_type",strings[1])
                    .addFormDataPart("lat",strings[2])
                    .addFormDataPart("long",strings[3])
                    .addFormDataPart("max_credit",strings[4])
                    .addFormDataPart("business_address",strings[5])
                    .addFormDataPart("business_photo",shop.getName(),RequestBody.create(MEDIA_TYPE_PNG,shop))
                    .build();

            Request request = new Request.Builder()
                    .url("https://daancorona.tech/api/recipient_profile/")
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
            dialog.dismissDialog();
            if(s!=null) {
                Toast.makeText(ShopInfoActivity.this,s,Toast.LENGTH_LONG).show();
                SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPref.edit();
                editor.putBoolean("Page2",true);
                editor.apply();

                Intent intent = new Intent(ShopInfoActivity.this, PaymentModeActivity.class);
                startActivity(intent);
                finish();
            }
            else
                Toast.makeText(ShopInfoActivity.this,"Error",Toast.LENGTH_LONG).show();
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
        else{
            if(requestCode== MY_GALLERY_REQUEST_CODE){

                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("image/*");
                startActivityForResult(gallery, SHOP_IMAGE);
            }
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

                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("image/*");
                startActivityForResult(gallery, SHOP_IMAGE);

            }
            else {
                Toast.makeText(this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else{
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "Location Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(ShopInfoActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(this,
                        "Location Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void getLocationPermission(int requestCode){
        Log.d("isnull","Null");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }else{
            if(requestCode== LOCATION_PERMISSION_REQUEST_CODE){
                Intent intent = new Intent(ShopInfoActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

}
