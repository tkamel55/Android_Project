package com.example.android_project;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class TopUsersListAdapter extends RecyclerView.Adapter<TopUsersListAdapter.TopUsersListViewholder> {
    Context mContext;
    List<TopUserScoreModel> scoresList;

    public TopUsersListAdapter(Context mContext, List<TopUserScoreModel> scoresList) {
        this.scoresList = scoresList;
        this.mContext = mContext;
    }

    @Override
    public TopUsersListViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = parent.inflate(mContext, R.layout.log_list_row, null);
        TopUsersListViewholder topUsersListViewholder = new TopUsersListViewholder(view);
        return topUsersListViewholder;
    }

    @Override
    public void onBindViewHolder(TopUsersListViewholder holder, final int position) {

        holder.tvName.setText(scoresList.get(position).getUser().getFirstName() + " " + scoresList.get(position).getUser().getLastName());
        holder.tvDescription.setVisibility(View.GONE);
        holder.tvStepsCount.setText(scoresList.get(position).getScore().getSteps() + " steps");

    }


    @Override
    public int getItemCount() {
        return scoresList.size() > 0 ? scoresList.size() : 0;
    }

    public void updateData(List<TopUserScoreModel> topUsers) {
        this.scoresList.clear();
        this.scoresList.addAll(topUsers);
        notifyDataSetChanged();
    }

    public class TopUsersListViewholder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDescription, tvStepsCount;

        public TopUsersListViewholder(View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tvName);
            this.tvDescription = itemView.findViewById(R.id.tvDescription);
            this.tvStepsCount = itemView.findViewById(R.id.tvStepsCount);
        }
    }

}
