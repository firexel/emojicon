package github.ankushsachdeva.emojicon;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandr.naumov on 14.05.2015.
 */
class EmojiconGroupsLoader {
    private static EmojiconGroupsLoader sInstance;
    private final List<EmojiconGroup> mGroups;

    public static synchronized EmojiconGroupsLoader getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new EmojiconGroupsLoader(context);
        }
        return sInstance;
    }

    private EmojiconGroupsLoader(Context context) {
        try {
            mGroups = parseEmojiXml(context);
        } catch (IOException | XmlPullParserException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<EmojiconGroup> parseEmojiXml(Context context) throws IOException, XmlPullParserException {
        ArrayList<EmojiconGroup> groups = new ArrayList<>();
        XmlPullParser parser = context.getResources().getXml(R.xml.emoji);
        parser.next();
        String groupName = null;
        for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next()) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("group".equals(parser.getName())) {
                        groupName = parser.getAttributeValue(0);
                    }
                    break;
                case XmlPullParser.TEXT:
                    if (groupName != null) {
                        groups.add(EmojiconGroup.fromString(
                                unescapeString(parser.getText()),
                                getGroupNameIcon(groupName)
                        ));
                        groupName = null;
                    }
                    break;
            }
        }
        return groups;
    }

    private String unescapeString(String escapedString) {
        String[] strings = escapedString.split(" ");
        byte bytes[] = new byte[strings.length];
        for (int i = 0; i < strings.length; i++) {
            bytes[i] = (byte) Integer.parseInt(strings[i], 16);
        }
        return new String(bytes, Charset.forName("Utf-8"));
    }

    private int getGroupNameIcon(String groupName) {
        for (KnownGroupNames knownGroupName : KnownGroupNames.values()) {
            if (groupName.toLowerCase().equals(knownGroupName.name().toLowerCase())) {
                return knownGroupName.getIconResId();
            }
        }
        throw new IllegalArgumentException(String.format("Unknown group name '%s'", groupName));
    }

    public List<EmojiconGroup> getGroups() {
        return mGroups;
    }

    public enum KnownGroupNames {
        RECENT(R.drawable.ic_emoji_recent_light),
        SMILES(R.drawable.ic_emoji_people_light),
        NATURE(R.drawable.ic_emoji_nature_light),
        OBJECTS(R.drawable.ic_emoji_objects_light),
        TECH(R.drawable.ic_emoji_places_light),
        SYMBOLS(R.drawable.ic_emoji_symbols_light);

        private final int iconResId;

        KnownGroupNames(int iconResId) {
            this.iconResId = iconResId;
        }

        public int getIconResId() {
            return iconResId;
        }
    }
}
