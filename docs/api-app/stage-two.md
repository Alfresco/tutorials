# Expanding your application

This tutorial will show you how to expand your base application by adding new useful features.


## Prerequisites

To continue, you need to complete the [API- Application (not yet linked)]() tutorial first as this guide will build on the accomplishments you made.

## What’s the plan

In the last part of this series, you built the basic classes for an application that would communicate with an ACS instance via the Alfresco APIs.  
You also created the first operation class called `Get`, which returned information on a node you chose within a site you created called *myInc*.

This tutorial will focus on fleshing out that application by adding the following operation classes:
- Create a node
- Rename a node
- Delete a node

As practiced in the last part, every chapter will start with the class’s imports you will build in the respective section.


## Create a node
 
```
import org.alfresco.example.api.APIs;
import org.alfresco.example.api.Authorization;
import org.alfresco.example.api.Requests;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
```

*Note: Each of the following classes belongs in the* operations *folder, where you already created the `OperationInterface`.*

As discussed when we created the OperationInterface, an operation class needs to have a `targetNode`, a `resultNode`, or both.  
The `targetNode` of this class will be the `root` attribute of the Logic class, which determines where the new node is created. More on `root` later.  
The `resultNode` will be the newly created node.  
Next up are the two String parameters, here `nodeName` and `nodetype`.
Also, we need the `answer` attribute to return a response to the user, and every operation class has access to its own `Requests` instance.  
All of this is set within the Constructor of the Create class. The exception being `targetNode` and `resultNode`. Because the former is set by `Logic` after executing a `Get` instance, and the latter is set by the `execute()` method.  

```
private JSONObject targetNode;
private JSONObject resultNode;

private String nodeName;
private String nodeType;

private String answer;

private Requests requests;


public Create(String nodeName, String nodeType){
    this.nodeName = nodeName;
    this.nodeType = nodeType;

    this.requests = new Requests();
}
```

The `OperationInterface` also dictates an `execute()` method, which runs the operation the class is designed for.  
Here it begins by checking which `nodeType` the new node will have.  
We will come to the reason for this in a bit, but for now it is important to know that after the Constructor was called, the `nodeType` can have one of two values: 
- *ct:contract*
- *-f*

*Ct:contract* is already a valid `nodeType` and can therefore stay unchanged, should the value be “*-f*”. However, it means that the new node is supposed to be a folder, so we change the `nodeType` to *cm:folder*.

Next, we will create a JSONObject, which you can picture as a table with two columns, titled *key* and *value*.  
Our two keys are name and `nodeType`. They have to be named exactly that. The API won’t understand our request later on.  
We are coupling our two keys with the values of `nodeName` and `nodeType`, and converting the JSONObject to a String the API will understand.

Next, we will call the `post()` method of our `Requests` instance and build our API `suffix` out of the parts provided by the `APIs` interface and the ID of our `targetNode`.  
The second parameter is the requestBody containing the information the API needs to create a new node.  
And lastly, we are passing along the `Authorization` instance to enable `post()` to log into ACS.

The last step is to assign the new node, which is returned by the API, to the `resultNode` attribute and form an answer to communicate to the user that the operation was successful.

If the JSONObject `entry` can’t be found within the response, it means that something went wrong. For example, the user was trying to create a duplicate.  
Should that be the case, our method will look for the property `briefSummary`, which is always part of an error response. You can see a sample below:

```
{
    "error": {
        "errorKey": "Duplicate child name not allowed: sample.txt",
        "statusCode": 409,
        "briefSummary": "08170000 Duplicate child name not allowed: sample.txt",
        "stackTrace": "For security reasons the stack trace is no longer displayed, but the property is kept for previous versions",
        "descriptionURL": "https://api-explorer.alfresco.com"
    }
}
```

Also, you can copy in the getters and setters every operation class has to implement.

```
@Override
public void setTargetNode(JSONObject targetNode) { this.targetNode = targetNode; }

@Override
public JSONObject getTargetNode() { return new JSONObject(); }

@Override
public void setResultNode(JSONObject resultNode){ this.resultNode = resultNode; }

@Override
public JSONObject getResultNode() { return null; }

@Override
public void setParam1(String nodeName) { this.nodeName = nodeName; }

@Override
public String getParam1() { return null; }

@Override
public void setParam2(String nodeType) { this.nodeType = nodeType; }

@Override
public String getParam2() { return this.nodeType; }

@Override
public void setAnswer(String answer) { this.answer = answer; }

@Override
public  String getAnswer(){ return answer; }
```

## Rename a node

```
import org.alfresco.example.api.APIs;
import org.alfresco.example.api.Authorization;
import org.alfresco.example.api.Requests;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
```

As always, we will begin with the attributes.  
The `targetNode` and `resultNode` of Rename represent the original and the renamed node within ACS.
Also, we have a String parameter called `reName` which will be the new name of the node. It is bad practice to begin any variable with *new*. That’s why we didn’t call it `newName`.  
`Answer` and `requests` are as always also present.  
Next, the String parameter and requests will be set or initialized respectively.

```
private JSONObject targetNode;
private JSONObject resultNode;

private String reName;

private String answer;

private Requests requests;


public Rename(String reName){
    this.reName = reName;

    this.requests = new Requests();
}
```


The `execute()` method is very similar to the one of `Create`:   
It builds a `requestBody` out of the attribute of the class but calls the `put()` method of requests this time.  
Again we build our `suffix` from the `APIs` interface and the ID of the `targetNode` and send along the `requestBody` and `authorization`.  
Also we try to assign the returned node to the `resultNode` and form an `answer`, and if it doesn’t work, we return the `briefSummary` property of our error response.

```
@Override
public void execute(Authorization authorization) throws IOException, JSONException, InterruptedException {

    JSONObject valueJson = new JSONObject();
    valueJson.put("name", reName);
    String requestBody = valueJson.toString();

    HttpResponse<String> response = requests.put(APIs.nodes + "/" + this.targetNode.getString("id"), requestBody, authorization);

    try {
        this.resultNode = new JSONObject(response.body()).getJSONObject("entry");

        answer = targetNode.getString("name") + " -> " + this.resultNode.getString("name");
    }
    catch (JSONException e) {
        answer = new JSONObject(response.body()).getJSONObject("error").getString("briefSummary");
    }
}

@Override
public JSONObject getTargetNode() {return this.targetNode; }

@Override
public void setTargetNode(JSONObject targetNode) { this.targetNode = targetNode; }

@Override
public void setResultNode(JSONObject resultNode){ this.resultNode = resultNode; }

@Override
public JSONObject getResultNode() { return this.resultNode; }

@Override
public void setParam1(String reName) { this.reName = reName; }

@Override
public String getParam1() { return reName; }

@Override
public void setParam2(String param2) { }

@Override
public String getParam2() { return null; }

@Override
public void setAnswer(String answer) { this.answer = answer; }

@Override
public  String getAnswer(){ return answer; }
```


## Delete a Node

```
import org.alfresco.example.api.APIs;
import org.alfresco.example.api.Authorization;
import org.alfresco.example.api.Requests;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
```

And finally, we will create a class to delete any node within your site.  
This class is minimalistic as it only needs a `targetNode` to work, plus the mandatory `answer` and `Requests` instance.  
In the Constructor, you only need to initialize `requests`.

The `execute()` method is also not very extensive:  
It only calls the `delete()` method of `requests` with the `suffix` *nodes/[nodeId]* and `authorization`.

But there is one difference to other operation classes in the `try catch()` statement. As the delete API returns an empty response, `execute()` has nothing to check whether the operation was successful or not.  
So it checks if the operation failed first by looking for the `briefSummary` property. If there is none, that means the node was deleted.


## Additional changes

Now that you have your operation classes, let’s tie them into the application to use them.


## Logic

Since the `execute()` method of Logic creates an instance of `Get` to provide a `targetNode`, we will have to create a separate `execute()` method for `Create`.  
This method has only one parameter, which is the operation we want to execute.

The `targetNode` of `Create` will be provided by `Logic` itself, it will use the `root` attribute for that so that every new node will be created within your site.  
From this point forward, this method is identical to its counterpart: Create gets executed and returned.

```
public OperationInterface execute(OperationInterface operation) throws JSONException, InterruptedException, IOException {
    operation.setTargetNode(this.root);

    operation.execute(authorization);

    return operation;
}
```
 

## Controller

First we will build the create case **ct**:  
It will assign the second part of the user's command to the `targetNodeName` variable and hardcode the `nodeType` to *ct:contract*.  
In case the user enters the flag to create a folder (**-f**) or some other type, like *cm:content*,  the `Controller` will pass it on as `nodeType`.
Then it will print the `answer` `Create` provides.

 ```
case "ct":

    String targetNodeName = commandParts[1];
    String nodeType = "ct:contract";

    if (commandParts.length == 3) {
        nodeType = commandParts[2];
    }

    System.out.println(logic.execute(new Create(targetNodeName, nodeType)).getAnswer());

    break;
 ```

Rename (**rn**) and delete (**dlt**) both follow the same pattern:  
They first check the length of the command, then assign the `targetNodeName` and rename the parameter variable.  
After that they execute their respective operation and print the `answer`.  
Or print an error message in case the length isn’t on par.

```
case "rn":

    if (commandParts.length == 3) {
        targetNodeName = commandParts[1];
        String reName = commandParts[2];

        System.out.println(logic.execute(new Rename(reName), targetNodeName).getAnswer());
    }
    else {

        System.out.println("Invalid length of command!");
    }

    break;


case "dlt":

    if (commandParts.length == 2) {
        targetNodeName = commandParts[1];

        System.out.println(logic.execute(new Delete(), targetNodeName).getAnswer());
    }
    else {

        System.out.println("Invalid length of command!");
    }

    break;
```


## Summary

Now that you have expanded your application you are actually able to manage your site within the ACS instance to a certain extent.

You can now create, rename, and delete nodes.

Also, you are able to write basic operation classes for your application.


## Next Steps

Now that you know the basic workings of an operation class for your application, maybe you could write one yourself.  
For that, we recommend familiarizing yourself with Alfresco APIs and what they can do [here]((https://api-explorer.alfresco.com/api-explorer/#/)).

If you want a more guided experience creating new features, check out [Part 3 (not yet linked)]() of this series, where we will create the following operations:  
- Get the content 
- Edit the content 
- Comment a node
- Lock and unlock 
- Update the properties of a node
- And add the “employee” aspect to a node

