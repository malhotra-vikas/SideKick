package main.java.com.mconsultants.alexa.utilities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="UtterDevEnablers")
public class SideKickContact {
    private String email;
    private String name;

    public SideKickContact(String email, String name) {
        this.email = email;
        this.name = name;
    }

    @DynamoDBHashKey(attributeName = "Email")
    public String getEmail() {
        return email;
    }

    @DynamoDBHashKey(attributeName = "Name")
    public String getName() {
        return name;
    }
}
