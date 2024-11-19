package com.myprograms.admin.validating.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private List<Users> pendingAccountList;
    private AccountViewAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_pending, container, false);


        pendingAccountRecycler = view.findViewById(R.id.userPendingRecycler);

        pendingAccountRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        pendingAccountRecycler.setHasFixedSize(true);

        Query query = usersRef.whereEqualTo("status", "pending").whereEqualTo("isHw", false);

        pendingAccountList = new ArrayList<>();

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Users users = documentSnapshot.toObject(Users.class);
                        pendingAccountList.add(users);
                    }

                    adapter = new AccountViewAdapter(pendingAccountList, getContext());

                    pendingAccountRecycler.setAdapter(adapter);

                    adapter.notifyDataSetChanged();

                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Loading Data Failed", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}