package demo.hello.client.cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import demo.hello.client.Commons;
import demo.hello.client.Logger;
import demo.hello.shared.cell.CellDTO;

/**
 * CellDtoProvider
 */
public class CellDtoProvider {

    final static List<CellDTO> CELLDTO_LIST = create_celldto_list();

    public static List<CellDTO> getCelldtoList() {
        return CELLDTO_LIST;
    }

    private static List<CellDTO> create_celldto_list() {
        final ArrayList<CellDTO> tableData = new ArrayList<CellDTO>();

        for (int i = 0; i < 12; i++) {
            tableData.add(new CellDTO(Commons.nextString(10), Commons.nextDouble(100), Commons
                .nextBoolean(), Commons.nextDate()));
        }

        return Collections.unmodifiableList(tableData);
    }

    public static CellDTO getByA(final String a) {
        Logger.logp(Level.INFO, "CellDtoProvider", "getByA", a);

        for (final CellDTO cellDTO : getCelldtoList()) {
            if (cellDTO.getA().equals(a)) return cellDTO;
        }
        return null;
    }
}
