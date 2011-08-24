package demo.lucene1.search;

import java.io.Serializable;

/**
 * NameValueUrl is a value object where: 
 * <ul>
 *      <li>name: the facet value</li>
 *      <li>value: the hit count, the number of documents concerned by the facet value</li>
 *      <li>url: the url selection</li>
 * </ul>
 */
public class NameValueUrl implements Serializable, Comparable<NameValueUrl> {
    private static final long serialVersionUID = 7925938865703946303L;

    private String name;
    private Integer value;
    private String url;

    /**
     * @param name
     * @param value
     * @param url
     */
    public NameValueUrl(final String name, final int value, final String url) {
        super();
        this.name = name;
        this.value = value;
        this.url = url;
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
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(final int value) {
        this.value = value;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // return String.format("%s (%d) : %s", name, value, url);
        return String.format("%s (%d)", name, value);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final NameValueUrl other) {
        return -1 * value.compareTo(other.value);
    }
}
