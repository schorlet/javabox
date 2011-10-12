package com.hellogin.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * HelloPlace
 */
public class PlaceOne extends BasePlace {
    private final String helloName;

    public PlaceOne(final String token) {
        this.helloName = token;
    }

    @Override
    public String getName() {
        return helloName;
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
