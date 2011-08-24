package demo.lucene1;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.StringDistance;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LuceneSpell
 * 
 * <pre>
 * TODO: this is not very accurate.
 * see: http://sujitpal.blogspot.com/2007/12/spelling-checker-with-lucene.html
 * see: http://norvig.com/spell-correct.html
 * </pre>
 * 
 * @author sch
 */
public class LuceneSpell implements Closeable {
    public static void main(final String[] args) throws Exception {
        final LuceneSpell luceneSpell = new LuceneSpell("/tmp/spell");

        final FSDirectory sourceDirectory = FSDirectory.open(new File(Commons.getLuceneIndex()));
        luceneSpell.createIndex(sourceDirectory);
        sourceDirectory.close();

        List<String> suggestions = luceneSpell.makeSuggestions("probabilite");
        System.out.println(suggestions);

        suggestions = luceneSpell.makeSuggestions("probabilité");
        System.out.println(suggestions);

        suggestions = luceneSpell.makeSuggestions("probablite");
        System.out.println(suggestions);

        suggestions = luceneSpell.makeSuggestions("probable");
        System.out.println(suggestions);

        luceneSpell.close();
    }

    final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * the index directory.
     */
    final FSDirectory fsDirectory;

    /**
        * the spell checker.
        */
    SpellChecker spellChecker;

    public LuceneSpell(final String indexDir) throws IOException {
        // opens the index directory
        fsDirectory = FSDirectory.open(new File(indexDir));
        logger.info("opening index dir {}", fsDirectory.getFile());

        // the string distance
        final StringDistance stringDistance = new LevensteinDistance();
        // final StringDistance stringDistance = new NGramDistance(6);

        // the spell checker
        // if fsDirectory doeas not exists, spellChecker will create it
        spellChecker = new SpellChecker(fsDirectory, stringDistance);
        spellChecker.setAccuracy(0.6f);
    }

    public synchronized void close() throws IOException {
        logger.info("closing index dir {}", fsDirectory.getFile());

        spellChecker.close();

        if (IndexWriter.isLocked(fsDirectory)) {
            IndexWriter.unlock(fsDirectory);
        }
        fsDirectory.close();
    }

    public void createIndex(final Directory sourceDirectory) throws IOException {
        final IndexReader sourceReader = IndexReader.open(sourceDirectory, true);

        try {
            createIndex(sourceReader);

        } finally {
            sourceReader.close();
        }
    }

    public void createIndex(final IndexReader sourceReader) throws IOException {
        logger.info("creating spell checker index");
        // spellChecker.clearIndex();

        final LuceneDictionary dictionary = new LuceneDictionary(sourceReader,
            Constants.FIELD_DEFAULT);

        spellChecker.clearIndex();
        spellChecker.indexDictionary(dictionary);
    }

    public List<String> makeSuggestions(final String word) throws IOException {
        final String[] similars = spellChecker.suggestSimilar(word, 20);
        final List<String> suggestions = Arrays.asList(similars);
        return suggestions;
    }

}
