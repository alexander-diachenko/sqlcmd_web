package ua.com.juja.positiv.sqlcmd.service;

import org.springframework.stereotype.Component;
import ua.com.juja.positiv.sqlcmd.databasemanager.DatabaseManager;
import ua.com.juja.positiv.sqlcmd.databasemanager.UserAction;

import java.util.List;

/**
 * Created by POSITIV on 31.10.2015.
 */
@Component
public interface Service {

    List<String> commandList();

    DatabaseManager connect(String database, String user, String password)
            throws ServiceException;

    void log(UserAction userAction);

    List<UserAction> getAllFor(String userName);
}
