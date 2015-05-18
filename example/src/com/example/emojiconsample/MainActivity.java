package com.example.emojiconsample;

import github.ankushsachdeva.emojicon.EmojiconPopupDelegate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity {

    private final EmojiconPopupDelegate mPopupDelegate = new EmojiconPopupDelegate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupPopupDelegate();
        if(savedInstanceState != null) {
            mPopupDelegate.restoreState(savedInstanceState);
        }
        setupSendButton();
    }

    private void setupPopupDelegate() {
        final ImageView emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        mPopupDelegate.attach(findViewById(R.id.root_view));
        mPopupDelegate.setInputEditText((EditText) findViewById(R.id.emojicon_edit_text));
        mPopupDelegate.setShowHideListener(new EmojiconPopupDelegate.PopupShownListener() {
            @Override
            public void onPopupShown() {
                emojiButton.setImageResource(R.drawable.ic_action_keyboard);
            }

            @Override
            public void onPopupHidden() {
                emojiButton.setImageResource(R.drawable.smiley);
            }
        });
        emojiButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupDelegate.toggle();
            }
        });
    }

    private void setupSendButton() {
        ListView listView = (ListView) findViewById(R.id.lv);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listview_row_layout);
        listView.setAdapter(adapter);
        findViewById(R.id.submit_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = ((EditText) findViewById(R.id.emojicon_edit_text)).getText().toString();
                ((EditText) findViewById(R.id.emojicon_edit_text)).getText().clear();
                adapter.add(newText);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
