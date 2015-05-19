package github.ankushsachdeva.emojicon;

import android.content.Context;

/**
 * Created by aleksandr.naumov on 14.05.2015.
 */
class EmojiRecentAdapter extends EmojiAdapter implements Updatable {
    private final EmojiconRecentsManager mRecentsManager;

    public EmojiRecentAdapter(Context context, EmojiconRecentsManager mRecentsManager) {
        super(context);
        this.mRecentsManager = mRecentsManager;
        update();
    }

    @Override
    public void update() {
        setEmojiconList(mRecentsManager.getEmojiList());
    }
}
