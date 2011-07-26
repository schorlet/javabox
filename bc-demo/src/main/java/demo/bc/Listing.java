package demo.bc;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Listing {
    private static final Log log = LogFactory.getLog(Listing.class);

    public static void main(final String[] args) {
        list();
    }

    static void list() {
        final Provider[] providers = Security.getProviders();
        for (final Provider provider : providers) {
            list(provider);
        }
    }

    static void list(final Provider provider) {
        log.debug(provider.getName());

        String indexed = null;
        final Provider.Service[] services = provider.getServices().toArray(
            new Provider.Service[] {});
        Arrays.sort(services, new Comparator<Provider.Service>() {
            public int compare(final Provider.Service o1, final Provider.Service o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });
        for (final Provider.Service service : services) {
            if (!service.getType().equals(indexed)) {
                indexed = service.getType();
                log.debug("  " + indexed);
            }
            log.debug("    " + service.getAlgorithm());
        }
    }
}
