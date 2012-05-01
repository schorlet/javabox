package demo.gap.shared.domain.pojo;

import java.util.Date;

import demo.gap.shared.domain.service.Filter;

/**
 * JstlFunctions
 */
public class JstlFunctions {

    public static boolean isEmpty(final Gap gap) {
        return gap.isEmpty();
    }

    public static boolean equals(final Date a, final Date b) {
        return Filter.equals(a, b);
    }
}
