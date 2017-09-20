package com.e7yoo.e7.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e7yoo.e7.util.DebugUtil;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.Robot;
import com.e7yoo.e7.util.RobotUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/28.
 */
public class RobotRefreshRecyclerAdapter extends RecyclerAdapter{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Robot> mRobots = new ArrayList<>();
    public static final int VIEW_TYPE_ROBOT = 0;
    public static final int VIEW_TYPE_FOOTER = 10;
    private int FOOTER_COUNT = 1;
    private int MAX_COUNT = 5;

    public RobotRefreshRecyclerAdapter(Context context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
        DebugUtil.setRobotDatas(mRobots, 3, true);
    }

    public void addItemTop(Robot newData) {
        mRobots.add(0, newData);
        notifyDataSetChanged();
    }

    public void addItemBottom(Robot newData) {
        mRobots.add(newData);
        notifyDataSetChanged();
    }

    public void refreshData(List<Robot> newDatas) {
        mRobots.clear();
        if(newDatas != null) {
            mRobots.addAll(newDatas);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;
        switch (viewType) {
            case VIEW_TYPE_FOOTER:
                view = mInflater.inflate(R.layout.item_robot_footer, parent, false);
                viewHolder = new ViewHolderFooter(view);
                break;
            case VIEW_TYPE_ROBOT:
            default:
                view = mInflater.inflate(R.layout.item_robot, parent, false);
                viewHolder = new ViewHolderRobot(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ViewHolderRobot) {
            Robot robot = mRobots.get(position);
            ViewHolderRobot viewHolderRobot = (ViewHolderRobot) holder;
            int resIcon = RobotUtil.getDefaultIconResId(robot);
            if(robot.getIcon() != null) {
                Glide.with(mContext).load(robot.getIcon()).placeholder(resIcon).error(resIcon).into(viewHolderRobot.robotIcon);
            } else {
                viewHolderRobot.robotIcon.setImageResource(resIcon);
            }
            setSex(viewHolderRobot.sexIcon, robot.getSex());
            viewHolderRobot.nameTv.setText(robot.getName());
            viewHolderRobot.contentTv.setText(robot.getWelcome());

            addClickListener(holder.itemView, position);
        } else if(holder instanceof ViewHolderFooter) {
            ViewHolderFooter viewHolderFooter = (ViewHolderFooter) holder;

            addClickListener(viewHolderFooter.addTv, position);
        }
        holder.itemView.setTag(position);
    }

    private void setSex(ImageView imageView, int sex) {
        switch (sex) {
            case 1:
                imageView.setImageResource(R.mipmap.sex_male_selected);
                break;
            case 2:
                imageView.setImageResource(R.mipmap.sex_female_selected);
                break;
            case 0:
            default:
                imageView.setImageResource(R.mipmap.sex_unknow_selected);
                break;
        }
    }

    private void addClickListener(View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(view, position);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return mOnItemLongClickListener.onItemLongClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mRobots == null) {
            FOOTER_COUNT = 1;
            return FOOTER_COUNT;
        } else {
            if(mRobots.size() >= MAX_COUNT) {
                FOOTER_COUNT = 0;
                return MAX_COUNT;
            } else {
                FOOTER_COUNT = 1;
                return mRobots.size() + FOOTER_COUNT;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        int itemViewType;
        if (position == getItemCount() - FOOTER_COUNT) {
            itemViewType = VIEW_TYPE_FOOTER;
        } else {
            itemViewType = VIEW_TYPE_ROBOT;
        }
        return itemViewType;
    }

    /**
     * item
     */
    public static class ViewHolderRobot extends RecyclerView.ViewHolder {
        public ImageView robotIcon;
        public ImageView sexIcon;
        public TextView nameTv;
        public TextView timeTv;
        public TextView contentTv;

        public ViewHolderRobot(View view) {
            super(view);
            robotIcon = view.findViewById(R.id.item_robot_icon);
            sexIcon = view.findViewById(R.id.item_robot_sex);
            nameTv = view.findViewById(R.id.item_robot_name);
            timeTv = view.findViewById(R.id.item_robot_time);
            contentTv = view.findViewById(R.id.item_robot_content);
        }
    }

    /**
     * 底部item（用于增加item）
     */
    public static class ViewHolderFooter extends RecyclerView.ViewHolder {
        public TextView addTv;
        public ViewHolderFooter(View view) {
            super(view);
            addTv = view.findViewById(R.id.item_robot_add);
        }
    }

    public Robot getRobot(int position) {
        return mRobots != null && mRobots.size() > position && position >= 0 ? mRobots.get(position) : null;
    }
}
