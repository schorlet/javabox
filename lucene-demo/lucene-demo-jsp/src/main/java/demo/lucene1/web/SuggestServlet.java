package demo.lucene1.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.lucene1.Commons;
import demo.lucene1.LuceneSuggest;
import demo.lucene1.LuceneSupport;
import demo.lucene1.search.DemoSearcher;

/**
 * Servlet proxy for {@link DemoSearcher}.
 */
public final class SuggestServlet extends HttpServlet {
    private static final long serialVersionUID = -340278956060390495L;
    final Logger logger = LoggerFactory.getLogger(getClass());

    LuceneSuggest luceneSuggest;
    Future<Void> createSuggestIndex;

    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        super.init();

        final ServletContext servletContext = getServletContext();

        try {
            final LuceneSupport luceneSupport = (LuceneSupport) servletContext
                .getAttribute("luceneSupport");

            final String indexDir = Commons.getLuceneSuggest();
            luceneSuggest = new LuceneSuggest(indexDir);

            if (luceneSuggest.numDocs() > 0) return;

            final Callable<Void> callable = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    luceneSuggest.createIndex(luceneSupport.getIndexReader());
                    return null;
                }
            };

            final ExecutorService executorService = Executors.newSingleThreadExecutor();
            createSuggestIndex = executorService.submit(callable);
            executorService.shutdown();
        } catch (final IOException e) {
            throw new ServletException(e);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
        try {
            luceneSuggest.close();
        } catch (final IOException e) {
            logger.error("error closing luceneSuggest", e);
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {

        final long start = System.currentTimeMillis();

        try {
            if (createSuggestIndex != null) {
                createSuggestIndex.get();
                createSuggestIndex = null;
            }

            final String query = request.getParameter("query");
            final List<String> suggestions = luceneSuggest.makeSuggestions(query);
            final StringBuilder sb = new StringBuilder();

            sb.append('[');
            for (int i = 0; i < suggestions.size(); i++) {
                final String suggestion = suggestions.get(i);
                final String escapedSuggestion = StringEscapeUtils.escapeJavaScript(suggestion);
                sb.append('\'').append(escapedSuggestion).append('\'');
                if (i < suggestions.size() - 1) {
                    sb.append(',');
                }
            }
            sb.append(']');
            System.err.println(sb.toString());

            response.setContentType("application/json");
            final PrintWriter writer = response.getWriter();
            final String escapedQuery = StringEscapeUtils.escapeJavaScript(query);
            writer.format("{query:'%s', suggestions:%s, data:[]}", escapedQuery, sb.toString());
            writer.flush();

            final long totaltime = System.currentTimeMillis() - start;
            logger.info("total time (msec): {}", totaltime);

        } catch (final Exception e) {
            logger.error("SuggestServlet error", e);
            throw new ServletException(e);
        }
    }
}
