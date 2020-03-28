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

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoActivity extends AppCompatActivity {

    private EditText email,address,name;
    private Button proceed;
    private CircleImageView userImageView,shopImage;
    private static final int USER_IMAGE = 100;
    private static final int SHOP_IMAGE = 101;
    private boolean userImageSelected = false;
    Uri userImageURI,shopImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initializeItems();

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
    }

    private void initializeItems() {
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        name = findViewById(R.id.name);
        proceed = findViewById(R.id.signin);
        userImageView = findViewById(R.id.user_image);
        shopImage = findViewById(R.id.shop_image);

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
