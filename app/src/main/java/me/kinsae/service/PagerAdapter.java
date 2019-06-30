package me.kinsae.service;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int tabNums;
    public PagerAdapter(FragmentManager FM, int TabNums){
        super(FM);
        this.tabNums = TabNums;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Stat one = new Stat();
                return one;
            case 1:
                Add two = new Add();
                return  two;
            case 2:
                Sub three = new Sub();
                return three;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNums;
    }
}
