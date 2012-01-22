package demo.hello.client.place;

import java.util.Map;

/**
 * PlaceThree
 */
public class PlaceThree extends BasePlace {
    static final String PREFIX = "Three=";

    public PlaceThree(final String suffix) {
        super(PREFIX, suffix);
    }

    public PlaceThree() {
        super(PREFIX, "");
    }

    @Override
    public final PlaceThree newPlace(final Map<String, String> parameters) {
        return new PlaceThree(getSuffixFromParameters(parameters));
    }
}
