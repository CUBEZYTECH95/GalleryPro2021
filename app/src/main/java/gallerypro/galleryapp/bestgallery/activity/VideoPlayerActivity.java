package gallerypro.galleryapp.bestgallery.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.database.VideoDbHelper;
import gallerypro.galleryapp.bestgallery.utils.Tools;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.text.CharacterIterator;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "activitylifecycle";
    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;

    private int id;
    private String path;
    private String title;
    private String fileName;
    private String size;
    private String dateAdded;
    private String duration;

    private TextView tvVideoTitle;
    private ImageView ivVideoLike;

    private VideoDbHelper videoDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        fullScreenView();
        Tools.FullScreencall(this);

        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.exo_player_view);
//        hideSystemUi();
        tvVideoTitle = findViewById(R.id.video_title);
        ivVideoLike = findViewById(R.id.video_like);

        videoDbHelper = new VideoDbHelper(this);


        id = getIntent().getIntExtra("id", 0);
        path = getIntent().getStringExtra("path");
        title = getIntent().getStringExtra("title");
        fileName = getIntent().getStringExtra("fileName");
        size = getIntent().getStringExtra("size");
        dateAdded = getIntent().getStringExtra("dateAdded");
        duration = getIntent().getStringExtra("duration");

        tvVideoTitle.setText(title);

        if (videoDbHelper.getStatus(String.valueOf(id))) {
            ivVideoLike.setImageResource(R.drawable.ic_liked);
        } else {
            ivVideoLike.setImageResource(R.drawable.ic_like_24);
        }


        ivVideoLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoDbHelper.getStatus(String.valueOf(id))) {
                    ivVideoLike.setImageResource(R.drawable.ic_like_24);
                    videoDbHelper.removeFav(String.valueOf(id));
                } else {
                    ivVideoLike.setImageResource(R.drawable.ic_liked);
                    videoDbHelper.setFav(String.valueOf(id));
                }
            }
        });

    }


    private void fullScreenView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorOnlyBlack));
        }
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
        initializePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    private void initializePlayer() {
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(simpleExoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(path);
        simpleExoPlayer.setMediaItem(mediaItem);
        simpleExoPlayer.prepare();
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_IDLE || state == Player.STATE_ENDED) {
                    playerView.setKeepScreenOn(false);
                } else {
                    playerView.setKeepScreenOn(true);
                }

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
        releasePlayer();
    }


    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    public void onExit(View view) {
        finish();
    }

    public void onVideoLike(View view) {
    }

    public void onVideoInfoClick(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.video_details_dialog, null);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        Button button = view.findViewById(R.id.btn_details_ok);
        TextView tvImageSize = view.findViewById(R.id.tv_image_size);
        TextView tvImagePath = view.findViewById(R.id.tv_image_path);
        TextView tvImageName = view.findViewById(R.id.tv_image_name);
        TextView tvDateModified = view.findViewById(R.id.tv_date_taken);

        tvImageSize.setText(humanReadableByteCountSI(Long.parseLong(size)));
        tvImagePath.setText(path);
        tvImageName.setText(title);
        tvDateModified.setText(convertTimeDateModified(Long.parseLong(dateAdded)));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();


    }

    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public String convertTimeDateModified(long time) {
        Date date = new Date(time * 1000);
        @SuppressLint("SimpleDateFormat") Format format = new SimpleDateFormat("dd.MM.yyyy , HH:mm:aa");
        return format.format(date);
    }

}