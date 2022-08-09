package Repository;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import Entity.TestTable;
import sample.common.DynamoDbClient;

@RunWith(JUnit4.class)
public class TestTableCrudTest extends DynamoDbClient {

	private static TestTableCrud testClass;

	public TestTableCrudTest() {
		testClass = new TestTableCrud();
	}

	@Test
	public void insertRecordTest() {
		try {
			testClass.createTable();
			testClass.insertRecord();
			List<TestTable> items = testClass.scanTableWithKeyFilter("hoge");

			for(TestTable item : items)
			System.out.println(item.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			testClass.deleteTable();
		}
	}

}
