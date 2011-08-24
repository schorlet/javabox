package demo.lucene1.browse;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;

/**
 * Node
 */
public class Node {
    final String term;
    Integer freq;

    final Node parent;
    final Map<String, Node> children = new LinkedHashMap<String, Node>();

    /**
     * Creates a new Node with the specified arguments.
     * @throws IllegalArgumentException if term is null
     */
    public Node(final String term, final Node parent) {
        this(term, 0, parent);
    }

    /**
     * Creates a new Node with the specified arguments.
     * @throws IllegalArgumentException if term or freq are null
     */
    public Node(final String term, final Integer freq, final Node parent) {
        Validate.notNull(term, "term must not be null");
        Validate.notNull(freq, "freq must not be null");

        this.term = term;
        this.freq = freq;
        this.parent = parent;

        if (parent != null) {
            parent.children.put(term, this);
        }
    }

    boolean childExists(final String node) {
        return children.containsKey(node);
    }

    Node child(final String node) {
        return children.get(node);
    }

    public Integer getFreq() {
        return freq;
    }

    public void setFreq(final Integer freq) {
        this.freq = freq;
    }

    public String getName() {
        return String.format("%s (%d)", term, freq);
    }

    public Collection<Node> getChildren() {
        return children.values();
    }

    public String getFullPath() {
        final StringBuilder builder = new StringBuilder();

        builder.append('/').append(term);
        Node parentNode = parent;

        while (parentNode != null) {
            builder.insert(0, '/').insert(1, parentNode.term);
            parentNode = parentNode.parent;
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return getName();
    }
}