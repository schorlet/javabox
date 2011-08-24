package demo.lucene1.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.lucene1.LuceneSupport;
import demo.lucene1.TermFreq;
import demo.lucene1.search.DemoSearcher;
import demo.lucene1.search.Documents;
import demo.lucene1.search.FacetHitCount;

/**
 * Servlet proxy for {@link DemoSearcher}.
 */
public final class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1694653438755124564L;
    final Logger logger = LoggerFactory.getLogger(getClass());

    DemoSearcher demoSearcher;
    List<TermFreq> tagCloud;

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

        if (tagCloud == null) {
            tagCloud = demoSearcher.getTagCloud(40);
        }

        @SuppressWarnings("unchecked")
        final Map<String, String[]> parameters = request.getParameterMap();
        final long start = System.currentTimeMillis();

        try {
            final ServletContext servletContext = getServletContext();
            servletContext.setAttribute("contextPath", servletContext.getContextPath());

            // build the query from search parameters
            final Query query = demoSearcher.buildQuery(parameters);

            // get documents from the query
            final Documents documents = demoSearcher.getDocuments(query, parameters);
            servletContext.setAttribute("documents", documents);

            // get facets from the query
            final List<FacetHitCount> facets = demoSearcher.getFacets(query, parameters);
            servletContext.setAttribute("facets", facets);

            // tag cloud
            servletContext.setAttribute("tagCloud", tagCloud);

            // totaltime
            final long totaltime = System.currentTimeMillis() - start;
            servletContext.setAttribute("totaltime", totaltime + "ms");
            logger.info("total time (msec): {}", totaltime);

            // forward request to search.jsp
            final RequestDispatcher requestDispatcher = servletContext
                .getRequestDispatcher("/WEB-INF/search.jsp");
            requestDispatcher.forward(request, response);

        } catch (final Exception e) {
            logger.error("SearchServlet error", e);
            throw new ServletException(e);
        }
    }

}
