package github.ankushsachdeva.emojicon;

import java.nio.charset.Charset;

/**
 * Created by aleksandr.naumov on 13.05.2015.
 */
public class Emojicon {
    private final String mStringRepresentation;

    public Emojicon(String stringRepresentation) {
        byte bytes[] = new byte[stringRepresentation.length() / 2];
        for (int i = 0; i < stringRepresentation.length(); i += 2) {
            try {
                bytes[i / 2] = (byte) Integer.parseInt(stringRepresentation.substring(i, i + 2), 16);
            } catch (NumberFormatException ex) {
                throw new RuntimeException(String.format("Cannot parse '%s' as emojicon code", stringRepresentation), ex);
            }
        }
        this.mStringRepresentation = new String(bytes, Charset.forName("Utf-8"));
    }

    @Override
    public String toString() {
        return mStringRepresentation;
    }

    public String getId() {
        String id = "";
        for (byte b : mStringRepresentation.getBytes()) {
            id += String.format("%x", b);
        }
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Emojicon emojicon = (Emojicon) o;
        return mStringRepresentation.equals(emojicon.mStringRepresentation);

    }

    @Override
    public int hashCode() {
        return mStringRepresentation.hashCode();
    }
}
