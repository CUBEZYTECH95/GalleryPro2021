package gallerypro.galleryapp.bestgallery.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.DrawerTransformer;
import gallerypro.galleryapp.bestgallery.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class HideImageSliderActivity extends AppCompatActivity {

    //bottom bar
    private LinearLayout image_unhide;
    private LinearLayout image_share;
    private LinearLayout image_delete;

    private ViewPager viewPager;

    private TextView tvImageTitle;

    private ArrayList<String> hideImageList;
    private int position;

    private ZoomageView imageView;

    private HideImagePager hideImagePager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_image_slider);

        image_unhide = findViewById(R.id.container_iv_rotate);
        image_share = findViewById(R.id.container_iv_share);
        image_delete = findViewById(R.id.container_iv_delete);

        tvImageTitle = findViewById(R.id.image_title);

        viewPager = findViewById(R.id.hide_image_view_pager);

        hideImageList = getIntent().getStringArrayListExtra("list");
        position = getIntent().getIntExtra("position", 0);

        hideImagePager = new HideImagePager();
        viewPager.setAdapter(hideImagePager);
        viewPager.setPageTransformer(true, new DrawerTransformer());
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (hideImagePager.getCount() - 1) && position < (hideImageList.size() - 1)) {

                    setImageTitle(hideImageList.get(position));

                    image_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteImage(hideImageList.get(position), position);
                        }
                    });

                    image_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareImage(hideImageList.get(position));
                        }
                    });

                    image_unhide.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            try {
//                                unhideImage(hideImageList.get(position), position);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            File file1 = new File(hideImageList.get(position));
//                            File file = new File(getArrayList().get(position));
//                            moveFile(file1.getParent().toString(),file1.getName(),file.toString(),position);

                            copyImage(hideImageList.get(position), position);
                            Log.e("imageCopy", "onClick: " + position);
                        }
                    });

                } else {

                    setImageTitle(hideImageList.get(hideImageList.size() - 1));

                    image_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteImage(hideImageList.get(hideImageList.size() - 1), hideImageList.size() - 1);
                        }
                    });

                    image_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareImage(hideImageList.get(hideImageList.size() - 1));
                        }
                    });

                    image_unhide.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            try {
//                                unhideImage(hideImageList.get(hideImageList.size() - 1), hideImageList.size() - 1);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }

//                            File file1 = new File(hideImageList.get(hideImageList.size() - 1));
//                            File file = new File(getArrayList().get(hideImageList.size() - 1));
//                            moveFile(file1.getParent().toString(),file1.getName(),file.toString(),hideImageList.size() - 1);


                            copyImage(hideImageList.get(hideImageList.size() - 1), hideImageList.size() - 1);
                            Log.e("imageCopy", "onClick: " + (hideImageList.size() - 1));
                        }
                    });

                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void unhideImage(String s, int postion) throws IOException {
        // image file path
        File file = new File(s);
        // folder path
        File dir = new File(getArrayList().get(postion));
        File newFile = new File(dir, file.getName());

        Log.d("hidepath", "unhideImage: " + dir.getAbsolutePath());
        Log.d("hidepath", "unhideImage: " + newFile.getAbsolutePath());


        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {

            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(file).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();

            boolean isDeleted = file.delete();
            if (isDeleted) {
                Toast.makeText(this, "file deleted", Toast.LENGTH_SHORT).show();
                hideImageList.remove(postion);


                ArrayList<String> hideList = new ArrayList<>();
                if (getArrayList().size() > 0) {
                    hideList.addAll(getArrayList());
                }
                hideList.remove(postion);
                saveArrayList(new ArrayList<>());
                saveArrayList(hideList);


                hideImagePager.notifyDataSetChanged();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("unhideImageExeption", "unhideImage: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("unhideImageExeption", "unhideImage: " + e.getMessage());

        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }

    }


    private void moveFile(String inputPath, String inputFile, String outputPath, int postion) {

        Log.d("tag 3", "moveFile: " + inputPath + "\n" + inputFile + "\n" + outputPath);


        InputStream in = null;
        OutputStream out = null;
        try {


            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + "/" + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            boolean isDeleted = new File(inputPath + "/" + inputFile).delete();

            if (isDeleted) {
//                Toast.makeText(this, "file deleted", Toast.LENGTH_SHORT).show();
                hideImageList.remove(postion);


                ArrayList<String> hideList = new ArrayList<>();
                if (getArrayList().size() > 0) {
                    hideList.addAll(getArrayList());
                }
                hideList.remove(postion);
                saveArrayList(new ArrayList<>());
                saveArrayList(hideList);


                hideImagePager.notifyDataSetChanged();
            }


        } catch (FileNotFoundException fnfe1) {
            Log.e("tag 1", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag 2`", e.getMessage());
        }

    }

    private void copyImage(String imagePath, int position) {

        File imageFile = new File(imagePath);

        Bitmap bitmap = BitmapFactory.decodeFile(new File(imagePath).getAbsolutePath());
        String filepath = SaveImage(imageFile, bitmap, new File(getArrayList().get(position) + "/" + imageFile.getName()).getAbsolutePath(), position);
//        Toast.makeText(this, filepath, Toast.LENGTH_SHORT).show();
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filepath))));


    }

    private String SaveImage(File deleteFile, Bitmap finalBitmap, String destinationPath, int position) {


//        File direcory = new File(getArrayList().get(position));
//        if (!direcory.exists())
//            direcory.mkdirs();

        File file = new File(destinationPath);

        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            boolean isDeleted = deleteFile.delete();
            if (isDeleted) {
//                Toast.makeText(this, "file deleted", Toast.LENGTH_SHORT).show();
                hideImageList.remove(position);


                ArrayList<String> hideList = new ArrayList<>();
                if (getArrayList().size() > 0) {
                    hideList.addAll(getArrayList());
                }
                hideList.remove(position);
                saveArrayList(new ArrayList<>());
                saveArrayList(hideList);
                hideImagePager.notifyDataSetChanged();
                if (hideImagePager.getCount()<=0){
                    onBackPressed();
                }

                if (hideList.isEmpty()) {
                    onBackPressed();
                }

            }

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SaveImageAAAA", "SaveImage: " + e.getMessage());
        }


        return file.getAbsolutePath();
    }


    public ArrayList<String> getArrayList() {
        ArrayList<String> hideList = new ArrayList<>();
        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString("HIDE_LIST", "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        if (json.equals("")) {
            return hideList;
        } else {
            hideList = gson.fromJson(json, type);
        }
        return hideList;
    }

    public void saveArrayList(ArrayList<String> list) {
        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json;
        json = gson.toJson(list);
        editor.putString("HIDE_LIST", json);
        editor.apply();
    }

    private void shareImage(String s) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        File file = new File(s);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(intent, "share via"));
    }

    private void deleteImage(String s, int i) {

        Dialog dialog = new Dialog(this, R.style.Dialog_Theme);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.delete_dialog);

        Button delete = dialog.findViewById(R.id.btn_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(s);
                if (file.exists()) {
                    file.delete();
                    hideImageList.remove(i);
                    hideImagePager.notifyDataSetChanged();
                }

                dialog.dismiss();
            }
        });
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();


    }

    private void setImageTitle(String title) {
        String folderPath = title.substring(title.lastIndexOf("/"), title.lastIndexOf(""));
        tvImageTitle.setText(folderPath);
    }

    public void onBackPressed(View view) {
        finish();
    }

    private class HideImagePager extends PagerAdapter {

        @Override
        public int getCount() {
            return hideImageList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == (View) object;
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            LayoutInflater layoutInflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.display_image_item, null);
            imageView = view.findViewById(R.id.zoomImageView);


//            Glide.with(container.getContext()).load(hideImageList.get(position)).apply(new RequestOptions().fitCenter()).placeholder(R.drawable.no_image_bg).into(imageView);
            Picasso.get().load(new File(hideImageList.get(position))).placeholder(R.drawable.no_image_bg).into(imageView);

            ((ViewPager) container).addView(view);

            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

}