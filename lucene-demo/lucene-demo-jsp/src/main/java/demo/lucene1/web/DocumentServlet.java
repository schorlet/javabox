package demo.lucene1.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.lucene1.Constants;
import demo.lucene1.LuceneSupport;
import demo.lucene1.TermFreq;
import demo.lucene1.search.DemoSearcher;

/**
 * Document display servlet.
 */
public final class DocumentServlet extends HttpServlet {
    private static final long serialVersionUID = -5582435513948782989L;

    final Logger logger = LoggerFactory.getLogger(getClass());

    DemoSearcher demoSearcher;

    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        super.init();

        final ServletContext servletContext = getServletContext();

        final LuceneSupport luceneSupport = (LuceneSupport) servletContext
            .getAttribute("luceneSupport");

        demoSearcher = new DemoSearcher(luceneSupport);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {

        final long start = System.currentTimeMillis();
        try {
            final String id = request.getParameter("id");
            if (id == null || id.length() == 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            final Integer docId = Integer.valueOf(id);

            final ServletContext servletContext = getServletContext();
            servletContext.setAttribute("contextPath", servletContext.getContextPath());
            servletContext.setAttribute("queryString", "id=" + id);

            // get the document with the specified id
            final Document document = demoSearcher.get(docId);
            servletContext.setAttribute("document", document);

            // hightlight
            final String text = request.getParameter(Constants.FIELD_DEFAULT);
            if (text != null && text.length() > 2) {
                final String hightlight = demoSearcher.hightlight(docId, text);

                final Field field = document.getField(Constants.FIELD_DEFAULT);
                field.setValue(hightlight);

            } else {
                final Field field = document.getField(Constants.FIELD_DEFAULT);
                field.setValue(StringEscapeUtils.escapeHtml(field.stringValue()));
            }

            // tag cloud
            final List<TermFreq> tagCloud = demoSearcher.getTagCloud(docId, 40);
            servletContext.setAttribute("tagCloud", tagCloud);

            // totaltime
            final long totaltime = System.currentTimeMillis() - start;
            servletContext.setAttribute("totaltime", totaltime + "ms");
            logger.info("total time (msec): {}", totaltime);

            // forward request to document.jsp
            final RequestDispatcher requestDispatcher = servletContext
                .getRequestDispatcher("/WEB-INF/document.jsp");
            requestDispatcher.forward(request, response);

        } catch (final Exception e) {
            logger.error("DocumentServlet error", e);
            throw new ServletException(e);
        }
    }

}
