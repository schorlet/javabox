package demo.hello.client.place;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.place.shared.Place;

/**
 * BasePlace
 */
public abstract class BasePlace extends Place {
    private final String prefix;
    private final String suffix;

    protected BasePlace(final String prefix, final String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * newPlace
     */
    public abstract BasePlace newPlace(final Map<String, String> parameters);

    /**
     * getPrefix
     */
    public final String getPrefix() {
        return prefix;
    }

    /**
     * getSuffix
     */
    public final String getSuffix() {
        return suffix;
    }

    /**
     * getToken
     */
    public final String getToken() {
        return prefix + suffix;
    }

    /**
     * getParameters
     */
    public Map<String, String> getParameters() {
        final Map<String, String> parameters = new HashMap<String, String>();
        final String params[] = suffix.split("&");

        for (final String param : params) {
            final String kv[] = param.split("=");
            if (kv.length == 2) {
                parameters.put(kv[0], kv[1]);
            }
        }
        return parameters;
    }

    protected final String getSuffixFromParameters(final Map<String, String> parameters) {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, String> parameter : parameters.entrySet()) {
            sb.append(parameter.getKey()).append('=').append(parameter.getValue()).append('&');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getPrefix()).append(" [").append(getSuffix()).append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (prefix == null ? 0 : prefix.hashCode());
        result = prime * result + (suffix == null ? 0 : suffix.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final BasePlace other = (BasePlace) obj;
        if (prefix == null) {
            if (other.prefix != null) return false;
        } else if (!prefix.equals(other.prefix)) return false;
        if (suffix == null) {
            if (other.suffix != null) return false;
        } else if (!suffix.equals(other.suffix)) return false;
        return true;
    }
}
