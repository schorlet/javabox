package demo.lucene1.index;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

/**
 * NumberFilter
 *
 * @author sch
 */
public class NumberFilter extends TokenFilter {
    /** integers and floating point numbers */
    // static final Pattern pattern = Pattern.compile("\\b[-+]?[0-9]*\\.?[0-9]+\\b");
    static final Pattern pattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");

    final TermAttribute termAttribute;

    public NumberFilter(final TokenStream input) {
        super(input);
        termAttribute = (TermAttribute) addAttribute(TermAttribute.class);
    }

    @Override
    public final boolean incrementToken() throws IOException {

        while (input.incrementToken()) {
            final String term = termAttribute.term();

            if (term.matches(pattern.pattern())) {
                continue;

            } else {
                final Matcher matcher = pattern.matcher(term);
                if (matcher.find()) {
                    continue;
                }
            }

            return true;
        }

        return false;
    }

}
