package gallerypro.galleryapp.bestgallery.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.activity.ForgotPinActivity;
import gallerypro.galleryapp.bestgallery.activity.HideImageSliderActivity;
import gallerypro.galleryapp.bestgallery.activity.SecurityActivity;
import gallerypro.galleryapp.bestgallery.adapter.HideImageAdapter;
import gallerypro.galleryapp.bestgallery.interfaces.HideImageClickListener;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.utils.CustomKeyboard;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PrivacyFragment extends Fragment {

    Context context;
    Button btnSetSecurity;
    LinearLayout securityContainer;
    LinearLayout pinContainer;
    PreferenceManager preferenceManager;
    CustomKeyboard customKeyboard;
    PinView pinView;
    RecyclerView recyclerView;
    LinearLayout noImageContainer;
    TextView tvForgotPin;
    boolean pinShow = false;


    public PrivacyFragment(Context context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_privacy, container, false);

        getActivity().setTitle("Privacy");

        preferenceManager = new PreferenceManager(context);
        btnSetSecurity = view.findViewById(R.id.btn_set_security);
        securityContainer = view.findViewById(R.id.security_container);
        pinContainer = view.findViewById(R.id.pin_container);
        customKeyboard = view.findViewById(R.id.customKeyboard_fargment);
        pinView = view.findViewById(R.id.pinView_fragment);
        pinView.setPasswordHidden(true);
        recyclerView = view.findViewById(R.id.recyclerview_privacy);
        noImageContainer = view.findViewById(R.id.no_image_container);
        tvForgotPin = view.findViewById(R.id.tv_forgot_pin);



        InputConnection ic = pinView.onCreateInputConnection(new EditorInfo());
        customKeyboard.setInputConnection(ic);


        if (preferenceManager.isPinSeted()) {
            pinContainer.setVisibility(View.VISIBLE);
            securityContainer.setVisibility(View.GONE);
        } else {
            securityContainer.setVisibility(View.VISIBLE);
            pinContainer.setVisibility(View.GONE);
        }


        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 3) {
                    if (!preferenceManager.getPin().equals(s.toString())) {
                        pinView.getText().clear();
                        Toast.makeText(getContext(), "Wrong Pin", Toast.LENGTH_SHORT).show();
                    } else {
                        pinContainer.setVisibility(View.GONE);
                        securityContainer.setVisibility(View.GONE);
                        noImageContainer.setVisibility(View.VISIBLE);
                        if (!getHideImage().isEmpty()) {
                            recyclerView.setVisibility(View.VISIBLE);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
                            recyclerView.setLayoutManager(gridLayoutManager);
                            HideImageAdapter hideImageAdapter = new HideImageAdapter(getContext(), getHideImage(), hideImageClickListener);
                            recyclerView.setAdapter(hideImageAdapter);

                        }
                        pinView.getText().clear();

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvForgotPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ForgotPinActivity.class));
            }
        });

        btnSetSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SecurityActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    HideImageClickListener hideImageClickListener = (hideImageList, position) -> {


        Intent intent = new Intent(getContext(), HideImageSliderActivity.class);
        intent.putStringArrayListExtra("list", (ArrayList<String>) hideImageList);
        intent.putExtra("position", position);
//        startActivity(intent);
        startActivityForResult(intent, 200);
    };


    private List<String> getHideImage() {
        File[] files = null;
        File path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            path = new File(getContext().getExternalFilesDir(null),".HideImage");
        } else {
            path = new File(Environment.getExternalStorageDirectory(), ".HideImage");
        }
        List<String> allImageFiles = new ArrayList<String>();

        if (path.exists()) {
            files = path.listFiles();
        }

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                ///Now set this bitmap on imageview
                Log.d("getHideImage", "getHideImage: " + files[i]);
                allImageFiles.add(files[i].toString());
            }
        }

        return allImageFiles;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!pinShow) {
            if (preferenceManager.isPinSeted()) {
                pinContainer.setVisibility(View.VISIBLE);
                securityContainer.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
//            } else {
////                securityContainer.setVisibility(View.VISIBLE);
////                pinContainer.setVisibility(View.GONE);
//            }

            }

        }

        pinShow = false;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            pinShow = true;

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
            recyclerView.setLayoutManager(gridLayoutManager);
            HideImageAdapter hideImageAdapter = new HideImageAdapter(getContext(), getHideImage(), hideImageClickListener);
            recyclerView.setAdapter(hideImageAdapter);

            if (getHideImage().isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                noImageContainer.setVisibility(View.VISIBLE);
            }
        }
    }
}