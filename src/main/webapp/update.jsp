<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <title>sqlcmd</title>
</head>
  <body>
    <form action="update" method="post">
      <table>
        <tr>
          <td>Table name</td>
          <td><input type="text" name="tableName"/></td>
        </tr>

        <tr>
          <td>Primary key name</td>
          <td><input type="text" name="keyName"/></td>

          <td>Primary key value</td>
          <td><input type="text" name="keyValue"/></td>
        </tr>

        <c:forEach begin="1" end="3" varStatus="loop">
          <tr>
            <td>Column name${loop.count}</td>
            <td><input type="text" name="columnName${loop.count}"/></td>

            <td>Column value${loop.count}</td>
            <td><input type="text" name="columnValue${loop.count}"/></td>
          </tr>
        </c:forEach>

        <tr>
          <td></td>
          <td><input type="submit" value="update"/></td>
        </tr>
      </table>
    </form>
  </body>
</html>