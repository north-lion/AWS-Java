package Entity;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBTable(tableName = "TESTTABLE")
public class TestTable {

	// テーブル名
	protected static final String TEST_TABLE = "TESTTABLE";
	/**
	 * テーブル項目
	 *
	 */
	protected enum AttributeName {
		UserName,
		Uuid,
		Jender;
	}

	@DynamoDBHashKey(attributeName = "UserName")
	private String userName;
	@DynamoDBRangeKey(attributeName = "Uuid")
	private String uuid;
	// Additional properties go here.
	@DynamoDBAttribute(attributeName = "Jender")
	private String jender;

	public String toString() {
		Map<String,String> itemMap = new HashMap<>();
		for (AttributeName attrn : AttributeName.values()) {
			switch (attrn) {
			case UserName:
				itemMap.put(attrn.name(), userName);
				break;
			case Uuid:
				itemMap.put(attrn.name(), uuid);
				break;
			case Jender:
				itemMap.put(attrn.name(), jender);
				break;
			default:
				break;
			}
		}
		return itemMap.toString();
	}
}

