package gallerypro.galleryapp.bestgallery.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;

import java.util.ArrayList;

public class SecurityActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner spinner;
    private Button btnSubmit;
    private EditText etAnswer;
    ArrayList<String> questionList = new ArrayList<>();
    PreferenceManager preferenceManager;

    int selectionPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        toolbar = findViewById(R.id.toolbar_security);
        spinner = findViewById(R.id.spinner_question);
        btnSubmit = findViewById(R.id.btn_submit);
        etAnswer = findViewById(R.id.et_answer);
        preferenceManager = new PreferenceManager(this);

        setSupportActionBar(toolbar);
        if (preferenceManager.checkMode()){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorWhite));
        }
        getSupportActionBar().setTitle("Security Question");

        questionList.add(0, "Select your question");
        questionList.add("What's your father name?");
        questionList.add("What's your mother name?");
        questionList.add("What's your favourite movie?");
        questionList.add("What's your pets name?");
        questionList.add("What's your dream job?");
        questionList.add("In what city did you parents meet?");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, questionList);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectionPosition = position;
                if (position >= 1) {
                    preferenceManager.saveSecurityQuestion(arrayAdapter.getItem(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectionPosition = 0;
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (selectionPosition <= 0) {
                    Toast.makeText(SecurityActivity.this, "Select Security Question!", Toast.LENGTH_SHORT).show();
                } else if (etAnswer.getText().toString().trim().equals("")) {
                    etAnswer.setError("Enter security answer.");
                } else {
                    String answer = etAnswer.getText().toString().trim();
                    preferenceManager.saveSecurityAnswer(answer);
                    startActivity(new Intent(SecurityActivity.this,PinActivity.class));
                    finish();
                }
            }
        });


    }
}