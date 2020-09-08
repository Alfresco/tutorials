package org.alfresco.example.api;

import java.io.IOException;
import java.util.Scanner;

import org.json.JSONException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Controller implements ApplicationRunner{

	public static void main(String[] args) throws IOException, InterruptedException, JSONException {
		

		System.out.println("Use any of the following commands:");
        System.out.println();
        System.out.println("auth [userName] - change credentials to specified user");
        System.out.println("get [nodeName] - get information on specified node");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        Logic logic = new Logic();

        String command = "";

        while (!command.equals("exit")){

            //Waiting for command
            command = scanner.nextLine();

            //splitting command into processable bits
            String[] commandParts = command.split(" ");

            String operationCommand = commandParts[0];

            if (commandParts.length < 2 || commandParts.length > 3){
                if (operationCommand.equals("exit")){
                    System.out.println("Bye bye");

                    continue;
                }

                System.out.println("Invalid length of command!");

                continue;
            }


            //determine operation
            switch (operationCommand) {
                case "auth":
                    if (commandParts.length == 3) {

                        String acsUser = commandParts[1];
                        String acsPassword = commandParts[2];

                        System.out.println(logic.changeAuthorization(acsUser, acsPassword));
                    }
                    else {

                        System.out.println("Invalid length of command!");
                    }

                    break;


                //returns every node related to searchTerm
                case "get":
                    if (commandParts.length == 2) {
                        String targetNodeName = commandParts[1];

                        System.out.println(logic.executeGet(targetNodeName).getAnswer());
                    }

                    break;


                default:
                    System.out.println("'" + operationCommand + "' is not recognized as an internal or external command");
                    
                    break;
            }
        }
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// TODO Auto-generated method stub

	}

}
