package demo.lucene1.search;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLEncoder;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;

import demo.lucene1.LuceneSupport;

/**
 * Highlighting
 *
 * @author sch
 */
class Highlighting {
    final Highlighter highlighter;
    final Analyzer analyzer;
    final IndexReader indexReader;

    Highlighting(final Query query, final LuceneSupport luceneSupport) {
        final QueryScorer scorer = new QueryScorer(query);
        final Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 50);
        // final Fragmenter fragmenter = new SimpleFragmenter(50);

        highlighter = new Highlighter(scorer);
        highlighter.setTextFragmenter(fragmenter);
        highlighter.setEncoder(new SimpleHTMLEncoder());
        // highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);

        analyzer = luceneSupport.getAnalyzer();
        indexReader = luceneSupport.getIndexReader();
    }

    String highlights(final int doc, final String fieldDefault, final String fieldContent)
        throws IOException {
        return highlights(doc, fieldDefault, fieldContent, 4);
    }

    String highlights(final int docId, final String fieldDefault, final String fieldContent,
        final int maxNumFragments) throws IOException {

        final TokenStream tokenStream = TokenSources.getAnyTokenStream(indexReader, docId,
            fieldDefault, analyzer);

        try {
            final String fragments = highlighter.getBestFragments(tokenStream, fieldContent,
                maxNumFragments, "...");
            return fragments;

        } catch (final InvalidTokenOffsetsException e) {
            e.printStackTrace(System.err);
        }

        return "";
    }
}
