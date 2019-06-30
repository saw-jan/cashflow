package me.kinsae.service;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddNew extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DatabaseHelper myDB;
    EditText name,initAmount,remark;
    Spinner accType;
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        myDB = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        name = findViewById(R.id.name);
        initAmount = findViewById(R.id.initamount);
        remark = findViewById(R.id.remark);
        add = findViewById(R.id.addnew);

        accType = findViewById(R.id.accType);
        accType.setOnItemSelectedListener(this);
        List<String> types = new ArrayList<String>();
        types.add("ACCOUNT TYPE");
        types.add("TO RECEIVE");
        types.add("TO PAY");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accType.setAdapter(dataAdapter);

        initAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String in = initAmount.getText().toString().trim();
                if(!in.equals("")){
                    if(Validation.isNumeric(in)){
                    }else {
                        //Toast.makeText(AddNew.this,"Paisa vaneko numbers hunxa!",Toast.LENGTH_SHORT).show();
                        initAmount.setText(in.substring(0,in.length()-1));
                    }
                }else {
                    initAmount.setHint("Initial Amount");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //sql functions
        addData();
    }
    public void addData(){
        add.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Spinner accType = findViewById(R.id.accType);
                        String acc = accType.getSelectedItem().toString().trim();
                        String accVal = "0";
                        if(acc.equals("TO PAY")){
                            accVal = "1";
                        }
                        if(!acc.equals("ACCOUNT TYPE")){
                        //Toast.makeText(getBaseContext(),accVal+" "+acc,Toast.LENGTH_SHORT).show();
                        if(!name.getText().toString().trim().equals("")){
                            if(!initAmount.getText().toString().trim().equals("")&&!initAmount.getText().toString().trim().equals("Initial Amount")){
                                String nam = name.getText().toString();
                                String amt = initAmount.getText().toString();
                                    boolean isInserted = myDB.insertData(name.getText().toString(),initAmount.getText().toString(),remark.getText().toString(), accVal);
                                    if(isInserted == true){
                                        //add to history
                                        myDB.addToHistoy(nam,"0",amt,"0",amt);

                                        AlertDialog.Builder diag = new AlertDialog.Builder(AddNew.this);
                                        diag.setMessage("Name: "+name.getText().toString()+"\nAmount: Rs. "+initAmount.getText().toString()+"\nRemarks: "+remark.getText().toString());
                                        diag.setTitle("New Record");
                                        diag.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        diag.create().show();
                                        name.setText("");
                                        initAmount.setText("");
                                        remark.setText("");
                                    }
                                    else {
                                        //Toast.makeText(AddNew.this,"Cannot Create New Record",Toast.LENGTH_LONG).show();
                                        AlertDialog.Builder diag2 = new AlertDialog.Builder(AddNew.this);
                                        diag2.setMessage("Name: " + name.getText().toString() + "\n\nName already exists.\nPlease try another.");
                                        diag2.setTitle("Conflict");
                                        diag2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        diag2.create().show();
                                    }
                            }else {
                                //Toast.makeText(AddNew.this,"Amount Khai?",Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            //Toast.makeText(AddNew.this,"Nam khali  xa ta",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(AddNew.this,"Choose Account Type",Toast.LENGTH_SHORT).show();
                    }
                    }
                }
        );

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
