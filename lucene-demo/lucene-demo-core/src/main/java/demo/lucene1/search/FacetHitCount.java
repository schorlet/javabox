package demo.lucene1.search;

import java.io.Serializable;
import java.util.List;

/**
 * FacetHitCount
 *
 * @author sch
 */
public class FacetHitCount implements Serializable, Comparable<FacetHitCount> {
    private static final long serialVersionUID = -9183400175096742310L;

    private String name;
    private List<NameValueUrl> hitCounts;

    private String seeAllQueryString;
    private String[] values;

    /**
     * @return the values
     */
    public String getSelectedValues() {
        final StringBuilder sb = new StringBuilder();
        for (final String value : values) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * @param values the values to set
     */
    public void setSelectedValuesArray(final String[] values) {
        this.values = values;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the hitCounts
     */
    public List<NameValueUrl> getHitCounts() {
        return hitCounts;
    }

    /**
     * @param hitCounts the hitCounts to set
     */
    public void setHitCounts(final List<NameValueUrl> hitCounts) {
        this.hitCounts = hitCounts;
    }

    /**
     * @return the seeAllQueryString
     */
    public String getSeeAllQueryString() {
        return seeAllQueryString;
    }

    /**
     * @param seeAllQueryString the seeAllQueryString to set
     */
    public void setAllQueryString(final String seeAllQueryString) {
        this.seeAllQueryString = seeAllQueryString;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(name).append("\n");

        if (hitCounts == null) {
            sb.append("  see all: ").append(seeAllQueryString);

        } else {
            for (final NameValueUrl triple : hitCounts) {
                sb.append("  ").append(triple.toString()).append("\n");
            }
        }

        return sb.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final FacetHitCount other) {
        return name.compareTo(other.name);
    }
}
