package demo.lucene1;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter.Side;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LuceneSuggest
 *
 * @author sch
 */
public class LuceneSuggest implements Closeable {
    public static void main(final String[] args) throws Exception {
        final String suggestDir = Commons.getLuceneSuggest();
        final LuceneSuggest luceneSuggest = new LuceneSuggest(suggestDir);

        if (luceneSuggest.numDocs() == 0) {
            final String indexDir = Commons.getLuceneIndex();
            final FSDirectory sourceDirectory = FSDirectory.open(new File(indexDir));
            luceneSuggest.createIndex(sourceDirectory);
            sourceDirectory.close();
        }

        List<String> suggestions = luceneSuggest.makeSuggestions("cons");
        System.out.println("cons: " + suggestions);
        suggestions = luceneSuggest.makeSuggestions("conse");
        System.out.println("conse: " + suggestions);
        suggestions = luceneSuggest.makeSuggestions("consé");
        System.out.println("consé: " + suggestions);

        suggestions = luceneSuggest.makeSuggestions("dat");
        System.out.println("dat: " + suggestions);
        suggestions = luceneSuggest.makeSuggestions("data");
        System.out.println("data: " + suggestions);
        suggestions = luceneSuggest.makeSuggestions("DATA");
        System.out.println("DATA: " + suggestions);
        suggestions = luceneSuggest.makeSuggestions("datab");
        System.out.println("datab: " + suggestions);

        luceneSuggest.close();
    }

    final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * the index directory.
     */
    final FSDirectory fsDirectory;

    /**
     * the index reader.
     */
    IndexReader indexReader;

    /**
     * the index searcher.
     */
    IndexSearcher indexSearcher;

    /**
     * the query parser.
     */
    final QueryParser queryParser;

    public LuceneSuggest(final String indexDir) throws IOException {
        final QueryAnalyzer queryAnalyzer = new QueryAnalyzer();
        queryParser = createQueryParser(queryAnalyzer);

        // opens the index directory
        fsDirectory = FSDirectory.open(new File(indexDir));
        logger.info("opening index dir {}", fsDirectory.getFile());

        // ensure directory exists
        if (!IndexReader.indexExists(fsDirectory)) {
            final IndexWriter writer = new IndexWriter(fsDirectory, null, true,
                IndexWriter.MaxFieldLength.LIMITED);
            writer.close();
        }

        // index reader
        indexReader = createIndexReader();

        // index searcher
        indexSearcher = createIndexSearcher(indexReader);
    }

    public synchronized void close() throws IOException {
        logger.info("closing index dir {}", fsDirectory.getFile());

        indexReader.close();
        indexSearcher.close();

        if (IndexWriter.isLocked(fsDirectory)) {
            IndexWriter.unlock(fsDirectory);
        }
        fsDirectory.close();
    }

    /**
     * Create or update the suggest index with the specified {@code sourceDirectory}.
     * 
     * @param sourceDirectory the source index
     */
    public void createIndex(final Directory sourceDirectory) throws IOException {
        final IndexReader sourceReader = IndexReader.open(sourceDirectory, true);

        try {
            createIndex(sourceReader);

        } finally {
            sourceReader.close();
        }
    }

    /**
     * Create or update the suggest index with the specified {@code sourceReader}.
     * 
     * @param sourceReader the source index reader
     */
    public void createIndex(final IndexReader sourceReader) throws IOException {
        final long start = System.currentTimeMillis();
        final Analyzer indexAnalyzer = new IndexAnalyzer();

        // creates the index writer
        final IndexWriter indexWriter = new IndexWriter(fsDirectory, indexAnalyzer, false,
            MaxFieldLength.LIMITED);

        try {
            createIndex(sourceReader, indexWriter);
            indexWriter.commit();
            indexWriter.optimize();

        } catch (Exception e) {
            logger.error("unable to create suggest index", e);

        } finally {
            indexWriter.close();

            // reopens index reader/searcher
            reopen();
            final long end = System.currentTimeMillis();
            logger.info("suggest index created");

            logger.info("total docs: {}", numDocs());
            logger.info("total time (msec): {}", end - start);
        }
    }

    void createIndex(final IndexReader sourceReader, final IndexWriter indexWriter)
        throws IOException {
        final String sourceField = Constants.FIELD_SUGGEST;

        // create a dictionary from the provided index reader
        final LuceneDictionary dictionary = new LuceneDictionary(sourceReader, sourceField);

        @SuppressWarnings("unchecked")
        final Iterator<String> iterator = dictionary.getWordsIterator();

        while (iterator.hasNext()) {
            final String word = iterator.next();
            if (!isValidWord(word)) continue;

            // select the document frequency
            final Term term = new Term(sourceField, word);
            final String docFreq = String.valueOf(sourceReader.docFreq(term));

            // set the fields value
            ORIGINAL_FIELD.setValue(word);
            GRAMMED_FIELD.setValue(word);
            COUNT_FIELD.setValue(docFreq);

            final Document document = new Document();
            document.add(ORIGINAL_FIELD);
            document.add(GRAMMED_FIELD);
            document.add(COUNT_FIELD);

            // index the document
            indexWriter.addDocument(document);
        }
    }

    public int numDocs() {
        return indexReader.numDocs();
    }

    static final Pattern pattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");

    boolean isValidWord(String word) {
        if (word.matches(pattern.pattern())) {
            return false;

        } else {
            final Matcher matcher = pattern.matcher(word);
            if (matcher.find()) { return false; }
        }

        return true;
    }

    static final String ORIGINAL_WORD = "original";
    static final String GRAMMED_WORD = "grammed";
    static final String COUNT_WORD = "count";

    static final Field ORIGINAL_FIELD = new Field(ORIGINAL_WORD, "", Store.YES, Index.NOT_ANALYZED);
    static final Field GRAMMED_FIELD = new Field(GRAMMED_WORD, "", Store.NO,
        Index.ANALYZED_NO_NORMS);
    static final Field COUNT_FIELD = new Field(COUNT_WORD, "", Store.NO, Index.NOT_ANALYZED);

    static final SortField SORT_FIELD = new SortField(COUNT_WORD, SortField.INT, true);
    static final Sort SORT = new Sort(SORT_FIELD);

    public List<String> makeSuggestions(final String term) throws IOException, ParseException {
        final List<String> suggestions = new ArrayList<String>();

        // parse the query
        final Query query = queryParser.parse(term);

        // execute search
        final TopDocs docs = indexSearcher.search(query, null, 10, SORT);

        for (final ScoreDoc doc : docs.scoreDocs) {
            final Document document = indexReader.document(doc.doc);
            final String original = document.get(ORIGINAL_WORD);

            if (!original.equalsIgnoreCase(term)) {
                suggestions.add(original);
            }
        }

        return suggestions;
    }

    final synchronized void reopen() throws IOException {
        final boolean do_reopen = !indexReader.isCurrent();
        if (do_reopen) {
            logger.info("reopen index dir {}", fsDirectory.getFile());

            indexSearcher.close();
            indexReader = indexReader.reopen(true);
            indexSearcher = createIndexSearcher(indexReader);
        }
    }

    /*
     * this final as it is called in the constructor
     */
    final IndexReader createIndexReader() throws IOException {
        // opens the IndexReader with readOnly=true.
        final IndexReader indexReader = IndexReader.open(fsDirectory, true);
        return indexReader;
    }

    final IndexSearcher createIndexSearcher(final IndexReader indexReader) throws IOException {
        // use one instance of IndexSearcher.
        // share a single IndexSearcher across queries and across threads in your application.
        final IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        return indexSearcher;
    }

    final QueryParser createQueryParser(final Analyzer analyzer) {
        final QueryParser parser = new QueryParser(Constants.VERSION, GRAMMED_WORD, analyzer);
        parser.setAllowLeadingWildcard(true);
        parser.setDefaultOperator(Operator.AND);
        return parser;
    }

    /**
     * QueryAnalyzer
     */
    final class QueryAnalyzer extends Analyzer {
        @Override
        public TokenStream tokenStream(final String fieldName, final Reader reader) {
            TokenStream tokenStream = new KeywordTokenizer(reader);
            tokenStream = new LowerCaseFilter(tokenStream);
            // tokenStream = new ASCIIFoldingFilter(tokenStream);

            return tokenStream;
        }

    }

    /**
     * IndexAnalyzer
     */
    final class IndexAnalyzer extends Analyzer {
        @Override
        public TokenStream tokenStream(final String fieldName, final Reader reader) {
            TokenStream tokenStream = new KeywordTokenizer(reader);
            tokenStream = new LowerCaseFilter(tokenStream);
            // tokenStream = new ASCIIFoldingFilter(tokenStream);

            tokenStream = new EdgeNGramTokenFilter(tokenStream, Side.FRONT, 3, 20);
            return tokenStream;
        }

    }
}
