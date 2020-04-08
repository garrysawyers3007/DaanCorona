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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalInfoActivity extends AppCompatActivity {

    private static final int MY_GALLERY_REQUEST_CODE =102 ;
    private static final int STORAGE_PERMISSION_CODE = 103;
    private EditText first_name,last_name,address;
    private Button proceed;
    private CircleImageView userImageView;
    private static final int USER_IMAGE = 100;
    String firstName,lastName,shopAddress;
    Uri userImageURI;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

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

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declaration();
                if(firstName.isEmpty() || lastName.isEmpty() || userImageURI==null || shopAddress.isEmpty())
                    Toast.makeText(PersonalInfoActivity.this,"Enter all details",Toast.LENGTH_SHORT).show();
                else
                    new PersonalInfoActivity.sendDataTask().execute(firstName,lastName,shopAddress);
            }
        });
    }

    private void initializeItems() {

        first_name = findViewById(R.id.firstname);
        last_name = findViewById(R.id.lastname);
        address = findViewById(R.id.address);
        proceed = findViewById(R.id.signin);
        userImageView = findViewById(R.id.user_image);

        userImageView.setImageResource(R.drawable.profile_pic);
    }

    private void declaration() {
        firstName = first_name.getText().toString();
        lastName = last_name.getText().toString();
        shopAddress = address.getText().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == USER_IMAGE) {
            userImageURI = data.getData();
            userImageView.setImageURI(userImageURI);
        }
    }

    class sendDataTask extends AsyncTask<String,Void,String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {

            final OkHttpClient httpClient = new OkHttpClient();

            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");

            Log.d("TAG",""+userImageURI);

            String userPath=getPath(userImageURI);
            Log.d("TAG",""+userPath);

            File user = new File(userPath);

            Log.d("TAG",""+user.getName());

            RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("first_name",strings[0])
                    .addFormDataPart("last_name",strings[1])
                    .addFormDataPart("address",strings[2])
                    .addFormDataPart("recipient_photo",user.getName(),RequestBody.create(MEDIA_TYPE_PNG,user))
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
                Toast.makeText(PersonalInfoActivity.this,s,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PersonalInfoActivity.this, ShopInfoActivity.class);
                startActivity(intent);
                finish();
            }
            else
                Toast.makeText(PersonalInfoActivity.this,"Error",Toast.LENGTH_LONG).show();
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
