package id.rentist.mitrarentist.Tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by mdhif on 07/07/2017.
 */

public class TemplateTabAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles={"Rental", "Pemilik"};
    private int mCount = titles.length;

    // CHANGE STARTS HERE
    private int current_position=0;
    public void set_current_position(int i) {
        current_position = i;
    }
    // CHANGE ENDS HERE

    public TemplateTabAdapter(FragmentManager fm, Context c){
        super(fm);
        mContext = c;
        double scale = c.getResources().getDisplayMetrics().density;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        if(position == 0){
//            frag = new YourFragment();
        }else if(position == 1){
//            frag = new YourFragment();
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
//        Drawable d = mContext.getResources().getDrawable(icon[position]);
//        d.setBounds(0,0,heightIcon, heightIcon);
//        ImageSpan im = new ImageSpan(d);
//
//        SpannableString sp = new SpannableString(" ");
//        sp.setSpan(im, 0,sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

//        return sp;
        return null;
    }
}
