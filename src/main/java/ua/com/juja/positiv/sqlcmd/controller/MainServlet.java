package ua.com.juja.positiv.sqlcmd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.juja.positiv.sqlcmd.databasemanager.DatabaseManager;
import ua.com.juja.positiv.sqlcmd.service.Service;
import ua.com.juja.positiv.sqlcmd.service.ServiceException;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by POSITIV on 11.12.2015.
 */
@Controller
public class MainServlet {

    @Autowired
    private Service service;

    @RequestMapping(value = {"/clear-table", "/delete-table",
                             "update-record", "/create-record"}, method = RequestMethod.GET)
    public String tableName() {
        return "table-name";
    }

    @RequestMapping(value = {"/create-database", "/delete-database"}, method = RequestMethod.GET)
    public String databaseName() {
        return "database-name";
    }

    @RequestMapping(value = {"/menu", "/"}, method = RequestMethod.GET)
    public String menu(ModelMap model) {
        model.put("items", service.commandList());
        return "menu";
    }

    @RequestMapping(value = "/connect", method = RequestMethod.GET)
    public String connect(HttpSession session) {
        if (getManager(session) == null || getManager(session) == DatabaseManager.NULL) {
            return "connect";
        }
        return "redirect:menu";
    }

    @RequestMapping(value = "/connect", method = RequestMethod.POST)
    public String connecting(Model model, HttpSession session,
                             @RequestParam(value = "database") String database,
                             @RequestParam(value = "user") String user,
                             @RequestParam(value = "password") String password) {
        try {
            DatabaseManager manager = service.connect(database, user, password);
            session.setAttribute("manager", manager);
            return "redirect:menu";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    @RequestMapping(value = "/tables", method = RequestMethod.GET)
    public String tableNames(Model model, HttpSession session) {
        try {
            model.addAttribute("list", getManager(session).getTableNames());
            return "tables";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    @RequestMapping(value = "/tables/{tableName}", method = RequestMethod.GET)
    public String tableData(Model model, HttpSession session,
                            @PathVariable(value = "tableName") String tableName) {
        try {
            model.addAttribute("table", service.getTableData(getManager(session), tableName));
            return "table-data";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    @RequestMapping(value = "/clear-table", method = RequestMethod.POST)
    public String clearingTable(Model model, HttpSession session,
                                @RequestParam(value = "tableName") String tableName) {
        try {
            getManager(session).clearTable(tableName);
            return "success";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    @RequestMapping(value = "/delete-record", method = RequestMethod.GET)
    public String deleteRecord() {
        return "delete-record";
    }

    @RequestMapping(value = "/delete-record", method = RequestMethod.POST)
    public String deletingRecord(Model model, HttpSession session,
                                 @RequestParam(value = "tableName") String tableName,
                                 @RequestParam(value = "keyName") String keyName,
                                 @RequestParam(value = "keyValue") String keyValue) {
        try {
            getManager(session).deleteRecord(tableName, keyName, keyValue);
            return "success";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    @RequestMapping(value = "/create-record", method = RequestMethod.POST)
    public String createRecord(Model model, HttpSession session,
                               @RequestParam(value = "tableName") String tableName) {
        try {
            model.addAttribute("columnCount", getColumnCount(session, tableName));
            model.addAttribute("tableName", tableName);
            return "create-record";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String creatingRecord(Model model, HttpSession session,
                                 @RequestParam Map<String,String> allRequestParams) {
        String tableName = allRequestParams.remove("tableName");
        Map<String, Object> data = getData(allRequestParams);
        try {
            getManager(session).createRecord(tableName, data);
            return "success";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    @RequestMapping(value = "/delete-table", method = RequestMethod.POST)
    public String deleteTable(Model model, HttpSession session,
                              @RequestParam(value = "tableName") String tableName) {
        try {
            getManager(session).dropTable(tableName);
            return "success";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    @RequestMapping(value = "/delete-database", method = RequestMethod.POST)
    public String deleteDatabase(Model model, HttpSession session,
                                 @RequestParam(value = "databaseName") String databaseName) {
        try {
            getManager(session).dropBase(databaseName);
            return "success";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    @RequestMapping(value = "/create-database", method = RequestMethod.POST)
    public String createDatabase(Model model, HttpSession session,
                                 @RequestParam(value = "databaseName") String databaseName) {
        try {
            getManager(session).createBase(databaseName);
            return "success";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    @RequestMapping(value = "/update-record", method = RequestMethod.POST)
    public String updateRecord(Model model, HttpSession session,
                               @RequestParam(value = "tableName") String tableName) {
        try {
            model.addAttribute("columnCount", getColumnCount(session, tableName));
            model.addAttribute("tableName", tableName);
            return "update-record";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updatingRecord(Model model, HttpSession session,
                                 @RequestParam Map<String,String> allRequestParams) {
        try {
            String tableName = allRequestParams.remove("tableName");
            String keyName = allRequestParams.remove("keyName");
            String value = allRequestParams.remove("keyValue");
            Map<String, Object> data = getData(allRequestParams);
            getManager(session).updateRecord(tableName, keyName, value, data);
            return "success";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    @RequestMapping(value = "/create-table", method = RequestMethod.GET)
    public String tableSize() {
        return "table-name-column-count";
    }

    @RequestMapping(value = "/column-parameters", method = RequestMethod.POST)
    public String createTable(Model model,
                             @RequestParam(value = "tableName") String tableName,
                             @RequestParam(value = "columnCount") String columnCount) {
        model.addAttribute("tableName", tableName);
        model.addAttribute("columnCount", columnCount);
        return "create-table";
    }

    @RequestMapping(value = "/create-table", method = RequestMethod.POST)
    public String creatingTable(Model model, HttpSession session,
                                 @RequestParam Map<String,String> allRequestParams) {
        String tableName = allRequestParams.remove("tableName");
        String keyName = allRequestParams.remove("keyName");
        Map<String, Object> data = getData(allRequestParams);
        try {
            getManager(session).createTable(tableName, keyName, data);
            return "success";
        } catch (Exception e) {
            return error(model, e);
        }
    }

    private Map<String, Object> getData(@RequestParam Map<String, String> allRequestParams) {
        Map<String, Object> data = new LinkedHashMap<>();
        Iterator<Map.Entry<String, String>> iterator = allRequestParams.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> pair = iterator.next();
            String key = pair.getValue();
            pair = iterator.next();
            String value = pair.getValue();
            data.put(key, value);
        }
        return data;
    }

    private int getColumnCount(HttpSession session,
                               @RequestParam(value = "tableName") String tableName)
                                                                 throws ServiceException {
        List<List<String>> tableData = service.getTableData(getManager(session), tableName);
        return tableData.get(0).size();
    }

    private DatabaseManager getManager(HttpSession session) {
        DatabaseManager manager = (DatabaseManager) session.getAttribute("manager");
        if(manager == null) {
            return DatabaseManager.NULL;
        }
        return manager;
    }

    private String error(Model model, Exception e) {
        model.addAttribute("message", e.getMessage());
        return "error";
    }
}
