package github.ankushsachdeva.emojicon;

/**
 * Created by aleksandr.naumov on 14.05.2015.
 */
class EmojiRecentAdapter extends EmojiAdapter implements Updatable {
    private final EmojiconRecentsManager mRecentsManager;

    public EmojiRecentAdapter(EmojiconRecentsManager mRecentsManager) {
        this.mRecentsManager = mRecentsManager;
        update();
    }

    @Override
    public void update() {
        setEmojiconList(mRecentsManager.getEmojiList());
    }
}
