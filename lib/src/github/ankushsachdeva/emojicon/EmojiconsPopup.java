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
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


/**
 * @author Ankush Sachdeva (sankush@yahoo.co.in).
 */

public class EmojiconsPopup extends PopupWindow implements ViewPager.OnPageChangeListener {
    private View[] mEmojiTabs;
    private EmojiconRecentsManager mRecentsManager;
    private int keyBoardHeight = 0;
    private boolean mWaitingForKbOpen = false;
    private boolean mIsOpened = false;
    private View mRootView;
    private Context mContext;
    private ViewPager mEmojisPager;

    @Nullable
    private OnSoftKeyboardOpenCloseListener mSoftKeyboardOpenCloseListener;

    @Nullable
    private OnEmojiconClickedListener mEmojiconClickedListener;

    @Nullable
    private OnEmojiconBackspaceClickedListener mEmojiconBackspaceClickedListener;
    private GlobalLayoutListener mGlobalLayoutListener = new GlobalLayoutListener();

    /**
     * Constructor
     *
     * @param rootView The top most layout in your view hierarchy. The difference of this view and the screen height will be used to calculate the keyboard height.
     * @param context  The context of current activity.
     */
    public EmojiconsPopup(View rootView, Context context) {
        super(context);
        mContext = context;
        mRootView = rootView;
        mRecentsManager = new EmojiconRecentsManager(context);
        setContentView(createCustomView());
        setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setSize((int) mContext.getResources().getDimension(R.dimen.keyboard_height), LayoutParams.MATCH_PARENT);
    }

    /**
     * Set the listener for the event of keyboard opening or closing.
     */
    public void setOnSoftKeyboardOpenCloseListener(OnSoftKeyboardOpenCloseListener listener) {
        this.mSoftKeyboardOpenCloseListener = listener;
    }

    /**
     * Set the listener for the event when any of the emojicon is clicked
     */
    public void setOnEmojiconClickedListener(OnEmojiconClickedListener listener) {
        this.mEmojiconClickedListener = listener;
    }

    /**
     * Set the listener for the event when backspace on emojicon popup is clicked
     */
    public void setOnEmojiconBackspaceClickedListener(OnEmojiconBackspaceClickedListener listener) {
        this.mEmojiconBackspaceClickedListener = listener;
    }

    /**
     * Use this function to show the emoji popup.
     * NOTE: Since, the soft keyboard sizes are variable on different android devices, the
     * library needs you to open the soft keyboard atleast once before calling this function.
     * If that is not possible see showAtBottomPending() function.
     */
    public void showAtBottom() {
        showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * Use this function when the soft keyboard has not been opened yet. This
     * will show the emoji popup after the keyboard is up next time.
     * Generally, you will be calling InputMethodManager.showSoftInput function after
     * calling this function.
     */
    public void showAtBottomPending() {
        if (isKeyBoardOpen()) {
            showAtBottom();
        } else {
            mWaitingForKbOpen = true;
        }
    }

    /**
     * @return Returns true if the soft keyboard is open, false otherwise.
     */
    public Boolean isKeyBoardOpen() {
        return mIsOpened;
    }

    /**
     * Dismiss the popup
     */
    @Override
    public void dismiss() {
        super.dismiss();
        mRecentsManager.save();
    }

    /**
     * Call this function to resize the emoji popup according to your soft keyboard size
     */
    public void attachGlobalLayoutListener() {
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    public void detachGlobalLayoutListener() {
        mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalLayoutListener);
    }

    /**
     * Manually set the popup window size
     *
     * @param width  Width of the popup
     * @param height Height of the popup
     */
    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    private View createCustomView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.emojicons, null);
        List<EmojiconGroup> displayedGroups = collectDisplayedGroups();
        initViewPager(view, displayedGroups);
        initTabs(view, displayedGroups);
        initCurrentPage();
        return view;
    }

    private void initCurrentPage() {
        // get last selected page
        int page = mRecentsManager.getRecentPage();
        // last page was recents, check if there are recents to use
        // if none was found, go to page 1
        if (page == 0 && mRecentsManager.getEmojiList().isEmpty()) {
            page = 1;
        }
        if (page == 0) {
            onPageSelected(page);
        } else {
            mEmojisPager.setCurrentItem(page, false);
        }
    }

    private void initTabs(View view, List<EmojiconGroup> displayedGroups) {
        LinearLayout tabHostLayout = (LinearLayout) view.findViewById(R.id.emojis_tab);
        mEmojiTabs = new View[displayedGroups.size()];
        for (int i = 0; i < displayedGroups.size(); i++) {
            final int position = i;
            mEmojiTabs[i] = inflateTab(tabHostLayout, displayedGroups.get(i).getIconResId());
            mEmojiTabs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEmojisPager.setCurrentItem(position);
                }
            });
            inflateDivider(tabHostLayout);
        }
        View backSpace = inflateTab(tabHostLayout, R.drawable.sym_keyboard_delete_holo_dark);
        backSpace.setOnTouchListener(new RepeatTouchListener(1000, 50, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiconBackspaceClickedListener != null) {
                    mEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);
                }
            }
        }));
    }

    private View inflateTab(LinearLayout tabHostLayout, int iconResId) {
        ImageButton tabView = (ImageButton) LayoutInflater.from(mContext)
                .inflate(R.layout.emojicon_tab, tabHostLayout, false);

        tabHostLayout.addView(tabView);
        tabView.setImageDrawable(mContext.getResources().getDrawable(iconResId));
        return tabView;
    }

    private View inflateDivider(LinearLayout tabHostLayout) {
        return LayoutInflater.from(mContext).inflate(R.layout.emojicon_tab_divider, tabHostLayout);
    }

    private void initViewPager(View view, List<EmojiconGroup> displayedGroups) {
        mEmojisPager = (ViewPager) view.findViewById(R.id.emojis_pager);
        mEmojisPager.setOnPageChangeListener(this);
        mEmojisPager.setAdapter(new EmojisPagerAdapter(displayedGroups));
    }

    private List<EmojiconGroup> collectDisplayedGroups() {
        List<EmojiconGroup> groups = new ArrayList<>();
        groups.add(new RecentsEmojiconGroup(mRecentsManager, EmojiconGroupsLoader.KnownGroupNames.RECENT.getIconResId()));
        groups.addAll(EmojiconGroupsLoader.getInstance(mContext).getGroups());
        return groups;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        // ignore
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mEmojiTabs.length; i++) {
            mEmojiTabs[i].setSelected(i == position);
        }
        mRecentsManager.setRecentPage(position);
        EmojiAdapter adapter = ((EmojisPagerAdapter) mEmojisPager.getAdapter()).getPageAdapter(position);
        if (adapter instanceof Updatable) {
            ((Updatable) adapter).update();
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        // ignore
    }

    public void onEmojiClicked(Emojicon emojicon) {
        mRecentsManager.push(emojicon);
        if (mEmojiconClickedListener != null) {
            mEmojiconClickedListener.onEmojiconClicked(emojicon);
        }
    }

    private class EmojisPagerAdapter extends PagerAdapter {
        private final List<EmojiconGroup> mGroups;
        private final SparseArray<EmojiAdapter> mAdapters = new SparseArray<>();

        public EmojisPagerAdapter(List<EmojiconGroup> groups) {
            mGroups = groups;
        }

        public EmojiAdapter getPageAdapter(int pageIndex) {
            return mAdapters.get(pageIndex);
        }

        @Override
        public int getCount() {
            return mGroups.size();
        }

        @Override
        public GridView instantiateItem(ViewGroup container, final int viewPosition) {
            GridView gridView = (GridView) LayoutInflater.from(mContext).inflate(R.layout.emojicon_grid, container, false);
            container.addView(gridView);
            final EmojiAdapter adapter = mGroups.get(viewPosition).createAdapter();
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int emojiconPosition, long id) {
                    onEmojiClicked(adapter.getItem(emojiconPosition));
                }
            });
            mAdapters.put(viewPosition, adapter);
            return gridView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            container.removeView((View) view);
            mAdapters.remove(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object key) {
            return key == view;
        }
    }

    public interface OnEmojiconClickedListener {
        void onEmojiconClicked(Emojicon emojicon);
    }

    public interface OnEmojiconBackspaceClickedListener {
        void onEmojiconBackspaceClicked(View v);
    }

    public interface OnSoftKeyboardOpenCloseListener {
        void onKeyboardOpen(int keyBoardHeight);

        void onKeyboardClose();
    }

    private class GlobalLayoutListener implements OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            mRootView.getWindowVisibleDisplayFrame(r);

            int screenHeight = mRootView.getRootView().getHeight();
            int heightDifference = screenHeight - (r.bottom - r.top);
            int resourceId = mContext.getResources()
                    .getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                heightDifference -= mContext.getResources().getDimensionPixelSize(resourceId);
            }
            Log.d("Emojicons Popup", "Height difference is " + heightDifference);
            if (heightDifference > 100) {
                int oldHeight = getHeight();
                keyBoardHeight = heightDifference;
                setSize(LayoutParams.MATCH_PARENT, keyBoardHeight);
                if (!mIsOpened) {
                    if (mSoftKeyboardOpenCloseListener != null) {
                        mSoftKeyboardOpenCloseListener.onKeyboardOpen(keyBoardHeight);
                    }
                }
                mIsOpened = true;
                if (mWaitingForKbOpen) {
                    Log.d("Emojicons Popup", "Showing with height of " + keyBoardHeight);
                    showAtBottom();
                    mWaitingForKbOpen = false;
                } else if(isShowing() && oldHeight != keyBoardHeight) {
                    Log.d("Emojicons Popup", "Dismissing and showing again with height of " + keyBoardHeight);
                    dismiss();
                    showAtBottom();
                }
            } else {
                mIsOpened = false;
                if (mSoftKeyboardOpenCloseListener != null) {
                    mSoftKeyboardOpenCloseListener.onKeyboardClose();
                }
            }
        }
    }
}
