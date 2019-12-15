package com.example.wordsbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentActivity extends Fragment {

    private TextView textView1,textView2,textView3;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment,null);
        textView1=view.findViewById(R.id.fragment_textView1);
        textView2=view.findViewById(R.id.fragment_textView2);
        textView3=view.findViewById(R.id.fragment_textView3);
        Bundle bundle=getArguments();
        textView1.setText(bundle.getString("word"));
        textView2.setText(bundle.getString("meaning"));
        textView3.setText(bundle.getString("sample"));
        return view;
    }
}
