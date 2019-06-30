package me.kinsae.service;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AllAccounts extends AppCompatActivity {

    DatabaseHelper myDB;
    public static final String PREFS = "prefs";
    private PopupWindow details;
    private LinearLayout body;
    TextView atype,to_from,total;
    private Context mContext;
    private Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_accounts);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        mContext = getApplicationContext();
        mActivity = this;
        //database
        myDB = new DatabaseHelper(this);
        atype = findViewById(R.id.actype);
        to_from = findViewById(R.id.to_from);
        total = findViewById(R.id.gtotal);
        body = findViewById(R.id.body);

        LinearLayout contain = findViewById(R.id.datarow);

        Intent stat = getIntent();
        String type = stat.getExtras().getString("TYPE","");
        if(type.equals("0")){

            atype.setText("TO RECEIVE");
            to_from.setText("FROM");
            Cursor rttl = myDB.getTotalToReceive();
                while(rttl.moveToNext()){
                    if(rttl.getString(0)==null){
                        total.setText("Rs. 0");
                    }else {
                        total.setText("Rs. " + Validation.formatToMoney(rttl.getString(0)));
                    }
                }
            //extracting data from db
            Cursor res = myDB.getAllAccToReceive();
            if(res.getCount() == 0){
                final LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(30,30,30,30);
                //wrap.setId(R.id.contain);
                contain.addView(row);

                final TextView nodata = new TextView(this);
                nodata.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                nodata.setText("No Records");
                //nm.setTypeface(Typeface.DEFAULT_BOLD);
                nodata.setTextSize(14);
                row.addView(nodata);
            }
            while(res.moveToNext()){
                final String  id = res.getString(0);
                final String  name = res.getString(1);
                String  amount = res.getString(2);
                String  remark = res.getString(3);


                //creating dynamic layouts
                final LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(30,30,30,30);
                row.setWeightSum(100);
                //wrap.setId(R.id.contain);
                contain.addView(row);

                final TextView i = new TextView(this);
                i.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
                i.setText(id);
                i.setVisibility(View.INVISIBLE);

                final TextView nm = new TextView(this);
                nm.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,30));
                nm.setText(name);
                //nm.setTypeface(Typeface.DEFAULT_BOLD);
                nm.setTextSize(12);
                //nm.setBackgroundResource(R.drawable.button_round);
                nm.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                final TextView amt = new TextView(this);
                LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,40);
                //pm.gravity = Gravity.CENTER_HORIZONTAL;
                amt.setLayoutParams(pm);
                amt.setText("Rs. "+amount);
                //amt.setBackgroundResource(R.drawable.button_round);
                //amt.setTypeface(Typeface.DEFAULT_BOLD);
                amt.setTextSize(12);
                amt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                final TextView rmk = new TextView(this);
                rmk.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,30));
                rmk.setText(remark);
                //rmk.setTypeface(Typeface.DEFAULT_BOLD);
                rmk.setTextSize(12);
                //rmk.setBackgroundResource(R.drawable.button_round);
                rmk.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

                //history here
                //show more detail
                final LinearLayout popContainer = body;
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popup = inflate.inflate(R.layout.history,null);
                        details = new PopupWindow(popup, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
                        if(Build.VERSION.SDK_INT>=21){
                            details.setElevation(5.0f);
                        }

                        final ScrollView scrollView = popup.findViewById(R.id.popscroll);
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.scrollTo(0,scrollView.getBottom());
                            }
                        });
                        LinearLayout box = popup.findViewById(R.id.container);
                        //header container
                        LinearLayout header = popup.findViewById(R.id.acname);
                        //account name
                        final TextView acname = new TextView(getBaseContext());
                        LinearLayout.LayoutParams pn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,40);
                        pn.setMargins(8,5,0,5);
                        pn.weight = 100;
                        acname.setLayoutParams(pn);
                        acname.setText(name);
                        acname.setTextSize(14);
                        acname.setTypeface(Typeface.DEFAULT_BOLD);
                        acname.setGravity(Gravity.LEFT);
                        //delete
                        final ImageView del= new ImageView(getBaseContext());
                        LinearLayout.LayoutParams pcm = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        pcm.setMargins(20,0,20,0);
                        del.setLayoutParams(pcm);
                        rmk.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                        del.setImageResource(R.drawable.ic_delete_white);
                        //close
                        final TextView close = new TextView(getBaseContext());
                        LinearLayout.LayoutParams pc = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,40);
                        pc.setMargins(0,5,0,5);
                        pc.weight = 1;
                        close.setLayoutParams(pc);
                        close.setPadding(10,0,10,0);
                        close.setTextSize(16);
                        close.setBackgroundResource(R.drawable.ic_close);
                        close.setGravity(Gravity.RIGHT);
                        header.addView(acname);
                        header.addView(del);
                        header.addView(close);
                        del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(getActivity(),"Delete?",Toast.LENGTH_SHORT).show();
                                Cursor res = myDB.getOneData(nm.getText().toString());
                                String amt = "";
                                if(res.getCount() == 0){

                                }
                                while(res.moveToNext()) {
                                    amt = res.getString(2);
                                    //Toast.makeText(getActivity(),i.getText().toString().trim()+amt,Toast.LENGTH_SHORT).show();
                                }
                                //Toast.makeText(getActivity(),amt,Toast.LENGTH_SHORT).show();
                                if(amt.trim().equals("0")||amt.trim().equals("")){
                                    int max = 4;
                                    TextInputLayout div = new TextInputLayout(getBaseContext());
                                    div.setHint("CODE");

                                    final EditText input = new EditText(getBaseContext());
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(50,0,50,0);
                                    input.setLayoutParams(params);
                                    input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
                                    input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                    div.addView(input);

                                    input.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            String in = input.getText().toString().trim();
                                            if(!in.equals("")){
                                                if(Validation.isNumeric(in)){
                                                }else{
                                                    //Toast.makeText(getActivity(),"Must be numeric.",Toast.LENGTH_LONG).show();
                                                    input.setText(in.substring(0,in.length()-1));
                                                }
                                            }else {
                                                input.setHint("Code");
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });
                                    new AlertDialog.Builder(getBaseContext())
                                            .setTitle("Confirm Delete?")
                                            .setMessage("Please enter code")
                                            .setView(div)
                                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String code = input.getText().toString().trim();
                                                    SharedPreferences prefs = getApplication().getSharedPreferences(PREFS,0);
                                                    //Toast.makeText(this,prefs.getString("code","")+code,Toast.LENGTH_SHORT).show();
                                                    if(prefs.getString("code","").trim().equals(code.trim())){
                                                        Integer delRow = myDB.deleteData(i.getText().toString().trim());
                                                        if(delRow>0) {
                                                            Toast.makeText(getApplicationContext(), nm.getText().toString() + " has been deleted", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(intent);
                                                            getParent().finish();
                                                        }
                                                        else
                                                            Toast.makeText(getApplicationContext(),"Cannot delete record",Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(getApplicationContext(),"Invalid Code",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();

                                }else {
                                    AlertDialog.Builder diag2 = new AlertDialog.Builder(AllAccounts.this);
                                    diag2.setMessage("This account has Rs. "+amt+" to be cleared.");
                                    diag2.setTitle("Cannot be deleted");
                                    diag2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    diag2.create().show();
                                }
                            }
                        });
                        //
                        //get history from db
                        //
                        Cursor hres = myDB.getHistory(name);
                        if(hres.getCount() != 0){
                            while(hres.moveToNext()) {
                                final String hid = hres.getString(0);
                                final String balance = hres.getString(1);
                                String added = hres.getString(2);
                                String subtracted = hres.getString(3);
                                String net = hres.getString(4);
                                String today = hres.getString(5);

                                //check date
                                Date date = Calendar.getInstance().getTime();
                                DateFormat aja = new SimpleDateFormat("d MMM yyyy");
                                DateFormat aday = new SimpleDateFormat("d");
                                String ad = aday.format(date).replaceAll("\\s+","");
                                String ajadate = aja.format(date).replaceAll("\\s+","");
                                //[abc] 1 jan, 1989
                                String[] tdate = today.split(",");
                                String dm = tdate[0].replaceAll("\\s+","");
                                String y = tdate[1].replaceAll("\\s+","");
                                String dmy = dm.substring(5,dm.length())+y.substring(0,4);
                                //getting day
                                String d = dm.substring(5,dm.length()).substring(0,dm.substring(5,dm.length()).length()-3);
                                //extracting time
                                String t = tdate[1].substring(5,tdate[1].length());
                                //day difference
                                Integer diff = Integer.parseInt(d) - Integer.parseInt(ad);
                                //Integer ch = dm.substring(5,dm.length()).length();
                                //Toast.makeText(getActivity(),ch.toString(),Toast.LENGTH_SHORT).show();

                                if(ajadate.equals(dmy)){
                                    today = "Today"+t;
                                }else if(diff==1){
                                    today = "Yesterday"+t;
                                }

                                final LinearLayout hr = new LinearLayout(getBaseContext());
                                hr.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                hr.setOrientation(LinearLayout.VERTICAL);
                                hr.setPadding(30, 10, 10, 10);
                                box.addView(hr);
                                //date time
                                final TextView dt = new TextView(getBaseContext());
                                dt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                dt.setText(today);
                                dt.setTextSize(10);
                                //balance
                                final TextView b = new TextView(getBaseContext());
                                b.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                b.setText("Bal: " + balance);
                                b.setTextColor(Color.parseColor("#5499c7"));
                                hr.addView(dt);
                                hr.addView(b);
                                //added
                                final TextView add = new TextView(getBaseContext());
                                if(!added.equals("") && !added.equals("0")) {
                                    add.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    add.setText("+ " + added);
                                    add.setPadding(45, 0, 0, 0);
                                    add.setTextColor(Color.parseColor("#0b6623"));
                                    hr.addView(add);
                                }
                                //Toast.makeText(getActivity(),subtracted,Toast.LENGTH_SHORT).show();
                                final TextView sub = new TextView(getBaseContext());
                                if(!subtracted.equals("") && !subtracted.equals("0")) {
                                    sub.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    sub.setText("- " + subtracted);
                                    sub.setPadding(45, 0, 0, 0);
                                    sub.setTextColor(Color.parseColor("#ed2939"));
                                    hr.addView(sub);
                                }
                                //Net Balance
                                final TextView nb = new TextView(getBaseContext());
                                nb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                nb.setText("Net: "+net);
                                nb.setTypeface(Typeface.DEFAULT_BOLD);
                                //Divider
                                final View div = new View(getBaseContext());
                                LinearLayout.LayoutParams pd = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                                pd.setMargins(0, 5, 0, 5);
                                div.setLayoutParams(pd);
                                div.setBackgroundResource(R.color.colorGray);
                                hr.addView(nb);
                                hr.addView(div);
                            }
                        }else {
                            Toast.makeText(getBaseContext(),"No History",Toast.LENGTH_SHORT).show();
                        }
                        //TextView close = popup.findViewById(R.id.close);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                details.dismiss();
                            }
                        });
                        details.showAtLocation(popContainer,Gravity.CENTER,0,0);
                    }
                });
                row.addView(nm);
                row.addView(amt);
                row.addView(rmk);
            }
        }

        //accounts to pay
        if(type.equals("1")){

            atype.setText("TO PAY");
            to_from.setText("TO");
            Cursor rttl = myDB.getTotalToPay();
            while(rttl.moveToNext()){
                if(rttl.getString(0)==null){
                    total.setText("Rs. 0");
                }else {
                    total.setText("Rs. " + Validation.formatToMoney(rttl.getString(0)));
                }
            }
            //extracting data from db
            Cursor res = myDB.getAllAccToPay();
            if(res.getCount() == 0){
                final LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(30,30,30,30);
                //wrap.setId(R.id.contain);
                contain.addView(row);

                final TextView nodata = new TextView(this);
                nodata.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                nodata.setText("No Records");
                //nm.setTypeface(Typeface.DEFAULT_BOLD);
                nodata.setTextSize(14);
                row.addView(nodata);
            }
            while(res.moveToNext()){
                final String  id = res.getString(0);
                final String  name = res.getString(1);
                String  amount = res.getString(2);
                String  remark = res.getString(3);


                //creating dynamic layouts
                final LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(30,30,30,30);
                row.setWeightSum(100);
                //wrap.setId(R.id.contain);
                contain.addView(row);

                final TextView i = new TextView(this);
                i.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
                i.setText(id);
                i.setVisibility(View.INVISIBLE);

                final TextView nm = new TextView(this);
                nm.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,30));
                nm.setText(name);
                //nm.setTypeface(Typeface.DEFAULT_BOLD);
                nm.setTextSize(12);
                //nm.setBackgroundResource(R.drawable.button_round);
                nm.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                final TextView amt = new TextView(this);
                LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,40);
                //pm.gravity = Gravity.CENTER_HORIZONTAL;
                amt.setLayoutParams(pm);
                amt.setText("Rs. "+amount);
                //amt.setBackgroundResource(R.drawable.button_round);
                //amt.setTypeface(Typeface.DEFAULT_BOLD);
                amt.setTextSize(12);
                amt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                final TextView rmk = new TextView(this);
                rmk.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,30));
                rmk.setText(remark);
                //rmk.setTypeface(Typeface.DEFAULT_BOLD);
                rmk.setTextSize(12);
                //rmk.setBackgroundResource(R.drawable.button_round);
                rmk.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

                //history here
                //show more detail
                final LinearLayout popContainer = body;
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popup = inflate.inflate(R.layout.history,null);
                        details = new PopupWindow(popup, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
                        if(Build.VERSION.SDK_INT>=21){
                            details.setElevation(5.0f);
                        }

                        final ScrollView scrollView = popup.findViewById(R.id.popscroll);
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.scrollTo(0,scrollView.getBottom());
                            }
                        });
                        LinearLayout box = popup.findViewById(R.id.container);
                        //header container
                        LinearLayout header = popup.findViewById(R.id.acname);
                        //account name
                        final TextView acname = new TextView(getBaseContext());
                        LinearLayout.LayoutParams pn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,40);
                        pn.setMargins(8,5,0,5);
                        pn.weight = 100;
                        acname.setLayoutParams(pn);
                        acname.setText(name);
                        acname.setTextSize(14);
                        acname.setTypeface(Typeface.DEFAULT_BOLD);
                        acname.setGravity(Gravity.LEFT);
                        //delete
                        final ImageView del= new ImageView(getBaseContext());
                        LinearLayout.LayoutParams pcm = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        pcm.setMargins(20,0,20,0);
                        del.setLayoutParams(pcm);
                        rmk.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                        del.setImageResource(R.drawable.ic_delete_white);
                        //close
                        final TextView close = new TextView(getBaseContext());
                        LinearLayout.LayoutParams pc = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,40);
                        pc.setMargins(0,5,0,5);
                        pc.weight = 1;
                        close.setLayoutParams(pc);
                        close.setPadding(10,0,10,0);
                        close.setTextSize(16);
                        close.setBackgroundResource(R.drawable.ic_close);
                        close.setGravity(Gravity.RIGHT);
                        header.addView(acname);
                        header.addView(del);
                        header.addView(close);
                        del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(getActivity(),"Delete?",Toast.LENGTH_SHORT).show();
                                Cursor res = myDB.getOneData(nm.getText().toString());
                                String amt = "";
                                if(res.getCount() == 0){

                                }
                                while(res.moveToNext()) {
                                    amt = res.getString(2);
                                    //Toast.makeText(getActivity(),i.getText().toString().trim()+amt,Toast.LENGTH_SHORT).show();
                                }
                                //Toast.makeText(getActivity(),amt,Toast.LENGTH_SHORT).show();
                                if(amt.trim().equals("0")||amt.trim().equals("")){
                                    int max = 4;
                                    TextInputLayout div = new TextInputLayout(getBaseContext());
                                    div.setHint("CODE");

                                    final EditText input = new EditText(getBaseContext());
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(50,0,50,0);
                                    input.setLayoutParams(params);
                                    input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
                                    input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                    div.addView(input);

                                    input.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            String in = input.getText().toString().trim();
                                            if(!in.equals("")){
                                                if(Validation.isNumeric(in)){
                                                }else{
                                                    //Toast.makeText(getActivity(),"Must be numeric.",Toast.LENGTH_LONG).show();
                                                    input.setText(in.substring(0,in.length()-1));
                                                }
                                            }else {
                                                input.setHint("Code");
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });
                                    new AlertDialog.Builder(getBaseContext())
                                            .setTitle("Confirm Delete?")
                                            .setMessage("Please enter code")
                                            .setView(div)
                                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String code = input.getText().toString().trim();
                                                    SharedPreferences prefs = getApplication().getSharedPreferences(PREFS,0);
                                                    //Toast.makeText(this,prefs.getString("code","")+code,Toast.LENGTH_SHORT).show();
                                                    if(prefs.getString("code","").trim().equals(code.trim())){
                                                        Integer delRow = myDB.deleteData(i.getText().toString().trim());
                                                        if(delRow>0) {
                                                            Toast.makeText(getApplicationContext(), nm.getText().toString() + " has been deleted", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(intent);
                                                            getParent().finish();
                                                        }
                                                        else
                                                            Toast.makeText(getApplicationContext(),"Cannot delete record",Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(getApplicationContext(),"Invalid Code",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();

                                }else {
                                    AlertDialog.Builder diag2 = new AlertDialog.Builder(AllAccounts.this);
                                    diag2.setMessage("This account has Rs. "+amt+" to be cleared.");
                                    diag2.setTitle("Cannot be deleted");
                                    diag2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    diag2.create().show();
                                }
                            }
                        });
                        //
                        //get history from db
                        //
                        Cursor hres = myDB.getHistory(name);
                        if(hres.getCount() != 0){
                            while(hres.moveToNext()) {
                                final String hid = hres.getString(0);
                                final String balance = hres.getString(1);
                                String added = hres.getString(2);
                                String subtracted = hres.getString(3);
                                String net = hres.getString(4);
                                String today = hres.getString(5);

                                //check date
                                Date date = Calendar.getInstance().getTime();
                                DateFormat aja = new SimpleDateFormat("d MMM yyyy");
                                DateFormat aday = new SimpleDateFormat("d");
                                String ad = aday.format(date).replaceAll("\\s+","");
                                String ajadate = aja.format(date).replaceAll("\\s+","");
                                //[abc] 1 jan, 1989
                                String[] tdate = today.split(",");
                                String dm = tdate[0].replaceAll("\\s+","");
                                String y = tdate[1].replaceAll("\\s+","");
                                String dmy = dm.substring(5,dm.length())+y.substring(0,4);
                                //getting day
                                String d = dm.substring(5,dm.length()).substring(0,dm.substring(5,dm.length()).length()-3);
                                //extracting time
                                String t = tdate[1].substring(5,tdate[1].length());
                                //day difference
                                Integer diff = Integer.parseInt(d) - Integer.parseInt(ad);
                                //Integer ch = dm.substring(5,dm.length()).length();
                                //Toast.makeText(getActivity(),ch.toString(),Toast.LENGTH_SHORT).show();

                                if(ajadate.equals(dmy)){
                                    today = "Today"+t;
                                }else if(diff==1){
                                    today = "Yesterday"+t;
                                }

                                final LinearLayout hr = new LinearLayout(getBaseContext());
                                hr.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                hr.setOrientation(LinearLayout.VERTICAL);
                                hr.setPadding(30, 10, 10, 10);
                                box.addView(hr);
                                //date time
                                final TextView dt = new TextView(getBaseContext());
                                dt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                dt.setText(today);
                                dt.setTextSize(10);
                                //balance
                                final TextView b = new TextView(getBaseContext());
                                b.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                b.setText("Bal: " + balance);
                                b.setTextColor(Color.parseColor("#5499c7"));
                                hr.addView(dt);
                                hr.addView(b);
                                //added
                                final TextView add = new TextView(getBaseContext());
                                if(!added.equals("") && !added.equals("0")) {
                                    add.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    add.setText("+ " + added);
                                    add.setPadding(45, 0, 0, 0);
                                    add.setTextColor(Color.parseColor("#0b6623"));
                                    hr.addView(add);
                                }
                                //Toast.makeText(getActivity(),subtracted,Toast.LENGTH_SHORT).show();
                                final TextView sub = new TextView(getBaseContext());
                                if(!subtracted.equals("") && !subtracted.equals("0")) {
                                    sub.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                    sub.setText("- " + subtracted);
                                    sub.setPadding(45, 0, 0, 0);
                                    sub.setTextColor(Color.parseColor("#ed2939"));
                                    hr.addView(sub);
                                }
                                //Net Balance
                                final TextView nb = new TextView(getBaseContext());
                                nb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                nb.setText("Net: "+net);
                                nb.setTypeface(Typeface.DEFAULT_BOLD);
                                //Divider
                                final View div = new View(getBaseContext());
                                LinearLayout.LayoutParams pd = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                                pd.setMargins(0, 5, 0, 5);
                                div.setLayoutParams(pd);
                                div.setBackgroundResource(R.color.colorGray);
                                hr.addView(nb);
                                hr.addView(div);
                            }
                        }else {
                            Toast.makeText(getBaseContext(),"No History",Toast.LENGTH_SHORT).show();
                        }
                        //TextView close = popup.findViewById(R.id.close);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                details.dismiss();
                            }
                        });
                        details.showAtLocation(popContainer,Gravity.CENTER,0,0);
                    }
                });
                row.addView(nm);
                row.addView(amt);
                row.addView(rmk);
            }
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
