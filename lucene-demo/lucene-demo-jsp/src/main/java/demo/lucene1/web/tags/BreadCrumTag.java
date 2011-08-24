package demo.lucene1.web.tags;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringEscapeUtils;

import demo.lucene1.Constants;

/**
 * BreadCrumTag
 *
 * @author sch
 */
public class BreadCrumTag extends TagSupport {
    private static final long serialVersionUID = 167371067325906469L;

    @Override
    public int doStartTag() throws JspTagException {
        try {
            final JspWriter out = pageContext.getOut();
            final ServletRequest request = pageContext.getRequest();

            @SuppressWarnings("unchecked")
            final Map<String, String[]> parameters = request.getParameterMap();

            out.println("<div class=\"breadcrum\">\n");
            for (final String parameter : parameters.keySet()) {
                // do not display default field
                if (dontDisplayParam(parameter)) {
                    continue;
                }

                final String[] values = parameters.get(parameter);

                for (final String value : values) {
                    out.print(parameter);
                    out.print(": ");
                    out.print(StringEscapeUtils.escapeHtml(value));
                    out.println("<br/>\n");
                }
            }
            out.println("</div>\n");

        } catch (final Exception e) {
            throw new JspTagException(e);
        }

        return SKIP_BODY;
    }

    boolean dontDisplayParam(final String parameter) {
        if (parameter.equals(Constants.FIELD_DEFAULT) || parameter.equals(Constants.PAGE)
            || parameter.equals(Constants.ID)) return true;
        return false;
    }
}
