package demo.lucene1;

import java.io.Serializable;

/**
 * TermFreq is the association of a term and its frequency of appearence.
 */
public class TermFreq implements Comparable<TermFreq>, Serializable {
    private static final long serialVersionUID = -6045842353910053479L;

    public final String term;
    public final Integer frequency;

    public TermFreq(final String term, final int frequency) {
        this.term = term;
        this.frequency = Integer.valueOf(frequency);
    }

    public int compareTo(final TermFreq freq) {
        return -1 * frequency.compareTo(freq.frequency);
    }

    @Override
    public String toString() {
        return frequency + " | " + term;
    }
}