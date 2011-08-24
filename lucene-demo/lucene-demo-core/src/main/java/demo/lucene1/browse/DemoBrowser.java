package demo.lucene1.browse;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.lucene1.Commons;
import demo.lucene1.Constants;
import demo.lucene1.LuceneSupport;
import demo.lucene1.TermFreq;
import demo.lucene1.search.DocumentItem;
import demo.lucene1.search.Documents;

/**
 * DemoBrowser
 *
 * @author sch
 */
public class DemoBrowser implements Closeable {
    final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param args
     * @throws IOException 
     * @throws ParseException 
     */
    public static void main(final String[] args) throws IOException, ParseException {
        final String indexDir = Commons.getLuceneIndex();
        final DemoBrowser demoBrowser = new DemoBrowser(indexDir);

        final long start = System.currentTimeMillis();
        final Hierarchy hierarchy = demoBrowser.getParents();
        final long end = System.currentTimeMillis();
        System.err.printf("total hierarchy time (msec): %d%n", end - start);

        browse(hierarchy, demoBrowser);
        demoBrowser.close();
    }

    static void browse(final Hierarchy hierarchy, final DemoBrowser demoBrowser)
        throws ParseException, IOException {
        for (final Node node : hierarchy.getNodes()) {
            browse(node, 0, demoBrowser);
        }
    }

    static void browse(final Node node, final int i, final DemoBrowser demoBrowser)
        throws ParseException, IOException {
        System.err.printf("%s%s%n", StringUtils.repeat(" ", i), node);
        // search(node, i, demoBrowser);

        for (final Node child : node.children.values()) {
            browse(child, i + 2, demoBrowser);
        }
    }

    static void search(final Node node, final int i, final DemoBrowser demoBrowser)
        throws ParseException, IOException {
        final Documents documents = demoBrowser.getDocuments(node.getFullPath(), 1);

        for (final DocumentItem document : documents) {
            System.err.printf("%s%s%n", StringUtils.repeat(" ", i + 2),
                document.get(Constants.FIELD_FILE_NAME));
        }
        System.err.printf("%stotal hits: %d%n", StringUtils.repeat(" ", i),
            documents.getTotalHits());
    }

    // ///////////////////////////////////////////////////////

    final LuceneSupport luceneSupport;
    Map<String, Set<String>> facetList;

    /**
     * Creates a new instance of DemoBrowser.
     * 
     * @param luceneSupport the lucene support
     */
    public DemoBrowser(final LuceneSupport luceneSupport) {
        this.luceneSupport = luceneSupport;
    }

    /**
     * Creates a new instance of DemoBrowser, creating a lucene support 
     * for the specified {@code indexDir}.
     * 
     * @param indexDir the lucene index directory
     */
    DemoBrowser(final String indexDir) throws IOException {
        this(new LuceneSupport(indexDir, false));
    }

    /**
     * closes the lucene support.
     */
    public void close() throws IOException {
        luceneSupport.close();
    }

    public Hierarchy getParents() throws IOException {
        final List<TermFreq> termsDocFreq = luceneSupport.termsDocFreq(Constants.FIELD_FILE_PARENT,
            1);

        final Map<String, Node> roots = new LinkedHashMap<String, Node>();

        for (final TermFreq termFreq : termsDocFreq) {
            final Set<String> nodeset = getNodes(termFreq.term);

            int i = 0;
            final int last = nodeset.size();
            Node node = null;

            for (final String nodeitem : nodeset) {
                if (i++ == 0) {
                    if (!roots.containsKey(nodeitem)) {
                        node = new Node(nodeitem, null);
                        roots.put(nodeitem, node);

                    } else {
                        node = roots.get(nodeitem);
                    }

                } else if (node != null) { // null node should not happen
                    if (!node.childExists(nodeitem)) {
                        node = new Node(nodeitem, node);

                    } else {
                        node = node.child(nodeitem);
                    }
                }

                if (node != null && i == last) { // null node should not happen
                    node.setFreq(termFreq.frequency);
                }
            }
        }

        final Hierarchy hierarchy = new Hierarchy(roots.values());
        return hierarchy;
    }

    public Hierarchy getHierarchy() throws IOException {
        final List<TermFreq> termsDocFreq = luceneSupport.termsDocFreq(
            Constants.FIELD_FILE_HIERARCHY, 1);

        final Map<String, Node> roots = new LinkedHashMap<String, Node>();

        for (final TermFreq termFreq : termsDocFreq) {
            final Set<String> nodeset = getNodes(termFreq.term);

            int i = 0;
            Node node = null;

            for (final String nodeitem : nodeset) {
                if (i++ == 0) {
                    if (!roots.containsKey(nodeitem)) {
                        node = new Node(nodeitem, termFreq.frequency, null);
                        roots.put(nodeitem, node);

                    } else {
                        node = roots.get(nodeitem);
                    }

                } else if (node != null) { // null node should not happen
                    if (!node.childExists(nodeitem)) {
                        node = new Node(nodeitem, termFreq.frequency, node);

                    } else {
                        node = node.child(nodeitem);
                    }
                }
            }
        }

        final Hierarchy hierarchy = new Hierarchy(roots.values());
        return hierarchy;
    }

    public Documents getDocuments(final String parent, final int page) throws ParseException,
        IOException {
        // check if any changes have occurred to the index
        luceneSupport.reopen();

        // filter
        final Query query = new TermQuery(new Term(Constants.FIELD_FILE_PARENT, parent));
        final QueryWrapperFilter queryFilter = new QueryWrapperFilter(query);
        final CachingWrapperFilter cachingFilter = new CachingWrapperFilter(queryFilter);

        // query
        final MatchAllDocsQuery matchAllDocsQuery = new MatchAllDocsQuery();
        final FilteredQuery filteredQuery = new FilteredQuery(matchAllDocsQuery, cachingFilter);

        // execute search
        return executeSearch(luceneSupport.getIndexSearcher(), filteredQuery, page);
    }

    Documents executeSearch(final Searcher searcher, final Query query, int page)
        throws IOException {
        // search
        final int hitsPerPage = 10;
        final int maxHits = hitsPerPage * page;

        final TopFieldCollector collector = TopFieldCollector.create(Sort.RELEVANCE, maxHits, // maximum number of hits
            false, // fillFields - not needed, we want score and doc only
            true, // trackDocScores - need doc and score fields
            true, // trackMaxScore - related to trackDocScores
            false);

        logger.info(String.valueOf(query));
        searcher.search(query, collector);
        // search end

        // select only stored fields
        final FieldSelector fieldSelector = new MapFieldSelector(new String[] {
            Constants.FIELD_FILE_NAME, Constants.FIELD_FILE_PATH, Constants.FIELD_FILE_PARENT,
            Constants.FIELD_FILE_TYPE, Constants.FIELD_DEFAULT });

        // pagination
        final int totalHits = collector.getTotalHits();
        int pages = totalHits / hitsPerPage;
        if (totalHits % hitsPerPage != 0) {
            pages++;
        }
        if (page > pages) {
            page = 1;
        }

        final int startIndex = (page - 1) * hitsPerPage;
        final int endIndex = Math.min(totalHits, page * hitsPerPage);

        final TopDocs topDocs = collector.topDocs(startIndex, endIndex - startIndex);
        final ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        // list of matching documents
        final List<DocumentItem> documentList = new ArrayList<DocumentItem>(Math.min(hitsPerPage,
            endIndex - startIndex));

        for (int i = 0; i < scoreDocs.length; i++) {
            final ScoreDoc scoreDoc = scoreDocs[i];
            final Document document = searcher.doc(scoreDoc.doc, fieldSelector);
            final DocumentItem documentItem = new DocumentItem(document, scoreDoc.doc,
                scoreDoc.score, startIndex + i + 1);
            documentList.add(documentItem);
        }

        final Documents documents = new Documents(documentList, totalHits, hitsPerPage);
        return documents;
    }

    Set<String> getNodes(final String parent) {
        final Set<String> hierarchies = new LinkedHashSet<String>();
        final StringTokenizer st = new StringTokenizer(parent, "/", false);

        while (st.hasMoreElements()) {
            final String token = st.nextToken();
            hierarchies.add(token);
        }

        return hierarchies;
    }

}
