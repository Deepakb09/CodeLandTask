package com.deepak.codelandtask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    Button button1;
    TextView textView;
    String url = null;
    String image1 = null;
    ImageView imageView;
    Button button;
    ProgressDialog mProgressDialog;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Uri downloadUrl;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // Get link-URL.
            url = (String) msg.getData().get("url");

            if (url != null) {
                button1.setText("Save image");
                button1.setEnabled(true);
                new DownloadImage().execute(url);
            }
        }
    };

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        String imageURL;
        InputStream input;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Setting dialog title
            mProgressDialog.setTitle("Download Image Tutorial");
            // Setting dialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {
            imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
                SaveImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }


        @Override
        protected void onPostExecute(final Bitmap result) {
            super.onPostExecute(result);
            webView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(result);
            mProgressDialog.dismiss();
            button1.setText("Send Image");
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, SendActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("image", result);
                    startActivity(intent);
                }
            });
        }
    }

    String fname;
    File myDir;

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory(/*Environment.DIRECTORY_DCIM*/).toString();
        myDir = new File(root + "/DeepakApp");
        myDir.mkdirs();
        /*Random generator = new Random();
        int n = 10000;
        //n = generator.nextInt(n);*/
        fname = "Image-"+ String.valueOf(System.currentTimeMillis()) +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        webView = (WebView) findViewById(R.id.webView1);
        button1 = (Button) findViewById(R.id.button1);
        imageView = (ImageView) findViewById(R.id.imageView1); imageView.setVisibility(View.INVISIBLE);

        webView.loadUrl("http://www.google.com");
        webView.setWebViewClient(new WebViewClient());

        webView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = webView.getHitTestResult();
                if (result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    Message msg = mHandler.obtainMessage();
                    webView.requestFocusNodeHref(msg);
                }
                return false;
            }
        });

        if(url == null){
            button1.setEnabled(false);
        }

        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
