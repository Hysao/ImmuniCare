package com.myprograms.immunicare.healthworker.update;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>
{

    private Context context;
    private List<History> historyList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference historyRef = db.collection("history");

    public HistoryAdapter(List<History> historyList, Context context) {
        this.historyList = historyList;
        this.context = context;
    }


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_history_hw, parent, false);
        return new HistoryViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);

        holder.historyDate.setText(history.getUpdatedDate());
        holder.childName.setText(history.getChildName());
        holder.childId.setText(history.getChildId());
        holder.hWorkerId.setText(history.gethWorkerId());
        holder.hWorkerName.setText(history.gethWorkerName());
        holder.updatedCheckboxes.setText(history.getUpdatedCheckboxes());
        holder.historyDocumentId.setText(history.getHistoryDocumentId());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class  HistoryViewHolder extends RecyclerView.ViewHolder {

        public TextView historyDate, childName, childId, hWorkerId, hWorkerName, updatedCheckboxes, historyDocumentId;
        public CardView cardView;
        public LinearLayout linearLayout;


        public HistoryViewHolder(View itemView) {
            super(itemView);

            historyDate = itemView.findViewById(R.id.updatedDate);
            childName = itemView.findViewById(R.id.childName);
            childId = itemView.findViewById(R.id.childId);
            hWorkerId = itemView.findViewById(R.id.hWorkerId);
            hWorkerName = itemView.findViewById(R.id.hWorkerName);
            cardView = itemView.findViewById(R.id.cardViewHistory);
            linearLayout = itemView.findViewById(R.id.updatedMore);
            updatedCheckboxes = itemView.findViewById(R.id.updatedCheckboxes);
            historyDocumentId = itemView.findViewById(R.id.historyDocumentId);



        }
    }
}
