package com.darkarmed.chesttrackerforclashroyale;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * Created by Xu on 6/4/16.
 */
public class ChestTrackerPagerAdapter extends FragmentPagerAdapter {
    private GuiderFragment mGuiderFragment;
    private TrackerFragment mTrackerFragment;

    public ChestTrackerPagerAdapter(FragmentManager fm, GuiderFragment gf, TrackerFragment tf) {
        super(fm);
        mGuiderFragment = gf;
        mTrackerFragment = tf;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mGuiderFragment;
            case 1:
                return mTrackerFragment;
            default:
                return null;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
