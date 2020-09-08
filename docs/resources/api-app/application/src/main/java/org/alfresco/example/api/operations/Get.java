package org.alfresco.example.api.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.alfresco.example.api.APIs;
import org.alfresco.example.api.Authorization;
import org.alfresco.example.api.Requests;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Get implements OperationInterface{
    private JSONObject resultNode;

    private String searchTerm;

    private String answer;

    private List<JSONObject> queryList;
    private JSONObject relativeRoot;

    private Requests requests;


    public Get(String searchTerm, JSONObject root) throws InterruptedException, IOException, JSONException {
        this.searchTerm = searchTerm;

        this.requests = new Requests();

        this.relativeRoot = root;
        this.queryList = new ArrayList<>();
    }


    private void assambleQueryList(Authorization authorization) throws JSONException, IOException, InterruptedException {

        //gets every node inside relativeRoot
        JSONArray entries = new JSONObject(requests.get(APIs.nodes + "/" + relativeRoot.getString("id") + APIs.children,
                authorization).body())
                .getJSONObject("list").getJSONArray("entries");

        //interrupts if array is empty
        if (entries.length() == 0) {
            return;
        }

        //searches every node inside the array for param
        for (int i = 0; i < entries.length(); i++) {

            JSONObject entry = entries.getJSONObject(i).getJSONObject("entry");

            String entryName = entry.getString("name");

            //if match is found gets added to querylist
            if (entryName.contains(this.searchTerm)) {
                this.queryList.add(entry);
            }

            //if node is folder method calls itself with relativeRoot set to this node
            if (entry.getBoolean("isFolder") && !entry.getString("name").equals("Smart Folder")) {
                this.relativeRoot = entry;

                this.assambleQueryList(authorization);
            }
        }
    }


    @Override
    public void execute(Authorization authorization) throws IOException, JSONException, InterruptedException {
        this.assambleQueryList(authorization);

        List<JSONObject> list = this.queryList;

        if (list.isEmpty()) {
            this.answer = "Node not found!";

            return;
        }

        int choice = 0;

        //if only one node is returned it gets chosen without asking the user
        if (list.size() > 1) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Choose the desired node:");
            //lists every node in the queryList for the user
            for (int i = 0; i < list.size(); i++) {

                String parentNodeName = new JSONObject(requests.get(APIs.nodes
                        + list.get(i).getString("parentId"), authorization).body())
                        .getJSONObject("entry").getString("name");

                System.out.println(i + ": " + list.get(i).getString("name") + " (" + parentNodeName + ")");
            }

            choice = scanner.nextInt();

            while (choice >= list.size()) {
                System.out.println("Invalid input!");

                choice = scanner.nextInt();
            }
        }

        this.resultNode = list.get(choice);

        //formats JSONObject
        this.answer = this.resultNode.toString(4);
    }


    @Override
    public void setTargetNode(JSONObject resultNode) { }

    @Override
    public JSONObject getTargetNode() {
        return null;
    }

    @Override
    public void setResultNode(JSONObject resultNode) {
        this.resultNode = resultNode;
    }

    @Override
    public JSONObject getResultNode(){ return this.resultNode; }

    @Override
    public void setParam1(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @Override
    public String getParam1() {
        return null;
    }

    @Override
    public void setParam2(String param2) {
    }

    @Override
    public String getParam2() {
        return null;
    }

    @Override
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String getAnswer() {
        return this.answer;
    }
}
