package github.ankushsachdeva.emojicon;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;

/**
 * Created by aleksandr.naumov on 15.05.2015.
 */
public class EmojiconPopupDelegate {
    public static final String KEY_EMOJI_POPUP_SHOWN = "key_emoji_popup_shown";
    private Context mContext;
    private EmojiconsPopup mPopup;
    private EditText mInputEditText;
    private boolean mPendingShow;

    @Nullable
    private PopupShownListener mListener;

    public void setShowHideListener(PopupShownListener listener) {
        this.mListener = listener;
    }

    public void setInputEditText(EditText editText) {
        mInputEditText = editText;
    }

    public void attach(View rootView) {
        mContext = rootView.getContext();
        mPopup = new EmojiconsPopup(rootView, mContext);
        mPopup.setOnDismissListener(new DismissListener());
        mPopup.setOnSoftKeyboardOpenCloseListener(new KeyboardOpenCloseListener());
        mPopup.setOnEmojiconClickedListener(new EmojiconClickedListener());
        mPopup.setOnEmojiconBackspaceClickedListener(new BackspaceClickedListener());
        mPopup.attachGlobalLayoutListener();
        if (mPendingShow) {
            show();
        }
    }

    public void detach() {
        hide();
        if(mPopup != null) {
            mPopup.detachGlobalLayoutListener();
            mPopup = null;
        }
        mContext = null;
        mInputEditText = null;
    }

    public void saveState(Bundle bundle) {
        bundle.putBoolean(KEY_EMOJI_POPUP_SHOWN, isShown());
    }

    public void restoreState(Bundle bundle) {
        if (bundle.containsKey(KEY_EMOJI_POPUP_SHOWN)) {
            if (bundle.getBoolean(KEY_EMOJI_POPUP_SHOWN)) {
                show();
            } else {
                hide();
            }
        }
    }

    public void show() {
        if (mPopup != null && !mPopup.isShowing()) {
            if (mPopup.isKeyBoardOpen()) {
                //If keyboard is visible, simply show the emoji popup
                mPopup.showAtBottom();
            } else {
                //else, open the text keyboard first and immediately after that show the emoji popup
                mInputEditText.setFocusableInTouchMode(true);
                mInputEditText.requestFocus();
                mPopup.showAtBottomPending();
                final InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mInputEditText, InputMethodManager.SHOW_IMPLICIT);
            }
            mPendingShow = false;
            notifyShown();
        } else if (!mPendingShow) {
            mPendingShow = true;
            notifyShown();
        }
    }

    public void hide() {
        if (mPopup != null && mPopup.isShowing()) {
            mPopup.dismiss();
            notifyHidden();
        } else if (mPendingShow) {
            mPendingShow = false;
            notifyHidden();
        }
    }

    private void notifyShown() {
        if (mListener != null) {
            mListener.onPopupShown();
        }
    }

    private void notifyHidden() {
        if (mListener != null) {
            mListener.onPopupHidden();
        }
    }

    public boolean isShown() {
        return mPopup != null ? mPopup.isShowing() : mPendingShow;
    }

    public void toggle() {
        if (isShown()) {
            hide();
        } else {
            show();
        }
    }

    public interface PopupShownListener {
        void onPopupShown();

        void onPopupHidden();
    }

    private class BackspaceClickedListener implements EmojiconsPopup.OnEmojiconBackspaceClickedListener {
        @Override
        public void onEmojiconBackspaceClicked(View v) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            mInputEditText.dispatchKeyEvent(event);
        }
    }

    private class EmojiconClickedListener implements EmojiconsPopup.OnEmojiconClickedListener {

        @Override
        public void onEmojiconClicked(Emojicon emojicon) {
            String textToInsert = emojicon.toString();
            int start = Math.max(mInputEditText.getSelectionStart(), 0);
            int end = Math.max(mInputEditText.getSelectionEnd(), 0);
            mInputEditText.getText().replace(Math.min(start, end), Math.max(start, end), textToInsert, 0, textToInsert.length());
        }
    }

    private class KeyboardOpenCloseListener implements EmojiconsPopup.OnSoftKeyboardOpenCloseListener {
        @Override
        public void onKeyboardOpen(int keyBoardHeight) {

        }

        @Override
        public void onKeyboardClose() {
            hide();
        }
    }

    private class DismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            notifyHidden();
        }
    }
}
