package com.function;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class WriteToCosmosDBbasic {
    /**
     * This function listens at endpoint "/api/WriteToCosmosDBbasic". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/WriteToCosmosDBbasic
     * 2. curl {your host}/api/WriteToCosmosDBbasic?name=HTTP%20Query
     */
    @FunctionName("WriteToCosmosDBbasic")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @CosmosDBOutput(name = "database",
            databaseName = "demodb",
            collectionName = "democont",
            connectionStringSetting = "CosmosDBConnectionString")
            OutputBinding<String> outputItem,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        // Item list
        context.getLogger().info("Parameters are: " + request.getQueryParameters());

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            // Generate random ID
            final int id = Math.abs(new Random().nextInt());
            // Generate document
            final String jsonDocument = "{\"id\":\"" + id + "\", " +
                                            "\"description\": \"" + name + "\"}";
        
            context.getLogger().info("Document to be saved: " + jsonDocument);
        
            // Set outputItem's value to the JSON document to be saved
            outputItem.setValue(jsonDocument);
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }
}
