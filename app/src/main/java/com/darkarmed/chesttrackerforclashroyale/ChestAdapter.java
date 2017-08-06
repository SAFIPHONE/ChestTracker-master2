package com.darkarmed.chesttrackerforclashroyale;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xu on 5/18/16.
 */
public class ChestAdapter extends BaseAdapter {
    private final Context mContext;
    private List<Chest> mChests = new ArrayList<>();
    private boolean mShowIndexes;
    private String mLoop;
    private int mLastOpened = 0;
    private final int BUFFER_LENGTH = 12;
    private final int SHORT_LOOP_LENGTH = 40;
    private final int FULL_LOOP_LENGTH = 240;

    public ChestAdapter(Context context, List<Chest> chests, boolean showIndexes) {
        mContext = context;
        mChests = chests;
        mShowIndexes = showIndexes;
        mLoop = mContext.getString(R.string.chest_loop);
    }

    public void add(Chest chest) {
        mChests.add(chest);
        notifyDataSetChanged();
    }

    public void clear() {
        mChests.clear();
        notifyDataSetChanged();
    }

    public void remove(Chest chest) {
        mChests.remove(chest);
        notifyDataSetChanged();
    }

    public void remove(int pos) {
        this.remove(mChests.get(pos));
    }

    private void extend(Boolean force) {
        int current_size = mChests.size();
        int pos = getLastOpened();
        if (pos + BUFFER_LENGTH >= current_size) {
            for (int i = current_size; i < current_size + SHORT_LOOP_LENGTH; ++i) {
                mChests.add(new Chest(i % FULL_LOOP_LENGTH + 1, mLoop.charAt(i % FULL_LOOP_LENGTH)));
            }
            notifyDataSetChanged();
        }
    }

    public void open(int pos) {
//        this.open(mChests.get(pos));
        Chest chest = mChests.get(pos);
        if (chest.getStatus() != Chest.Status.OPENED) {
            chest.setStatus(Chest.Status.OPENED);
            skip(pos - 1);
            if (mLastOpened < pos) {
                mLastOpened = pos;
            }
            extend(false);
            notifyDataSetChanged();
        }
    }

    public void open(Chest.Type type) {
        // TODO: open chest
    }

    public void skip(int pos) {
        if (pos >= 0) {
            Chest chest = mChests.get(pos);
            if (chest != null && chest.getStatus() == Chest.Status.LOCKED) {
                chest.setStatus(Chest.Status.SKIPPED);
                skip(pos - 1);
            }
        }
    }

    public void lock(int pos) {
//        this.lock(mChests.get(pos));
        Chest chest = mChests.get(pos);
        if (chest.getStatus() == Chest.Status.OPENED) {
            if (pos == mChests.size() - 1 ||
                    mChests.get(pos + 1).getStatus() == Chest.Status.LOCKED) {
                chest.setStatus(Chest.Status.LOCKED);
                mLastOpened = restore(pos - 1);
            } else {
                chest.setStatus(Chest.Status.SKIPPED);
            }
            notifyDataSetChanged();
        }
    }

    public int restore(int pos){
        if (pos >= 0) {
            Chest chest = mChests.get(pos);
            if (chest != null && chest.getStatus() == Chest.Status.SKIPPED) {
                chest.setStatus(Chest.Status.LOCKED);
                return restore(pos - 1);
            }
        }
        return pos;
    }

    public void updateChests(List<Chest> chests) {
        mChests = chests;
        notifyDataSetChanged();
    }

    public int getLastOpened() {
        return mLastOpened;
    }

    public void setShowIndexes(boolean showIndexes) {
        mShowIndexes = showIndexes;
    }

    @Override
    public int getCount() {
        return mChests.size();
    }

    @Override
    public Object getItem(int pos) {
        return mChests.get(pos);
    }

    public List<Chest> getItems() {
        return mChests;
    }

    @Override
    public long getItemId(int pos) {
        return mChests.get(pos).getIndex();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {

            LayoutInflater layoutInflater = LayoutInflater.from(mContext);

            convertView = layoutInflater.inflate(
                    R.layout.view_chest, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.chest_image);
            holder.textView = (TextView) convertView.findViewById(R.id.chest_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Chest chest = (Chest) getItem(position);
        Boolean matched = chest.getMatched();
        if (matched != null) {
            if (matched) {
                holder.imageView.setBackgroundColor(
                        mContext.getResources().getColor(R.color.colorBackgroundMatched));
            } else {
                holder.imageView.setBackgroundColor(
                        mContext.getResources().getColor(R.color.colorBackgroundNotMatched));
            }
        } else {
            holder.imageView.setBackgroundColor(Color.TRANSPARENT);
        }
        loadImage(holder.imageView, chest);
        if (mShowIndexes) {
            holder.textView.setText(chest.getIndex().toString());
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Supercell-Magic_5.ttf");
            holder.textView.setTypeface(tf);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    private void loadImage(ImageView imageView, Chest chest) {
        imageView.setImageResource(chest.getThumb());
        switch (chest.getStatus()) {
            case LOCKED:
                imageView.setImageAlpha(191);
                break;
            case SKIPPED:
                imageView.setImageAlpha(127);
                break;
            case OPENED:
                imageView.setImageAlpha(255);
                break;
            default:
                imageView.setImageAlpha(255);
        }
    }
}
