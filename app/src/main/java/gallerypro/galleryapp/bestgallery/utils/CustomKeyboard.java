package gallerypro.galleryapp.bestgallery.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import gallerypro.galleryapp.bestgallery.R;

public class CustomKeyboard extends LinearLayout implements View.OnClickListener {

    TextView tv_1, tv_2, tv_3, tv_4, tv_5, tv_6, tv_7, tv_8, tv_9, tv_10;
    ImageView iv_backspace;

    private SparseArray<String> keyValues = new SparseArray<>();
    private InputConnection inputConnection;

    public CustomKeyboard(Context context) {
        this(context, null, 0);
    }

    public CustomKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_keyboard,this,true);
        tv_1 = findViewById(R.id.tv_1);
        tv_2 = findViewById(R.id.tv_2);
        tv_3 = findViewById(R.id.tv_3);
        tv_4 = findViewById(R.id.tv_4);
        tv_5 = findViewById(R.id.tv_5);
        tv_6 = findViewById(R.id.tv_6);
        tv_7 = findViewById(R.id.tv_7);
        tv_8 = findViewById(R.id.tv_8);
        tv_9 = findViewById(R.id.tv_9);
        tv_10 = findViewById(R.id.tv_10);
        iv_backspace = findViewById(R.id.iv_backspace);

        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        tv_3.setOnClickListener(this);
        tv_4.setOnClickListener(this);
        tv_5.setOnClickListener(this);
        tv_6.setOnClickListener(this);
        tv_7.setOnClickListener(this);
        tv_8.setOnClickListener(this);
        tv_9.setOnClickListener(this);
        tv_10.setOnClickListener(this);
        iv_backspace.setOnClickListener(this);

        keyValues.put(R.id.tv_1,"1");
        keyValues.put(R.id.tv_2,"2");
        keyValues.put(R.id.tv_3,"3");
        keyValues.put(R.id.tv_4,"4");
        keyValues.put(R.id.tv_5,"5");
        keyValues.put(R.id.tv_6,"6");
        keyValues.put(R.id.tv_7,"7");
        keyValues.put(R.id.tv_8,"8");
        keyValues.put(R.id.tv_9,"9");
        keyValues.put(R.id.tv_10,"0");
    }


    @Override
    public void onClick(View v) {

        if (inputConnection == null){
            return;
        }

        if (v.getId() == R.id.iv_backspace){
            CharSequence selectedText  = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)){
                inputConnection.deleteSurroundingText(1,0);
            }else {
                inputConnection.commitText("",1);
            }
        }else {
            String value = keyValues.get(v.getId());
            inputConnection.commitText(value,1);
        }

    }

    public void setInputConnection(InputConnection ic){
        inputConnection = ic;
    }

}
