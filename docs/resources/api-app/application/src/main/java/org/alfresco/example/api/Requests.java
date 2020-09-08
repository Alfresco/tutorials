package org.alfresco.example.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class Requests {


    //sets the root directory for Get to your Site (hard coded)
    public JSONObject setRoot(Authorization authorization) throws JSONException, IOException, InterruptedException {

        //builds request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APIs.base + APIs.nodes + APIs.root + APIs.children + APIs.relativePathToSite))
                .setHeader("Authorization", authorization.getBasicAuth())
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        //converts response into JSONOboject
        JSONObject body = new JSONObject(response.body());

        //drills down to the first node in your Document Library (should be "Contracts")
        try {
            return body.getJSONObject("list").getJSONArray("entries").getJSONObject(0).getJSONObject("entry");
        }
        catch (JSONException e){
            return null;
        }
    }


    //suffix dictates which API is called and is determined by the calling operation
    //basic API requests follow
    public HttpResponse<String> get(String suffix, Authorization authorization) throws IOException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APIs.base + suffix))
                .setHeader("Authorization", authorization.getBasicAuth())
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response;
    }


    public HttpResponse<String> post(String suffix, String requestBody, Authorization authorization) throws IOException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APIs.base + suffix))
                .setHeader("Authorization", authorization.getBasicAuth())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return client.send(request,
                HttpResponse.BodyHandlers.ofString());
    }


    public HttpResponse<String> put(String suffix, String requestBody, Authorization authorization) throws InterruptedException, IOException, JSONException {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APIs.base + suffix))
                .setHeader("Authorization", authorization.getBasicAuth())
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return client.send(request,
                HttpResponse.BodyHandlers.ofString());
    }


    public HttpResponse<String > delete(String suffix, Authorization authorization) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APIs.base + suffix))
                .setHeader("Authorization", authorization.getBasicAuth())
                .DELETE()
                .build();

        return client.send(request,
                HttpResponse.BodyHandlers.ofString());
    }
}
