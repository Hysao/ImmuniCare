package com.myprograms.immunicare.healthworker.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.healthworker.update.History;
import com.myprograms.immunicare.healthworker.update.HistoryAdapter;

import java.util.List;


public class HistoryFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference historyRef = db.collection("history");
    private DocumentReference historyDocRef = historyRef.document("historyId");

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private RecyclerView historyRecycler;
    private Button viewHistory;

    private HistoryAdapter historyAdapter;
    private List<History> historyList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        historyRecycler = view.findViewById(R.id.historyRecycler);
        historyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch top 3 recent history records
        historyRef.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        historyList = task.getResult().toObjects(History.class);
                        historyAdapter = new HistoryAdapter(historyList, getContext());
                        historyRecycler.setAdapter(historyAdapter);
                    } else {
                        // Handle error
                        System.out.println("Error fetching history: " + task.getException());
                    }
                });

        return view;
    }
}