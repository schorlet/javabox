package com.hellogin.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * GoodbyePlace
 */
public class PlaceTwo extends BasePlace {
    private final String goodbyeName;

    public PlaceTwo(final String token) {
        this.goodbyeName = token;
    }

    @Override
    public String getName() {
        return goodbyeName;
    }

    public static class Tokenizer implements PlaceTokenizer<PlaceTwo> {
        @Override
        public String getToken(final PlaceTwo place) {
            return place.getName();
        }

        @Override
        public PlaceTwo getPlace(final String token) {
            return new PlaceTwo(token);
        }
    }

}
