package ua.com.juja.positiv.sqlcmd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.com.juja.positiv.sqlcmd.dao.databasemanager.DatabaseManager;
import ua.com.juja.positiv.sqlcmd.service.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 * Created by POSITIV on 11.01.2016.
 */
@RestController
public class RestService {

    @Autowired
    private Service service;

    @RequestMapping(value = "/menu/content", method = RequestMethod.GET)
    public List<String> menuItems() {
        return service.commandList();
    }

    @RequestMapping(value = "/tables/content", method = RequestMethod.GET)
    public Set<String> tableNames(HttpSession session) {
        return service.getTableNames(getManager(session));
    }


    @RequestMapping(value = "tables/car/content", method = RequestMethod.GET)
    public List<List<String>> tableData(HttpSession session)
    {
        return service.getTableData(getManager(session), "car");
    }

    private DatabaseManager getManager(HttpSession session) {
        DatabaseManager manager = (DatabaseManager) session.getAttribute("manager");
        if (manager == null) {
            return DatabaseManager.NULL;
        }
        return manager;
    }

}
