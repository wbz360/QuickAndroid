package com.appdsn.qa.adapter;


import android.app.Fragment;
import android.app.FragmentManager;

import java.util.List;

public class CommonBaseFragmentAdapter extends BaseFragmentAdapter
{

    List<Fragment> fragments;
    public CommonBaseFragmentAdapter(FragmentManager fm, List<Fragment> fragments)
    {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getCount()
    {
        return fragments.size();
    }


}