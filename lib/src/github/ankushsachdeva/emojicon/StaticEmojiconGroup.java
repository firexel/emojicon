package github.ankushsachdeva.emojicon;

import android.content.Context;

import java.util.List;

/**
 * Created by aleksandr.naumov on 13.05.2015.
 */
class StaticEmojiconGroup extends EmojiconGroup {
    private final List<Emojicon> mEmojicons;

    public StaticEmojiconGroup(List<Emojicon> emojicons, int iconResId) {
        super(iconResId);
        mEmojicons = emojicons;
    }

    @Override
    public List<Emojicon> getEmojicons() {
        return mEmojicons;
    }

    @Override
    public EmojiAdapter createAdapter(Context context) {
        EmojiAdapter adapter = new EmojiAdapter(context);
        adapter.setEmojiconList(getEmojicons());
        return adapter;
    }
}
