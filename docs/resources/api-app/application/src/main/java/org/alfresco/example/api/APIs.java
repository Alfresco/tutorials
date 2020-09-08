package org.alfresco.example.api;

public interface APIs {
    public static final String base = "http://localhost:8080/alfresco/api/-default-/public/alfresco/versions/1";

    public static final String nodes = "/nodes";
    public static final String people = "/people";
    public static final String sites = "/sites";
    public static final String root = "/-root-";

    public static final String relativePathToSite = "?relativePath=%2FSites%2Fmyinc%2FdocumentLibrary";

    public static final String content = "/content";
    public static final String comments = "/comments";
    public static final String children = "/children";
    public static final String lock = "/lock";
    public static final String unlock = "/unlock";
    public static final String action = "/action-executions";
}
