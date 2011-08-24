package demo.lucene1.search;

import java.io.Serializable;

import org.apache.lucene.document.Document;

/**
 * DocumentItem
 *
 * @author sch
 */
public class DocumentItem implements Serializable {
    private static final long serialVersionUID = -1766808092984986573L;

    final Document document;
    final int docId;
    final float score;
    final int index;
    String highlights;

    /**
     * @param document
     * @param index
     */
    public DocumentItem(final Document document, final int docId, final float score, final int index) {
        this.document = document;
        this.docId = docId;
        this.score = score;
        this.index = index;
    }

    /**
     * @return the document
     */
    public String get(final String name) {
        return document.get(name);
    }

    /**
     * @return the docId
     */
    public int getDocId() {
        return docId;
    }

    /**
     * @return the score
     */
    public float getScore() {
        return score;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param highlights
     */
    public void setHighlights(final String highlights) {
        this.highlights = highlights;
    }

    /**
     * @return the highlights
     */
    public String getHighlights() {
        return highlights;
    }

}
