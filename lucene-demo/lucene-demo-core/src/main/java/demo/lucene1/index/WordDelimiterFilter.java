package demo.lucene1.index;

import java.io.IOException;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.AttributeSource;

/**
 * WordDelimiterFilter
 * <br/>
 * WARN: runs very slowly
 */
public class WordDelimiterFilter extends TokenFilter {

    final TermAttribute termAttribute;
    final PositionIncrementAttribute positionAttribute;

    public WordDelimiterFilter(final TokenStream input) {
        super(input);
        termAttribute = (TermAttribute) addAttribute(TermAttribute.class);
        positionAttribute = (PositionIncrementAttribute) addAttribute(PositionIncrementAttribute.class);
    }

    final Stack<String> stack = new Stack<String>();
    AttributeSource.State current = null;

    @Override
    public final boolean incrementToken() throws IOException {

        if (!stack.empty() && current != null) {
            createToken(stack.pop(), current);
            return true;
        }

        while (input.incrementToken()) {
            // lenght filter
            if (termAttribute.termLength() < 3) {
                continue;
            }

            final String term = termAttribute.term();

            if (term.indexOf('-') > 0) {
                // join on '-' character
                final String[] split = term.split("-");
                for (int i = 0; i < split.length - 1; i++) {
                    stack.push(StringUtils.join(split, null, i, i + 2));
                }

            } else {

                final String[] punctuation = term.split("\\p{Punct}");
                if (punctuation.length > 1) {
                    for (final String element : punctuation) {
                        if (element.length() > 2) {
                            stack.push(element);
                        }
                    }
                }

                // split on uppercase character
                final String[] uppercase = term.split("(?=\\p{Lu})");
                if (uppercase.length > 1) {
                    for (final String element : uppercase) {
                        if (element.length() > 2) {
                            stack.push(element);
                        }
                    }
                }
            }

            if (stack.size() > 0) {
                current = captureState();
            }

            // remove protocol
            if (term.startsWith("http://")) {
                termAttribute.setTermBuffer(term, 7, term.length() - 7);

            } else if (term.startsWith("ftp://")) {
                termAttribute.setTermBuffer(term, 6, term.length() - 6);
            }

            return true;
        }

        return false;
    }

    void createToken(final String term, final AttributeSource.State current) {
        restoreState(current);
        termAttribute.setTermBuffer(term);
        positionAttribute.setPositionIncrement(0);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        current = null;
    }
}
