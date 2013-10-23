<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <META http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <title>Upload</title>
</head>
<body>
<form:form modelAttribute="uploadItem" method="post" enctype="multipart/form-data">
    <fieldset>
        <legend>Upload Fields</legend>

        <p>
            <form:label for="name" path="name">Name</form:label><br/>
            <form:input path="name"/>
        </p>

        <p>
            <form:label for="jar" path="jar">Jar</form:label><br/>
            <form:input path="jar"/>
        </p>

        <p>
            <form:label for="cls" path="cls">Class</form:label><br/>
            <form:input path="cls"/>
        </p>

        <p>
            <form:label for="pkgs" path="pkgs">Packages (package1[;package2]...)</form:label><br/>
            <form:input path="pkgs"/>
        </p>

        <p>
            <form:label for="args" path="args">Arguments</form:label><br/>
            <form:input path="args"/>
        </p>

        <p>
            <form:label for="fileData" path="fileData">File</form:label><br/>
            <form:input path="fileData" type="file"/>
        </p>

        <p>
            <input type="submit" />
        </p>

    </fieldset>
</form:form>
</body>
</html>