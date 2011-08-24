package demo.lucene1.web.tags;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringEscapeUtils;

import demo.lucene1.Constants;
import demo.lucene1.search.FacetHitCount;
import demo.lucene1.search.NameValueUrl;

/**
 * FacetFilterTag
 *
 * @author sch
 */
public class FacetFilterTag extends TagSupport {
    private static final long serialVersionUID = -2258078131187256794L;

    List<FacetHitCount> facets;

    /**
     * @param facets the facets to set
     */
    public void setFacets(final List<FacetHitCount> facets) {
        this.facets = facets;
    }

    @Override
    public int doStartTag() throws JspTagException {
        try {
            final ServletContext servletContext = pageContext.getServletContext();
            final String contextPath = servletContext.getContextPath();

            final StringBuilder sb = new StringBuilder();
            sb.append("<div id=\"facets_pane\">\n");
            sb.append("<p><b><a href=\"?\">Reset filters</a></b></p><br/>\n");

            for (final FacetHitCount facet : facets) {
                final String seeAllQueryString = facet.getSeeAllQueryString();
                final String facetName = facet.getName();

                if (seeAllQueryString != null) {
                    sb.append(String.format(
                        "<b><a href=\"%s/search?%s\">See all %s</a></b><br/>%n", contextPath,
                        seeAllQueryString, StringEscapeUtils.escapeHtml(facetName)));
                    sb.append(facet.getSelectedValues());
                    sb.append("<br/>\n");

                } else {
                    final List<NameValueUrl> hitCounts = facet.getHitCounts();
                    // facet filters count must be 2 at least
                    if (hitCounts == null || hitCounts.size() == 1) {
                        continue;
                    }

                    // do not display too restrictive facet filters
                    final int value = hitCounts.get(0).getValue();
                    if (value == 1) {
                        continue;
                    }

                    // do not display large facet filters
                    if (hitCounts.size() > 30) {
                        continue;
                    }

                    sb.append(String.format("<b>Filter by %s</b>",
                        StringEscapeUtils.escapeHtml(facetName)));
                    if (Constants.FIELD_FILE_HIERARCHY.equals(facetName)) {
                        sb.append(String
                            .format(
                                " <a href=\"%s/browse\" target=\"blank\"><img src=\"external.png\"/></a>",
                                contextPath));
                    }

                    sb.append("<ul>");
                    for (final NameValueUrl hit : hitCounts) {
                        sb.append(String.format("<li><a href=\"%s/search?%s\">%s (%s)</a></li>%n",
                            contextPath, hit.getUrl(), StringEscapeUtils.escapeHtml(hit.getName()),
                            hit.getValue()));
                    }
                    sb.append("</ul>\n");
                }
            }
            sb.append("</div>\n");

            final JspWriter out = pageContext.getOut();
            out.print(sb.toString());

        } catch (final Exception e) {
            throw new JspTagException(e);
        }

        return SKIP_BODY;
    }
}
