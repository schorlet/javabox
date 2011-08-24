package demo.lucene1.web.tags;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import demo.lucene1.Constants;
import demo.lucene1.search.DocumentItem;
import demo.lucene1.search.Documents;

/**
 * DocumentsTag
 *
 * @author sch
 */
public class DocumentsTag extends TagSupport {
    private static final long serialVersionUID = -2598301581201203468L;

    Documents documents;

    /**
     * the documents.
     *
     * @param documents
     */
    public void setDocuments(final Documents documents) {
        this.documents = documents;
    }

    @Override
    public int doStartTag() throws JspTagException {
        try {
            if (documents.getPages() == 0) return SKIP_BODY;

            final JspWriter out = pageContext.getOut();

            out.println("<div class=\"results\">");
            for (final DocumentItem document : documents) {
                out.println("<div class=\"result\">");
                out.println("  <div class=\"result_title\">");
                out.print(document.getIndex());
                out.print(": ");
                out.print(String.format("<a href=\"document?id=%s\">%s</a>", document.getDocId(),
                    document.get(Constants.FIELD_FILE_NAME)));
                out.println("  </div>");

                final String highlights = document.getHighlights();
                if (highlights != null && highlights.length() > 0) {
                    out.print("  <pre class=\"result_highlight\">");
                    out.print(highlights);
                    out.println("  </pre>");
                }

                out.print("  <div class=\"result_parent\">");
                out.print(document.get(Constants.FIELD_FILE_PARENT));
                out.print(" (");
                out.print(document.getScore());
                out.print(" %)");
                out.println("  </div>");

                out.println("</div>");
            }
            out.println("</div>");

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
        documents = null;
    }
}
