
package offline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;

import db.mongodb.MongoDBUtil;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

//Purify 的作用就是把Tomcat内的数据转换成MongoDB的格式储存
public class Purify {
	public static void main(String[] args) {
		//21, 22 连接MongoDB
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
                             // Switch to your own path
		String fileName = "/Users/hekai/Downloads/tomcat_log.txt";    

		try {
			db.getCollection("logs").drop();
			
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				// Sample input: 
				// 73.223.210.212 - - [19/Aug/2017:22:00:24 +0000] "GET /Titan/history?user_id=1111 HTTP/1.1" 200 11410
				
                List<String> values = Arrays.asList(line.split(" ")); //按照空格分隔
				
				String ip = values.size() > 0 ? values.get(0) : null;
				String timestamp = values.size() > 3 ? values.get(3) : null;
				String method = values.size() > 5 ? values.get(5) : null;
				String url = values.size() > 6 ? values.get(6) : null;
				String status = values.size() > 8 ? values.get(8) : null;
                 
				// 提取时间段，不考虑日期，提取方法是使用正则表达式 
				// '\\'告诉java普通符号，'\['告诉正则表达式普通'['，不需要做特殊处理  
				//  '.'匹配任意字符，'+'之前字符的一个或者多个, '.\'匹配任意长度的字符串, '?'告诉'.+'匹配地长度尽量短
				// 默认隐藏()"(\\[(.+?):(.+))"
				// group(1) = 19/Aug/2017, group(2) = 22:00:24
				// group(0) = [19/Aug/2017:22:00:24 +0000]
				Pattern pattern = Pattern.compile("\\[(.+?):(.+)");// [(日期) : (时间)
				Matcher matcher = pattern.matcher(timestamp);
			    matcher.find();
				
				db.getCollection("logs")
						.insertOne(new Document().append("ip", ip).append("date", matcher.group(1)) 
								.append("time", matcher.group(2)).append("method", method.substring(1))
								.append("url", url).append("status", status));
			}
			System.out.println("Import Done!");
			bufferedReader.close();
			mongoClient.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
