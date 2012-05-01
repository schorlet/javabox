package demo.gap.server.plumbing;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;

/**
 * PersistFilter2
 */
@Singleton
public final class PersistFilter2 implements Filter {
    static final Logger logger = LoggerFactory.getLogger(PersistFilter2.class);

    private final UnitOfWork unitOfWork;
    private final PersistService persistService;

    @Inject
    public PersistFilter2(final UnitOfWork unitOfWork, final PersistService persistService) {
        this.unitOfWork = unitOfWork;
        this.persistService = persistService;
    }

    public void init(final FilterConfig filterConfig) throws ServletException {
        logger.info("STARTING");
        persistService.start();
    }

    public void destroy() {
        logger.info("STOP");
        persistService.stop();
    }

    public void doFilter(final ServletRequest servletRequest,
        final ServletResponse servletResponse, final FilterChain filterChain) throws IOException,
        ServletException {

        logger.debug("UNIT_OF_WORK BEGIN");
        unitOfWork.begin();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            logger.debug("UNIT_OF_WORK END");
            unitOfWork.end();
        }
    }
}
