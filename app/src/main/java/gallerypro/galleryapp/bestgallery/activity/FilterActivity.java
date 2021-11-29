package gallerypro.galleryapp.bestgallery.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.adapter.ImageFilterAdapter;
import gallerypro.galleryapp.bestgallery.interfaces.FilterListener;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.utils.Tools;

import net.alhazmy13.imagefilter.ImageFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilterActivity extends AppCompatActivity implements FilterListener {

    Bitmap bmp;
    ImageView imageView;

    private RecyclerView recyclerView;

    private Toolbar toolbar;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        bmp = EditActivity.bitmap;
        preferenceManager = new PreferenceManager(this);

        toolbar = findViewById(R.id.filter_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Filter");
        if (preferenceManager.checkMode()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        recyclerView = findViewById(R.id.filter_recyclerview);
        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(EditActivity.bitmap);


        List<ImageFilter.Filter> allImageFilter = new ArrayList<>();
        allImageFilter.add(null);
        allImageFilter.add(ImageFilter.Filter.GRAY);
        allImageFilter.add(ImageFilter.Filter.RELIEF);
        allImageFilter.add(ImageFilter.Filter.AVERAGE_BLUR);
        allImageFilter.add(ImageFilter.Filter.OIL);
        allImageFilter.add(ImageFilter.Filter.NEON);
        allImageFilter.add(ImageFilter.Filter.PIXELATE);
//        allImageFilter.add(ImageFilter.Filter.TV);
        allImageFilter.add(ImageFilter.Filter.INVERT);
        allImageFilter.add(ImageFilter.Filter.BLOCK);
        allImageFilter.add(ImageFilter.Filter.OLD);
        allImageFilter.add(ImageFilter.Filter.SHARPEN);
        allImageFilter.add(ImageFilter.Filter.LIGHT);
        allImageFilter.add(ImageFilter.Filter.LOMO);
        allImageFilter.add(ImageFilter.Filter.HDR);
        allImageFilter.add(ImageFilter.Filter.GAUSSIAN_BLUR);
        allImageFilter.add(ImageFilter.Filter.SOFT_GLOW);
        allImageFilter.add(ImageFilter.Filter.SKETCH);
        allImageFilter.add(ImageFilter.Filter.MOTION_BLUR);
        allImageFilter.add(ImageFilter.Filter.GOTHAM);


        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(FilterActivity.this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(horizontalLayoutManagaer);

        ImageFilterAdapter imageFilterAdapter = new ImageFilterAdapter(FilterActivity.this, allImageFilter, bmp, this);
        recyclerView.setAdapter(imageFilterAdapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:

                File file;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    file = new File(getExternalFilesDir(null), getString(R.string.app_name));
                } else {
                    file = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
                }


                if (!file.exists()) file.mkdir();

                String filename = System.currentTimeMillis() + ".jpg";
                File sd = Environment.getExternalStorageDirectory();
                File dest = new File(file, filename);

                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

                Bitmap bitmap = drawable.getBitmap();

                saveImage(bitmap, file.getAbsolutePath(), String.valueOf(System.currentTimeMillis()));

               /* try {
                FileOutputStream out = new FileOutputStream(dest);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }*/

             /*   this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dest)));

                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                finish();
*/
//                Intent intent = new Intent(FilterActivity.this,AllImageSliderActivity.class);
////                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImage(Bitmap bitmap, String filePath, String displayName) {
        FileOutputStream fos;
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                ContentResolver contentResolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.HEIGHT, bitmap.getHeight());
                contentValues.put(MediaStore.MediaColumns.WIDTH, bitmap.getWidth());
//                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, getExternalFilesDir("") +"/Gallery Pro"+ "/" + displayName);
                Uri insert = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);


                fos = (FileOutputStream) contentResolver.openOutputStream(Objects.requireNonNull(insert));

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                Objects.requireNonNull(fos);
                finish();
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            }else {
                File dest = new File(filePath, displayName+".jpeg");
                fos = new FileOutputStream(dest);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dest)));
                finish();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFilterClick(ImageFilter.Filter filter) {
        imageView.setImageBitmap(null);
        if (filter == null) {
            imageView.setImageBitmap(bmp);
        } else {

            imageView.setImageBitmap(ImageFilter.applyFilter(bmp, filter));
        }
    }

}