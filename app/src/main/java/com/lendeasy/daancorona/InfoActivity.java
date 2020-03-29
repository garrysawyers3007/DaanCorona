package com.lendeasy.daancorona;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoActivity extends AppCompatActivity {

    private EditText email,name,shopName,shopDesc;
    private Button proceed,address;
    private CircleImageView userImageView,shopImage;
    private static final int USER_IMAGE = 100;
    private static final int SHOP_IMAGE = 101;
    String username,emailId,shopAddress;
    Uri userImageURI,shopImageURI;

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
                startActivityForResult(gallery,USER_IMAGE);
            }
        });

        shopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                gallery.setType("image/*");
                startActivityForResult(gallery,SHOP_IMAGE);
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (username.isEmpty() || emailId.isEmpty() || shopAddress.isEmpty()){
//                    Toast.makeText(InfoActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
//                }else{
                    Intent intent = new Intent(InfoActivity.this,MainActivity.class);
                    startActivity(intent);
              //  }
            }
        });
    }

    private void declaration() {
        username = name.getText().toString();
        emailId = email.getText().toString();
        shopAddress = address.getText().toString();
    }

    private void initializeItems() {
        email = findViewById(R.id.email);
        address = findViewById(R.id.shopLocation);
        name = findViewById(R.id.name);
        proceed = findViewById(R.id.signin);
        userImageView = findViewById(R.id.user_image);
        shopImage = findViewById(R.id.shop_image);
        shopName = findViewById(R.id.shopName);
        shopDesc = findViewById(R.id.shopDesc);

        userImageView.setImageResource(R.drawable.ic_launcher_background);
        shopImage.setImageResource(R.drawable.ic_launcher_background);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == USER_IMAGE){
            userImageURI = data.getData();
            userImageView.setImageURI(userImageURI);
        }
        if (resultCode == RESULT_OK && requestCode == SHOP_IMAGE){
            shopImageURI = data.getData();
            shopImage.setImageURI(shopImageURI);
        }
    }
}
