package com.example.android_project;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewholder> {
    Context mContext;
    List<User> userList;
    FriendButtonClickedListener friendButtonClickedListener;
    int form = -1;

    public FriendListAdapter(Context mContext, List<User> userList, FriendButtonClickedListener friendButtonClickedListener, int form) {
        this.userList = userList;
        this.mContext = mContext;
        this.friendButtonClickedListener = friendButtonClickedListener;
        this.form=form;
    }

    @Override
    public FriendListAdapter.FriendListViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = parent.inflate(mContext, R.layout.user_list_row, null);
        FriendListViewholder friendListViewholder = new FriendListViewholder(view);
        return friendListViewholder;
    }

    @Override
    public void onBindViewHolder(FriendListAdapter.FriendListViewholder holder, final int position) {

        holder.tvName.setText(userList.get(position).getFirstName() + " " + userList.get(position).getLastName());
        holder.tvDescription.setText(userList.get(position).getAddress());
        if (form==2) {
            holder.tvAddFriend.setVisibility(View.VISIBLE);

            holder.tvAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friendButtonClickedListener.onAddFriendButtonClicked(userList.get(position));
                }
            });
        }else {
            holder.tvAddFriend.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return userList.size() > 0 ? userList.size() : 0;
    }

    public void updateData(List<User> userList) {
        this.userList.clear();
        this.userList.addAll(userList);
        notifyDataSetChanged();
    }

    public class FriendListViewholder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDescription;
        private Button tvAddFriend;

        public FriendListViewholder(View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tvName);
            this.tvDescription = itemView.findViewById(R.id.tvDescription);
            this.tvAddFriend = itemView.findViewById(R.id.tvAddFriend);
        }
    }

    public interface FriendButtonClickedListener {
        void onAddFriendButtonClicked(User user);
    }
}
