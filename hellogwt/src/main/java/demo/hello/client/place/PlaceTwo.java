package demo.hello.client.place;

import java.util.Map;

/**
 * PlaceTwo
 */
public class PlaceTwo extends BasePlace {
    static final String PREFIX = "Two//";

    public PlaceTwo(final String suffix) {
        super(PREFIX, suffix);
    }

    public PlaceTwo() {
        super(PREFIX, "");
    }

    @Override
    public final PlaceTwo newPlace(final Map<String, String> parameters) {
        return new PlaceTwo(getSuffixFromParameters(parameters));
    }
}
