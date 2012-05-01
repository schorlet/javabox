package demo.gap.shared.domain.pojo;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import demo.gap.shared.mem.MemDomainUtil;

/**
 * JstlObject
 */
public class JstlObject {
    private final Set<Gap> gaps;
    private final Set<Date> days;

    public JstlObject(final Set<Gap> gaps, final Integer from, final Integer to) {
        this.gaps = gaps;

        this.days = new TreeSet<Date>();
        if (from != null && to != null) {
            for (int i = from; i <= to; i++) {
                final Date day = MemDomainUtil.selectDay(i);
                if (0 < day.getDay() && day.getDay() < 6) {
                    days.add(day);
                }
            }
        }
    }

    public Set<Gap> getGaps() {
        return gaps;
    }

    public Set<Date> getDays() {
        return days;
    }
}
