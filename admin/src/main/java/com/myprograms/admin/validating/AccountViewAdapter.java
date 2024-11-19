package com.myprograms.admin.validating;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.admin.R;

import java.util.List;
import java.util.Objects;

public class AccountViewAdapter extends RecyclerView.Adapter<AccountViewAdapter.AccountViewHolder> implements OnUserStatusChangeListener {

    private final List<Users> usersList;
    private final Context context;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = db.collection("users");

    public AccountViewAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }


    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_view, parent, false);
        return new AccountViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Users users = usersList.get(position);

        holder.userName.setText(users.getName());
        holder.userId.setText(users.getUid());
        holder.userEmail.setText(users.getEmail());
        holder.userAddress.setText(users.getAddress());

        holder.userData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.moreData.getVisibility() == View.GONE) {
                    holder.moreData.setVisibility(View.VISIBLE);
                } else {
                    holder.moreData.setVisibility(View.GONE);
                }
            }
        });

        if (!Objects.equals(users.getStatus(), "pending")) {
            holder.btnLinear.setVisibility(View.GONE);
            holder.userStatus.setVisibility(View.VISIBLE);
            holder.userStatus.setText(users.getStatus());
        } else {
            holder.btnLinear.setVisibility(View.VISIBLE);
            holder.userStatus.setVisibility(View.GONE);
        }

        holder.approve.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                usersRef.document(users.getUid()).update("status", "approved")
                        .addOnSuccessListener(aVoid -> {

                            usersList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, usersList.size());

                        })
                        .addOnFailureListener(e -> {

                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();


                        });
            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                usersRef.document(users.getUid()).update("status", "rejected")
                        .addOnSuccessListener(aVoid -> {

                            usersList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, usersList.size());
                        })

                        .addOnFailureListener(e -> {

                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onUserStatusChange() {
        notifyDataSetChanged();

    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userId, userEmail, userAddress, userStatus;
        public LinearLayout userData, moreData, btnLinear;
        public Button approve, reject;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);


            userName = itemView.findViewById(R.id.userName);
            userId = itemView.findViewById(R.id.userId);
            userEmail = itemView.findViewById(R.id.userEmail);
            userAddress = itemView.findViewById(R.id.userAddress);
            userData = itemView.findViewById(R.id.userData);
            moreData = itemView.findViewById(R.id.moreData);
            approve = itemView.findViewById(R.id.ApprovedBtn);
            reject = itemView.findViewById(R.id.rejectBtn);
            btnLinear = itemView.findViewById(R.id.btnLinear);
            userStatus = itemView.findViewById(R.id.userStatus);


        }
    }
}
