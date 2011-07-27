package demo.axon.customer.command;

/**
 * CustomerValidation.
 *
 * @author schorlet
 * @version $Revision$ $Date$
 * @since 2 oct. 2010 22:34:08
 */
public interface CustomerValidation {

    void uniqueCustomerName(String name);

}