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


public class HepatitisBFragment extends Fragment {

    private TextView desc1, desc2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hepatitis_b, container, false);

        desc1.findViewById(R.id.hepDesc1);
        desc2.findViewById(R.id.hepDesc2);

        String text1 = "\n" + getString(R.string.hep_txt1)
                + "\n" + getString(R.string.hep_txt2)
                + "\n" + getString(R.string.hep_txt3);

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

        // ... (similar logic for desc2 if needed)

        String text2 = "\n" + getString(R.string.hep_txt4) +
                "\n" + getString(R.string.hep_txt5) +
                "\n" + getString(R.string.hep_txt6);

        SpannableString spannable2 = new SpannableString(text2);
        BulletSpan bulletSpan2 = new OutlinedBulletSpan(20, Color.WHITE, Color.BLACK);

        int start2 = 0;
        while (start2 < text2.length()) {
            int end = text2.indexOf('\n', start2);
            if (end != -1) {
                spannable2.setSpan(bulletSpan2, start2, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                start2 = end + 1;
            } else {
                break;
            }
        }

        desc2.setText(spannable2);


        return view;
    }
}