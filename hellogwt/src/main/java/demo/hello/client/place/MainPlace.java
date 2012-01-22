package demo.hello.client.place;

import java.util.Map;

/**
 * MainPlace
 */
public class MainPlace extends BasePlace {
    static final String PREFIX = ":";

    public MainPlace(final String suffix) {
        super(PREFIX, suffix);
    }

    public MainPlace() {
        super(PREFIX, "");
    }

    @Override
    public final MainPlace newPlace(final Map<String, String> parameters) {
        return new MainPlace(getSuffixFromParameters(parameters));
    }
}
