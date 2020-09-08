package org.alfresco.example.api;

import java.io.IOException;

import org.alfresco.example.api.operations.Get;
import org.alfresco.example.api.operations.OperationInterface;
import org.json.JSONException;
import org.json.JSONObject;

public class Logic {
    private Authorization authorization;
    
    private JSONObject root;

    private Requests requests;


    //Logic is the only class with access to Authorization, so every action has to go through it first
    public Logic() throws InterruptedException, IOException, JSONException {
        this.authorization = new Authorization();
        this.requests = new Requests();

        this.root = requests.setRoot(this.authorization);
    }


    //case in point - the Controller can't call Authorization directly
    public String changeAuthorization(String acsUser, String acsPassword) throws InterruptedException, IOException, JSONException {
        String oldAcsUser = authorization.getAcsUser();
        String oldAcsPassword = authorization.getAcsPassword();

        this.authorization.changeAuth(acsUser, acsPassword);

        this.root = this.requests.setRoot(this.authorization);

        if (this.root == null){
            this.authorization.changeAuth(oldAcsUser, oldAcsPassword);

            this.root = this.requests.setRoot(this.authorization);

            return "Invalid credentials! You are logged back in as " + this.authorization.getAcsUser();
        }

        return "Authorization changed to " + this.authorization.getAcsUser();
    }


    public OperationInterface executeGet(String searchTerm) throws JSONException, InterruptedException, IOException {
        OperationInterface get = new Get(searchTerm, this.root);

        get.execute(this.authorization);

        return  get;
    }


    public OperationInterface execute(OperationInterface operation, String targetNodeName) throws InterruptedException, JSONException, IOException {

        //Get searches for targetNodeName and saves the node as resultNode
        OperationInterface get = this.executeGet(targetNodeName);

        //if nothing is found resultNode is null
        if (get.getResultNode() == null){

            return get;
        }


        //the operation gets the node to work with from Get
        operation.setTargetNode(get.getResultNode());


        operation.execute(this.authorization);


        return operation;
    }
}
