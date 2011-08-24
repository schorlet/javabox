package demo.lucene1;

import java.text.SimpleDateFormat;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.util.Version;

/**
 * Constants
 *
 * @author sch
 */
public interface Constants {
    /**
     * Lucene version.
     */
    public static final Version VERSION = Version.LUCENE_29;

    public static final Set<String> STOP_WORDS = Commons.stopWords();

    /**
     * @see Commons#standardAnalyzer()
     */
    public static final Analyzer STANDARD_ANALYSER = Commons.standardAnalyzer();

    /**
     * @see Commons#suggestAnalyzer()
     */
    public static final Analyzer SUGGEST_ANALYSER = Commons.suggestAnalyzer();

    /**
     * KeywordAnalyzer is used for fields witch were NOT analyzed.
     */
    public static final Analyzer KEYWORD_ANALYSER = new KeywordAnalyzer();

    /**
     * format is yyyyMMdd.
     */
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");

    /**
     * default field.<br/>
     * Store.YES, Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS
     */
    public static final String FIELD_DEFAULT = "text";

    /**
     * suggest field.<br/>
     * Store.NO, Index.ANALYZED
     */
    public static final String FIELD_SUGGEST = "suggest";

    /**
     * file name.<br/>
     * Store.YES, Index.NOT_ANALYZED
     */
    public static final String FIELD_FILE_NAME = "FILE_NAME";

    /**
     * full file path.<br/>
     * Store.YES, Index.NOT_ANALYZED
     */
    public static final String FIELD_FILE_PATH = "FILE_PATH";

    /**
     * file extension.<br/>
     * Store.YES, Index.NOT_ANALYZED
     */
    public static final String FIELD_FILE_TYPE = "FILE_TYPE";

    /**
     * file parent dir.<br/>
     * Store.YES, Index.NOT_ANALYZED
     */
    public static final String FIELD_FILE_PARENT = "FILE_PARENT";

    /**
     * file parents hirerachy dir, mutlivalued.<br/>
     * Store.YES, Index.NOT_ANALYZED
     */
    public static final String FIELD_FILE_HIERARCHY = "FILE_HIERARCHY";

    public static final String PAGE = "page";
    public static final String ID = "id";
    public static final String PARENT = "parent";

}
