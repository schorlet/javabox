package demo.hello;

import com.google.gwt.inject.client.Ginjector;

import demo.hello.client.cell.CellClient;

/**
 * GinTestModule. NOT IN USE !!
 */
interface GinTestModule extends Ginjector {
    CellClient getCellClient();
}
