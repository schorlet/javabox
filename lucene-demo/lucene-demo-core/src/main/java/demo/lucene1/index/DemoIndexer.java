package demo.lucene1.index;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.lucene1.Commons;
import demo.lucene1.LuceneSupport;
import demo.lucene1.ParserException;
import demo.lucene1.jdbc.FacetDAO;

/**
 * DemoIndexer
 *
 * @author sch
 */
public class DemoIndexer implements Closeable {
    final Logger logger = LoggerFactory.getLogger(getClass());

    final LuceneSupport luceneSupport;

    final FacetDAO facetDAO;

    /**
     * file extension filter
     */
    final ExtensionFilter extensionFilter;

    public static void main(String[] args) throws IOException, SQLException {
        if (args.length == 1) {
            args = new String[] { Commons.getLuceneIndex(), args[0] };

        } else {
            args = new String[] { Commons.getLuceneIndex(), "/home/sch/doc" };
        }

        if (args.length != 2)
            throw new IllegalArgumentException("Usage: DemoIndexer [index] path");

        final DemoIndexer indexer = new DemoIndexer(args[0], true);
        indexer.index(args[1]);
        indexer.close();
    }

    /**
     * Creates a new FileIndexer with the specified {@code indexPath} and {@code create}.
     *
     * @param indexPath location of the lucene index
     * @param create lucene index creation
     * @throws IOException
     * @throws SQLException
     */
    public DemoIndexer(final String indexDir, final boolean create) throws IOException,
        SQLException {
        // creates lucene support
        luceneSupport = new LuceneSupport(indexDir, create);

        // creates facet repository
        facetDAO = new FacetDAO();
        if (create) {
            facetDAO.createTable();
        }

        // creates file filter
        extensionFilter = new ExtensionFilter();
    }

    /**
     * Closes the lucene writer.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        luceneSupport.close();
    }

    /**
     * Starts the indexation of the specified {@code path}.
     *
     * @param path the path or file to be indexed
     * @throws IOException
     */
    public void index(final String path) throws IOException {
        final long start = System.currentTimeMillis();
        int nbDocs = 0;

        try {
            // gets index writer
            final IndexWriter indexWriter = luceneSupport.getIndexWriter();

            // creates file handler
            final FileHandler fileHandler = new FileHandler();

            // index path
            nbDocs = indexDirectory(new File(path), fileHandler, indexWriter);
            indexWriter.commit();
            indexWriter.optimize();

            // save facets: if indexWriter.commit() fails then facets are not saved
            final Map<String, Set<String>> facets = fileHandler.getFacets();
            saveFacets(facets);

        } catch (final Exception e) {
            logger.error("FATAL", e);
        }

        final long end = System.currentTimeMillis();
        logger.info("total docs: {}", nbDocs);
        logger.info("total time (msec): {}", end - start);
        if (nbDocs > 0) {
            logger.info("average (msec): {}", (end - start) / nbDocs);
        }
    }

    /**
     * Loops thru the specified {@code path} for files to be indexed.
     *
     * @param path the path or file to be indexed
     * @param writer  the lucene index writer
     * @return the number of indexed files
     * @throws IOException
     * @throws CorruptIndexException
     */
    int indexDirectory(final File path, final FileHandler fileHandler, final IndexWriter writer)
        throws CorruptIndexException, IOException {
        int count = 0;

        if (path.isFile() && path.canRead() && extensionFilter.accept(path)) {
            count = indexFile(path, fileHandler, writer);

        } else if (path.isDirectory() && path.canRead()) {
            final File[] files = path.listFiles(extensionFilter);
            for (final File file : files) {
                count += indexDirectory(file, fileHandler, writer);
            }
        }

        return count;
    }

    /**
     * Process indexation of the specified {@code file}.
     *
     * @param file the file to be indexed
     * @param writer the lucene index writer
     * @return 1 if file has been indexed, 0 otherwise
     * @throws IOException
     * @throws CorruptIndexException
     */
    int indexFile(final File file, final FileHandler fileHandler, final IndexWriter writer)
        throws CorruptIndexException, IOException {
        try {
            logger.info("indexing {}", file.getPath());

            final Document document = fileHandler.parseFile(file);
            writer.addDocument(document);
            return 1;

        } catch (final UnsupportedOperationException e) {
            logger.error("file:  {}, {}", file, e);
            return 0;

        } catch (final ParserException e) {
            logger.error("file:  {}, {}", file, e);
            return 0;
        }
    }

    void saveFacets(final Map<String, Set<String>> facetMap) throws SQLException {
        facetDAO.saveFacetMap(facetMap);
    }

}
