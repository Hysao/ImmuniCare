package com.myprograms.immunicare.healthworker.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.healthworker.update.History;
import com.myprograms.immunicare.healthworker.update.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference historyRef = db.collection("history");
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private RecyclerView historyRecycler;
    private HistoryAdapter historyAdapter;
    private List<History> historyList;

    private Button viewHistoryBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        viewHistoryBtn = view.findViewById(R.id.viewHistoryBtn);
        historyRecycler = view.findViewById(R.id.historyRecycler);

        viewHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), HwHistoryActivity.class);
                startActivity(i);
            }
        });

        // Initialize RecyclerView
        historyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyList, getContext());
        historyRecycler.setAdapter(historyAdapter);

        // Fetch and display history data
        fetchHistory();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchHistory();

    }

    private void fetchHistory() {
        if (mUser == null) return;

        historyRef.whereEqualTo("hWorkerId", mUser.getUid())
                .limit(2)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        historyList = task.getResult().toObjects(History.class);
                        historyAdapter = new HistoryAdapter(historyList, getContext());
                        historyRecycler.setAdapter(historyAdapter);
                    } else {
                        System.err.println("Error fetching history: " + task.getException());
                    }
                });
    }
}
