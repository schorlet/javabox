package demo.hello.client.place;

import java.util.logging.Level;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;

import demo.hello.client.Logger;

/**
 * Maps {@link Place}s to/from tokens, used to configure a
 * {@link PlaceHistoryHandler}.
 */
public class ProjectPlaceHistoryMapper implements PlaceHistoryMapper {
    ProjectPlaceHistoryMapper() {}

    @Override
    public Place getPlace(final String token) {
        Logger.logp(Level.INFO, "ProjectPlaceHistoryMapper", "getPlace", token);
        BasePlace place = null;

        if (token == null || token.isEmpty() || token.startsWith(MainPlace.PREFIX)) {
            place = new MainPlace(getSuffix(token, MainPlace.PREFIX));

        } else if (token.startsWith(PlaceOne.PREFIX)) {
            place = new PlaceOne(getSuffix(token, PlaceOne.PREFIX));

        } else if (token.startsWith(PlaceTwo.PREFIX)) {
            place = new PlaceTwo(getSuffix(token, PlaceTwo.PREFIX));

        } else if (token.startsWith(PlaceThree.PREFIX)) {
            place = new PlaceThree(getSuffix(token, PlaceThree.PREFIX));
        }

        return place;
    }

    String getSuffix(final String token, final String prefix) {
        return token.substring(prefix.length());
    }

    @Override
    public String getToken(final Place place) {
        return ((BasePlace) place).getToken();
    }
}
