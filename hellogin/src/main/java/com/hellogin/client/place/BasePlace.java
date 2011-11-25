package com.hellogin.client.place;

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

    public final String getPrefix() {
        return prefix;
    }

    public final String getSuffix() {
        return suffix;
    }

    public final String getToken() {
        return prefix + suffix;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BasePlace [").append(getToken()).append("]");
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
