package demo.lucene1;

/**
 * ParseException
 *
 * @author sch
 */
public class ParserException extends Exception {

    private static final long serialVersionUID = -1979526303798078623L;

    /**
     * {@inheritDoc}
     */
    public ParserException(final Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public ParserException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
