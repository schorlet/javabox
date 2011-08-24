package demo.lucene1.web.tags;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringEscapeUtils;

import demo.lucene1.Commons;
import demo.lucene1.Constants;

/**
 * SearchTag
 *
 * @author sch
 */
public class SearchTag extends TagSupport {

    private static final long serialVersionUID = 8624238358861453068L;

    final static String SERVLET_PATH = "/search".intern();
    String servletPath = SERVLET_PATH;

    /**
     * @param servletPath the servletPath
     */
    public void setServletPath(final String servletPath) {
        this.servletPath = servletPath.startsWith("/") ? servletPath : "/" + servletPath;
    }

    @Override
    public int doStartTag() throws JspTagException {
        try {
            final ServletContext servletContext = pageContext.getServletContext();
            final String contextPath = servletContext.getContextPath();

            final ServletRequest request = pageContext.getRequest();

            @SuppressWarnings("unchecked")
            final Map<String, String[]> parameters = request.getParameterMap();

            final StringBuilder sb = new StringBuilder();
            sb.append("<form method=\"get\" action=\"").append(contextPath).append(servletPath)
                .append("\">");
            sb.append("  <input type=\"text\" size=\"40\" name=\"").append(Constants.FIELD_DEFAULT);
            sb.append("\" id=\"").append(Constants.FIELD_DEFAULT);
            final String escapeHtml = StringEscapeUtils.escapeHtml(Commons.nvl(request
                .getParameter("text")));
            sb.append("\" value=\"").append(escapeHtml).append("\"/>\n");

            for (final String parameter : parameters.keySet()) {
                if (parameter.equals(Constants.FIELD_DEFAULT)) {
                    continue;
                }

                final String[] values = parameters.get(parameter);

                for (final String value : values) {
                    sb.append("  <input type=\"hidden\"").append(" name=\"")
                        .append(StringEscapeUtils.escapeHtml(parameter));
                    sb.append("\" value=\"").append(value).append("\"/>\n");
                }
            }

            sb.append("  <input type=\"submit\" value=\"search\"/>\n");
            if (!SERVLET_PATH.equals(servletPath)) {
                sb.append("  <input type=\"submit\" value=\"search all\"");
                sb.append(" onclick=\"this.form.action='" + contextPath + SERVLET_PATH
                    + "';\" value=\"search all\"/>\n");
            }
            sb.append("</form>\n");

            final JspWriter out = pageContext.getOut();
            out.println(sb.toString());

        } catch (final Exception e) {
            throw new JspTagException(e);
        }

        return SKIP_BODY;
    }
}
