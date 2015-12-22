package ua.com.juja.positiv.sqlcmd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ua.com.juja.positiv.sqlcmd.databasemanager.DatabaseException;
import ua.com.juja.positiv.sqlcmd.databasemanager.DatabaseManager;
import ua.com.juja.positiv.sqlcmd.databasemanager.UserAction;
import ua.com.juja.positiv.sqlcmd.databasemanager.UserActionsDao;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by POSITIV on 31.10.2015.
 */
@Component
public abstract class ServiceImpl implements Service {

    private List<String> commands;

    public abstract DatabaseManager getManager();

    @Autowired
    @Qualifier("userActionsDaoImpl")
    private UserActionsDao actionsDao;

    @Override
    public List<String> commandList() {
        return commands;
    }

    @Override
    public DatabaseManager connect(String database, String user, String password)
                                                        throws ServiceException {
        DatabaseManager manager = getManager();
        try {
            manager.connect(database, user, password);
            actionsDao.log(manager.getUser(),
                    manager.getDatabase(),
                    "CONNECT");
            return manager;
        } catch (DatabaseException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Set<String> getTableNames(DatabaseManager manager) {
        actionsDao.log(manager.getUser(),
                manager.getDatabase(), "GET TABLES LIST");
        return manager.getTableNames();
    }

    @Override
    public List<String> getColumnNames(DatabaseManager manager, String tableName) {
        return manager.getColumnNames(tableName);
    }

    @Override
    public List<List<String>> getTableData(DatabaseManager manager, String tableName) {
        actionsDao.log(manager.getUser(), manager.getDatabase(),
                "GET TABLE ( " + tableName + " )");
        return manager.getTableData(tableName);
    }

    @Override
    public void createTable(DatabaseManager manager, String tableName, String keyName,
                            Map<String, Object> columnParameters) {
        manager.createTable(tableName, keyName, columnParameters);
        actionsDao.log(manager.getUser(), manager.getDatabase(),
                "CREATE TABLE ( " + tableName + " )");
    }

    @Override
    public void createRecord(DatabaseManager manager, String tableName,
                             Map<String, Object> columnData) {
        manager.createRecord(tableName, columnData);
        actionsDao.log(manager.getUser(), manager.getDatabase(),
                "CREATE RECORD IN TABLE ( " + tableName + " )");
    }

    @Override
    public void updateRecord(DatabaseManager manager, String tableName,
                             String keyName, String keyValue,
                             Map<String, Object> columnData) {
        manager.updateRecord(tableName, keyName, keyValue, columnData);
        actionsDao.log(manager.getUser(), manager.getDatabase(),
                "UPDATE RECORD IN TABLE ( " + tableName + " ) KEY = " + keyValue);
    }

    @Override
    public void deleteRecord(DatabaseManager manager, String tableName,
                             String keyName, String keyValue) {
        manager.deleteRecord(tableName, keyName, keyValue);
        actionsDao.log(manager.getUser(), manager.getDatabase(),
                "DELETE RECORD IN TABLE ( " + tableName + " ) KEY = " + keyValue);
    }

    @Override
    public void clearTable(DatabaseManager manager, String tableName) {
        manager.clearTable(tableName);
        actionsDao.log(manager.getUser(), manager.getDatabase(),
                "CLEAR TABLE ( " + tableName + " )");
    }

    @Override
    public void dropTable(DatabaseManager manager, String tableName) {
        manager.dropTable(tableName);
        actionsDao.log(manager.getUser(), manager.getDatabase(),
                "DELETE TABLE ( " + tableName + " )");
    }

    @Override
    public void createBase(DatabaseManager manager, String database) {
        manager.createBase(database);
        actionsDao.log(manager.getUser(), manager.getDatabase(),
                "CREATE DATABASE ( " + database + " )");
    }

    @Override
    public void dropBase(DatabaseManager manager, String database) {
        manager.dropBase(database);
        actionsDao.log(manager.getUser(), manager.getDatabase(),
                "DELETE DATABASE ( " + database + " )");
    }

    @Override
    public String getPrimaryKey(DatabaseManager manager, String tableName)
                                                throws DatabaseException {
        return manager.getPrimaryKey(tableName);
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    @Override
    public void log(UserAction userAction) {
        actionsDao.log(userAction.getUserName(),
                userAction.getDbName(),
                userAction.getUserAction());
    }

    @Override
    public List<UserAction> getAllFor(String userName){
        if(userName == null) {
            throw new IllegalArgumentException("User name cant be null");
        }
        return actionsDao.getAllFor(userName);
    }
}
