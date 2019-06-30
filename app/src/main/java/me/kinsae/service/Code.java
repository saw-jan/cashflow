package me.kinsae.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class Code extends AppCompatActivity {

    EditText code,confirm;
    Button create;
    public static final String PREFS = "prefs";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        sharedPreferences = getSharedPreferences("code",Context.MODE_PRIVATE);

        code = findViewById(R.id.pass);
        confirm = findViewById(R.id.confirm);
        create = findViewById(R.id.create);

        //limit input  characters
        int max = 4;
        code.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
        confirm.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String c1 = code.getText().toString().trim();
                if(!c1.equals("")){
                    if(Validation.isNumeric(c1)){
                    }else{
                        Toast.makeText(Code.this,"Must be numeric.",Toast.LENGTH_LONG).show();
                        code.setText(c1.substring(0,c1.length()-1));
                    }
                }else {
                    code.setHint("CODE");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String c2 = confirm.getText().toString().trim();
                if(!c2.equals("")){
                    if(Validation.isNumeric(c2)){
                    }else{
                        Toast.makeText(Code.this,"Must be numeric.",Toast.LENGTH_LONG).show();
                        code.setText(c2.substring(0,c2.length()-1));
                    }
                }else {
                    code.setHint("Confirm");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String c1 = code.getText().toString().trim();
                String c2 = confirm.getText().toString().trim();
                if(c1.equals("")||c2.equals("")){
                    Toast.makeText(Code.this,"Empty Field(s).",Toast.LENGTH_LONG).show();
                }else{

                    if(c1.equals(c2)){

                        SharedPreferences prefs = getSharedPreferences(PREFS,0);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString("code",c1);
                        edit.apply();

                        Intent mainIntent = new Intent(Code.this, AddNew.class);
                        startActivity(mainIntent);
                        finish();
                    }else{
                        Toast.makeText(Code.this,"Code didn't match.",Toast.LENGTH_LONG).show();
                        confirm.setText("");
                    }
                }
            }
        });

    }
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
