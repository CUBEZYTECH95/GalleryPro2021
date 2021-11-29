package gallerypro.galleryapp.bestgallery.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.utils.CustomKeyboard;
import gallerypro.galleryapp.bestgallery.R;

public class PinActivity extends AppCompatActivity {

    PinView pinView, pinView1;
    TextView tvEnterPass;
    String pass;
    String confirmPass;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        pinView = findViewById(R.id.pinView_fragment);
        pinView.setPasswordHidden(true);
        pinView1 = findViewById(R.id.pinView1);
        pinView1.setPasswordHidden(true);
        preferenceManager = new PreferenceManager(this);

        tvEnterPass = findViewById(R.id.tv_enter_pass);
        CustomKeyboard customKeyboard = findViewById(R.id.customKeyboard);

        InputConnection ic = pinView.onCreateInputConnection(new EditorInfo());
        customKeyboard.setInputConnection(ic);


        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 3){
                    pass =  s.toString();
                    pinView.getText().clear();
                    tvEnterPass.setText("CONFIRM YOUR PIN");
                    pinView.setVisibility(View.GONE);
                    pinView1.setVisibility(View.VISIBLE);
                    InputConnection ic1 = pinView1.onCreateInputConnection(new EditorInfo());
                    customKeyboard.setInputConnection(ic1);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        pinView1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 3){
                    confirmPass =  s.toString();
                    if (!confirmPass.equals(pass)){

                        pinView1.getText().clear();
                        pinView1.setVisibility(View.GONE);
                        pinView.setVisibility(View.VISIBLE);
                        tvEnterPass.setText("ENTER YOUR PIN");
                        InputConnection ic = pinView.onCreateInputConnection(new EditorInfo());
                        customKeyboard.setInputConnection(ic);


                    }else {
                        confirmPass = s.toString();
                        Toast.makeText(PinActivity.this, "pin set successfully", Toast.LENGTH_SHORT).show();
                        preferenceManager.savePin(confirmPass);
                        preferenceManager.pinSet(true);
                        Intent intent = new Intent(PinActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);


                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }
}