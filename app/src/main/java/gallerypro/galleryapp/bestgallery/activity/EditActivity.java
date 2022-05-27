package gallerypro.galleryapp.bestgallery.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.utils.Tools;

import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;
import java.io.IOException;

import static gallerypro.galleryapp.bestgallery.R.id.tv_crop_free;

public class EditActivity extends AppCompatActivity {

    Uri uri;

    Toolbar toolbar;

    ImageView imageView;
    CropImageView cropImageView;

    ImageView ivCrop;
    ImageView ivCustomCrop;
    ImageView ivRoate;
    ImageView ivLeftRight;
    ImageView ivTopBottom;

    LinearLayout cropOptionLayout;

    TextView tvCropFree;
    TextView tvCrop1_1;
    TextView tvCrop4_3;
    TextView tvCrop16_9;
    TextView tvCropOther;

    static Bitmap bitmap = null;

    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        preferenceManager = new PreferenceManager(this);

        toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Editor");


        if (preferenceManager.checkMode()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorWhite));
        }


        imageView = findViewById(R.id.image_edit);
        cropImageView = findViewById(R.id.cropImageView);
        cropImageView.setGuideColor(getResources().getColor(R.color.colorOnlyWhite));
        cropImageView.setFrameColor(getResources().getColor(R.color.colorOnlyWhite));

        ivCrop = findViewById(R.id.iv_crop);
        ivCustomCrop = findViewById(R.id.iv_custom_crop);
        ivRoate = findViewById(R.id.iv_roate);
        ivLeftRight = findViewById(R.id.iv_left_right);
        ivTopBottom = findViewById(R.id.iv_top_bottom);

        cropOptionLayout = findViewById(R.id.crop_op_layout);

        tvCropFree = findViewById(tv_crop_free);
        tvCrop1_1 = findViewById(R.id.tv_crop_1_1);
        tvCrop4_3 = findViewById(R.id.tv_crop_4_3);
        tvCrop16_9 = findViewById(R.id.tv_crop_16_9);
        tvCropOther = findViewById(R.id.tv_crop_other);


        String imgPath = getIntent().getStringExtra("img");
        uri = Uri.fromFile(new File(imgPath));

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cropImageView.setImageBitmap(bitmap);
//        cropImageView.setCropMode(CropImageView.CropMode.FREE);


        ivCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cropOptionLayout.getVisibility() == View.VISIBLE) {
                    cropOptionLayout.setVisibility(View.GONE);
                    ivCrop.setColorFilter(ContextCompat.getColor(EditActivity.this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);


                } else {
                    cropOptionLayout.setVisibility(View.VISIBLE);
                    ivCrop.setColorFilter(ContextCompat.getColor(EditActivity.this, R.color.crop_blue), android.graphics.PorterDuff.Mode.SRC_IN);

                }

            }
        });

        ivCustomCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.setCropMode(CropImageView.CropMode.CUSTOM);
            }
        });

        ivRoate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });

        ivLeftRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable) cropImageView.getDrawable();

                Bitmap bitmap = drawable.getBitmap();

                Bitmap bInput = bitmap;
                Matrix matrix = new Matrix();
                matrix.preScale(-1.0f, 1.0f);
                Bitmap bOutput = Bitmap.createBitmap(bInput, 0, 0, bInput.getWidth(), bInput.getHeight(), matrix, true);
                cropImageView.setImageBitmap(bOutput);

            }
        });

        ivTopBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                BitmapDrawable drawable = (BitmapDrawable) cropImageView.getDrawable();

                Bitmap bitmap = drawable.getBitmap();

                Bitmap bInput = bitmap;
                Matrix matrix = new Matrix();
                matrix.preScale(1.0f, -1.0f);
                Bitmap bOutput = Bitmap.createBitmap(bInput, 0, 0, bInput.getWidth(), bInput.getHeight(), matrix, true);
                cropImageView.setImageBitmap(bOutput);


            }
        });

        tvCropFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCropFree.setBackground(getResources().getDrawable(R.drawable.crop_item_bg));
                tvCropFree.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorOnlyWhite));
                tvCrop1_1.setBackground(null);
                tvCrop1_1.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCrop4_3.setBackground(null);
                tvCrop4_3.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCrop16_9.setBackground(null);
                tvCrop16_9.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCropOther.setBackground(null);
                tvCropOther.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));


                cropImageView.setCropMode(CropImageView.CropMode.FREE);
            }
        });

        tvCrop1_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCropFree.setBackground(null);
                tvCropFree.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCrop1_1.setBackground(getResources().getDrawable(R.drawable.crop_item_bg));
                tvCrop1_1.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorOnlyWhite));
                tvCrop4_3.setBackground(null);
                tvCrop4_3.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCrop16_9.setBackground(null);
                tvCrop16_9.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCropOther.setBackground(null);
                tvCropOther.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));


                cropImageView.setCropMode(CropImageView.CropMode.SQUARE);
            }
        });

        tvCrop4_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCropFree.setBackground(null);
                tvCropFree.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCrop1_1.setBackground(null);
                tvCrop1_1.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCrop4_3.setBackground(getResources().getDrawable(R.drawable.crop_item_bg));
                tvCrop4_3.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorOnlyWhite));
                tvCrop16_9.setBackground(null);
                tvCrop16_9.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCropOther.setBackground(null);
                tvCropOther.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));


                cropImageView.setCropMode(CropImageView.CropMode.RATIO_4_3);
            }
        });

        tvCrop16_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCropFree.setBackground(null);
                tvCropFree.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCrop1_1.setBackground(null);
                tvCrop1_1.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCrop4_3.setBackground(null);
                tvCrop4_3.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCrop16_9.setBackground(getResources().getDrawable(R.drawable.crop_item_bg));
                tvCrop16_9.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorOnlyWhite));
                tvCropOther.setBackground(null);
                tvCropOther.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));

                cropImageView.setCropMode(CropImageView.CropMode.RATIO_16_9);
            }
        });

        tvCropOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCropFree.setBackground(null);
                tvCropFree.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCrop1_1.setBackground(null);
                tvCrop1_1.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCrop4_3.setBackground(null);
                tvCrop4_3.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCrop16_9.setBackground(null);
                tvCrop16_9.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorBlack));
                tvCropOther.setBackground(getResources().getDrawable(R.drawable.crop_item_bg));
                tvCropOther.setTextColor(ContextCompat.getColor(EditActivity.this, R.color.colorOnlyWhite));

                cropImageView.setCropMode(CropImageView.CropMode.CUSTOM);
            }
        });


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

                bitmap = cropImageView.getCroppedBitmap();

                Intent intent = new Intent(this, FilterActivity.class);
                startActivityForResult(intent,414);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 414){
            finish();
        }
    }

}