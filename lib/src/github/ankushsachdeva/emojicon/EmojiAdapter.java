/*
 * Copyright 2014 Ankush Sachdeva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.ankushsachdeva.emojicon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Ankush Sachdeva (sankush@yahoo.co.in)
 */
class EmojiAdapter extends BaseAdapter {
    private final List<Emojicon> mEmojicons = new ArrayList<>();
    private OnEmojiClickedListener mClickListener;
    private final Context mContext;

    public EmojiAdapter(Context context) {
        this.mContext = context;
    }

    public void setEmojiconList(List<Emojicon> emojiconList) {
        mEmojicons.clear();
        mEmojicons.addAll(emojiconList);
        notifyDataSetChanged();
    }

    public void setClickListener(OnEmojiClickedListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public int getCount() {
        return mEmojicons.size();
    }

    @Override
    public Emojicon getItem(int position) {
        return mEmojicons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.emojicon_item, null);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onEmojiClicked(((ViewHolder) v.getTag()).mEmoji);
                    }
                }
            });
            ViewHolder holder = new ViewHolder();
            holder.mEmojiContainer = (TextView) v.findViewById(R.id.emojiContainer);
            holder.mEmojiId = (TextView) v.findViewById(R.id.emojiId);
            v.setTag(holder);
        }
        Emojicon emoji = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.mEmoji = emoji;
        holder.mEmojiId.setText(emoji.getId());
        holder.mEmojiContainer.setText(emoji.toString());
        return v;
    }

    private class ViewHolder {
        TextView mEmojiContainer;
        TextView mEmojiId;
        Emojicon mEmoji;
    }

    public interface OnEmojiClickedListener {
        void onEmojiClicked(Emojicon emojicon);
    }
}