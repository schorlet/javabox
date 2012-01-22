package demo.hello.client.place;

import java.util.Map;

/**
 * PlaceOne
 */
public class PlaceOne extends BasePlace {
    static final String PREFIX = "One:";

    public PlaceOne(final String suffix) {
        super(PREFIX, suffix);
    }

    public PlaceOne() {
        super(PREFIX, "");
    }

    @Override
    public final PlaceOne newPlace(final Map<String, String> parameters) {
        return new PlaceOne(getSuffixFromParameters(parameters));
    }
}
