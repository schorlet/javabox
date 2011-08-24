package demo.lucene1.search;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Documents
 *
 * @author sch
 */
public class Documents implements Serializable, Iterable<DocumentItem> {

    private static final long serialVersionUID = 3659497690369169683L;

    private final List<DocumentItem> documents;
    private final int totalHits;
    private final int pages;

    /**
     * @param documents the documents to display
     * @param totalHits the total documents count
     * @param hitsPerPage the number of documents per page
     */
    public Documents(final List<DocumentItem> documents, final int totalHits, final int hitsPerPage) {
        super();
        this.documents = documents;
        this.totalHits = totalHits;

        final int i = totalHits / hitsPerPage;
        if (totalHits % hitsPerPage == 0) {
            pages = i;
        } else {
            pages = i + 1;
        }
    }

    /**
     * @return the documents
     */
    public List<DocumentItem> getDocuments() {
        return documents;
    }

    /**
     * @return the totalHits
     */
    public int getTotalHits() {
        return totalHits;
    }

    /**
     * @return the pages
     */
    public int getPages() {
        return pages;
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<DocumentItem> iterator() {
        return documents.iterator();
    }

}
