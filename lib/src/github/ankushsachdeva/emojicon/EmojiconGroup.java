package github.ankushsachdeva.emojicon;

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
        for (int charIndex = 0; charIndex < emojiconsString.length(); charIndex++) {
            int codePoint = emojiconsString.codePointAt(charIndex);
            int codePointLength = Character.charCount(codePoint);
            emojicons.add(new Emojicon(emojiconsString.substring(charIndex, charIndex + codePointLength)));
            if (codePointLength > 1) {
                charIndex += codePointLength - 1;
            }
        }
        return emojicons;
    }

    public abstract List<Emojicon> getEmojicons();

    public abstract EmojiAdapter createAdapter();

    public int getIconResId() {
        return mIconResId;
    }
}
