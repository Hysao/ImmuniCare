package com.myprograms.immunicare.user.articles;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myprograms.immunicare.R;


public class OpvFragment extends Fragment {

    private TextView desc1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_opv, container, false);

        desc1 = view.findViewById(R.id.opvDesc1);

        String text1 = "\n" + getString(R.string.opv_txt1)
                + "\n" + getString(R.string.opv_txt2)
                + "\n" + getString(R.string.opv_txt3)
                + "\n" + getString(R.string.opv_txt4);

        SpannableString spannable1 = new SpannableString(text1); // Use SpannableString

        BulletSpan bulletSpan = new BulletSpan(20, Color.BLACK);

        int start = 0;
        while (start < text1.length()) {
            int end = text1.indexOf('\n', start);
            if (end != -1) { // Apply bullet only if newline is found
                spannable1.setSpan(bulletSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = end + 1;
            } else {
                break; // Exit loop if no more newlines
            }
        }

        desc1.setText(spannable1);

        return view;
    }
}