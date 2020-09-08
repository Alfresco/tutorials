package org.alfresco.example.api;

import java.util.Base64;

public class Authorization {

    private String acsUser;
    private String acsPassword;

    private String basicAuth;


    //sets default credentials to admin/admin
    public Authorization(){

        this.acsUser = "admin";
        this.acsPassword = "admin";

        this.basicAuth = this.buildBasicAuth();
    }


    //sets new credentials
    public void changeAuth(String acsUser, String acsPassword){
        this.acsUser = acsUser;
        this.acsPassword = acsPassword;

        this.basicAuth = this.buildBasicAuth();
    }


    //builds authorization
    private String buildBasicAuth(){
        return "Basic " + Base64.getEncoder().encodeToString((this.acsUser + ":" + this.acsPassword).getBytes());
    }

    public String getAcsUser(){ return acsUser; }

    public String getAcsPassword(){ return acsPassword; }

    public String getBasicAuth() { return basicAuth; }
}
