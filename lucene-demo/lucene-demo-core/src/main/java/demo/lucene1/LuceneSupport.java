package demo.lucene1;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LuceneSupport manages index directory, writer, reader and searcher 
 * for one specified index dir.
 *
 * @author sch
 */
public final class LuceneSupport implements Closeable {
    final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * the index directory.
     */
    final FSDirectory fsDirectory;

    /**
     * the index writer.
     */
    final IndexWriter indexWriter;

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

    /**
     * the field analyser.
     */
    final Analyzer analyzer;

    /**
     * Create a new lucene instance of lucene support.
     * 
     * @throws IOException 
     */
    public LuceneSupport(final String indexDir, final boolean create) throws IOException {
        logger.info("opening index dir {}", indexDir);

        // opens the index directory
        final File indexPath = new File(indexDir);
        fsDirectory = FSDirectory.open(indexPath);

        // field analyzer
        analyzer = createAnalyzer();

        // query parser
        queryParser = createQueryParser(analyzer);

        // index writer
        indexWriter = createIndexWriter(analyzer, create);

        // index reader
        indexReader = createIndexReader();

        // index searcher
        indexSearcher = createIndexSearcher(indexReader);
    }

    /**
     * Check whether any new changes have occurred to the index.
     * 
     * @return true if new changes have occurred and the index reader has been reopened
     * @throws CorruptIndexException
     * @throws IOException
     */
    public synchronized boolean reopen() throws CorruptIndexException, IOException {
        final boolean do_reopen = !indexReader.isCurrent();

        if (do_reopen) {
            indexSearcher.close();
            indexReader = indexReader.reopen(true);
            indexSearcher = createIndexSearcher(indexReader);
        }

        return do_reopen;
    }

    /**
     * Closes the lucene index reader.
     * 
     * @throws IOException
     */
    public synchronized void close() throws IOException {
        logger.info("closing index dir {}", fsDirectory.getFile());

        indexReader.close();
        indexWriter.close();
        indexSearcher.close();

        if (IndexWriter.isLocked(fsDirectory)) {
            IndexWriter.unlock(fsDirectory);
        }
        fsDirectory.close();
    }

    /**
     * @return the indexReader
     */
    public IndexReader getIndexReader() {
        return indexReader;
    }

    /**
     * @return the indexSearcher
     */
    public Searcher getIndexSearcher() {
        return indexSearcher;
    }

    /**
     * @return the indexWriter
     */
    public IndexWriter getIndexWriter() {
        return indexWriter;
    }

    /**
     * @return the parser
     */
    public QueryParser getQueryParser() {
        return queryParser;
    }

    /**
     * @return the analyzer
     */
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    final IndexWriter createIndexWriter(final Analyzer analyzer, final boolean create)
        throws IOException {
        // opens the IndexWriter.
        // autoCommit is set to false with this constructor.
        final IndexWriter indexWriter = new IndexWriter(fsDirectory, analyzer, create,
            MaxFieldLength.LIMITED);

        // 100 in-memory documents before flushing to a new segment
        indexWriter.setMaxBufferedDocs(100);
        // 10 Mo buffer size in-memory documents before flushing to a new segment
        // so it will be optimal for files of 100Ko size
        indexWriter.setRAMBufferSizeMB(10);

        // use the same value as maxBufferedDocs
        indexWriter.setMergeFactor(100);
        // maximum number of documents per segment index
        indexWriter.setMaxMergeDocs(Integer.MAX_VALUE);

        indexWriter.setUseCompoundFile(false);

        return indexWriter;
    }

    final IndexReader createIndexReader() throws IOException {
        // opens the IndexReader with readOnly=true.
        final IndexReader indexReader = IndexReader.open(fsDirectory, true);
        return indexReader;
    }

    final IndexSearcher createIndexSearcher(final IndexReader indexReader) throws IOException {
        // use one instance of IndexSearcher.
        // share a single IndexSearcher across queries and across threads in your application.
        final IndexSearcher searcher = new IndexSearcher(indexReader);
        return searcher;
    }

    final QueryParser createQueryParser(final Analyzer analyzer) {
        final QueryParser parser = new QueryParser(Constants.VERSION, Constants.FIELD_DEFAULT,
            analyzer);
        parser.setAllowLeadingWildcard(true);
        parser.setDefaultOperator(Operator.AND);
        return parser;
    }

    final Analyzer createAnalyzer() {
        // creates a per filed analyzer
        // all fields will be keyword analyzed except "test" field
        final PerFieldAnalyzerWrapper perFieldAnalyzerWrapper = new PerFieldAnalyzerWrapper(
            Constants.KEYWORD_ANALYSER);
        perFieldAnalyzerWrapper.addAnalyzer(Constants.FIELD_DEFAULT, Constants.STANDARD_ANALYSER);
        perFieldAnalyzerWrapper.addAnalyzer(Constants.FIELD_SUGGEST, Constants.SUGGEST_ANALYSER);
        return perFieldAnalyzerWrapper;
    }

    /**
     * Enumerates the {@link Constants.FIELD_DEFAULT default field} terms by document frequency.
     * 
     * @param mindocFreq the minimum frequency of documents
     * @return a list of terms sorted by frequency in descendant order
     */
    public List<TermFreq> termsDocFreq(final int minDocFreq) throws IOException {
        return termsDocFreq(Constants.FIELD_DEFAULT, minDocFreq);
    }

    /**
     * Enumerates the specified {@code field} terms by document frequency 
     * using {@link IndexReader#terms()}.
     * 
     * @param field the field to search terms
     * @param mindocFreq the minimum frequency of documents
     * @return a list of terms sorted by frequency in descendant order
     */
    public List<TermFreq> termsDocFreq(final String field, final int minDocFreq) throws IOException {
        final List<TermFreq> termFreqs = new ArrayList<TermFreq>();

        final IndexReader indexReader = getIndexReader();
        // returns an enumeration of all terms **starting** at a given term ...
        // indexReader.terms(new Term(field)); this is buggy, it always miss one term (lucene 2.9)
        final TermEnum termEnum = indexReader.terms();
        boolean found = false;

        while (termEnum.next()) {
            final Term term = termEnum.term();
            if (!field.equals(term.field())) {
                if (found) {
                    break;
                } else {
                    continue;
                }
            }

            found = true;
            final int docFreq = termEnum.docFreq();

            if (docFreq >= minDocFreq) {
                final TermFreq termFreq = new TermFreq(term.text(), docFreq);
                termFreqs.add(termFreq);
            }
        }
        termEnum.close();

        Collections.sort(termFreqs);
        return termFreqs;
    }

    /**
     * Enumerates terms by term frequency.
     * 
     * @param minTermFreq minimum frequency of term
     * @return a list of terms sorted by frequency in descendant order
     */
    public List<TermFreq> termsTermFreq(final int minTermFreq) throws IOException {
        final List<TermFreq> termFreqs = new ArrayList<TermFreq>();

        final IndexReader indexReader = getIndexReader();
        final TermEnum termEnum = indexReader.terms(new Term(Constants.FIELD_DEFAULT));

        while (termEnum.next()) {
            final Term term = termEnum.term();
            if (!Constants.FIELD_DEFAULT.equals(term.field())) {
                break;
            }

            int termFreq = 0;

            final TermDocs termDocs = indexReader.termDocs(term);
            while (termDocs.next()) {
                termFreq += termDocs.freq();
            }
            termDocs.close();

            final TermFreq freq = new TermFreq(term.text(), termFreq);
            termFreqs.add(freq);
        }
        termEnum.close();

        Collections.sort(termFreqs);
        return termFreqs;
    }

    /**
     * Enumerates terms by document frequency.
     * 
     * @param maxTerms the maximum number of terms
     * @return a list of terms sorted by frequency in descendant order
     */
    public List<TermFreq> termsTermFreqMaxTerms(final int maxTerms) throws IOException {
        final List<TermFreq> termsTermFreq = termsTermFreq(1);

        final int toIndex = maxTerms < termsTermFreq.size() ? maxTerms : termsTermFreq.size();
        return termsTermFreq.subList(0, toIndex);
    }

    /**
     * Enumerates terms by document frequency.
     * 
     * @param docId the document id
     * @param maxTerms the maximum number of terms
     * @return a list of terms sorted by frequency in descendant order
     */
    public List<TermFreq> termsTermFreqMaxTerms(final int docId, final int maxTerms)
        throws IOException {
        final TermFreqVector termFreqVector = indexReader.getTermFreqVector(docId,
            Constants.FIELD_DEFAULT);
        if (termFreqVector == null) return Collections.emptyList();

        final List<TermFreq> termFreqs = new ArrayList<TermFreq>(termFreqVector.size());

        final String[] terms = termFreqVector.getTerms();
        final int[] frequencies = termFreqVector.getTermFrequencies();

        for (int i = 0; i < terms.length; i++) {
            final TermFreq termFreq = new TermFreq(terms[i], frequencies[i]);
            termFreqs.add(termFreq);
        }

        Collections.sort(termFreqs);

        final int toIndex = maxTerms < termFreqs.size() ? maxTerms : termFreqs.size();
        return termFreqs.subList(0, toIndex);
    }
}
