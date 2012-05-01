package demo.gap.shared.domain.pojo;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * DateAdapter
 */
public class DateAdapter extends XmlAdapter<String, Date> {

    @Override
    public Date unmarshal(final String date) throws Exception {
        return DateUtils.parseDate(date,
            new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
    }

    @Override
    public String marshal(final Date date) throws Exception {
        return DateFormatUtils.ISO_DATE_FORMAT.format(date);
    }

}
