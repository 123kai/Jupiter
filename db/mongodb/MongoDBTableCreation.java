package db.mongodb;

import org.bson.Document;   
//bson 存储格式   
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class MongoDBTableCreation {
	// Run as Java application to create MongoDB collections with index.
	public static void main(String[] args) {

		// Step 1: Connection to MongoDB
		MongoClient mongoclient = new MongoClient(); // 括号里写不写"localhost: 27017"都行， 默认值
		MongoDatabase db = mongoclient.getDatabase(MongoDBUtil.DB_NAME);

		// Step 2: remove old collections.
		db.getCollection("users").drop();
		db.getCollection("items").drop();

		// Step 3: create new collections.
		// unique 限制不能为空
		// 排序 1 从小到大， －1从大到小
		IndexOptions indexOptions = new IndexOptions().unique(true);
		db.getCollection("users").createIndex(new Document("user_id", 1), indexOptions);
		db.getCollection("items").createIndex(new Document("item_id", 1), indexOptions);

		// Step 4: insert fake user data
		db.getCollection("users").insertOne(new Document().append("first_name", "john").append("last_name", "smith")
				.append("user_id", "1111").append("password", "3229c1097c00d497a0fd282d586be050"));

		mongoclient.close();

		System.out.println("Import is done successfully.");
	}
}
