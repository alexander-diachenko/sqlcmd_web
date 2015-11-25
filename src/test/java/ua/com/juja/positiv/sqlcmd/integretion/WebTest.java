package ua.com.juja.positiv.sqlcmd.integretion;

import org.junit.*;
import ua.com.juja.positiv.sqlcmd.DatabaseLogin;
import ua.com.juja.positiv.sqlcmd.DatabasePreparation;

import java.util.Random;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * Created by POSITIV on 25.11.2015.
 */

public class WebTest {

    DatabaseLogin login = new DatabaseLogin();
    DatabasePreparation preparation = new DatabasePreparation();

    @Before
    public void prepare() {
        setBaseUrl("http://localhost:8080/sqlcmd");
        beginAt("/connect");
        setTextField("database", login.getDatabase());
        setTextField("user", login.getUser());
        setTextField("password", login.getPassword());
        submit();
    }

    @Test
    public void testMenu() {
        assertLinkPresentWithText("connect");
        assertLinkPresentWithText("create-table");
        assertLinkPresentWithText("table-names");
        assertLinkPresentWithText("table-data");
        assertLinkPresentWithText("update-record");
        assertLinkPresentWithText("clear-table");
        assertLinkPresentWithText("create-record");
        assertLinkPresentWithText("delete-record");
        assertLinkPresentWithText("create-database");
        assertLinkPresentWithText("delete-database");
    }

    @Test
    public void testTableNames(){
        preparation.run();
        clickLinkWithText("table-names");

        assertTableEquals("",
                new String[][] {
                        {"", "Tables"},
                        {"1", "car"},
                        {"2", "client"}});
        assertTextPresent("To menu menu");
        assertLinkPresentWithText("menu");
    }

    @Test
    public void testTableData(){
        preparation.run();
        clickLinkWithText("table-data");

        assertTextPresent("Table name");
        setTextField("tableName", "car");
        submit();

        assertTableEquals("",
                new String[][] {
                        {"id", "name", "color", "year"},
                        {"1", "ferrari", "red", "2002"},
                        {"2", "porsche", "black", "1964"},
                        {"3", "bmw", "blue", "2001"}});
        assertTextPresent("To menu menu");
        assertLinkPresentWithText("menu");
    }

    @Test
    public void testCreateTable(){
        String tableName = "test"+ Math.abs(new Random(100000).nextInt());
        clickLinkWithText("create-table");

        assertTextPresent("Table name");
        assertTextPresent("Column count");
        setTextField("tableName", tableName);
        setTextField("columnCount", "2");
        submit();

        assertTextPresent("Primary key name");
        assertTextPresent("Column name 1");
        assertTextPresent("Column type 1");
        setTextField("keyName", "id");
        setTextField("columnName1", "test");
        setTextField("columnType1", "text");
        submit();

        assertTextPresent("Success!");
        assertTextPresent("To menu menu");
        assertLinkPresentWithText("menu");
    }

    @Test
    public void testClearTable(){
        preparation.run();
        clickLinkWithText("clear-table");

        assertTextPresent("Table name");
        setTextField("tableName", "car");
        submit();

        assertTextPresent("Success!");
        assertTextPresent("To menu menu");
        assertLinkPresentWithText("menu");
    }

    @Test
    public void testDeleteRecord(){
        preparation.run();
        clickLinkWithText("delete-record");

        assertTextPresent("Table name");
        assertTextPresent("Primary key name");
        assertTextPresent("Primary key value");
        setTextField("tableName", "car");
        setTextField("keyName", "id");
        setTextField("keyValue", "1");
        submit();

        assertTextPresent("Success!");
        assertTextPresent("To menu menu");
        assertLinkPresentWithText("menu");
    }

    @Test
    public void testCreateRecord(){
        preparation.run();
        clickLinkWithText("create-record");

        assertTextPresent("Table name");
        setTextField("tableName", "car");
        submit();

        assertTextPresent("Column name 1");
        assertTextPresent("Column name 2");
        assertTextPresent("Column name 3");
        assertTextPresent("Column name 4");
        assertTextPresent("Column value 1");
        assertTextPresent("Column value 2");
        assertTextPresent("Column value 3");
        assertTextPresent("Column value 4");
        setTextField("columnName1", "id");
        setTextField("columnName2", "name");
        setTextField("columnName3", "color");
        setTextField("columnName4", "year");
        setTextField("columnValue1", "5");
        setTextField("columnValue2", "testName");
        setTextField("columnValue3", "testColor");
        setTextField("columnValue4", "1");
        submit();

        assertTextPresent("Success!");
        assertTextPresent("To menu menu");
        assertLinkPresentWithText("menu");
    }

    @Test
    public void testUpdateRecord(){
        preparation.run();
        clickLinkWithText("update-record");

        assertTextPresent("Table name");
        setTextField("tableName", "car");
        submit();

        assertTextPresent("Primary key name");
        assertTextPresent("Primary key value");
        assertTextPresent("Column name 1");
        assertTextPresent("Column name 2");
        assertTextPresent("Column name 3");
        assertTextPresent("Column value 1");
        assertTextPresent("Column value 2");
        assertTextPresent("Column value 3");
        setTextField("keyName", "id");
        setTextField("keyValue", "1");
        setTextField("columnName1", "name");
        setTextField("columnName2", "color");
        setTextField("columnName3", "year");
        setTextField("columnValue1", "testName");
        setTextField("columnValue2", "testColor");
        setTextField("columnValue3", "1");
        submit();

        assertTextPresent("Success!");
        assertTextPresent("To menu menu");
        assertLinkPresentWithText("menu");
    }

    @Test
    public void testCreateAndDeleteDatabase(){
        String database = "test" + Math.abs(new Random(10000).nextInt());
        clickLinkWithText("create-database");

        assertTextPresent("Database name");
        setTextField("databaseName", database);
        submit();

        assertTextPresent("Success!");
        assertTextPresent("To menu menu");
        assertLinkPresentWithText("menu");

        clickLinkWithText("menu");
        clickLinkWithText("delete-database");

        assertTextPresent("Database name");
        setTextField("databaseName", database);
        submit();

        assertTextPresent("Success!");
        assertTextPresent("To menu menu");
        assertLinkPresentWithText("menu");
    }
}