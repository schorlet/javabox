package demo.lucene1;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LengthFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.fr.ElisionFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.solr.analysis.WordDelimiterFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Commons
 *
 * @author sch
 */
public class Commons {
    static final Logger logger = LoggerFactory.getLogger(Commons.class);

    /**
     * Application properties.
     */
    static final Configuration CONFIGURATION = new Configuration();

    /**
     * Formats the date with {@link demo.lucene1.Constants#DATE_FORMATTER}.
     *
     * @param d the date
     * @return formatted date
     */
    public static String formatDate(final Date d) {
        return Constants.DATE_FORMATTER.format(d);
    }

    /**
     * Formats the date with {@link demo.lucene1.Constants#DATE_FORMATTER}.
     *
     * @param l the date
     * @return formatted date
     */
    public static String formatDate(final long l) {
        return formatDate(new Date(l));
    }

    public static String normalize(final String s) {
        String normalized = s.replaceAll("[ \t]{2,}", " ");
        normalized = normalized.replaceAll("\r", "\n");
        normalized = normalized.replaceAll("\n +", "\n");
        normalized = normalized.replaceAll("\n{2,}", "\n");
        normalized = StringUtils.chomp(normalized);
        return normalized;
    }

    /**
     * Gets supported extensions from app.properties file.
     *
     * @return a set of extensions as String
     */
    public static Set<String> getFileExtensions() {
        final String extensions = CONFIGURATION.get("file.extensions");

        final HashSet<String> extensionSet = new HashSet<String>();
        extensionSet.addAll(Arrays.asList(extensions.split(",")));

        return extensionSet;
    }

    public static String getJDBCDriver() {
        final String jdbcDriver = CONFIGURATION.get("jdbc.driver");
        return jdbcDriver;
    }

    public static String getJDBCUrl() {
        final String jdbcURL = CONFIGURATION.get("jdbc.url");
        return jdbcURL;
    }

    public static String getLuceneIndex() {
        final String luceneIndex = CONFIGURATION.get("lucene.index");
        return luceneIndex;
    }

    public static String getLuceneSuggest() {
        final String luceneSuggest = CONFIGURATION.get("lucene.suggest");
        return luceneSuggest;
    }

    /**
     * return {@code ""} if the value is null.
     */
    public static String nvl(final String value) {
        return StringUtils.defaultString(value);
    }

    /**
     * returns a list of french and english common words.
     */
    static Set<String> stopWords() {
        final Set<String> stopWords = new HashSet<String>(555);

        stopWords.addAll(Arrays.asList("ai", "au", "ci", "de", "du", "en", "et", "eu", "il", "je",
            "la", "le", "ma", "ne", "ni", "on", "ou", "me", "qu", "sa", "se", "si", "ta", "te",
            "tu", "un", "va", "vu", "afin", "ainsi", "après", "attendu", "aujourd", "auquel",
            "aussi", "autre", "autres", "aux", "auxquelles", "auxquels", "avait", "avant", "avec",
            "avoir", "car", "ceci", "cela", "celle", "celles", "celui", "cependant", "certain",
            "certaine", "certaines", "certains", "ces", "cet", "cette", "ceux", "chez", "combien",
            "comme", "comment", "concernant", "contre", "dans", "debout", "dedans", "dehors",
            "delà", "depuis", "derrière", "des", "désormais", "desquelles", "desquels", "dessous",
            "dessus", "devant", "devers", "devra", "divers", "diverse", "diverses", "doit", "donc",
            "dont", "duquel", "durant", "dès", "elle", "elles", "entre", "environ", "est", "etc",
            "etre", "eux", "excepté", "hormis", "hors", "hélas", "hui", "ils", "jusqu", "jusque",
            "laquelle", "lequel", "les", "lesquelles", "lesquels", "leur", "leurs", "lorsque",
            "lui", "mais", "malgré", "merci", "mes", "mien", "mienne", "miennes", "miens", "moi",
            "moins", "mon", "moyennant", "même", "mêmes", "non", "nos", "notre", "nous",
            "néanmoins", "nôtre", "nôtres", "ont", "outre", "par", "parmi", "partant", "pas",
            "passé", "pendant", "plein", "plus", "plusieurs", "pour", "pourquoi", "proche", "près",
            "puisque", "quand", "que", "quel", "quelle", "quelles", "quels", "qui", "quoi",
            "quoique", "revoici", "revoilà", "sans", "sauf", "selon", "seront", "ses", "sien",
            "sienne", "siennes", "siens", "sinon", "soi", "soit", "son", "sont", "sous", "suivant",
            "sur", "tes", "tien", "tienne", "tiennes", "tiens", "toi", "ton", "tous", "tout",
            "toute", "toutes", "une", "vers", "voici", "voilà", "vos", "votre", "vous", "vôtre",
            "vôtres", "été", "être", "est", "avez"));

        stopWords.addAll(Arrays.asList("an", "as", "at", "be", "by", "if", "in", "is", "it", "of",
            "on", "or", "to", "do", "so", "no", "we", "he", "us", "and", "are", "but", "for",
            "into", "not", "such", "that", "the", "their", "then", "there", "these", "they",
            "this", "was", "will", "with", "all", "from", "use", "can", "new", "any", "more",
            "when", "data", "using", "what", "each", "has", "have", "many", "only", "other", "set",
            "time", "available", "code", "make", "may", "need", "some", "user", "you", "your",
            "get", "one", "used", "access", "create", "its", "most", "value", "web", "first",
            "file", "how", "information", "name", "run", "start", "about", "like", "multiple",
            "number", "open", "out", "over", "same", "should", "than", "where", "without", "key",
            "default", "example", "list", "single", "version", "also", "client", "does",
            "provides", "provide", "source", "support", "systems", "system", "which", "write",
            "configuration", "software", "two", "users", "via", "based", "common", "now", "order",
            "see", "them", "way", "well", "while", "work", "allow", "both", "end", "feature",
            "features", "however", "include", "level", "must", "own", "possible", "simple",
            "allows", "been", "directly", "load", "service", "another", "written", "before",
            "between", "control", "components", "every", "group", "point", "under", "very", "view",
            "within", "add", "being", "better", "find", "following", "per", "state", "string",
            "text", "through", "update", "uses", "because", "build", "created", "easy", "even",
            "full", "long", "return", "standard", "test", "always", "call", "cannot", "case",
            "change", "much", "read", "would", "automatically", "current", "either", "general",
            "includes", "often", "once", "size", "address", "built", "changes", "during", "found",
            "including", "large", "means", "part", "off", "public", "rather", "related", "right",
            "those", "unique", "until", "whether", "ability", "active", "added", "after", "basic",
            "best", "clients", "define", "given", "here", "instead", "just", "put", "target",
            "supports", "too", "base", "could", "date", "creating", "high", "less", "main",
            "needs", "note", "null", "perform", "real", "remove", "section", "since", "three",
            "true", "unless", "adding", "against", "down", "might", "needed", "defined", "ever",
            "having", "later", "makes", "our", "second", "try", "want", "why", "across", "keep",
            "something", "take", "cases", "else", "few", "made", "mode", "top", "void", "were",
            "who", "below", "beyond", "currently", "far", "follow", "highly", "info", "last",
            "pass", "bad", "boolean", "box", "byte", "columns", "component", "consider",
            "considered", "don", "don't", "due", "inc", "occur", "occurs", "sets", "taken",
            "today", "break", "clear", "early", "faster", "let", "rows", "sent", "causes", "con",
            "fast", "had", "adds", "doing", "his", "says", "come", "pre", "third", "thus", "begin",
            "behind", "did", "ends", "org", "com", "net", "http", "ftp"));

        // [a-z]
        for (int i = 97; i <= 122; i++) {
            stopWords.add(String.valueOf((char) i));
        }

        logger.debug("{} stop words", stopWords.size());

        return stopWords;
    }

    /**
     * Creates a TokenStream composed of :
     * {@link WhitespaceTokenizer},
     * {@link ElisionFilter},
     * {@link WordDelimiterFilter},
     * {@link LengthFilter},
     * {@link LowerCaseFilter},
     * {@link ASCIIFoldingFilter},
     * {@link StopFilter}.
     *
     * @return a newly instance of TokenStream
     */
    static TokenStream standardTokenStream(final Reader reader) {
        // TokenStream tokenStream = new StandardTokenizer(Constants.VERSION, reader);
        // tokenStream = new StandardFilter(tokenStream);

        TokenStream tokenStream = new WhitespaceTokenizer(reader);
        tokenStream = new ElisionFilter(tokenStream, ELISION_SET);

        tokenStream = new WordDelimiterFilter(tokenStream, 1, // generateWordParts
            1, // generateNumberParts,
            1, // catenateWords
            0, // catenateNumbers
            0, // catenateAll
            1, // splitOnCaseChange
            0, // preserveOriginal
            0, // splitOnNumerics,
            1, // stemEnglishPossessive
            null // protWords
        );
        tokenStream = new LengthFilter(tokenStream, 2, Integer.MAX_VALUE);
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new StopFilter(false, tokenStream, Constants.STOP_WORDS);
        tokenStream = new ASCIIFoldingFilter(tokenStream);
        return tokenStream;
    }

    static final List<String> ELISION_LIST = Arrays.asList("d", "c", "l", "m", "t", "qu", "n", "s",
        "j");
    static final HashSet<String> ELISION_SET = new HashSet<String>(ELISION_LIST);

    /**
     * Creates a TokenStream composed of :
     * {@link WhitespaceTokenizer},
     * {@link ElisionFilter},
     * {@link WordDelimiterFilter},
     * {@link LengthFilter},
     * {@link LowerCaseFilter},
     * {@link StopFilter}.
     *
     * @return a newly instance of TokenStream
     */
    static TokenStream suggestTokenStream(final Reader reader) {
        TokenStream tokenStream = new WhitespaceTokenizer(reader);
        tokenStream = new ElisionFilter(tokenStream, ELISION_SET);

        tokenStream = new WordDelimiterFilter(tokenStream, 1, // generateWordParts
            1, // generateNumberParts,
            1, // catenateWords
            0, // catenateNumbers
            0, // catenateAll
            1, // splitOnCaseChange
            0, // preserveOriginal
            0, // splitOnNumerics,
            1, // stemEnglishPossessive
            null // protWords
        );
        tokenStream = new LengthFilter(tokenStream, 2, Integer.MAX_VALUE);
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new StopFilter(false, tokenStream, Constants.STOP_WORDS);
        return tokenStream;
    }

    /*
     * see http://www.gossamer-threads.com/lists/lucene/java-user/123704
     * in order to not creating shingles accross stop words.
     * (lucene version must be >= 3.1)
     */
    /**
     * Creates a TokenStream based on {@link KeywordTokenizer}
     * with the addition of : {@link ShingleFilter}.
     *
     * @return a newly instance of TokenStream
     */
    static TokenStream shingleTokenStream(final Reader reader) {
        TokenStream tokenStream = new KeywordTokenizer(reader);

        tokenStream = new ShingleFilter(tokenStream);
        return tokenStream;
    }

    /**
     * @see #standardTokenStream(Reader)
     */
    static Analyzer standardAnalyzer() {
        final Analyzer analyzer = new Analyzer() {
            @Override
            public TokenStream tokenStream(final String fieldName, final Reader reader) {
                return standardTokenStream(reader);
            }
        };

        return analyzer;
    }

    /**
     * @see #suggestTokenStream(Reader)
     */
    static Analyzer suggestAnalyzer() {
        final Analyzer analyzer = new Analyzer() {
            @Override
            public TokenStream tokenStream(final String fieldName, final Reader reader) {
                return suggestTokenStream(reader);
            }
        };

        return analyzer;
    }

    public static String encodeurl(final String s) {
        try {
            final String encoded = URLEncoder.encode(s, "UTF-8");
            return encoded;

        } catch (final UnsupportedEncodingException e) {
            // highly improbable
            logger.error(String.format("URLEncoder.encode(%s, UTF-8)", s), e);
            return s;
        }
    }

    public static void main(final String[] args) {
        stopWords();

        final String s = "a\nb\n\nc\n\r\n\nd\n\n\r\n \n\r\n\n\n\ne\n\r\n\n\n\r\n\n";
        final String normalize = normalize(s);
        System.err.println(normalize);

    }

}
