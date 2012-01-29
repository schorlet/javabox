package demo.hello.server.plumbing;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.UnitOfWork;

@Singleton
final class GuicePersistFilter implements Filter {
    final UnitOfWork unitOfWork;

    @Inject
    public GuicePersistFilter(final UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
        final ServletResponse servletResponse, final FilterChain filterChain) throws IOException,
        ServletException {

        unitOfWork.begin();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            unitOfWork.end();
        }
    }

    @Override
    public void destroy() {}

    @Override
    public void init(final FilterConfig arg0) throws ServletException {}

}
