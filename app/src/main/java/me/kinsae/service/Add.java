package me.kinsae.service;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Add.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Add#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Add extends Fragment implements AdapterView.OnItemSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Add() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Add.
     */
    // TODO: Rename and change types and number of parameters
    public static Add newInstance(String param1, String param2) {
        Add fragment = new Add();
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

    DatabaseHelper myDB;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add,container,false);

        //initialization
        myDB = new DatabaseHelper(getActivity());
        final Spinner name = v.findViewById(R.id.name1);
        final EditText amount = v.findViewById(R.id.AmountAdd);
        Button add = v.findViewById(R.id.add);
        final CheckBox chkToRec = v.findViewById(R.id.chkRec);
        final CheckBox chkToPay = v.findViewById(R.id.chkPay);

        //collect names
        final Cursor[] res = {null};
        final ArrayList<String> values = new ArrayList<String>();
        values.add("Select a name");
        if(chkToRec.isChecked()) {
            res[0] = myDB.getAllAccToReceive();
            while(res[0].moveToNext()){
                values.add(res[0].getString(1));
            }
        }
        chkToRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res[0] = myDB.getAllAccToReceive();
                values.clear();
                name.setSelection(0);
                values.add("Select a name");
                while(res[0].moveToNext()){
                    values.add(res[0].getString(1));
                }
                chkToPay.setChecked(false);
                if(!chkToRec.isChecked()){
                    chkToRec.setChecked(true);
                }else {

                }
            }
        });
        chkToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res[0] = myDB.getAllAccToPay();
                values.clear();
                name.setSelection(0);
                values.add("Select a name");
                while(res[0].moveToNext()){
                    values.add(res[0].getString(1));
                }
                chkToRec.setChecked(false);
                if(!chkToPay.isChecked()){
                    chkToPay.setChecked(true);
                }else {

                }
            }
        });

        final Spinner nameList = v.findViewById(R.id.name1);
        nameList.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameList.setAdapter(dataAdapter);

        //text changed event
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String in = amount.getText().toString().trim();
                if(!in.equals("")){
                    if(Validation.isNumeric(in)){
                    }else {
                        //Toast.makeText(getActivity(),"!",Toast.LENGTH_SHORT).show();
                        amount.setText(in.substring(0,in.length()-1));
                    }
                }else {
                    amount.setHint("0.00");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //button action
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name.getSelectedItem().toString().equals("Select a name")){
                    if(!amount.getText().toString().trim().equals("")&&!amount.getText().toString().trim().equals("0.00")){
                        String plus = amount.getText().toString().trim();
                        String nam = name.getSelectedItem().toString().trim();
                        String id="";
                        String old = "";
                        String acname ="";
                        Cursor res = myDB.getOneData(nam);
                        if(res.getCount() == 0){

                        }
                        while(res.moveToNext()){
                            old = res.getString(2);
                            id = res.getString(0);
                            acname = res.getString(1);
                        }
                        Integer newAmt = Integer.parseInt(old)+Integer.parseInt(plus);

                        //updating db
                        boolean isUpdate = myDB.updateData(id,newAmt.toString());
                        if (isUpdate){
                            myDB.addToHistoy(acname,old,plus,"0",newAmt.toString());
                            //dialog
                            AlertDialog.Builder diag2 = new AlertDialog.Builder(getActivity());
                            diag2.setMessage("Previous Balance: " + old + "\nNew Balance: "+newAmt.toString()+"\nAdded: "+plus);
                            diag2.setTitle("INFO");
                            diag2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            diag2.create().show();
                            amount.setText("");
                        }else {

                        }
                    }else {
                        //Toast.makeText(getActivity(),"Amount khali xa!",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    //Toast.makeText(getActivity(),"Nam rojnus!",Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
