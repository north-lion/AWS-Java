package Repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BillingMode;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import Entity.TestTable;
import sample.common.DynamoDbClient;

public class TestTableCrud extends TestTable {
	// Must be init null or Blunk;
	private static AmazonDynamoDB dbClient = null;
	private static DynamoDBMapper mapper = null;
	private static DynamoDB dynamoDb = null;

	/**
	 * Constructor.
	 *
	 */
	public TestTableCrud() {
		dbClient = DynamoDbClient.getDynamoDBClient();
		dynamoDb = new DynamoDB(dbClient);
		mapper = new DynamoDBMapper(dbClient);
	}

	/**
	 * DynamoDBMapperを使用したscan.
	 * 公式doc : https://docs.aws.amazon.com/ja_jp/amazondynamodb/latest/developerguide/DynamoDBMapper.html
	 *
	 * @param key キー項目値
	 * @return フィルター結果.複数レコード
	 */
	public List<TestTable> scanTableWithKeyFilter(String key) {
		HashMap<String, AttributeValue> attrv = new HashMap<String, AttributeValue>();
		attrv.put(":placeHolder1", new AttributeValue().withS(key));
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
				.withFilterExpression(AttributeName.UserName.name() + "= :placeHolder1")
				.withExpressionAttributeValues(attrv);

		return mapper.scan(TestTable.class, scanExpression);

	}

	/**
	 * テーブルを作成する.
	 * 公式doc：https://docs.aws.amazon.com/ja_jp/sdk-for-java/v1/developer-guide/examples-dynamodb-tables.html
	 *
	 */
	public void createTable() {
		CreateTableRequest request = new CreateTableRequest()
				// 項目定義
				.withAttributeDefinitions(
						new AttributeDefinition(AttributeName.UserName.name(), ScalarAttributeType.S),
						new AttributeDefinition(AttributeName.Uuid.name(), ScalarAttributeType.S))
						/* Do Not define Other KeySchemaElement.
						new AttributeDefinition(AttributeName.Class.name(), ScalarAttributeType.S)) */
				// ハッシュ(プライマリキー）、レンジ（ソートキー）の指定
				.withKeySchema(
						new KeySchemaElement(AttributeName.UserName.name(), KeyType.HASH),
						new KeySchemaElement(AttributeName.Uuid.name(), KeyType.RANGE))
				/* DynamoDBの料金体系をプロビジョンドで作成する場合は設定が必要
				.withProvisionedThroughput(
						new ProvisionedThroughput().withReadCapacityUnits(10L).withWriteCapacityUnits(10L)) */

				// DynamoDBの料金体系をオンデマンドで作成する場合はこちらを使用
				.withBillingMode(BillingMode.PAY_PER_REQUEST)
				.withTableName(TEST_TABLE);

		dynamoDb.createTable(request);
	}

	/**
	 * テーブルを削除する.
	 *
	 */
	public void deleteTable() {
		dbClient.deleteTable(TEST_TABLE);
	}

	/**
	 * テーブルを取得する.
	 *
	 * @param tableName テーブル名
	 * @return テーブル
	 */
	private static Table getTable() {
		return dynamoDb.getTable(TEST_TABLE);
	}

	/**
	 * テーブルにデータを挿入する.
	 *
	 */
	public void insertRecord() {
		Item item = new Item()
				.withPrimaryKey(AttributeName.UserName.name(), "hoge")
				// 必ず一意のデータにしたいため、UUIDなんかを使うのがおすすめ
				.withString(AttributeName.Uuid.name(), UUID.randomUUID().toString());

		getTable().putItem(item);
	}

	/**
	 * スキャンを実行し、全件ログ出力する.
	 *
	 */
	public void scanDataOutput() {
		ScanRequest scanRequest = new ScanRequest().withTableName(TEST_TABLE);
		ScanResult result = dbClient.scan(scanRequest);

		// 拡張for文で回す場合
		for (Map<String, AttributeValue> item : result.getItems()) {
			System.out.println("item:" + item);
		}

		// イテレータで回す場合
		Iterator<Map<String, AttributeValue>> iterator = result.getItems().iterator();
		while (iterator.hasNext()) {
			Map<String, AttributeValue> item = iterator.next();

			System.out.println("item:" + item);
		}
	}

	/**
	 * クエリを実行し、クエリした結果を全件ログ出力する.
	 *
	 */
	public void queryDataOutput() {
		QuerySpec spec = new QuerySpec()
				// プライマリキーの指定は必須
				.withKeyConditionExpression(AttributeName.UserName.name() + "= :placeHolder1")
				// クエリの検索結果から、更に特定の値で絞りたい場合は、フィルターを使う
				.withFilterExpression(AttributeName.Jender.name() + "= ::placeHolder2")
				// バリューマップの設定により、プレースホルダー（仮値）を置換する。
				.withValueMap(new ValueMap()
						.withString(":placeHolder1", "hoge")
						.withString(":placeHolder2", "Knight"));

		ItemCollection<QueryOutcome> result = getTable().query(spec);
		// 拡張for文で回す場合
		for (Item item : result) {
			System.out.println("UserName:" + item.getString(AttributeName.UserName.name()));
			System.out.println("Uuid:" + item.getString(AttributeName.Uuid.name()));
			System.out.println("Class:" + item.getString(AttributeName.Jender.name()));

			// JSON文字列への変換もできそう.
			System.out.println("item:" + item.toJSONPretty());
		}

		// イテレータで回す場合
		Iterator<Item> iterator = result.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			System.out.println("UserName:" + item.getString(AttributeName.UserName.name()));
			System.out.println("Uuid:" + item.getString(AttributeName.Uuid.name()));
			System.out.println("Class:" + item.getString(AttributeName.Jender.name()));

			// JSON文字列への変換もできそう.
			System.out.println("item:" + item.toJSONPretty());
		}
	}
}
