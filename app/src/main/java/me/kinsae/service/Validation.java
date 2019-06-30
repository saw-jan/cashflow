package me.kinsae.service;

import android.text.TextUtils;

import java.text.DecimalFormat;

import static android.text.TextUtils.isDigitsOnly;

public class Validation {
    public static boolean IsEmptyText(String input){
        if (TextUtils.isEmpty(input)) {
            return true;
        } else {
            return false;
        }
    }
    //checks, is numbers?
    public static boolean isNumeric(String str)
    {
        return isDigitsOnly(str);
        //return str.matches("-?\\d+(\\.\\d+)?");
    }
    //remove invalid chars
    public static String removeWords(String word, String remove){
        return word.replace(remove,"");
    }
    //
    public static String formatToMoney(String money){
        if(money.equals("")){
            return "0";
        }else{
        Integer fmoney = Integer.parseInt(money);
        DecimalFormat format = new DecimalFormat("#,###");
        return format.format(fmoney);
        }
    }
}
