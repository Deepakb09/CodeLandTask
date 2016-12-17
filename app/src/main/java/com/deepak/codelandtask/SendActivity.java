package com.deepak.codelandtask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SendActivity extends AppCompatActivity {
    ImageView imageView;
    Button emailButton, galleryButton;// shareButton;

    String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.imageView2);
        emailButton = (Button) findViewById(R.id.emailButton);
        //galleryButton = (Button) findViewById(R.id.galleryButton);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        picturePath = bundle.getString("url");
        Bitmap image = bundle.getParcelable("image");
        imageView.setImageBitmap(image);
        Toast.makeText(SendActivity.this, "path is : "+picturePath, Toast.LENGTH_SHORT).show();
        final String body = "<a href=\"" + picturePath + "\">" + picturePath+ "</a>";

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "android@abcxyz.com" });
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Image");
                /*emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(picturePath));*/
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Please click to view image -> ");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body));
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });
    }
}
