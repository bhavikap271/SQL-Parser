package Screens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class LoadData {

	// public static void main(String[] args){
	public LoadData() {

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:complaint.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM COMPLAINTS LIMIT 100000;");

			int id = 1;
			JSONArray jArray = new JSONArray();

			while (rs.next()) {

				String Date_received = rs.getString("Date_received");
				String Product = rs.getString("Product");
				String SubProduct = rs.getString("SubProduct");
				String Issue = rs.getString("Issue");
				String Subissue = rs.getString("Subissue");
				String Consumer_narrative = rs.getString("Consumer_narrative");
				String Company_public_response = rs.getString("Company_public_response");
				String Company = rs.getString("Company");
				String State = rs.getString("State");
				String ZIP_code = rs.getString("ZIP_code");
				String Submitted_via = rs.getString("Submitted_via");
				String Date_sent_to_company = rs.getString("Date_sent_to_company");
				String Company_response = rs.getString("Company_response");
				String Timely_response = rs.getString("Timely_response");
				String Consumer_disputed = rs.getString("Consumer_disputed");
				String Complaint_id = rs.getString("Complaint_ID");

				JSONObject jObj = new JSONObject();
				jObj.put("id", id);
				jObj.put("Date_received", Date_received);
				jObj.put("Product", Product);
				jObj.put("SubProduct", SubProduct);
				jObj.put("Issue", Issue);
				jObj.put("Subissue", Subissue);
				jObj.put("Consumer_narrative", Consumer_narrative);
				jObj.put("Company_public_response", Company_public_response);
				jObj.put("Company", Company);
				jObj.put("State", State);
				jObj.put("ZIP_code", ZIP_code);
				jObj.put("Submitted_via", Submitted_via);
				jObj.put("Date_sent_to_company", Date_sent_to_company);
				jObj.put("Company_response", Company_response);
				jObj.put("Timely_response", Timely_response);
				jObj.put("Consumer_disputed", Consumer_disputed);
				jObj.put("Complaint_ID", Complaint_id);

				jArray.add(jObj);

				id++;

			}

			stmt.close();
			c.commit();
			c.close();

			JSONObject jObjDevice = new JSONObject();
			jObjDevice.put("Records", jArray);

			String tableName = "complaints";

			File file2 = new File("Data/Records/" + tableName + ".json");

			if (!file2.exists()) {

				file2.createNewFile();
			}

			FileWriter fw1;

			try {

				fw1 = new FileWriter(file2.getAbsoluteFile());
				BufferedWriter bw1 = new BufferedWriter(fw1);
				bw1.write(jObjDevice.toJSONString());
				bw1.flush();
				bw1.close();
				fw1.close();

			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}

			// save the tableName in globalList
			GlobalData.allTables.add(tableName);

			try {
				GlobalData.updateTableFile();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		System.out.println("Records created successfully");
	}
}
