package net.aayush.skooterapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;

import uk.co.senab.photoview.PhotoViewAttacher;


public class ViewImage extends BaseActivity {
    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        final ImageView largeImage = (ImageView) findViewById(R.id.large_image);

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("IMAGE_URL");

        final int sdkVersion = Build.VERSION.SDK_INT;

        if (sdkVersion >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.md_black_1000));
            window.setNavigationBarColor(getResources().getColor(R.color.md_black_1000));
        }
        imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    Bitmap image = response.getBitmap();
                    int nh;
                    int nw;
                    Bitmap scaled = response.getBitmap();
                    if(image.getWidth() > 2048 && image.getWidth() > image.getHeight()) {
                        nh = (int) (image.getHeight() * (2048.0 / image.getWidth()));
                        scaled = Bitmap.createScaledBitmap(image, 2048, nh, true);
                    } else if (image.getHeight() > 2048 && image.getHeight() > image.getWidth()) {
                        nw = (int) (image.getWidth() * (2048.0 / image.getHeight()));
                        scaled = Bitmap.createScaledBitmap(image, nw, 2048, true);
                    }
                    largeImage.setImageBitmap(scaled);
                    mAttacher = new PhotoViewAttacher(largeImage);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Image View Post", "Error: " + error.getMessage());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_view_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
