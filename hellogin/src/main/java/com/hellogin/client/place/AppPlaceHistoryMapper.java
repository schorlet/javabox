package com.hellogin.client.place;

import java.util.logging.Level;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.hellogin.client.Logger;

/**
 * PlaceHistoryMapper interface is used to attach all places which the
 * PlaceHistoryHandler should be aware of.
 */
public class AppPlaceHistoryMapper implements PlaceHistoryMapper {

    AppPlaceHistoryMapper() {}

    @Override
    public Place getPlace(final String token) {
        Logger.logp(Level.INFO, "AppPlaceHistoryMapper", "getPlace", token);

        if (token.startsWith(PlaceOne.PREFIX)) {
            final PlaceOne placeOne = new PlaceOne(token.substring(PlaceOne.PREFIX.length()));
            return placeOne;

        } else if (token.startsWith(PlaceTwo.PREFIX)) {
            final PlaceTwo placeTwo = new PlaceTwo(token.substring(PlaceTwo.PREFIX.length()));
            return placeTwo;
        }

        return null;
    }

    @Override
    public String getToken(final Place place) {
        if (place instanceof BasePlace) return ((BasePlace) place).getToken();
        return null;
    }
}
