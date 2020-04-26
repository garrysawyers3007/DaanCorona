package com.codingclub.daancorona;

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

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
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
    private TextView buss_info,pImage;
    private CircleImageView shopImage;
    private static final int SHOP_IMAGE = 101;
    String shopName,shopType,latitude,longitude,MaxCredit,BussAddress;
    double lat,lng;
    Uri shopImageURI,dwnldimageUri;
    String token;
    LoadingDialog dialog;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
//        Toast.makeText(ShopInfoActivity.this,"Enter Shop location first",Toast.LENGTH_LONG).show();

        sharedPref = getSharedPreferences("User",MODE_PRIVATE);
        token=sharedPref.getString("Token","");

        Intent intent1 = getIntent();
        lat=intent1.getDoubleExtra("lat",-1.0);
        lng=intent1.getDoubleExtra("lng",-1.0);
        latitude = Double.toString(lat);
        longitude = Double.toString(lng);

        initializeItems();

        if(sharedPref.getString("Lang","").equals("hin")){
            location.setText(getResources().getString(R.string.shop_location));
            pImage.setText(getResources().getString(R.string.select_buss));
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

                declaration();
                getLocationPermission(LOCATION_PERMISSION_REQUEST_CODE);
            }
        });


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declaration();
                if(shopName.isEmpty() || shopType.isEmpty() || lat==-1.0 || lng==-1.0 ||
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
        pImage=findViewById(R.id.pimg);

        dialog=new LoadingDialog(this);

        shop_name.setText(sharedPref.getString("shopName",""));
        shop_type.setText(sharedPref.getString("shopType",""));
        maxCredit.setText(sharedPref.getString("MaxCredit",""));
        buss_address.setText(sharedPref.getString("BussAddress",""));

        if(!sharedPref.getString("Uri","").equals("")) {
            shopImageURI = Uri.parse(sharedPref.getString("Uri", ""));
            shopImage.setImageURI(shopImageURI);
        }

        dialog.startloadingDialog();
        OkHttpClient httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://daancorona.tech/api/recipient_profile/")
                .addHeader("Authorization","JWT "+token)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                dialog.dismissDialog();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    ShopInfoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                            dialog.dismissDialog();
                            JSONObject jsonObject=new JSONObject(response.body().string());
                            Log.d("dcjsdc",""+jsonObject);
                            shop_name.setText(jsonObject.getString("business_name"));
                            shop_type.setText(jsonObject.getString("business_type"));
                            buss_address.setText(jsonObject.getString("business_address"));
                            maxCredit.setText(String.valueOf(jsonObject.getDouble("max_credit")));

                                shopImageURI=Uri.parse("https://daancorona.tech"+(String) jsonObject.get("business_photo"));

                            if(jsonObject.getString("business_photo").equals("/media/default.jpg"))
                                shopImageURI=null;
                            dwnldimageUri=shopImageURI;

                                if(!sharedPref.getString("shopName","").equals(""))
                                    shop_name.setText(sharedPref.getString("shopName",""));
                                if(!sharedPref.getString("shopType","").equals(""))
                                    shop_type.setText(sharedPref.getString("shopType",""));
                                if(!sharedPref.getString("MaxCredit","").equals(""))
                                    maxCredit.setText(sharedPref.getString("MaxCredit",""));
                                if(!sharedPref.getString("BussAddress","").equals(""))
                                    buss_address.setText(sharedPref.getString("BussAddress",""));


                                lat=jsonObject.getDouble("lat");
                                lng=jsonObject.getDouble("long");
                                latitude = Double.toString(lat);
                                longitude = Double.toString(lng);
                                if(shopImageURI!=null)
                                    Glide.with(ShopInfoActivity.this).load(shopImageURI).into(shopImage);

                        } catch (JSONException | IOException e) {

                                ShopInfoActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!sharedPref.getString("Uri","").equals("")) {
                                            shopImageURI = Uri.parse(sharedPref.getString("Uri", ""));
                                            shopImage.setImageURI(shopImageURI);
                                        }
                                        else
                                            shopImage.setImageDrawable(getResources().getDrawable(R.drawable.shop));
                                        dialog.dismissDialog();
                                    }
                                });

                            e.printStackTrace();
                        }
                        }
                    });


            }
        });
    }

    private void declaration() {
        shopName = shop_name.getText().toString().trim();
        shopType = shop_type.getText().toString().trim();
        MaxCredit=maxCredit.getText().toString().trim();
        BussAddress=buss_address.getText().toString().trim();

        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putString("shopName",shopName);
        editor.putString("shopType",shopType);
        editor.putString("MaxCredit",MaxCredit);
        editor.putString("BussAddress",BussAddress);
        if(shopImageURI!=null)
            editor.putString("Uri",shopImageURI.toString());
        editor.apply();
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

            RequestBody formBody;

            if(dwnldimageUri!=shopImageURI) {

                final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
                //File path = Environment.getExternalStoragePublicDirectory(
                //      Environment.DIRECTORY_PICTURES);

                String shopPath = getPath(shopImageURI);
                Log.d("TAG", "" + shopPath);

                File shop = new File(shopPath);

                formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("business_name", strings[0])
                        .addFormDataPart("business_type", strings[1])
                        .addFormDataPart("lat", strings[2])
                        .addFormDataPart("long", strings[3])
                        .addFormDataPart("max_credit", strings[4])
                        .addFormDataPart("business_address", strings[5])
                        .addFormDataPart("business_photo", shop.getName(), RequestBody.create(MEDIA_TYPE_PNG, shop))
                        .build();
            }
            else {
                formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("business_name", strings[0])
                        .addFormDataPart("business_type", strings[1])
                        .addFormDataPart("lat", strings[2])
                        .addFormDataPart("long", strings[3])
                        .addFormDataPart("max_credit", strings[4])
                        .addFormDataPart("business_address", strings[5])
                        .build();
            }

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

            }catch (SocketTimeoutException | SocketException e){
                e.printStackTrace();
                return "timeout";
            }
            catch (IOException e ) {
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

                if(s.equals("timeout")) {
                    Toast.makeText(ShopInfoActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ShopInfoActivity.this,s,Toast.LENGTH_LONG).show();
                SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPref.edit();
                editor.putBoolean("Page2",true);
                editor.apply();

                Intent intent = new Intent(ShopInfoActivity.this, PaymentModeActivity.class);
                startActivity(intent);
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

        if (requestCode ==  MY_GALLERY_REQUEST_CODE) {
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
