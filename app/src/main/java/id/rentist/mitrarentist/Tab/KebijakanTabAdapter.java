package id.rentist.mitrarentist.Tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import id.rentist.mitrarentist.fragment.KebijakanKhususFragment;
import id.rentist.mitrarentist.fragment.KebijakanUmumFragment;

/**
 * Created by Nugroho Tri Pambud on 7/7/2017.
 */

public class KebijakanTabAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles={"Umum","Khusus"};

    private int current_position=0;

    public void set_current_position(int i){current_position=i;}

    public KebijakanTabAdapter(FragmentManager fm, Context c) {
        super(fm);
        mContext = c;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        if(position == 0){
            frag =  new KebijakanUmumFragment();
        }else if(position == 1){
            frag = new KebijakanKhususFragment();
        }

        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public CharSequence getPageTitle(int position){
        if (position == current_position) {
            return "Umum";
        } else if (position == current_position+1) {
            return "Khusus";
        }

        return null;
    }
}
