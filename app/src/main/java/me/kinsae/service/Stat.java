package me.kinsae.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Stat.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Stat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Stat extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Stat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Stat.
     */
    // TODO: Rename and change types and number of parameters
    public static Stat newInstance(String param1, String param2) {
        Stat fragment = new Stat();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    public static final String PREFS = "prefs";
    DatabaseHelper myDB;
    private Context mContext;
    private Activity mActivity;
    private PopupWindow details;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stat,container,false);
        myDB = new DatabaseHelper(getActivity());

        mContext = getContext();
        mActivity = getActivity();

        //container = v.findViewById(R.id.container);
        //Floating  button action
        FloatingActionButton f_add = v.findViewById(R.id.f_add);
        f_add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //fingerprint intent
                /*
                //Toast.makeText(getActivity(),prefs.getString("code",""),Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(getActivity(), FingerPrint.class);
                startActivity(mainIntent);
                getActivity().finish();*/
                SharedPreferences prefs = getActivity().getSharedPreferences(PREFS,0);
                if(prefs.getString("code","")!=""){
                    Intent mainIntent = new Intent(getActivity(), AddNew.class);
                    startActivity(mainIntent);
                    getActivity().finish();
                }
                else {
                    AlertDialog.Builder diag2 = new AlertDialog.Builder(getActivity());
                    diag2.setMessage("Create a PASSCODE first\nAnd keep all your data safe.");
                    diag2.setTitle("ALERT");
                    diag2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), Code.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                    diag2.create().show();
                }
            }
        });

        //Layouts for "To Receive"
        LinearLayout contain = v.findViewById(R.id.toreceive);

        //extracting data from db
        Cursor res = myDB.getAccToReceive();
        if(res.getCount() == 0){
            final LinearLayout row = new LinearLayout(getContext());
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(30,30,30,30);
            //wrap.setId(R.id.contain);
            contain.addView(row);

            final TextView nodata = new TextView(getContext());
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
            final LinearLayout row = new LinearLayout(getContext());
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(30,30,30,30);
            row.setWeightSum(100);
            //wrap.setId(R.id.contain);
            contain.addView(row);

            final TextView i = new TextView(getContext());
            i.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            i.setText(id);
            i.setVisibility(View.INVISIBLE);

            final TextView nm = new TextView(getContext());
            nm.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,30));
            nm.setText(name);
            //nm.setTypeface(Typeface.DEFAULT_BOLD);
            nm.setTextSize(12);
            //nm.setBackgroundResource(R.drawable.button_round);
            nm.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            final TextView amt = new TextView(getContext());
            LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,40);
            //pm.gravity = Gravity.CENTER_HORIZONTAL;
            amt.setLayoutParams(pm);
            amt.setText("Rs. "+amount);
            //amt.setBackgroundResource(R.drawable.button_round);
            //amt.setTypeface(Typeface.DEFAULT_BOLD);
            amt.setTextSize(12);
            amt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            final TextView rmk = new TextView(getContext());
            rmk.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,30));
            rmk.setText(remark);
            //rmk.setTypeface(Typeface.DEFAULT_BOLD);
            rmk.setTextSize(12);
            //rmk.setBackgroundResource(R.drawable.button_round);
            rmk.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

            //history here
            //show more detail
            final ViewGroup finalContainer = container;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //LayoutInflater inflat = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View popup = LayoutInflater.from(getActivity()).inflate(R.layout.history,null);
                    details = new PopupWindow(popup, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
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
                    final TextView acname = new TextView(getContext());
                    LinearLayout.LayoutParams pn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,40);
                    pn.setMargins(8,5,0,5);
                    pn.weight = 100;
                    acname.setLayoutParams(pn);
                    acname.setText(name);
                    acname.setTextSize(14);
                    acname.setTypeface(Typeface.DEFAULT_BOLD);
                    acname.setGravity(Gravity.LEFT);
                    //delete
                    final ImageView del= new ImageView(getContext());
                    LinearLayout.LayoutParams pcm = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    pcm.setMargins(20,0,20,0);
                    del.setLayoutParams(pcm);
                    rmk.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    del.setImageResource(R.drawable.ic_delete_white);
                    //close
                    final TextView close = new TextView(getContext());
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
                                TextInputLayout div = new TextInputLayout(getContext());
                                div.setHint("CODE");

                                final EditText input = new EditText(getActivity());
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
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Confirm Delete?")
                                        .setMessage("Please enter code")
                                        .setView(div)
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String code = input.getText().toString().trim();
                                                SharedPreferences prefs = getActivity().getSharedPreferences(PREFS,0);
                                                //Toast.makeText(getContext(),prefs.getString("code","")+code,Toast.LENGTH_SHORT).show();
                                                if(prefs.getString("code","").trim().equals(code.trim())){
                                                    Integer delRow = myDB.deleteData(i.getText().toString().trim());
                                                    if(delRow>0) {
                                                        Toast.makeText(getContext(), nm.getText().toString() + " has been deleted", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                                        startActivity(intent);
                                                        getActivity().finish();
                                                    }
                                                    else
                                                        Toast.makeText(getContext(),"Cannot delete record",Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(getContext(),"Invalid Code",Toast.LENGTH_SHORT).show();
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
                                AlertDialog.Builder diag2 = new AlertDialog.Builder(getActivity());
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

                            final LinearLayout hr = new LinearLayout(getContext());
                            hr.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            hr.setOrientation(LinearLayout.VERTICAL);
                            hr.setPadding(30, 10, 10, 10);
                            box.addView(hr);
                            //date time
                            final TextView dt = new TextView(getContext());
                            dt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            dt.setText(today);
                            dt.setTextSize(10);
                            //balance
                            final TextView b = new TextView(getContext());
                            b.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            b.setText("Bal: " + balance);
                            b.setTextColor(Color.parseColor("#5499c7"));
                            hr.addView(dt);
                            hr.addView(b);
                            //added
                            final TextView add = new TextView(getContext());
                            if(!added.equals("") && !added.equals("0")) {
                                add.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                add.setText("+ " + added);
                                add.setPadding(45, 0, 0, 0);
                                add.setTextColor(Color.parseColor("#0b6623"));
                                hr.addView(add);
                            }
                            //Toast.makeText(getActivity(),subtracted,Toast.LENGTH_SHORT).show();
                            final TextView sub = new TextView(getContext());
                            if(!subtracted.equals("") && !subtracted.equals("0")) {
                                sub.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                sub.setText("- " + subtracted);
                                sub.setPadding(45, 0, 0, 0);
                                sub.setTextColor(Color.parseColor("#ed2939"));
                                hr.addView(sub);
                            }
                            //Net Balance
                            final TextView nb = new TextView(getContext());
                            nb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            nb.setText("Net: "+net);
                            nb.setTypeface(Typeface.DEFAULT_BOLD);
                            //Divider
                            final View div = new View(getContext());
                            LinearLayout.LayoutParams pd = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                            pd.setMargins(0, 5, 0, 5);
                            div.setLayoutParams(pd);
                            div.setBackgroundResource(R.color.colorGray);
                            hr.addView(nb);
                            hr.addView(div);
                        }
                    }else {
                        Toast.makeText(getActivity(),"No History",Toast.LENGTH_SHORT).show();
                    }
                    //TextView close = popup.findViewById(R.id.close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            details.dismiss();
                        }
                    });
                    details.showAtLocation(finalContainer,Gravity.CENTER,0,0);
                }
            });
            row.addView(nm);
            row.addView(amt);
            row.addView(rmk);
        }
        Cursor rcount = myDB.getRecCount();
        Integer num = rcount.getCount();
        if(num!=0){
            //Toast.makeText(getContext(),"no rec",Toast.LENGTH_SHORT).show();
        final LinearLayout more = new LinearLayout(getContext());
        more.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        more.setOrientation(LinearLayout.VERTICAL);
        more.setPadding(30,30,30,30);
        more.setWeightSum(100);
        contain.addView(more);
        final TextView txtmore = new TextView(getContext());
        LinearLayout.LayoutParams tmp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,40);
        tmp.weight=50;
        txtmore.setLayoutParams(tmp);
        txtmore.setText(".....\nSHOW ALL");
        txtmore.setTextSize(10);
        txtmore.setTypeface(Typeface.DEFAULT_BOLD);
        txtmore.setGravity(Gravity.CENTER_HORIZONTAL);
        txtmore.setTextColor(Color.parseColor("#008B8B"));
        more.addView(txtmore);
        txtmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"more",Toast.LENGTH_SHORT).show();
                Intent all = new Intent(getActivity(),AllAccounts.class);
                all.putExtra("TYPE","0");
                startActivity(all);
                getActivity().finish();
            }
        });
        }
        Cursor rTotal = myDB.getTotalToReceive();
        String Total="0";
            while(rTotal.moveToNext()) {
                Total = rTotal.getString(0);
            }
            if(Total==null){
                Total = "0";
            }
        //Toast.makeText(getContext(),Total,Toast.LENGTH_SHORT).show();

        final LinearLayout ttl = new LinearLayout(getContext());
        ttl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,100));
        ttl.setOrientation(LinearLayout.HORIZONTAL);
        ttl.setPadding(30,30,30,30);
        ttl.setBackgroundColor(Color.parseColor("#E2E2E2"));
        ttl.setWeightSum(100);
        contain.addView(ttl);
        final TextView total = new TextView(getContext());
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,40);
        tp.weight = 40;
        total.setLayoutParams(tp);
        total.setText("G. TOTAL");
        total.setTextSize(14);
        total.setTypeface(Typeface.DEFAULT_BOLD);
        final TextView ttlamt = new TextView(getContext());
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT,40);
        ap.weight = 60;
        ttlamt.setLayoutParams(ap);
        ttlamt.setText("Rs. "+Validation.formatToMoney(Total));
        ttlamt.setTextSize(14);
        ttlamt.setTypeface(Typeface.DEFAULT_BOLD);
        //total.setTextColor(Color.parseColor("#008B8B"));
        ttl.addView(total);
        ttl.addView(ttlamt);

        //To Pay layout
        LinearLayout topay = v.findViewById(R.id.topay);

        //extracting data from db
        Cursor res2 = myDB.getAccToPay();
        if(res2.getCount() == 0){
            final LinearLayout row2 = new LinearLayout(getContext());
            row2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row2.setOrientation(LinearLayout.HORIZONTAL);
            row2.setPadding(30,30,30,30);
            //wrap.setId(R.id.contain);
            topay.addView(row2);

            final TextView nodata2 = new TextView(getContext());
            nodata2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            nodata2.setText("No Records");
            //nm.setTypeface(Typeface.DEFAULT_BOLD);
            nodata2.setTextSize(14);
            row2.addView(nodata2);
        }
        while(res2.moveToNext()) {
            final String id2 = res2.getString(0);
            final String name2 = res2.getString(1);
            String amount2 = res2.getString(2);
            String remark2 = res2.getString(3);


            //creating dynamic layouts
            final LinearLayout row2 = new LinearLayout(getContext());
            row2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row2.setOrientation(LinearLayout.HORIZONTAL);
            row2.setPadding(30, 30, 30, 30);
            row2.setWeightSum(100);
            //wrap.setId(R.id.contain);
            topay.addView(row2);

            final TextView i2 = new TextView(getContext());
            i2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            i2.setText(id2);
            i2.setVisibility(View.INVISIBLE);

            final TextView nm2 = new TextView(getContext());
            nm2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 30));
            nm2.setText(name2);
            //nm.setTypeface(Typeface.DEFAULT_BOLD);
            nm2.setTextSize(12);
            nm2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            final TextView amt2 = new TextView(getContext());
            LinearLayout.LayoutParams pm2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 40);
            amt2.setLayoutParams(pm2);
            amt2.setText("Rs. "+amount2);
            amt2.setTextSize(12);
            amt2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            final TextView rmk2 = new TextView(getContext());
            rmk2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 30));
            rmk2.setText(remark2);
            //rmk.setTypeface(Typeface.DEFAULT_BOLD);
            rmk2.setTextSize(12);
            //rmk.setBackgroundResource(R.drawable.button_round);
            rmk2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

            //history here
            //show more detail
            final ViewGroup finalContainer2 = container;
            row2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //LayoutInflater inflat = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View popup = LayoutInflater.from(getActivity()).inflate(R.layout.history, null);
                    details = new PopupWindow(popup, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    final ScrollView scrollView = popup.findViewById(R.id.popscroll);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, scrollView.getBottom());
                        }
                    });
                    LinearLayout box = popup.findViewById(R.id.container);
                    //header container
                    LinearLayout header = popup.findViewById(R.id.acname);
                    //account name
                    final TextView acname = new TextView(getContext());
                    LinearLayout.LayoutParams pn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 40);
                    pn.setMargins(8, 5, 0, 5);
                    pn.weight = 100;
                    acname.setLayoutParams(pn);
                    acname.setText(name2);
                    acname.setTextSize(14);
                    acname.setTypeface(Typeface.DEFAULT_BOLD);
                    acname.setGravity(Gravity.LEFT);
                    //delete
                    final ImageView del = new ImageView(getContext());
                    LinearLayout.LayoutParams pcm = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    pcm.setMargins(20, 0, 20, 0);
                    del.setLayoutParams(pcm);
                    rmk2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    del.setImageResource(R.drawable.ic_delete_white);
                    //close
                    final TextView close = new TextView(getContext());
                    LinearLayout.LayoutParams pc = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 40);
                    pc.setMargins(0, 5, 0, 5);
                    pc.weight = 1;
                    close.setLayoutParams(pc);
                    close.setPadding(10, 0, 10, 0);
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
                            Cursor res = myDB.getOneData(nm2.getText().toString());
                            String amt = "";
                            if (res.getCount() == 0) {

                            }
                            while (res.moveToNext()) {
                                amt = res.getString(2);
                                //Toast.makeText(getActivity(),i.getText().toString().trim()+amt,Toast.LENGTH_SHORT).show();
                            }
                            //Toast.makeText(getActivity(),amt,Toast.LENGTH_SHORT).show();
                            if (amt.trim().equals("0") || amt.trim().equals("")) {
                                int max = 4;
                                TextInputLayout div = new TextInputLayout(getContext());
                                div.setHint("CODE");

                                final EditText input = new EditText(getActivity());
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(50, 0, 50, 0);
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
                                        if (!in.equals("")) {
                                            if (Validation.isNumeric(in)) {
                                            } else {
                                                //Toast.makeText(getActivity(),"Must be numeric.",Toast.LENGTH_LONG).show();
                                                input.setText(in.substring(0, in.length() - 1));
                                            }
                                        } else {
                                            input.setHint("Code");
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Confirm Delete?")
                                        .setMessage("Please enter code")
                                        .setView(div)
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String code = input.getText().toString().trim();
                                                SharedPreferences prefs = getActivity().getSharedPreferences(PREFS, 0);
                                                //Toast.makeText(getContext(),prefs.getString("code","")+code,Toast.LENGTH_SHORT).show();
                                                if (prefs.getString("code", "").trim().equals(code.trim())) {
                                                    Integer delRow = myDB.deleteData(i2.getText().toString().trim());
                                                    if (delRow > 0) {
                                                        Toast.makeText(getContext(), nm2.getText().toString() + " has been deleted", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                                        startActivity(intent);
                                                        getActivity().finish();
                                                    } else
                                                        Toast.makeText(getContext(), "Cannot delete record", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), "Invalid Code", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .show();

                            } else {
                                AlertDialog.Builder diag2 = new AlertDialog.Builder(getActivity());
                                diag2.setMessage("This account has Rs. " + amt + " to be cleared.");
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
                    Cursor hres = myDB.getHistory(name2);
                    if (hres.getCount() != 0) {
                        while (hres.moveToNext()) {
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
                            String ad = aday.format(date).replaceAll("\\s+", "");
                            String ajadate = aja.format(date).replaceAll("\\s+", "");
                            //[abc] 1 jan, 1989
                            String[] tdate = today.split(",");
                            String dm = tdate[0].replaceAll("\\s+", "");
                            String y = tdate[1].replaceAll("\\s+", "");
                            String dmy = dm.substring(5, dm.length()) + y.substring(0, 4);
                            //getting day
                            String d = dm.substring(5, dm.length()).substring(0, dm.substring(5, dm.length()).length() - 3);
                            //extracting time
                            String t = tdate[1].substring(5, tdate[1].length());
                            //day difference
                            Integer diff = Integer.parseInt(d) - Integer.parseInt(ad);
                            //Integer ch = dm.substring(5,dm.length()).length();
                            //Toast.makeText(getActivity(),ch.toString(),Toast.LENGTH_SHORT).show();

                            if (ajadate.equals(dmy)) {
                                today = "Today" + t;
                            } else if (diff == 1) {
                                today = "Yesterday" + t;
                            }

                            final LinearLayout hr = new LinearLayout(getContext());
                            hr.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            hr.setOrientation(LinearLayout.VERTICAL);
                            hr.setPadding(30, 10, 10, 10);
                            box.addView(hr);
                            //date time
                            final TextView dt = new TextView(getContext());
                            dt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            dt.setText(today);
                            dt.setTextSize(10);
                            //balance
                            final TextView b = new TextView(getContext());
                            b.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            b.setText("Bal: " + balance);
                            b.setTextColor(Color.parseColor("#5499c7"));
                            hr.addView(dt);
                            hr.addView(b);
                            //added
                            final TextView add = new TextView(getContext());
                            if (!added.equals("") && !added.equals("0")) {
                                add.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                add.setText("+ " + added);
                                add.setPadding(45, 0, 0, 0);
                                add.setTextColor(Color.parseColor("#0b6623"));
                                hr.addView(add);
                            }
                            //Toast.makeText(getActivity(),subtracted,Toast.LENGTH_SHORT).show();
                            final TextView sub = new TextView(getContext());
                            if (!subtracted.equals("") && !subtracted.equals("0")) {
                                sub.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                sub.setText("- " + subtracted);
                                sub.setPadding(45, 0, 0, 0);
                                sub.setTextColor(Color.parseColor("#ed2939"));
                                hr.addView(sub);
                            }
                            //Net Balance
                            final TextView nb = new TextView(getContext());
                            nb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            nb.setText("Net: " + net);
                            nb.setTypeface(Typeface.DEFAULT_BOLD);
                            //Divider
                            final View div = new View(getContext());
                            LinearLayout.LayoutParams pd = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                            pd.setMargins(0, 5, 0, 5);
                            div.setLayoutParams(pd);
                            div.setBackgroundResource(R.color.colorGray);
                            hr.addView(nb);
                            hr.addView(div);
                        }
                    } else {
                        Toast.makeText(getActivity(), "No History", Toast.LENGTH_SHORT).show();
                    }
                    //TextView close = popup.findViewById(R.id.close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            details.dismiss();
                        }
                    });
                    details.showAtLocation(finalContainer2, Gravity.CENTER, 0, 0);
                }
            });
            row2.addView(nm2);
            row2.addView(amt2);
            row2.addView(rmk2);
        }
        Cursor pcount = myDB.getPayCount();
        Integer pnum = pcount.getCount();
        if(pnum!=0) {
            final LinearLayout more2 = new LinearLayout(getContext());
            more2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            more2.setOrientation(LinearLayout.VERTICAL);
            more2.setPadding(30, 30, 30, 30);
            more2.setWeightSum(100);
            topay.addView(more2);
            final TextView txtmore2 = new TextView(getContext());
            LinearLayout.LayoutParams tmp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 40);
            tmp2.weight = 50;
            txtmore2.setLayoutParams(tmp2);
            txtmore2.setText(".....\nSHOW ALL");
            txtmore2.setTextSize(10);
            txtmore2.setTypeface(Typeface.DEFAULT_BOLD);
            txtmore2.setGravity(Gravity.CENTER_HORIZONTAL);
            txtmore2.setTextColor(Color.parseColor("#008B8B"));
            more2.addView(txtmore2);
            txtmore2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(),"more",Toast.LENGTH_SHORT).show();
                    Intent all = new Intent(getActivity(), AllAccounts.class);
                    all.putExtra("TYPE", "1");
                    startActivity(all);
                    getActivity().finish();
                }
            });
        }
        Cursor pTotal = myDB.getTotalToPay();
        String pTtl="0";
        while(pTotal.moveToNext()) {
            pTtl = pTotal.getString(0);
        }
        if(pTtl==null){
            pTtl="0";
        }
        final LinearLayout ttl2 = new LinearLayout(getContext());
        ttl2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,100));
        ttl2.setOrientation(LinearLayout.HORIZONTAL);
        ttl2.setPadding(30,30,30,30);
        ttl2.setBackgroundColor(Color.parseColor("#E2E2E2"));
        ttl2.setWeightSum(100);
        topay.addView(ttl2);
        final TextView total2 = new TextView(getContext());
        LinearLayout.LayoutParams tp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,40);
        tp2.weight = 40;
        total2.setLayoutParams(tp2);
        total2.setText("G. TOTAL");
        total2.setTextSize(14);
        total2.setTypeface(Typeface.DEFAULT_BOLD);
        final TextView ttlamt2 = new TextView(getContext());
        LinearLayout.LayoutParams ap2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT,40);
        ap2.weight = 60;
        ttlamt2.setLayoutParams(ap2);
        ttlamt2.setText("Rs. "+Validation.formatToMoney(pTtl));
        ttlamt2.setTextSize(14);
        ttlamt2.setTypeface(Typeface.DEFAULT_BOLD);
        //total.setTextColor(Color.parseColor("#008B8B"));
        ttl2.addView(total2);
        ttl2.addView(ttlamt2);
        //end to pay
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
