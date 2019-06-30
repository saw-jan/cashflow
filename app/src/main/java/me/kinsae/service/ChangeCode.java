package me.kinsae.service;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class ChangeCode extends AppCompatActivity {

    EditText oldC,newC,cC;
    Button update;
    public static final String PREFS = "prefs";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_code);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        oldC = findViewById(R.id.oldcode);
        newC = findViewById(R.id.newcode);
        cC = findViewById(R.id.confirm);
        update = findViewById(R.id.update);

        int max = 4;
        oldC.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
        newC.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
        cC.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});

        SharedPreferences prefs = getSharedPreferences(PREFS, 0);
        if (prefs.getString("code", "").trim().equals("")) {
            AlertDialog.Builder diag2 = new AlertDialog.Builder(this);
            diag2.setMessage("Set code first to use this feature.");
            diag2.setTitle("No CODE found");
            diag2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ChangeCode.this,Code.class);
                    startActivity(intent);
                    finish();
                }
            });
            diag2.create().show();
        }
        else {

        oldC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String c1 = oldC.getText().toString().trim();
                if (!c1.equals("")) {
                    if (Validation.isNumeric(c1)) {
                    } else {
                        //Toast.makeText(Code.this,"Must be numeric.",Toast.LENGTH_LONG).show();
                        oldC.setText(c1.substring(0, c1.length() - 1));
                    }
                } else {
                    oldC.setHint("Old Code");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        newC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String c1 = newC.getText().toString().trim();
                if (!c1.equals("")) {
                    if (Validation.isNumeric(c1)) {
                    } else {
                        //Toast.makeText(Code.this,"Must be numeric.",Toast.LENGTH_LONG).show();
                        newC.setText(c1.substring(0, c1.length() - 1));
                    }
                } else {
                    newC.setHint("New Code");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String c1 = cC.getText().toString().trim();
                if (!c1.equals("")) {
                    if (Validation.isNumeric(c1)) {
                    } else {
                        //Toast.makeText(Code.this,"Must be numeric.",Toast.LENGTH_LONG).show();
                        cC.setText(c1.substring(0, c1.length() - 1));
                    }
                } else {
                    cC.setHint("Confirm");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!oldC.getText().toString().equals("") && !newC.getText().toString().equals("") && !cC.getText().toString().equals("")) {
                    if (newC.getText().toString().trim().length() == 4) {
                        if (newC.getText().toString().equals(cC.getText().toString())) {
                            SharedPreferences prefs = getSharedPreferences(PREFS, 0);
                            if (prefs.getString("code", "").trim().equals(oldC.getText().toString().trim())) {
                                //Toast.makeText(ChangeCode.this,"pass",Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor edit = prefs.edit();
                                edit.putString("code", newC.getText().toString().trim());
                                edit.apply();
                                Intent intent = new Intent(ChangeCode.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(ChangeCode.this, "Code updated", Toast.LENGTH_SHORT).show();
                            } else {
                                AlertDialog.Builder diag2 = new AlertDialog.Builder(ChangeCode.this);
                                diag2.setMessage("");
                                diag2.setTitle("Incorrect Code");
                                diag2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                diag2.create().show();
                            }
                        } else
                            Toast.makeText(ChangeCode.this, "Code didn't match", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(ChangeCode.this, "Code must be 4 digits", Toast.LENGTH_SHORT).show();
                } else {
                }

            }
        });
        }
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
