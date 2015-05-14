package github.ankushsachdeva.emojicon;

import java.util.List;

/**
 * Created by aleksandr.naumov on 14.05.2015.
 */
class RecentsEmojiconGroup extends EmojiconGroup {

    private final EmojiconRecentsManager mRecentsManager;

    public RecentsEmojiconGroup(EmojiconRecentsManager mRecentsManager, int iconResId) {
        super(iconResId);
        this.mRecentsManager = mRecentsManager;
    }

    @Override
    public List<Emojicon> getEmojicons() {
        return mRecentsManager.getEmojiList();
    }

    @Override
    public EmojiAdapter createAdapter() {
        return new EmojiRecentAdapter(mRecentsManager);
    }
}
