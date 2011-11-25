package com.hellogin.client.view;

import java.util.Arrays;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.hellogin.client.place.BasePlace;
import com.hellogin.client.place.PlaceOne;
import com.hellogin.client.place.PlaceTwo;

/**
 * HelloActivity
 */
public class HelloActivity extends AbstractActivity {
    final HelloView helloView;
    final BasePlace place;

    @AssistedInject
    public HelloActivity(final HelloView helloView, @Assisted final BasePlace place) {
        this.helloView = helloView;
        this.place = place;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        final String suffix = place.getSuffix();
        String newSuffix = shuffle(suffix);

        final boolean b = Random.nextBoolean();
        newSuffix = b ? reverse(newSuffix) : sort(newSuffix);
        if (newSuffix.equals(suffix)) {
            newSuffix = !b ? reverse(newSuffix) : sort(newSuffix);
        }

        final boolean c = Random.nextBoolean();
        final BasePlace place = c ? new PlaceOne(newSuffix) : new PlaceTwo(newSuffix);

        helloView.setSuffix(suffix);
        helloView.setToken(place.getToken());
        containerWidget.setWidget(helloView.asWidget());
    }

    String shuffle(final String s) {
        final char[] dest = s.toCharArray();
        final int length = dest.length;

        for (int i = 0; i < length; i++) {
            final int j = Random.nextInt(length);

            final char c = dest[i];
            dest[i] = dest[j];
            dest[j] = c;
        }
        return String.valueOf(dest);
    }

    String sort(final String s) {
        final char[] dest = s.toCharArray();
        Arrays.sort(dest);
        return String.valueOf(dest);
    }

    String reverse(final String s) {
        final char[] source = s.toCharArray();
        final int length = source.length;
        final char[] dest = new char[length];

        for (int i = 0; i < length; i++) {
            dest[i] = source[length - 1 - i];
        }
        return String.valueOf(dest);
    }
}
