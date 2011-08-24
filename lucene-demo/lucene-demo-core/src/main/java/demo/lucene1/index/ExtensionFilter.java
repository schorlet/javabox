package demo.lucene1.index;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import demo.lucene1.Commons;

/**
 * ExtensionFilter
 *
 * @author sch
 */
class ExtensionFilter implements FileFilter {
    private final Set<String> extensions;

    /**
     * Creates a new ExtensionFilter with the specified {@code extensions}.
     *
     * @param keySet set of file extensions
     */
    public ExtensionFilter(final Set<String> extensions) {
        this.extensions = extensions;
    }

    public ExtensionFilter(final String[] extensions) {
        this.extensions = new HashSet<String>(extensions.length);
        this.extensions.addAll(Arrays.asList(extensions));
    }

    /**
     * Creates a new ExtensionFilter with extensions specified in {@code app.properties} file.
     */
    public ExtensionFilter() {
        this(Commons.getFileExtensions());
    }

    static String getExtension(final File file) {
        final String filename = file.getName();
        final int i = filename.lastIndexOf(".");
        if (i > 0) {
            return filename.substring(i + 1);
        }
        return "";
    }

    /* (non-Javadoc)
     * @see java.io.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(final File f) {
        if (!f.canRead()) return false;

        if (f.isDirectory()) {
            return true;
        }

        if (!f.isFile()) return false;

        final String extension = getExtension(f).toLowerCase();
        if (extensions.contains(extension)) {
            return true;
        }

        return false;
    }

}
