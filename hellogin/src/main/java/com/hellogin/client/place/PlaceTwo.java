package com.hellogin.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * PlaceTwo
 */
public class PlaceTwo extends BasePlace {

    public PlaceTwo(final String token) {
        super(token);
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
