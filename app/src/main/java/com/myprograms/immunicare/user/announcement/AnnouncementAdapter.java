package com.myprograms.immunicare.user.announcement;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.myprograms.immunicare.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {

    private Context context;
    private List<Announcements> listAnnouncements;

    public AnnouncementAdapter(Context context, List<Announcements> listAnnouncements) {
        this.context = context;
        this.listAnnouncements = listAnnouncements;
    }


    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {

        Announcements announcements = listAnnouncements.get(position);
        holder.title.setText(announcements.getAnnouncementTitle());
        holder.description.setText(announcements.getAnnouncementDesc());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String formattedDate = sdf.format(announcements.getAnnouncementDate().toDate());
        holder.date.setText(formattedDate);


        holder.author.setText(announcements.getAnnouncementAuthor());

    }

    @Override
    public int getItemCount() {
        return listAnnouncements.size();
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder{

        public TextView title, description, date, author;
        public CardView cardView;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);


            title = itemView.findViewById(R.id.announcementTitleTxt);
            description = itemView.findViewById(R.id.announcementDescriptionTxt);
            date = itemView.findViewById(R.id.announcementDateTxt);
            author = itemView.findViewById(R.id.announcementAuthor);
            cardView = itemView.findViewById(R.id.announcementCard);



        }
    }
}
