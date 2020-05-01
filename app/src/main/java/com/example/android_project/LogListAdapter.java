package com.example.android_project;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.LogListViewholder> {
    Context mContext;
    List<Scores> scoresList;

    public LogListAdapter(Context mContext, List<Scores> scoresList) {
        this.scoresList = scoresList;
        this.mContext = mContext;
    }

    @Override
    public LogListViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = parent.inflate(mContext, R.layout.log_list_row, null);
        LogListViewholder logListViewholder = new LogListViewholder(view);
        return logListViewholder;
    }

    @Override
    public void onBindViewHolder(LogListViewholder holder, final int position) {

        holder.tvName.setText(StaticAccess.parseCreatedDate(scoresList.get(position).getCreatedAt()));
        holder.tvDescription.setText(StaticAccess.getMiniFromStartEndTime(scoresList.get(position).getStartTime(),
                scoresList.get(position).getEndTime()));
        holder.tvStepsCount.setText(scoresList.get(position).getSteps() + " steps");

    }


    @Override
    public int getItemCount() {
        return scoresList.size() > 0 ? scoresList.size() : 0;
    }

    public class LogListViewholder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDescription, tvStepsCount;

        public LogListViewholder(View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tvName);
            this.tvDescription = itemView.findViewById(R.id.tvDescription);
            this.tvStepsCount = itemView.findViewById(R.id.tvStepsCount);
        }
    }

    public interface FriendButtonClickedListener {
        void onAddFriendButtonClicked(User user);
    }


}
