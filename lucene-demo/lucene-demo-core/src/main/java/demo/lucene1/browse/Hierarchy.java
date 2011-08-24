package demo.lucene1.browse;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.Validate;

public class Hierarchy {
    private final Collection<Node> nodes;

    /**
     * Creates a new Hierarchy based on the specified {@code nodes}.
     * @param nodes the root nodes
     * @throws IllegalArgumentException if nodes is null or is empty
     */
    public Hierarchy(final Collection<Node> nodes) throws IllegalArgumentException {
        Validate.notNull(nodes, "nodes must not be null");
        Validate.notEmpty(nodes, "nodes must not be empty");
        Validate.noNullElements(nodes, "nodes must not have null elements");
        this.nodes = nodes;
    }

    /**
     * @return the nodes
     */
    public Collection<Node> getNodes() {
        return nodes;
    }

    public Node getFirst() {
        final Iterator<Node> iterator = nodes.iterator();
        return iterator.next();
    }
}
