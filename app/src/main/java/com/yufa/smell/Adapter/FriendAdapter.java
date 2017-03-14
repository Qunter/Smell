package com.yufa.smell.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yufa.smell.Activity.ChatCenter.HaoyouFragment;
import com.yufa.smell.Activity.ChatCenter.ImageLoader;
import com.yufa.smell.Activity.ChatCenter.RongTt.Friend;
import com.yufa.smell.Entity.UserInformation;
import com.yufa.smell.R;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;


public class FriendAdapter extends BaseAdapter implements AbsListView.OnScrollListener,RongIM.UserInfoProvider{
    private List<UserInformation> mList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private int mStart, mEnd;
    public static String[] URLS;
    private Boolean mFirstIn;
    private List<Friend> userIdList;

    public FriendAdapter(Context context, List<UserInformation> data, ListView listView) {
        mList = data;
        mInflater = LayoutInflater.from(context);
        initUserInfo();
        mImageLoader = new ImageLoader(listView);
        URLS = new String[data.size()];
        for (int i = 0; i < data.size(); i ++){
            URLS[i] = data.get(i).getImage();
        }
        mFirstIn = true;
        //注意为ListView注册滚动监听器
        listView.setOnScrollListener(this);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 重写getView方法，并通过构造ViewHolder和利用ConvertView缓存提高性能
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_fragment_haoyou, null);
            viewHolder.userFriendImg = (ImageView) convertView.findViewById(R.id.friend_image);
            viewHolder.userFriendNickName = (TextView) convertView.findViewById(R.id.friend_nickname);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String url = mList.get(position).getImage();
        //viewHolder.userFriendImg.setTag(url);
        viewHolder.userFriendImg.setTag(position);
//        new ImageLoader().showImageByThread(viewHolder.ivIcon, url);
        mImageLoader.showImageByAsyncTask(viewHolder.userFriendImg, url,position);
        viewHolder.userFriendNickName.setText(mList.get(position).getNickName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("onClick", "onClick:");
                RongIM.getInstance().startPrivateChat(mInflater.getContext(), mList.get(position).getPhone(), mList.get(position).getNickName());
            }
        });
        return convertView;
    }

    /**
     * Callback method to be invoked while the list view or grid view is being scrolled. If the
     * view is being scrolled, this method will be called before the next frame of the scroll is
     * rendered. In particular, it will be called before any calls to
     * @param view        The view whose scroll state is being reported
     * @param scrollState The current scroll state. One of
     *                    {@link #SCROLL_STATE_TOUCH_SCROLL} or {@link #SCROLL_STATE_IDLE}.
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            //加载可见项
            mImageLoader.loadImages(mStart, mEnd);
        } else {
            //停止任务
            mImageLoader.cancelAllTasks();
        }
    }

    /**
     * Callback method to be invoked when the list or grid has been scrolled. This will be
     * called after the scroll has completed
     * @param view             The view whose scroll state is being reported
     * @param firstVisibleItem the index of the first visible cell (ignore if
     *                         visibleItemCount == 0)
     * @param visibleItemCount the number of visible cells
     * @param totalItemCount   the number of items in the list adaptor
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        if (mFirstIn && visibleItemCount > 0){
            mImageLoader.loadImages(mStart, mEnd);
            mFirstIn = false;
        }
    }


    class ViewHolder {
        public TextView userFriendNickName;
        public ImageView userFriendImg;
    }
    /**
     * 储存好友相关信息：id，昵称，token
     */
    private void initUserInfo() {
        userIdList = new ArrayList<Friend>();
        UserInformation userFriendInformation;
        for (int i=0;i<mList.size();i++){
            userFriendInformation = mList.get(i);
            //好友内容为:id,昵称,头像url
            userIdList.add(new Friend(userFriendInformation.getPhone(),userFriendInformation.getNickName(),userFriendInformation.getImage()));
        }
        RongIM.setUserInfoProvider(this, true);
    }
    @Override
    public UserInfo getUserInfo(String s) {

        for (Friend i : userIdList) {
            if (i.getUserId().equals(s)) {
                //Log.e(TAG, i.getPortraitUri());
                return new UserInfo(i.getUserId(), i.getName(), Uri.parse(i.getPortraitUri()));
            }
        }


        Log.e("MainActivity", "UserId is : " + s);
        return null;
    }
}
