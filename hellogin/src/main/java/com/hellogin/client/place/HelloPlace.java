package com.hellogin.client.place;

import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * HelloPlace
 */
public class HelloPlace extends BasePlace {
    private final String helloName;

    public HelloPlace(final String token) {
        this.helloName = token;
    }

    @Override
    public String getName() {
        return helloName;
    }

    public static class Tokenizer implements PlaceTokenizer<HelloPlace> {

        @Override
        public String getToken(final HelloPlace place) {
            return place.getName();
        }

        @Override
        public HelloPlace getPlace(final String token) {
            return new HelloPlace(token);
        }

    }

}
