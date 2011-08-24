package demo.lucene1.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.lucene1.Constants;
import demo.lucene1.LuceneSupport;
import demo.lucene1.browse.DemoBrowser;
import demo.lucene1.browse.Hierarchy;
import demo.lucene1.browse.Node;
import demo.lucene1.search.Documents;

/**
 * Servlet proxy for {@link DemoBrowser}.
 */
public final class BrowseServlet extends HttpServlet {
    private static final long serialVersionUID = 1596275462844321361L;
    final Logger logger = LoggerFactory.getLogger(getClass());

    DemoBrowser demoBrowser;

    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        super.init();

        final ServletContext servletContext = getServletContext();

        final LuceneSupport luceneSupport = (LuceneSupport) servletContext
            .getAttribute("luceneSupport");

        demoBrowser = new DemoBrowser(luceneSupport);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {

        String parent = request.getParameter(Constants.PARENT);
        final String pageParameter = request.getParameter(Constants.PAGE);
        int page = 1;
        if (pageParameter != null) {
            page = Integer.valueOf(pageParameter);
        }

        final long start = System.currentTimeMillis();

        try {
            final ServletContext servletContext = getServletContext();
            servletContext.setAttribute("contextPath", servletContext.getContextPath());

            // build the query from search parameters
            // final Hierarchy hierarchy = demoBrowser.getHierarchy();
            final Hierarchy hierarchy = demoBrowser.getParents();
            servletContext.setAttribute("hierarchy", hierarchy);

            // get documents from the query
            if (parent == null) {
                final Node node = hierarchy.getFirst();
                parent = node.getFullPath();
            }

            final Documents documents = demoBrowser.getDocuments(parent, page);
            servletContext.setAttribute("documents", documents);

            // totaltime
            final long totaltime = System.currentTimeMillis() - start;
            servletContext.setAttribute("totaltime", totaltime + "ms");
            logger.info("total time (msec): {}", totaltime);

            // forward request to browse.jsp
            final RequestDispatcher requestDispatcher = servletContext
                .getRequestDispatcher("/WEB-INF/browse.jsp");
            requestDispatcher.forward(request, response);

        } catch (final Exception e) {
            logger.error("BrowseServlet error", e);
            throw new ServletException(e);
        }
    }

}
