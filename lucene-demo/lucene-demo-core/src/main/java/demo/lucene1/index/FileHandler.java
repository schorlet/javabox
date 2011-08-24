package demo.lucene1.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import demo.lucene1.Constants;
import demo.lucene1.ParserException;

/**
 * FileHandler tansforms a {@link java.io.File File} into
 * a lucene {@link org.apache.lucene.document.Document Document}.
 *
 * @author sch
 */
class FileHandler {
    /**
     * tikaConfig
     */
    final TikaConfig tikaConfig;

    /**
     * map of facet values indexed by facet name
     */
    final private Map<String, Set<String>> facetList;

    /**
     * @return the facets built while parsing a File.
     */
    public Map<String, Set<String>> getFacets() {
        return facetList;
    }

    /**
     * Creates a new {@code ExtensionHandler}.
     * @throws TikaException
     */
    public FileHandler() throws TikaException {
        tikaConfig = TikaConfig.getDefaultConfig();
        facetList = new HashMap<String, Set<String>>();
    }

    /**
     * Parses the specified {@code file}.
     * You must add the returned {@code document} to the index before parsing a new File.
     *
     * @param file the file to parse
     * @return {@link org.apache.lucene.document.Document Document}
     * @throws ParserException
     */
    public Document parseFile(final File file) throws ParserException {
        final Parser parser = getParser(file);

        final Document document = parseFile(file, parser);
        return document;
    }

    /**
     * Gets the appropriate parser for the specified {@code file}
     * from {@link org.apache.tika.config.TikaConfig TikaConfig}.
     *
     * @param file the file to be parsed
     * @return {@link org.apache.tika.parser.Parser Parser}
     */
    Parser getParser(final File file) {
        final MimeTypes mimeTypes = tikaConfig.getMimeRepository();
        final MimeType mimeType = mimeTypes.getMimeType(file);
        final Parser parser = tikaConfig.getParser(mimeType.toString());

        if (parser == null)
            throw new UnsupportedOperationException("unable to get parser for mimeType: "
                + mimeType.toString());

        return parser;
    }

    /**
     * Parses the specified {@code file} with the specified {@code parser}.
     *
     * @param file the file to parse
     * @param parser the parser
     * @return {@link org.apache.lucene.document.Document Document}
     * @throws ParserException
     */
    Document parseFile(final File file, final Parser parser) throws ParserException {
        final Document document = new Document();

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            final Metadata metadata = new Metadata();
            final ContentHandler contentHandler = new BodyContentHandler();

            parser.parse(inputStream, contentHandler, metadata);
            inputStream.close();

            setMetaData(file, document);
            setMetaData(metadata, document);

            // set document content
            textField.setValue(contentHandler.toString());
            document.add(textField);

            suggestField.setValue(contentHandler.toString());
            document.add(suggestField);

        } catch (final NoClassDefFoundError e) {
            // bouncycastle is excluded from dependencies
            // this occurs when org.apache.tika.parser.pdf.PDFParser try to decrypt files
            throw new UnsupportedOperationException(e);

        } catch (final Exception e) {
            throw new ParserException(e);

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                }
            }
        }

        return document;
    }

    void setMetaData(final Metadata metadata, final Document document) {
        final String[] names = metadata.names();

        for (final String name : names) {
            final String[] values = metadata.getValues(name);

            for (final String value : values) {
                // add metadata
                final Field field = new Field(name, value, Store.YES, Index.NOT_ANALYZED);
                document.add(field);
                collectFacetValue(name, value);
            }
        }
    }

    final Field textField = new Field(Constants.FIELD_DEFAULT, "", Store.YES, Index.ANALYZED,
        TermVector.WITH_POSITIONS_OFFSETS);
    final Field suggestField = new Field(Constants.FIELD_SUGGEST, "", Store.NO, Index.ANALYZED);

    final Field fieldName = new Field(Constants.FIELD_FILE_NAME, "", Store.YES, Index.NOT_ANALYZED);
    final Field fieldPath = new Field(Constants.FIELD_FILE_PATH, "", Store.YES, Index.NOT_ANALYZED);
    final Field fieldParent = new Field(Constants.FIELD_FILE_PARENT, "", Store.YES,
        Index.NOT_ANALYZED);
    final Field fieldExtension = new Field(Constants.FIELD_FILE_TYPE, "", Store.YES,
        Index.NOT_ANALYZED);

    void setMetaData(final File file, final Document document) {
        // name
        final String name = file.getName();
        fieldName.setValue(name);
        document.add(fieldName);

        // path
        final String path = file.getPath();
        fieldPath.setValue(path);
        document.add(fieldPath);

        // parent
        final String parent = file.getParent();
        fieldParent.setValue(parent);
        document.add(fieldParent);
        collectFacetValue(Constants.FIELD_FILE_PARENT, parent);

        // hierarchy
        final Set<String> hierarchy = getHierarchy(parent);
        for (final String p : hierarchy) {
            final Field field = new Field(Constants.FIELD_FILE_HIERARCHY, p, Store.YES,
                Index.NOT_ANALYZED);
            document.add(field);
            collectFacetValue(Constants.FIELD_FILE_HIERARCHY, p);
        }

        // extension
        final String extension = ExtensionFilter.getExtension(file).toLowerCase();
        fieldExtension.setValue(extension);
        document.add(fieldExtension);
        collectFacetValue(Constants.FIELD_FILE_TYPE, extension);
    }

    Set<String> getHierarchy(final String parent) {
        final Set<String> hierarchies = new LinkedHashSet<String>();

        final StringTokenizer st = new StringTokenizer(parent, "/", true);
        final StringBuilder sb = new StringBuilder();

        while (st.hasMoreElements()) {
            final String delim = (String) st.nextElement();
            final String token = st.nextToken();

            final String node = String.format("%s%s", delim, token);
            sb.append(node);

            hierarchies.add(sb.toString());
        }

        return hierarchies;
    }

    void collectFacetValue(final String name, final String value) {
        if (value == null || value.trim().length() == 0) return;

        Set<String> facetValues = facetList.get(name);
        if (facetValues == null) {
            facetValues = new HashSet<String>();
            facetList.put(name, facetValues);
        }

        if (!facetValues.contains(value)) {
            facetValues.add(value);
        }
    }
}
