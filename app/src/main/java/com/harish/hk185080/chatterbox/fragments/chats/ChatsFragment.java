package com.harish.hk185080.chatterbox.fragments.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.harish.hk185080.chatterbox.databinding.FragmentChatsBinding;

import com.harish.hk185080.chatterbox.adapter.ChatsAdapter;
import com.harish.hk185080.chatterbox.model.ChatThread;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private RecyclerView.Adapter mChatsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(ChatsViewModel.class);

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView mChatsList = binding.chats;
        mChatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mChatsList.setClipToPadding(false);
        mChatsList.setHasFixedSize(true);

        populateChats();

        mChatsList.setAdapter(mChatsAdapter);
        return root;
    }

    private void populateChats() {
        mChatsAdapter = new ChatsAdapter(getChats(), getContext());
    }

    private List<ChatThread> getChats() {
        List<ChatThread> chatThreads = new ArrayList<>();

        ChatThread chatThread1 = new ChatThread("1", "Harish", "This is a very very long string to test the UI if it can accommodate long strings");
        ChatThread chatThread2 = new ChatThread("2", "Barre", "Barre@gmail.com");
        ChatThread chatThread3 = new ChatThread("3", "comp", "comp@gmail.com");


        chatThreads.add(chatThread1);
        chatThreads.add(chatThread2);
        chatThreads.add(chatThread3);
        chatThreads.add(chatThread1);
        chatThreads.add(chatThread2);
        chatThreads.add(chatThread3);
        chatThreads.add(chatThread1);
        chatThreads.add(chatThread2);
        chatThreads.add(chatThread3);
        chatThreads.add(chatThread1);
        chatThreads.add(chatThread2);
        chatThreads.add(chatThread3);
        chatThreads.add(chatThread1);
        chatThreads.add(chatThread2);
        chatThreads.add(chatThread3);
        chatThreads.add(chatThread1);
        chatThreads.add(chatThread2);
        chatThreads.add(chatThread3);
        chatThreads.add(chatThread1);
        chatThreads.add(chatThread2);
        chatThreads.add(chatThread3);


        return chatThreads;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}