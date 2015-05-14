package github.ankushsachdeva.emojicon;

/**
 * Created by aleksandr.naumov on 13.05.2015.
 */
public class Emojicon {
    private final String mStringRepresentation;

    public Emojicon(String mStringRepresentation) {
        this.mStringRepresentation = mStringRepresentation;
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
