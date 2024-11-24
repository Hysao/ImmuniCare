package com.myprograms.immunicare.user.setting;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.user.AddMoreActivity;
import com.myprograms.immunicare.user.ChildDataActivity;
import com.myprograms.immunicare.user.setting.reminder.Children;

import java.util.List;

public class ChildrenViewAdapter extends RecyclerView.Adapter<ChildrenViewAdapter.ChildrenViewHolder>{

    private Context context;
    private List<Children> childrenList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference childrenRef = db.collection("children");

    public ChildrenViewAdapter(List<Children> childrenList, Context context) {
        this.childrenList = childrenList;
        this.context = context;
    }


    @NonNull
    @Override
    public ChildrenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_children, parent, false);
            return new ChildrenViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ChildrenViewHolder holder, int position) {
        Children child = childrenList.get(position);


        holder.childName.setText(child.getChildName());


        holder.cardView.setOnClickListener(v -> {
            String documentId = child.getDocumentId();
            Intent intent = new Intent(context, ChildDataActivity.class);
            intent.putExtra("documentId", documentId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return childrenList != null ? childrenList.size() : 0;
    }

    public static class ChildrenViewHolder extends RecyclerView.ViewHolder {

        public TextView childName;
        public CardView cardView;


        public ChildrenViewHolder(@NonNull View itemView) {
            super(itemView);

            childName = itemView.findViewById(R.id.childNameView);
            cardView = itemView.findViewById(R.id.cardViewChild);

        }
    }
}
