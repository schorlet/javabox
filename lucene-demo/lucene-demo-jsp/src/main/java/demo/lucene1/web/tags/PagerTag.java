package demo.lucene1.web.tags;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import demo.lucene1.Commons;

public class PagerTag extends TagSupport {
    private static final long serialVersionUID = -1802971089228782720L;

    @Override
    public int doStartTag() throws JspTagException {
        try {
            pageContext.getServletContext();
            final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            final String queryString = Commons.nvl(request.getQueryString());
            final int page = getPage(queryString);
            final String queryString2 = removePage(queryString);

            final JspWriter jspWriter = pageContext.getOut();
            final String fullPath = String.format("?%s", queryString2);
            paginate(jspWriter, fullPath, page);

        } catch (final Exception e) {
            throw new JspTagException(e);
        }

        return SKIP_BODY;
    }

    /**
     * @param pages the pages to set
     */
    public void setPages(final int pages) {
        this.pages = pages;
    }

    int pages;

    void paginate(final Writer writer, final String fullPath, int page) throws IOException {
        if (pages < 2) return;

        if (page > pages) {
            page = 1;
        }

        int start = (page - 1) / 10 * 10;
        if (start == 0) {
            start++;
        }

        int end = Math.min(start + 10, pages);
        if (end != pages && end % 10 == 0) {
            end++;
        }
        end++;

        writer.write("<div class=\"results_pager\">");

        for (; start < end; start++) {
            if (start == page) {
                writer.write(String.format("<a class=\"actualpage\">%d</a>%n", start));

            } else if (start == 1) {
                final String link = String.format("<a href=\"%1$s\">%2$d</a>%n", fullPath, start);
                writer.write(link);

            } else {
                final String link = String.format("<a href=\"%1$spage=%2$d\">%2$d</a>%n", fullPath,
                    start);
                writer.write(link);
            }
        }

        writer.write("</div>");
    }

    int getPage(final String queryString) {
        final int beginIndex = queryString.indexOf("page=");
        if (beginIndex > -1) {
            int endIndex = queryString.indexOf('&', beginIndex);
            if (endIndex == -1) {
                endIndex = queryString.length();
            }
            final String page = queryString.substring(beginIndex + 5, endIndex);
            final Integer pageNum = Integer.valueOf(page);
            return pageNum;
        }
        return 1;
    }

    String removePage(final String queryString) {
        String queryString2 = queryString;

        final int beginIndex = queryString.indexOf("page=");
        if (beginIndex > -1) {
            int endIndex = queryString.indexOf('&', beginIndex);
            if (endIndex == -1) {
                endIndex = queryString.length();
            }

            final StringBuilder sb = new StringBuilder();
            sb.append(queryString.substring(0, beginIndex));
            sb.append(queryString.substring(endIndex));

            queryString2 = sb.toString();
        }

        if (queryString2.length() > 0 && queryString2.charAt(queryString2.length() - 1) != '&') {
            queryString2 = queryString2 + '&';
        }

        return queryString2;
    }

}
