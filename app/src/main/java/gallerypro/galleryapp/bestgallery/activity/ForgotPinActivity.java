package gallerypro.galleryapp.bestgallery.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;

public class ForgotPinActivity extends AppCompatActivity {

    private TextView tvQuestion;
    private EditText etAnswer;
    private Button btnSubmit;
    private Toolbar toolbar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pin);

        tvQuestion = findViewById(R.id.tv_question);
        etAnswer = findViewById(R.id.et_answer);
        btnSubmit = findViewById(R.id.btn_submit);

        preferenceManager = new PreferenceManager(this);

        initToolbar();

        tvQuestion.setText(preferenceManager.getSecurityQuestion());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ans = etAnswer.getText().toString().trim();

                if (!etAnswer.getText().toString().trim().trim().equals("")){
                    if (ans.equals(preferenceManager.getSecurityAnswer())){
                        startActivity(new Intent(ForgotPinActivity.this,PinActivity.class));
                    }else {
                        Toast.makeText(ForgotPinActivity.this, "Wrong Answer", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    etAnswer.setError("Enter Answer");
                }

            }
        });



    }



    private void initToolbar() {
        preferenceManager = new PreferenceManager(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Forgot Pin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Tools.setSystemBarColor(this, R.color.colorPrimary);
        if (preferenceManager.checkMode()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorWhite));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}