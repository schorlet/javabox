package com.hellogin.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * GoodbyePlace
 */
public class GoodbyePlace extends BasePlace {
    private final String goodbyeName;

    public GoodbyePlace(final String token) {
        this.goodbyeName = token;
    }

    @Override
    public String getName() {
        return goodbyeName;
    }

    public static class Tokenizer implements PlaceTokenizer<GoodbyePlace> {
        @Override
        public String getToken(final GoodbyePlace place) {
            return place.getName();
        }

        @Override
        public GoodbyePlace getPlace(final String token) {
            return new GoodbyePlace(token);
        }
    }

}
