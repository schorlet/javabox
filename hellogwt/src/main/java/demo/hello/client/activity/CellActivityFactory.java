package demo.hello.client.activity;

import demo.hello.client.cell.CellDtoEditorActivity;
import demo.hello.client.cell.CellProxyEditorActivity;
import demo.hello.client.cell.DataAsyncActivity;
import demo.hello.client.cell.DataListActivity;
import demo.hello.client.cell.SimpleActivity;

/**
 * CellActivityFactory
 */
public interface CellActivityFactory {

    SimpleActivity simpleActivity();

    DataListActivity dataListActivity();

    DataAsyncActivity dataAsyncActivity();

    CellDtoEditorActivity dtoEditorActivity();

    CellProxyEditorActivity proxyEditorActivity();

}
