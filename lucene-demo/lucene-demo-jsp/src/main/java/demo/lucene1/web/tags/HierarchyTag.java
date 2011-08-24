package demo.lucene1.web.tags;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import demo.lucene1.Commons;
import demo.lucene1.Constants;
import demo.lucene1.browse.Hierarchy;
import demo.lucene1.browse.Node;

/**
 * HierarchyTag
 *
 * @author sch
 */
public class HierarchyTag extends TagSupport {
    private static final long serialVersionUID = -5748818919912335914L;
    Hierarchy hierarchy;
    String parent;

    /**
     * the hierarchy.
     *
     * @param hierarchy
     */
    public void setHierarchy(final Hierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Override
    public int doStartTag() throws JspTagException {
        try {
            final JspWriter out = pageContext.getOut();
            final ServletRequest request = pageContext.getRequest();
            parent = Commons.nvl(request.getParameter(Constants.PARENT));

            out.println("<div class=\"hierarchy\">");
            iterateul(hierarchy.getNodes(), out);
            out.println("</div>");

        } catch (final Exception e) {
            throw new JspTagException(e);
        }

        return SKIP_BODY;
    }

    void iterateul(final Collection<Node> children, final JspWriter out) throws IOException {
        out.println("<ul class=\"hierarchy\">");
        for (final Node child : children) {
            iterateli(child, out);
        }
        out.println("</ul>");
    }

    void iterateli(final Node node, final JspWriter out) throws IOException {
        final String fullPath = node.getFullPath();
        out.println("<li class=\"hierarchy\">");

        if (fullPath.equals(parent)) {
            out.println(String.format("<a class=\"selected\">%s</a>", node.getName()));

        } else {
            out.println(String.format("<a href=\"?%s=%s\">%s</a>", Constants.PARENT,
                Commons.encodeurl(fullPath), node.getName()));
        }
        out.println("</li>");

        if (node.getChildren().isEmpty()) return;
        iterateul(node.getChildren(), out);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.TagSupport#release()
     */
    @Override
    public void release() {
        hierarchy = null;
        parent = null;
    }
}
