package demo.lucene1.search;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCacheTermsFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.util.OpenBitSet;
import org.apache.lucene.util.OpenBitSetDISI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.lucene1.Commons;
import demo.lucene1.Constants;
import demo.lucene1.LuceneSupport;
import demo.lucene1.TermFreq;
import demo.lucene1.jdbc.FacetDAO;

/**
 * DemoSearcher
 *
 * @author sch
 */
public class DemoSearcher implements Closeable {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(final String[] args) throws ParseException, IOException {
        // search parameters
        final Map<String, String[]> parameters = new HashMap<String, String[]>();
        // parameters.put(Constants.FIELD_FILE_HIERARCHY, new String[] { "/home/sch" });
        parameters.put(Constants.FIELD_DEFAULT, new String[] { "java" });

        final String indexDir = Commons.getLuceneIndex();
        final DemoSearcher demoSearcher = new DemoSearcher(indexDir);

        // build the query from search parameters
        final Query query = demoSearcher.buildQuery(parameters);

        // get documents from the query
        long start = System.currentTimeMillis();
        final Documents documents = demoSearcher.getDocuments(query, parameters);
        long end = System.currentTimeMillis();
        System.err.printf("total search time (msec): %d%n", end - start);
        for (final DocumentItem document : documents) {
            System.err.println(document.get(Constants.FIELD_FILE_PATH));
        }
        System.err.printf("total hits: %d%n", documents.getTotalHits());

        // get facets from the query
        start = System.currentTimeMillis();
        final List<FacetHitCount> facets = demoSearcher.getFacets(query, parameters);
        end = System.currentTimeMillis();
        System.err.printf("total facets time (msec): %d%n", end - start);
        for (final FacetHitCount facet : facets) {
            System.err.println(facet.getName());
            final String allQueryString = facet.getSeeAllQueryString();

            if (allQueryString != null) {
                System.err.printf("  %s%n", allQueryString);
            } else {
                final List<NameValueUrl> hitCounts = facet.getHitCounts();
                for (final NameValueUrl hitCount : hitCounts) {
                    System.err.printf("  %s%n", hitCount.toString());
                }
            }
        }

        // get the most frequent terms
        final List<TermFreq> tagCloud = demoSearcher.getTagCloud(20);
        for (final TermFreq termFreq : tagCloud) {
            System.err.println(termFreq);
        }
        demoSearcher.close();
    }

    final LuceneSupport luceneSupport;
    Map<String, Set<String>> facetList;

    /**
     * Creates a new instance of DemoSearcher.
     * 
     * @param luceneSupport the lucene support
     */
    public DemoSearcher(final LuceneSupport luceneSupport) {
        this.luceneSupport = luceneSupport;
        facetList = facetList();
    }

    /**
     * Creates a new instance of DemoSearcher, creating a lucene support 
     * for the specified {@code indexDir}.
     * 
     * @param indexDir the lucene index directory
     */
    DemoSearcher(final String indexDir) throws IOException {
        this(new LuceneSupport(indexDir, false));
    }

    /**
     * closes the lucene support.
     */
    public void close() throws IOException {
        luceneSupport.close();
    }

    /**
     * Select facet values indexed by facet names.
     * 
     * @return the facet list
     */
    Map<String, Set<String>> facetList() {
        final FacetDAO facetDAO = new FacetDAO();
        final Map<String, Set<String>> facetList = facetDAO.getAllFacets();
        return facetList;
    }

    /**
     * Builds a lucene Query from the specified {@code parameters}.
     * 
     * @param parameters
     * @return
     * @throws ParseException
     */
    public Query buildQuery(final Map<String, String[]> parameters) throws ParseException {
        // builds the query from search parameters
        final Query query = getQueryFromParameterMap(parameters);
        final Filter filter = getFilterFromParameterMap(parameters);

        if (filter != null) {
            final FilteredQuery filteredQuery = new FilteredQuery(query, filter);
            return filteredQuery;
        }

        return query;
    }

    /**
     * Searches for results satisfying the specified {@code parameters}.
     *  
     * @param parameters the query string
     * @throws ParseException
     * @throws IOException
     */
    public Documents getDocuments(final Query query, final Map<String, String[]> parameters)
        throws ParseException, IOException {
        // check if any changes have occurred to the index
        final boolean reopen = luceneSupport.reopen();
        if (reopen) {
            facetList = facetList();
        }

        // execute search
        return executeSearch(luceneSupport.getIndexSearcher(), query, parameters);
    }

    public List<FacetHitCount> getFacets(final Query query, final Map<String, String[]> parameters)
        throws IOException {

        return getFacets(luceneSupport.getIndexSearcher(), query, parameters);
    }

    /**
     * returns a list of most frequent terms in the index.
     * 
     * @param maxTerms the maximum number of terms
     * @return a list of {@link TermFreq}
     */
    public List<TermFreq> getTagCloud(final int maxTerms) throws IOException {
        return luceneSupport.termsTermFreqMaxTerms(maxTerms);
    }

    /**
     * returns a list of most frequents terms in the index.
     * 
     * @param docId the document id
     * @param maxTerms the maximum number of terms
     * @return a list of {@link TermFreq}
     */
    public List<TermFreq> getTagCloud(final int docId, final int maxTerms) throws IOException {
        return luceneSupport.termsTermFreqMaxTerms(docId, maxTerms);
    }

    /**
     * @param id the doc id
     * @return the specified document
     * @throws IOException 
     * @throws CorruptIndexException 
     */
    public Document get(final int docId) throws CorruptIndexException, IOException {
        final IndexReader indexReader = luceneSupport.getIndexReader();
        final Document document = indexReader.document(docId);
        return document;
    }

    public String hightlight(final int docId, final String text) throws IOException, ParseException {
        // analyze the specified text
        final Query query = getQueryFromParameterMap(Constants.FIELD_DEFAULT, text);
        final Highlighting highlighting = new Highlighting(query, luceneSupport);

        final IndexReader indexReader = luceneSupport.getIndexReader();
        final Document document = indexReader.document(docId);
        final String content = document.get(Constants.FIELD_DEFAULT);

        // process highlighting
        final String highlights = highlighting.highlights(docId, Constants.FIELD_DEFAULT, content,
            10);
        return Commons.normalize(highlights);
    }

    Query getQueryFromParameterMap(final Map<String, String[]> parameters) throws ParseException {
        if (parameters != null && parameters.containsKey(Constants.FIELD_DEFAULT)) {
            final String[] terms = parameters.get(Constants.FIELD_DEFAULT);
            final Query query = getQueryFromParameterMap(Constants.FIELD_DEFAULT, terms);
            return query;
        }

        return new MatchAllDocsQuery();
    }

    /**
     * Builds a Filter with the specified {@code parameters}. May return null if empty or
     * if parameters are not facets names.
     */
    Filter getFilterFromParameterMap(final Map<String, String[]> parameters) throws ParseException {
        if (parameters == null || parameters.size() == 0) return null;

        final BooleanQuery query = new BooleanQuery();

        for (final String parameter : parameters.keySet()) {
            // do not process params which are not facets, unless "text"
            final boolean facetExists = facetList.containsKey(parameter);
            if (!facetExists) {
                continue;
            }

            final String[] values = parameters.get(parameter);
            if (values == null || values.length == 0) {
                continue;
            }

            final Query query2 = getQueryFromParameterMap(parameter, values);
            query.add(query2, Occur.MUST);
        }

        // return null if zero params
        if (query.getClauses().length == 0) return null;

        final QueryWrapperFilter queryFilter = new QueryWrapperFilter(query);
        final CachingWrapperFilter cachingFilter = new CachingWrapperFilter(queryFilter);
        return cachingFilter;
    }

    Query getQueryFromParameterMap(final String parameter, final String[] values)
        throws ParseException {

        if (values.length == 1) {
            final Query query = getQueryFromParameterMap(parameter, values[0]);
            return query;

        } else {
            final BooleanQuery query = new BooleanQuery();

            for (final String value : values) {
                final Query queryFromParameterMap = getQueryFromParameterMap(parameter, value);
                query.add(queryFromParameterMap, Occur.MUST);
            }

            return query;
        }
    }

    Query getQueryFromParameterMap(final String parameter, final String value)
        throws ParseException {

        if (StringUtils.isEmpty(value)) return new MatchAllDocsQuery();

        final Query query = luceneSupport.getQueryParser().parse(
            parameter + ":" + QueryParser.escape(value));
        return query;
    }

    /**
    * Searches for results for the specified {@code query}.
    * 
    * @param searcher the searcher
    * @param query the query
    * @throws IOException 
    */
    Documents executeSearch(final Searcher searcher, final Query query,
        final Map<String, String[]> parameters) throws IOException {

        // hightlighter
        Highlighting highlighting = null;
        if (parameters.containsKey(Constants.FIELD_DEFAULT)) {
            highlighting = new Highlighting(query, luceneSupport);
        }

        // search
        int page = 1;
        final int hitsPerPage = 10;

        final String[] pageParam = parameters.get(Constants.PAGE);
        if (pageParam != null && pageParam.length == 1) {
            page = Integer.valueOf(pageParam[0]);
        }

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

        /*
         * LuceneFAQ: How do I implement paging ?
         * Just re-execute the search and ignore the hits you don't want to show. 
         * As people usually look only at the first results this approach is usually fast enough.
         */
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

            // hightlighter
            if (highlighting != null) {
                final String content = document.get(Constants.FIELD_DEFAULT);
                final String highlights = highlighting.highlights(scoreDoc.doc,
                    Constants.FIELD_DEFAULT, content);
                final String normalize = Commons.normalize(highlights);
                documentItem.setHighlights(normalize);
            }
        }

        final Documents documents = new Documents(documentList, totalHits, hitsPerPage);
        return documents;
    }

    List<FacetHitCount> getFacets(final Searcher searcher, final Query query,
        final Map<String, String[]> parameters) throws IOException {

        // return filterCount(searcher, query, parameters);
        // bitset method should be faster
        return bitsetCount(searcher, query, parameters);
    }

    List<FacetHitCount> filterCount(final Searcher searcher, final Query query,
        final Map<String, String[]> parameters) throws IOException {

        final List<FacetHitCount> facetCounts = new ArrayList<FacetHitCount>();

        final Set<String> facetNames = facetList.keySet();
        for (final String facetName : facetNames) {

            final FacetHitCount facet = new FacetHitCount();
            facet.setName(facetName);

            if (parameters.get(facetName) != null) {
                final String resetQueryString = resetQueryString(facetName, parameters);
                facet.setAllQueryString(resetQueryString);
                facet.setSelectedValuesArray(parameters.get(facetName));
                facetCounts.add(facet);

            } else {
                final List<NameValueUrl> hitCounts = new ArrayList<NameValueUrl>();
                final Set<String> facetValues = facetList.get(facetName);

                for (final String facetValue : facetValues) {
                    final FieldCacheTermsFilter fieldCacheTermsFilter = new FieldCacheTermsFilter(
                        facetName, new String[] { facetValue });

                    final CachingWrapperFilter cachingWrapperFilter = new CachingWrapperFilter(
                        fieldCacheTermsFilter);

                    final TopDocs topDocs = searcher.search(query, cachingWrapperFilter, 1);
                    final int totalHits = topDocs.totalHits;

                    if (totalHits > 0) {
                        final String queryString = buildQueryString(facetName, facetValue,
                            parameters);

                        final NameValueUrl nameValueUrl = new NameValueUrl(facetValue, totalHits,
                            queryString);

                        hitCounts.add(nameValueUrl);
                    }
                }

                if (hitCounts.size() > 0) {
                    Collections.sort(hitCounts);
                    facet.setHitCounts(hitCounts);
                    facetCounts.add(facet);
                }
            }
        }

        return facetCounts;
    }

    List<FacetHitCount> bitsetCount(final Searcher searcher, final Query query,
        final Map<String, String[]> parameters) throws IOException {

        final List<FacetHitCount> facetCounts = new ArrayList<FacetHitCount>();

        // BASE query filter
        final QueryWrapperFilter baseQueryFilter = new QueryWrapperFilter(query);
        final CachingWrapperFilter baseCachingFilter = new CachingWrapperFilter(baseQueryFilter);

        final Set<String> facetNames = facetList.keySet();
        for (final String facetName : facetNames) {

            final FacetHitCount facetHitCount = new FacetHitCount();
            facetHitCount.setName(facetName);

            if (parameters.get(facetName) != null) {
                final String resetQueryString = resetQueryString(facetName, parameters);
                facetHitCount.setAllQueryString(resetQueryString);
                facetHitCount.setSelectedValuesArray(parameters.get(facetName));
                facetCounts.add(facetHitCount);

            } else {
                final List<NameValueUrl> hitCounts = new ArrayList<NameValueUrl>();

                final Set<String> facetValues = facetList.get(facetName);
                final String[] facetValueArray = new String[facetValues.size()];
                facetValues.toArray(facetValueArray);

                for (final String facetValue : facetValues) {
                    if (facetValue.trim().length() == 0) {
                        continue;
                    }

                    // FILTER query filter
                    Query queryFromParameterMap;
                    try {
                        queryFromParameterMap = getQueryFromParameterMap(facetName, facetValue);
                    } catch (final ParseException e) {
                        logger.error("", e);
                        continue;
                    }

                    final QueryWrapperFilter queryFilter = new QueryWrapperFilter(
                        queryFromParameterMap);
                    final CachingWrapperFilter cachingFilter = new CachingWrapperFilter(queryFilter);

                    // IndexReader
                    final IndexReader reader = luceneSupport.getIndexReader();

                    // BASE docIdSet
                    final DocIdSet baseDocIdSet = baseCachingFilter.getDocIdSet(reader);
                    final OpenBitSet baseOpenBitSet = new OpenBitSetDISI(baseDocIdSet.iterator(),
                        reader.maxDoc());

                    // FILTER docIdSet
                    final DocIdSet docIdSet = cachingFilter.getDocIdSet(reader);
                    final OpenBitSet openBitSet = new OpenBitSetDISI(docIdSet.iterator(),
                        reader.maxDoc());

                    // intersection between docIdSets
                    openBitSet.intersect(baseOpenBitSet);
                    final int totalHits = (int) openBitSet.cardinality();

                    if (totalHits > 0) {
                        final String queryString = buildQueryString(facetName, facetValue,
                            parameters);

                        final NameValueUrl nameValueUrl = new NameValueUrl(facetValue, totalHits,
                            queryString);

                        hitCounts.add(nameValueUrl);
                    }
                }

                if (hitCounts.size() > 0) {
                    if (facetName.equals(Constants.FIELD_FILE_HIERARCHY)) {
                        // sorting by name length
                        Collections.sort(hitCounts, new Comparator<NameValueUrl>() {
                            @Override
                            public int compare(final NameValueUrl o1, final NameValueUrl o2) {
                                final int o1length = o1.getName().length();
                                final int o2length = o2.getName().length();

                                int compare = Integer.valueOf(o1length).compareTo(o2length);
                                if (compare == 0) {
                                    compare = o1.getName().compareTo(o2.getName());
                                }
                                return compare;
                            }
                        });

                    } else {
                        Collections.sort(hitCounts);
                    }

                    facetHitCount.setHitCounts(hitCounts);
                    facetCounts.add(facetHitCount);
                }
            }
        }

        Collections.sort(facetCounts);
        return facetCounts;

    }

    String resetQueryString(final String facetName, final Map<String, String[]> parameters) {
        final StringBuilder sb = new StringBuilder();
        int i = 0;

        for (final String parameter : parameters.keySet()) {
            if (parameter.equals(facetName)) {
                continue;
            }

            final String[] values = parameters.get(parameter);

            for (final String value : values) {
                if (i > 0) {
                    sb.append("&");
                }

                sb.append(parameter).append("=").append(value);
                i++;
            }
        }

        if (sb.length() == 0) {
            sb.append("#");
        }

        return sb.toString();
    }

    String buildQueryString(final String facetName, final String facetValue,
        final Map<String, String[]> parameters) {

        final StringBuilder sb = new StringBuilder();
        int i = 0;

        for (final String parameter : parameters.keySet()) {
            final String[] parameterValues = parameters.get(parameter);

            for (final String parameterValue : parameterValues) {
                if (i > 0) {
                    sb.append("&");
                }

                sb.append(parameter).append("=").append(parameterValue);
                i++;
            }

        }

        if (parameters.size() > 0) {
            sb.append("&");
        }

        sb.append(facetName).append("=").append(Commons.encodeurl(facetValue));

        return sb.toString();
    }
}
