package ua.com.juja.positiv.sqlcmd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ua.com.juja.positiv.sqlcmd.databasemanager.DatabaseException;
import ua.com.juja.positiv.sqlcmd.databasemanager.DatabaseManager;
import ua.com.juja.positiv.sqlcmd.service.Service;
import ua.com.juja.positiv.sqlcmd.service.ServiceException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by POSITIV on 30.10.2015.
 */
public class MainServlet extends HttpServlet {

    @Autowired
    private Service service;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = getAction(request);

        if (action.equals("/connect")) {
            goTo("connect", request, response);
            return;
        }

        DatabaseManager manager = (DatabaseManager) request.getSession().getAttribute("manager");

        if (manager == null) {
            redirect("connect", request, response);
            return;
        }

        try {
            if (action.equals("/create-table")) {
                goTo("table-name-column-count", request, response);

            } else if (action.equals("/table-names")) {
                getTableNames(manager, request, response);

            } else if (action.equals("/table-data")) {
                goTo("table-name", request, response);

            } else if (action.equals("/clear-table")) {
                goTo("clear-table", request, response);

            } else if (action.equals("/delete-record")) {
                goTo("delete-record", request, response);

            } else if (action.equals("/delete-table")) {
                goTo("delete-table", request, response);

            } else if (action.equals("/create-record")) {
                setAttribute("actionURL", "create-record", request);
                goTo("table-name", request, response);

            } else if (action.equals("/update-record")) {
                setAttribute("actionURL", "update-record", request);
                goTo("table-name", request, response);

            } else if (action.equals("/create-database")) {
                setAttribute("actionURL", "create-database", request);
                goTo("database-name", request, response);

            } else if (action.equals("/delete-database")) {
                setAttribute("actionURL", "delete-database", request);
                goTo("database-name", request, response);

            } else {
                goTo("error", request, response);
            }
        } catch (Exception e) {
            error(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String action = getAction(request);

        DatabaseManager manager = (DatabaseManager) request.getSession().getAttribute("manager");

        try {
            if (action.equals("/connect")) {
                connect(request, response);

            } else if (action.equals("/table-data")) {
                getTableData(manager, request, response);

            } else if (action.equals("/clear-table")) {
                clearTable(manager, request, response);

            } else if (action.equals("/delete-record")) {
                deleteRecord(manager, request, response);

            } else if (action.equals("/create-record")) {
                setColumnCountAndTableName(request, manager);
                goTo("create-record", request, response);

            } else if (action.equals("/create-database")) {
                createDatabase(manager, request, response);

            } else if (action.equals("/create")) {
                createRecord(manager, request, response);

            } else if (action.equals("/delete-database")) {
                deleteDatabase(manager, request, response);

            } else if (action.equals("/delete-table")) {
                deleteTable(manager, request, response);

            } else if (action.equals("/column-parameters")) {
                setColumnCountAndTableName(request);
                goTo("create-table", request, response);

            } else if (action.equals("/create-table")) {
                createTable(manager, request, response);

            } else if (action.equals("/update-record")) {
                setColumnCountAndTableName(request, manager);
                goTo("update-record", request, response);

            } else if (action.equals("/update")) {
                updateRecord(manager, request, response);
            }
        } catch (Exception e) {
            error(request, response, e);
        }
    }

    private void setColumnCountAndTableName(HttpServletRequest request, DatabaseManager manager)
            throws Exception {
        setAttribute("columnCount", getColumnCount(manager, getParameter("tableName", request)), request);
        setAttribute("tableName", getParameter("tableName", request), request);
    }


    private void setColumnCountAndTableName(HttpServletRequest request) {
        setAttribute("tableName", getParameter("tableName", request), request);
        setAttribute("columnCount", getParameter("columnCount", request), request);
    }

    private void setAttribute(String attributeName, Object attributeValue, HttpServletRequest request) {
        request.setAttribute(attributeName, attributeValue);
    }

    private String getParameter(String parameterName, HttpServletRequest request) {
        return request.getParameter(parameterName);
    }

    private int getColumnCount(DatabaseManager manager, String tableName) throws DatabaseException {
        return Integer.parseInt(manager.getTableData(tableName).get(0));
    }

    private String getAction(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.substring(request.getContextPath().length(), requestURI.length());
    }

    private void error(HttpServletRequest request, HttpServletResponse response, Exception e) {
        setAttribute("message", e.getMessage(), request);
        goTo("error", request, response);
    }

    private void goTo(String jsp, HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher(jsp + ".jsp").forward(request, response);
        } catch (Exception e) {
            error(request, response, e);
        }
    }

    private void redirect(String url, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect(response.encodeRedirectURL(url));
        } catch (Exception e) {
            error(request, response, e);
        }
    }

    private void createTable(DatabaseManager manager, HttpServletRequest request,
                             HttpServletResponse response) {
        int columnCount = Integer.parseInt(getParameter("columnCount", request));
        Map<String, Object> data = getData(
                "columnName",
                "columnType",
                columnCount - 1,
                request);

        try {
            manager.createTable(getParameter("tableName", request), getParameter("keyName", request), data);
            goTo("success", request, response);
        } catch (DatabaseException e) {
            error(request, response, e);
        }
    }

    private void updateRecord(DatabaseManager manager, HttpServletRequest request,
                              HttpServletResponse response) {
        String tableName = getParameter("tableName", request);
        try {
            Map<String, Object> data = getData(
                    "columnName",
                    "columnValue",
                    getColumnCount(manager, tableName) - 1,
                    request);

            manager.updateRecord(tableName, getParameter("keyName", request), getParameter("keyValue", request), data);
            goTo("success", request, response);
        } catch (DatabaseException e) {
            error(request, response, e);
        }
    }

    private void createRecord(DatabaseManager manager, HttpServletRequest request,
                              HttpServletResponse response) {
        String tableName = getParameter("tableName", request);
        try {
            Map<String, Object> data = getData(
                    "columnName",
                    "columnValue",
                    getColumnCount(manager, tableName),
                    request);

                manager.createRecord(tableName, data);
            goTo("success", request, response);
        } catch (DatabaseException e) {
            error(request, response, e);
        }
    }

    private void deleteDatabase(DatabaseManager manager, HttpServletRequest request,
                                HttpServletResponse response) {
        try {
            manager.dropBase(getParameter("databaseName", request));
            goTo("success", request, response);
        } catch (DatabaseException e) {
            error(request, response, e);
        }
    }

    private void createDatabase(DatabaseManager manager, HttpServletRequest request,
                                HttpServletResponse response) {
        try {
            manager.createBase(getParameter("databaseName", request));
            goTo("success", request, response);
        } catch (DatabaseException e) {
            error(request, response, e);
        }
    }

    private void deleteTable(DatabaseManager manager, HttpServletRequest request, HttpServletResponse response) {
        try {
            manager.dropTable(getParameter("tableName", request));
            goTo("success", request, response);
        } catch (DatabaseException e) {
            error(request, response, e);
        }
    }

    private void deleteRecord(DatabaseManager manager, HttpServletRequest request,
                              HttpServletResponse response) {
        try {
            manager.deleteRecord(
                    getParameter("tableName", request),
                    getParameter("keyName", request),
                    getParameter("keyValue", request));
            goTo("success", request, response);
        } catch (DatabaseException e) {
            error(request, response, e);
        }
    }

    private void clearTable(DatabaseManager manager, HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            manager.clearTable(getParameter("tableName", request));
            goTo("success", request, response);
        } catch (DatabaseException e) {
            error(request, response, e);
        }
    }

    private void getTableNames(DatabaseManager manager, HttpServletRequest request,
                               HttpServletResponse response) {
        try {
            setAttribute("tables", manager.getTableNames(), request);
            goTo("table-names", request, response);
        } catch (DatabaseException e) {
            error(request, response, e);
        }
    }

    private void getTableData(DatabaseManager manager, HttpServletRequest request,
                              HttpServletResponse response) {
        String tableName = getParameter("tableName", request);
        try {
            setAttribute("table", service.getTableData(manager, tableName), request);
            goTo("table-data", request, response);
        } catch (ServiceException e) {
            error(request, response, e);
        }
    }

    private void connect(HttpServletRequest request, HttpServletResponse response) {
        try {
            DatabaseManager manager = service.connect(
                    getParameter("database", request),
                    getParameter("user", request),
                    getParameter("password", request));
            request.getSession().setAttribute("manager", manager);
            redirect("menu", request, response);
        } catch (Exception e) {
            error(request, response, e);
        }
    }

    private Map<String, Object> getData(String key, String value,
                                        int columnCount, HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();
        for (int index = 0; index < columnCount; index++) {
            int columnIndex = index + 1;
            data.put(getParameter(key + columnIndex, request),
                    getParameter(value + columnIndex, request));
        }
        return data;
    }
}