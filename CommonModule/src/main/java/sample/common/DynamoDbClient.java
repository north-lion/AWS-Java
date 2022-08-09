package sample.common;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

public class DynamoDbClient {

	private static AmazonDynamoDB dbClient = null;

	/**
	 * コンストラクタ
	 */
	public DynamoDbClient() {

		dbClient = AmazonDynamoDBClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "local"))
				.build();

	}
	
	public static AmazonDynamoDB getDynamoDBClient() {
		return dbClient;
	}
	
}
