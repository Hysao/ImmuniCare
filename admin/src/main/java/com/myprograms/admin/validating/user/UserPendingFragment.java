package com.myprograms.admin.validating.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.myprograms.admin.R;
import com.myprograms.admin.validating.AccountViewAdapter;
import com.myprograms.admin.validating.Users;

import java.util.ArrayList;
import java.util.List;

public class UserPendingFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");
    private RecyclerView pendingAccountRecycler;
    private List<Users> pendingAccountList = new ArrayList<>();

    private AccountViewAdapter adapter;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_pending, container, false);


        pendingAccountRecycler = view.findViewById(R.id.userPendingRecycler);

        pendingAccountRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        pendingAccountRecycler.setHasFixedSize(true);

        loadPending();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadPending();


    }

    private void loadPending() {
        Query query = usersRef.whereEqualTo("isHw", false);


        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Toast.makeText(getContext(), "No pending users found", Toast.LENGTH_SHORT).show();
                return;
            }

            pendingAccountList.clear();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Users users = documentSnapshot.toObject(Users.class);
                pendingAccountList.add(users);
            }

            if (adapter == null) {
                adapter = new AccountViewAdapter(pendingAccountList, requireContext());
                pendingAccountRecycler.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Loading Data Failed", Toast.LENGTH_SHORT).show();
            Log.e("UserPendingFragment", "Error loading data", e);
        });
    }
}