package com.douyin.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.douyin.bean.MainVideoBean;
import com.douyin.view.PlayTextureView;
import com.example.administrator.douyin.R;

import java.util.ArrayList;

public class VideoDetailsAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<MainVideoBean> mainVideoBeanList = new ArrayList<>();

    public VideoDetailsAdapter(Context mContext, ArrayList<MainVideoBean> mainVideoBeanList) {
        this.mContext = mContext;
        if (mainVideoBeanList != null) {
            this.mainVideoBeanList.addAll(mainVideoBeanList);
        }
    }

    public void setData(ArrayList<MainVideoBean> mainVideoBeanList) {
        if (mainVideoBeanList != null) {
            this.mainVideoBeanList.clear();
            this.mainVideoBeanList.addAll(mainVideoBeanList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(View.inflate(mContext, R.layout.item_video_details, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder vh = (MyViewHolder) holder;
        MainVideoBean mainVideoBean = mainVideoBeanList.get(position);

        vh.pb_play_progress.setSecondaryProgress(0);
        vh.pb_play_progress.setProgress(0);

        Glide.with(mContext).load(mainVideoBean.getCoverUrl()).into(vh.iv_cover);

        vh.tv_content.setText(mainVideoBean.getContent());
        vh.tv_name.setText(mainVideoBean.getUserName());
    }

    @Override
    public int getItemCount() {
        return mainVideoBeanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_cover;
        public PlayTextureView playTextureView;
        public ProgressBar pb_video;
        public ProgressBar pb_play_progress;
        public ImageView img_play;
        private ImageView iv_avatar;
        private TextView tv_name;
        private TextView tv_content;

        public MyViewHolder(View itemView) {
            super(itemView);

            playTextureView = itemView.findViewById(R.id.playTextureView);
            iv_cover = itemView.findViewById(R.id.iv_cover);
            pb_video = itemView.findViewById(R.id.pb_video);
            pb_play_progress = itemView.findViewById(R.id.pb_play_progress);
            img_play = itemView.findViewById(R.id.img_play);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);

            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            pb_play_progress.getProgressDrawable().setColorFilter(ContextCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_IN);
        }
    }
}
