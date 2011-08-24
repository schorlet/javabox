package demo.lucene1.web.tags;

import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * DocumentDisplayTag
 *
 * @author sch
 */
public class DocumentDisplayTag extends TagSupport {
    private static final long serialVersionUID = 167371067325906469L;

    Document document = null;

    /**
     * the document.
     *
     * @param document
     */
    public void setDocument(final Document document) {
        this.document = document;
    }

    @Override
    public int doStartTag() throws JspTagException {
        try {
            @SuppressWarnings("unchecked")
            final List<Field> fields = document.getFields();

            final StringBuilder stored = new StringBuilder();
            stored.append("<div class=\"document\"><table class=\"document_table\"><tbody>");

            for (final Field field : fields) {
                if (field.isStored() && !field.isBinary()) {
                    stored.append("<tr><td class=\"document_field\">");
                    stored.append(field.name());
                    stored.append(": </td>");
                    stored.append("<td class=\"document_value\">");
                    if (field.isTokenized()) {
                        stored.append("<pre>");
                    }
                    stored.append(field.stringValue());
                    if (field.isTokenized()) {
                        stored.append("</pre>");
                    }
                    stored.append("</td></tr>");
                }
            }
            stored.append("</tbody></table></div>");

            final JspWriter out = pageContext.getOut();
            out.print(stored.toString());

        } catch (final Exception e) {
            throw new JspTagException(e);
        }

        return SKIP_BODY;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.TagSupport#release()
     */
    @Override
    public void release() {
        document = null;
    }

}
