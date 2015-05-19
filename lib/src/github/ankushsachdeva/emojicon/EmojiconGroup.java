package github.ankushsachdeva.emojicon;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandr.naumov on 14.05.2015.
 */
abstract class EmojiconGroup {
    protected final int mIconResId;

    public EmojiconGroup(int iconResId) {
        mIconResId = iconResId;
    }

    public static EmojiconGroup fromString(String allEmojicons, int mIconResId) {
        return new StaticEmojiconGroup(extractEmojicons(allEmojicons), mIconResId);
    }

    private static List<Emojicon> extractEmojicons(String emojiconsString) {
        List<Emojicon> emojicons = new ArrayList<>();
        for (String emojiconString : emojiconsString.split(" ")) {
            if(!emojiconString.isEmpty()) {
                emojicons.add(new Emojicon(emojiconString));
            }
        }
        return emojicons;
    }

    public abstract List<Emojicon> getEmojicons();

    public abstract EmojiAdapter createAdapter(Context context);

    public int getIconResId() {
        return mIconResId;
    }
}
