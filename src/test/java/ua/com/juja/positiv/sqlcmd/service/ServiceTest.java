package ua.com.juja.positiv.sqlcmd.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ua.com.juja.positiv.sqlcmd.dao.databasemanager.DatabaseException;
import ua.com.juja.positiv.sqlcmd.dao.databasemanager.DatabaseManager;
import ua.com.juja.positiv.sqlcmd.dao.databasemanager.PostgreDatabaseManager;
import ua.com.juja.positiv.sqlcmd.dao.entity.UserAction;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by POSITIV on 27.11.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = ("classpath:test-application-context.xml"))
public class ServiceTest {

    @Autowired
    private Service service;

    private DatabaseManager manager;

    @Test
    public void testCommandList() {
        assertEquals("[connect, create-table, " +
                      "tables, " +
                      "create-database, " +
                      "delete-database]", service.commandList().toString());
    }

    @Test
    public void testConnect() throws ServiceException {
        manager = service.connect("sqlcmd", "postgres", "postgres");
        assertNotNull(manager);
    }

    @Test(expected = ServiceException.class)
    public void testConnect_WithIncorrectData() throws ServiceException {
        service.connect("qwe", "qwe", "qwe");
    }

    @Test
    public void testAllFor() throws ServiceException {
        manager = new PostgreDatabaseManager();
        service.connect("sqlcmd", "postgres", "postgres");
        List<UserAction> action = service.getAllFor("postgres");
        assertEquals("sqlcmd | postgres | CONNECT", action.get(0).getDbName() +  " | " +
                                                    action.get(0).getUserName() + " | " +
                                                    action.get(0).getUserAction());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAllFor_WithNullName() throws ServiceException {
        service.getAllFor(null);
    }

    @Test
    public void testLogger() throws DatabaseException, SQLException, ServiceException {
        manager = new PostgreDatabaseManager();
        manager.connect("sqlcmd_log", "postgres", "postgres");
        manager.clearTable("user_actions");
        DatabaseManager mockManager = mock(PostgreDatabaseManager.class);
        when(mockManager.getDatabase()).thenReturn("sqlcmd");
        when(mockManager.getUser()).thenReturn("postgres");

        service.connect("sqlcmd", "postgres", "postgres");
        service.clearTable(mockManager, "mockTable");
        service.getTableNames(mockManager);
        service.getTableData(mockManager, "mockTable");
        service.createBase(mockManager, "mockDatabase");
        service.dropBase(mockManager, "mockDatabase");
        service.deleteRecord(mockManager, "mockTable", "mockKeyName", "mockKeyValue");
        service.dropTable(mockManager, "mockTable");
        service.createRecord(mockManager, "mockTable", new HashMap<String, Object>());
        service.createTable(mockManager, "mockTable", "mockKeyName", new HashMap<String, Object>());
        service.updateRecord(mockManager, "mockTable", "mockKeyName", "mockKeyValue",
                                                      new HashMap<String, Object>());

        manager.connect("sqlcmd_log", "postgres", "postgres");
        List<List<String>> userActions = manager.getTableData("user_actions");
        for (List<String> row : userActions) {
            row.remove(0);
        }
        assertEquals(
                "[[sqlcmd, postgres, CONNECT], " +
                "[sqlcmd, postgres, CLEAR TABLE ( mockTable )], " +
                "[sqlcmd, postgres, GET TABLES LIST], " +
                "[sqlcmd, postgres, GET TABLE ( mockTable )], " +
                "[sqlcmd, postgres, CREATE DATABASE ( mockDatabase )], " +
                "[sqlcmd, postgres, DELETE DATABASE ( mockDatabase )], " +
                "[sqlcmd, postgres, DELETE RECORD IN TABLE ( mockTable ) KEY = mockKeyValue], " +
                "[sqlcmd, postgres, DELETE TABLE ( mockTable )], " +
                "[sqlcmd, postgres, CREATE RECORD IN TABLE ( mockTable )], " +
                "[sqlcmd, postgres, CREATE TABLE ( mockTable )], " +
                "[sqlcmd, postgres, UPDATE RECORD IN TABLE ( mockTable ) KEY = mockKeyValue]]",
                                                                     userActions.toString());
    }
}
