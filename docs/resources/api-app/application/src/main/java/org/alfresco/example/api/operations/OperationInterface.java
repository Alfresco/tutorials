package org.alfresco.example.api.operations;

import java.io.IOException;

import org.alfresco.example.api.Authorization;
import org.json.JSONException;
import org.json.JSONObject;

public interface OperationInterface {
    public void setTargetNode(JSONObject targetNode);

    public JSONObject getTargetNode();

    public void setResultNode(JSONObject resultNode);

    public JSONObject getResultNode();

    public void setParam1(String param1);

    public String getParam1();

    public void setParam2(String param2);

    public String getParam2();

    public void setAnswer(String answer);

    public String getAnswer();

    public void execute(Authorization authorization) throws IOException, JSONException, InterruptedException;
}
