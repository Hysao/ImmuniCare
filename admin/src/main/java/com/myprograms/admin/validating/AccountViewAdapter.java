package com.myprograms.admin.validating;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
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
        Users user = usersList.get(position);

        // Bind user data to the views
        holder.userName.setText(user.getName());
        holder.userEmail.setText(user.getEmail());
        holder.userPhone.setText(user.getPhone());
        holder.userAddress.setText(user.getAddress());

        // Set click listener for the card view
        holder.cardView.setOnClickListener(v -> {
            // Retrieve documentId
            String documentId = user.getUserId(); // Assuming userId is the documentId

            // Start the next activity and pass documentId
            Intent intent = new Intent(context, UserDetailsActivity.class);
            intent.putExtra("documentId", documentId);
            context.startActivity(intent);
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

        public MaterialTextView userName, userEmail, userPhone, userAddress;
        public MaterialCardView cardView;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);


            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            userPhone = itemView.findViewById(R.id.userPhone);
            userAddress = itemView.findViewById(R.id.userAddress);
            cardView = itemView.findViewById(R.id.cardView);



        }
    }
}
