package com.harish.hk185080.chatterbox.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.harish.hk185080.chatterbox.fragments.chats.ChatsFragment;
import com.harish.hk185080.chatterbox.fragments.contacts.ContactsFragment;
import com.harish.hk185080.chatterbox.fragments.search.SearchFragment;
import com.harish.hk185080.chatterbox.fragments.settings.SettingsFragment;
import com.harish.hk185080.chatterbox.fragments.settings.account_settings.AccountSettingsFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull AppCompatActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ContactsFragment();
            case 1:
                return new ChatsFragment();
            case 2:
                return new SettingsFragment();
            case 3:
                return new AccountSettingsFragment();
            case 4:
                return new SearchFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 5; // Number of fragments
    }
}
