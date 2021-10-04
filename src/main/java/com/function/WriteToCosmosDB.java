package com.function;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class WriteToCosmosDB {
    /**
     * This function listens at endpoint "/api/WriteToCosmosDB". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/WriteToCosmosDB
     * 2. curl {your host}/api/WriteToCosmosDB?name=HTTP%20Query
     */
    @FunctionName("WriteToCosmosDB")
    @StorageAccount("AzureBlobStorage")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BlobInput(
                name = "file", 
                dataType = "binary", 
                path = "samples-workitems/{Query.file}") 
              byte[] content,          
            @CosmosDBOutput(name = "database",
              databaseName = "demodb",
              collectionName = "democont",
              connectionStringSetting = "CosmosDBConnectionString")
            OutputBinding<String> outputItem,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Item list
        context.getLogger().info("Parameters are: " + request.getQueryParameters());

        // Parse query parameters
        String queryName = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(queryName);
        String queryFile = request.getQueryParameters().get("file");
        String file = request.getBody().orElse(queryFile);


        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            // Generate random ID
            final int id = Math.abs(new Random().nextInt());
            int contentBytes = 0;
            if (file != null && content != null && content.length > 0) {
              contentBytes = content.length; 
            } 
    
            // Generate document
            final String jsonDocument = "{\"id\":\"" + id + "\", " +
                                            "\"description\": \"" + name + "\", " +
                                            "\"contentlength\": \"" + contentBytes + "\"}";
        
            //final String jsonDocument = "{\"id\":\"" + id + "\", " +
              //                              "\"description\": \"" + name + "\"}";
        
            context.getLogger().info("Document to be saved: " + jsonDocument);
        
            // Set outputItem's value to the JSON document to be saved
            outputItem.setValue(jsonDocument);
 
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }
}
