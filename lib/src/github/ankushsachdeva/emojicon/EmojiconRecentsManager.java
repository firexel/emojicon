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

import java.util.Iterator;
import java.util.LinkedList;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * @author Daniele Ricci
 */
class EmojiconRecentsManager {

    private static final String PREFERENCE_NAME = "emojicon";
    private static final String PREF_RECENTS = "recent_emojis";
    private static final String PREF_PAGE = "recent_page";
    private static final String EMOJI_DIVIDER = "~";

    private final LinkedList<Emojicon> mEmoji;
    private Context mContext;

    public EmojiconRecentsManager(Context context) {
        mContext = context.getApplicationContext();
        mEmoji = loadRecent();
    }

    public LinkedList<Emojicon> getEmojiList() {
        return mEmoji;
    }

    public int getRecentPage() {
        return getPreferences().getInt(PREF_PAGE, 0);
    }

    public void setRecentPage(int page) {
        getPreferences().edit().putInt(PREF_PAGE, page).commit();
    }

    public void push(Emojicon object) {
        if (mEmoji.contains(object)) {
            mEmoji.remove(object);
        }
        mEmoji.addFirst(object);
    }

    private SharedPreferences getPreferences() {
        return mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    private LinkedList<Emojicon> loadRecent() {
        LinkedList<Emojicon> emojicons = new LinkedList<>();
        SharedPreferences prefs = getPreferences();
        String allEmoji = prefs.getString(PREF_RECENTS, "");
        if (allEmoji != null) {
            for (String emojiconString : allEmoji.split(EMOJI_DIVIDER)) {
                emojicons.add(new Emojicon(emojiconString));
            }
        }
        return emojicons;
    }

    public void save() {
        StringBuilder str = new StringBuilder();
        for (Iterator<Emojicon> iterator = mEmoji.iterator(); iterator.hasNext(); ) {
            Emojicon emojicon = iterator.next();
            str.append(emojicon.toString()).append(iterator.hasNext() ? EMOJI_DIVIDER : "");
        }
        getPreferences().edit().putString(PREF_RECENTS, str.toString()).apply();
    }
}
