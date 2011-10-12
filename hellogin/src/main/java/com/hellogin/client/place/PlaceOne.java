package com.hellogin.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * PlaceOne
 */
public class PlaceOne extends BasePlace {

    public PlaceOne(final String token) {
        super(token);
    }

    public static class Tokenizer implements PlaceTokenizer<PlaceOne> {

        @Override
        public String getToken(final PlaceOne place) {
            return place.getName();
        }

        @Override
        public PlaceOne getPlace(final String token) {
            return new PlaceOne(token);
        }

    }

}
