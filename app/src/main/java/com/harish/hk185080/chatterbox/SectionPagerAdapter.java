package com.harish.hk185080.chatterbox;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionPagerAdapter extends FragmentPagerAdapter {
    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
//            case 0:
//                RequestFragment requestFragment=new RequestFragment();
//                return requestFragment;
            case 0:
                ChatsFragment chatsFragment=new ChatsFragment();
                return chatsFragment;
            case 1:
                FriendsFragment friendsFragment=new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }
public CharSequence getPageTitle(int position)
{
    switch (position)
    {
//        case 0:
//            return "REQUESTS";
        case 0:
            return "CHATS";
        case 1:
            return "FRIENDS";
        default:
            return null;
    }
}
}
