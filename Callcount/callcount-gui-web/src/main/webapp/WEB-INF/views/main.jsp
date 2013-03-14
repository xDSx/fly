<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
    <a href="/upload">Upload</a>
    <table>
        <tr>
            <th>Row</th>
            <th>Project</th>
            <th>Download</th>
        </tr>
        <c:forEach var="proj" items="${projects.projects}" varStatus="counter">
            <tr>
                <td>${counter.index + 1}</td>
                <td>${proj.name}</td>
                <td>
                    <a href="/download?name=${proj.name}">download</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
