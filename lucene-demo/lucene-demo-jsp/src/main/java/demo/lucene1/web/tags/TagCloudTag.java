package demo.lucene1.web.tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringEscapeUtils;

import demo.lucene1.Commons;
import demo.lucene1.TermFreq;

/**
 * TagCloudTag
 *
 * @author sch
 */
public class TagCloudTag extends TagSupport {
    private static final long serialVersionUID = -8964192951103994738L;

    List<TermFreq> tagCloud;
    String queryString;

    /**
     * @param tagCloud the tagCloud to set
     */
    public void setTagCloud(final List<TermFreq> tagCloud) {
        this.tagCloud = new ArrayList<TermFreq>(tagCloud.size());
        for (final TermFreq freq : tagCloud) {
            this.tagCloud.add(freq);
        }
    }

    public void setQueryString(final String queryString) {
        this.queryString = queryString;
    }

    @Override
    public int doStartTag() throws JspTagException {
        try {
            if (tagCloud.size() < 2) return SKIP_BODY;

            final Integer minFrequency = tagCloud.get(tagCloud.size() - 1).frequency;
            final Integer maxFrequency = tagCloud.get(2).frequency;
            final Integer interval = (maxFrequency - minFrequency) / 3;

            final JspWriter out = pageContext.getOut();
            out.println("<div class=\"tagcloud\">\n");

            Collections.shuffle(tagCloud);
            final Iterator<TermFreq> iterator = tagCloud.iterator();

            while (iterator.hasNext()) {
                final TermFreq freq = iterator.next();
                if (freq.term.length() < 3) {
                    continue;
                }

                if (queryString != null && queryString.length() > 0) {
                    out.print(String.format("<a href=\"?text=%s&%s\"",
                        Commons.encodeurl(freq.term), queryString));

                } else {
                    out.print(String.format("<a href=\"?text=%s\"", Commons.encodeurl(freq.term)));
                }

                if (freq.frequency > maxFrequency - interval) {
                    out.print(String.format(" class=\"largeTag\">%s</a>%n",
                        StringEscapeUtils.escapeHtml(freq.term)));

                } else if (freq.frequency >= maxFrequency - interval * 2) {
                    out.print(String.format(" class=\"mediumTag\">%s</a>%n",
                        StringEscapeUtils.escapeHtml(freq.term)));

                } else {
                    out.print(String.format(" class=\"smallTag\">%s</a>%n",
                        StringEscapeUtils.escapeHtml(freq.term)));
                }
            }

            out.println("</div>\n");

        } catch (final Exception e) {
            throw new JspTagException(e);
        }

        return SKIP_BODY;
    }

}
