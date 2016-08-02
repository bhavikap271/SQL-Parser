
package Screens;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;//done
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ProjectionRecords {

	private JFrame frame;
	private JTable table;
	HashMap<String,String> columnDataType;
	static JSONArray headers;
	
	/**
	 * Launch the application.
	 */
	public void projectRecords(ArrayList tables,ArrayList alias,boolean cond)
	{
		try {
			// populate records in the table
			frame.setTitle("Joined Tables");
			populateMainJson(tables,alias,cond);
			populateRecords();

			// display all the records of this table

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void projectRecords(ArrayList tables,ArrayList alias,ArrayList<String> SelectedAttributes,boolean cond)
	{
		try {
			// populate records in the table
			frame.setTitle("Joined Tables");
			populateMainJson(tables,alias,cond);
			populateRecords(SelectedAttributes);

			// display all the records of this table

		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void projectRecords(ArrayList tables,ArrayList alias,boolean cond,OrderBy OB)
	{
		try {
			// populate records in the table
			frame.setTitle("Joined Tables");
			populateMainJson(tables,alias,cond);
			oderMainJson(tables,alias,OB);
			populateRecords();

			// display all the records of this table

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void projectRecords(ArrayList tables,ArrayList alias,ArrayList<String> SelectedAttributes,boolean cond,OrderBy OB)
	{
		try {
			// populate records in the table
			frame.setTitle("Joined Tables");
			populateMainJson(tables,alias,cond);
			oderMainJson(tables,alias,OB);
			populateRecords(SelectedAttributes);

			// display all the records of this table

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void oderMainJson(ArrayList tables, ArrayList alias, OrderBy OB) {
		String tb;
		if(tables.size()==2)
		{
			String att = OB.Ordercols.get(0);
			if(GlobalUtil.validateColumnNames(att, (String)tables.get(0)))
			{
				tb = (String)tables.get(0);
			}
			else
			{
				tb = (String)tables.get(1);
			}
			oderMainJson(tb, OB);
		}
		
	}
	public void projectRecords(String tableName, ArrayList<String> SelectedAttributes, boolean cond) 
	{
			try {
			// populate records in the table
			frame.setTitle(tableName);
			populateMainJson(tableName,cond);
			populateRecords(SelectedAttributes);

			// display all the records of this table

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void projectRecords(String tableName, ArrayList<String> SelectedAttributes, OrderBy OB, boolean cond) {
		

		try {
			// populate records in the table
			frame.setTitle(tableName);
			populateMainJson(tableName ,cond);
			oderMainJson(tableName,OB);
			populateRecords(SelectedAttributes);

			// display all the records of this table

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void projectRecords(String tableName, boolean cond) {
		//EventQueue.invokeLater(new Runnable() {
			//public void run() {
		try {

			ProjectionRecords window = new ProjectionRecords();
			System.out.println(tableName);

			// populate records in the table
			window.frame.setTitle(tableName);
			populateMainJson(tableName,cond);
			window.populateRecords();

			// display all the records of this table

		} catch (Exception e) {
			e.printStackTrace();
		}
   }
	public static void populateMainJson(String tableName,boolean cond)
	{
		if(!cond){
			JSONParser parser = new JSONParser();
			try {
				
				headers = GlobalData.tableJSonArray.get(tableName);
				System.out.println(headers.toString());
				
			}
			catch(Exception E)
			{
			
			}
		}
		else {
			applyConditionsOnMainJson(tableName);
		}
	}
	public static void populateMainJson(ArrayList tables,ArrayList alias,boolean cond)
	{
		if(!cond){
			JSONParser parser = new JSONParser();
			int cnt=0;
			Iterator<String> itr = tables.iterator();
			while(itr.hasNext())
			{
				String tableName = itr.next();
				if(cnt==0)
				{
					try {
						
						headers = GlobalData.tableJSonArray.get(tableName);
						System.out.println(headers.toString());
						
						cnt++;
					}
					catch(Exception E)
					{
					
					}
				}
				else
				{
					JSONArray temp = new JSONArray();
					try {
						
						temp = GlobalData.tableJSonArray.get(tableName);
						System.out.println(headers.toString());
						
						cnt++;
					}
					catch(Exception E)
					{
					
					}
					
					headers= crossProduct(temp,headers);
					
				}
				
			}
			
		}
		else {
			applyConditionsOnMainJson(tables,alias);
		}
	}
	public static JSONArray crossProduct(JSONArray J1,JSONArray J2)
	{
		JSONArray newJ =new JSONArray();
		Iterator<JSONObject> titr = J1.iterator();
		while(titr.hasNext())
		{
			JSONObject ob =titr.next();
			Iterator<JSONObject> hitr =J2.iterator();
			while(hitr.hasNext())
			{
				JSONObject tob = hitr.next();
				JSONObject tobn =new JSONObject();
				Iterator<String> kitr = ob.keySet().iterator();
				System.out.print(ob.toString());
				while(kitr.hasNext())
				{
					String key = kitr.next();
					tobn.put(key, ob.get(key));
				}
				Iterator<String> kitr2 = tob.keySet().iterator();
				
				while(kitr2.hasNext())
				{
					String key = kitr2.next();
					tobn.put(key, tob.get(key));
				}
				newJ.add(tobn);
				System.out.println(tob.toString());
			}
		}
		return newJ;
	}
	public static void applyConditionsOnMainJson(ArrayList tables,ArrayList alias)
	{
		if(tables.size()==2)
		{
			String tbnm1 = null , tbnm2 = null;
			Iterator< String> itr = GlobalData.allTables.iterator();
			while(itr.hasNext())
			{
				String tnm= itr.next();
				if(((String)tables.get(0)).equalsIgnoreCase(tnm))
				{
					tbnm1 = tnm;
				}
				if(((String)tables.get(1)).equalsIgnoreCase(tnm))
				{
					tbnm2 = tnm;
				}
			}

			boolean and=false, or=false;
			//System.out.println(QValidation.conditionArray.toString());
			Iterator<WhereClause> itr1= QValidation.conditionArray.iterator();
			while(itr1.hasNext())
			{
				WhereClause w = itr1.next();
				if(w.bool) // its a AND/OR
				{
					if(w.boolOP)
					{
						and =true;
						or=false;
					}
					else
					{
						and =false;
						or=true;
					}
				}
				else		//its a condition
				{
					if(and)
					{
						JSONArray andJ = getJSONCond(tbnm1,tbnm2,w);
						JSONArray andArray =new JSONArray();
						String key = (String)GlobalData.tablePrimaryKeyMap.get(tbnm1.toLowerCase());
						and=false;
						Iterator<JSONObject> andJitr = andJ.iterator();
						while(andJitr.hasNext())
						{
							JSONObject temp1 = andJitr.next();
							Iterator<JSONObject> hitr = headers.iterator();
							while(hitr.hasNext())
							{
								JSONObject temp2 = (JSONObject)hitr.next();
								if(((String)temp2.get(key)).equalsIgnoreCase((String)temp1.get(key)))
								{
									andArray.add(temp2);							
								}
							}
						}
						headers = andArray;
					}
					else if(or)
					{
						JSONArray orJ = getJSONCond(tbnm1,tbnm2,w);
						or=false;
						boolean pre =false;
						JSONArray andArray =new JSONArray();
						String key = (String)GlobalData.tablePrimaryKeyMap.get(tbnm1.toLowerCase());
						and=false;
						Iterator<JSONObject> andJitr = orJ.iterator();
						while(andJitr.hasNext())
						{
							JSONObject temp1 = andJitr.next();
							Iterator<JSONObject> hitr = headers.iterator();
							while(hitr.hasNext())
							{
								JSONObject temp2 = (JSONObject)hitr.next();
								if(((String)temp2.get(key)).equalsIgnoreCase((String)temp1.get(key)))
								{
									pre=true;
									break;
								}
							}
							if(!pre)
							{
								headers.add(temp1);
								pre=false;
							}
						}
					}
					else
					{
						loadJsonCond(tbnm1,tbnm2,w);
					}
				}
			}
		
		}
		
	}
	private static void loadJsonCond(String tbnm1, String tbnm2, WhereClause w) 
	{
		String att1,att2o;
		headers = new JSONArray();
		if(w.attribute1.contains("."))
		{
			String[] atts = w.attribute1.split("\\.");
			att1 = atts[1];
		}
		else
		{
			att1 = w.attribute1;
		}
		if(w.attribute2value)
		{
			String type,tb;
			if(GlobalUtil.validateColumnNames(att1,tbnm1))
			{
				type = GlobalUtil.getDataType(tbnm1, att1);
				tb=tbnm1;
			}
			else
			{
				type = GlobalUtil.getDataType(tbnm2, att1);
				tb=tbnm2;
			}
			if(GlobalData.tablePrimaryKeyMap.get(tb.toLowerCase()).equalsIgnoreCase(att1))
			{
				// btree code //*************timer************
				String op =""+w.operation;
				headers = BPlusTreeIndexing.qBptree(att1,op,Long.parseLong(w.attribute2));
			}
			else
			{
				
				JSONParser parser = new JSONParser();
				JSONArray tempArray1 =new JSONArray();
				try {
					
					tempArray1 = GlobalData.tableJSonArray.get(tbnm1);
					System.out.println(headers.toString());
					
				}
				catch(Exception E)
				{
				
				}
				JSONArray tempArray2 =new JSONArray();
				try {
					
					tempArray2 = GlobalData.tableJSonArray.get(tbnm2);
					System.out.println(headers.toString());
					
				}
				catch(Exception E)
				{
				
				}
				JSONArray tempArray = crossProduct(tempArray1, tempArray2);
				switch(type)
				{
				case "INT":
					int att2 = Integer.parseInt(w.attribute2);
					
					Iterator<JSONObject> itr = tempArray.iterator();
					while(itr.hasNext())
					{
						JSONObject json1 = (JSONObject) itr.next();
						switch(w.operation)
						{
							case '=':
							if(att2 == (long)json1.get(att1))
							{
								headers.add(json1);
							}
							break;
							case '>':
							if(att2 < (long)json1.get(att1))
							{
								headers.add(json1);
							}
							break;
							case '<':
								if(att2 > (long)json1.get(att1))
								{
									headers.add(json1);
								}
							break;
						}
					}
					break;
				case "FLOAT":
					float att21 = Float.parseFloat(w.attribute2);
					Iterator<JSONObject> itr1 = tempArray.iterator();
					while(itr1.hasNext())
					{
						JSONObject json1 = (JSONObject) itr1.next();
						switch(w.operation)
						{
						case '=':
						if(att21 == Float.parseFloat((String)json1.get(att1)))
						{
							headers.add(json1);
						}
						break;
						case '>':
							if(att21 < Float.parseFloat((String)json1.get(att1)))
							{
								headers.add(json1);
							}
							break;
						case '<':
							if(att21 > Float.parseFloat((String)json1.get(att1)))
							{
								headers.add(json1);
							}
							break;
						}
					}
					break;
				case "VARCHAR":
					String att22 = w.attribute2;
					Iterator<JSONObject> itr11 = tempArray.iterator();
					while(itr11.hasNext())
					{
						JSONObject json1 = (JSONObject) itr11.next();
						switch(w.operation)
						{
						case '=':
						if(att22.equalsIgnoreCase((String)json1.get(att1)))
						{
							headers.add(json1);
						}
						break;
						case '<':
							if(att22.compareTo((String)json1.get(att1))==1)
							{
								headers.add(json1);
							}
							break;
						case '>':
							if(att22.compareTo((String)json1.get(att1))==-1)
							{
								headers.add(json1);
							}
							break;
						}
					}
					break;
				}
			}
		}
		else
		{
			
			if(w.attribute2.contains("."))
			{
				String[] atts = w.attribute2.split("\\.");
				att2o = atts[1];
			}
			else
			{
				att2o = w.attribute2;
			}
			if(GlobalUtil.validateColumnNames(att1,tbnm1))
			{
				if(GlobalData.tablePrimaryKeyMap.get(tbnm1.toLowerCase()).equalsIgnoreCase(att1))
				{
					if(GlobalData.tablePrimaryKeyMap.get(tbnm2.toLowerCase()).equalsIgnoreCase(att2o))
					{
						//btree
						String op=""+w.operation;
						headers = BPlusTreeIndexing.qBptree(tbnm1,att1,op,tbnm2,att2o);
					}
					else
					{
						//btree
						String op=""+w.operation;
						headers = BPlusTreeIndexing.qBptree(tbnm1,att1,op,tbnm2,att2o);
					}
				}
				else
				{
					if(GlobalData.tablePrimaryKeyMap.get(tbnm2.toLowerCase()).equalsIgnoreCase(att2o))
					{
						//btree
						String op=""+w.operation;
						headers = BPlusTreeIndexing.qBptree(tbnm1,att1,op,tbnm2,att2o);
					}
					else
					{
						String type = GlobalUtil.getDataType(tbnm2, att2o);
						JSONParser parser = new JSONParser();
						JSONArray tempArray1 =new JSONArray();
						try {
							
							tempArray1 = GlobalData.tableJSonArray.get(tbnm1);
							System.out.println(headers.toString());
							
						}
						catch(Exception E)
						{
						
						}
						JSONArray tempArray2 =new JSONArray();
						try {
							
							tempArray2 = GlobalData.tableJSonArray.get(tbnm2);
							System.out.println(headers.toString());
							
						}
						catch(Exception E)
						{
						
						}
						JSONArray tempArray = crossProduct(tempArray1, tempArray2);
						switch(type)
						{
						case "INT":
							int att2 = Integer.parseInt(w.attribute2);
							Iterator<JSONObject> itr = tempArray.iterator();
							while(itr.hasNext())
							{
								JSONObject json1 = (JSONObject) itr.next();
								switch(w.operation)
								{
									case '=':
									if((long)json1.get(att2) == (long)json1.get(att1))
									{
										headers.add(json1);
									}
									break;
									case '>':
									if((long)json1.get(att2) < (long)json1.get(att1))
									{
										headers.add(json1);
									}
									break;
									case '<':
										if((long)json1.get(att2) > (long)json1.get(att1))
										{
											headers.add(json1);
										}
									break;
								}
							}
							break;
						case "FLOAT":
							float att21 = Float.parseFloat(w.attribute2);
							
							Iterator<JSONObject> itr1 = tempArray.iterator();
							while(itr1.hasNext())
							{
								JSONObject json1 = (JSONObject) itr1.next();
								switch(w.operation)
								{
								case '=':
								if(Float.parseFloat((String)json1.get(att21)) == Float.parseFloat((String)json1.get(att1)))
								{
									headers.add(json1);
								}
								break;
								case '>':
									if(Float.parseFloat((String)json1.get(att21)) < Float.parseFloat((String)json1.get(att1)))
									{
										headers.add(json1);
									}
									break;
								case '<':
									if(Float.parseFloat((String)json1.get(att21)) > Float.parseFloat((String)json1.get(att1)))
									{
										headers.add(json1);
									}
									break;
								}
							}
							break;
						case "VARCHAR":
							String att22 = w.attribute2;
							
							Iterator<JSONObject> itr11 = tempArray.iterator();
							while(itr11.hasNext())
							{
								JSONObject json1 = (JSONObject) itr11.next();
								switch(w.operation)
								{
								case '=':
								if(((String)json1.get(att22)).equalsIgnoreCase((String)json1.get(att1)))
								{
									headers.add(json1);
								}
								break;
								case '<':
									if(((String)json1.get(att22)).compareTo((String)json1.get(att1))==1)
									{
										headers.add(json1);
									}
									break;
								case '>':
									if(((String)json1.get(att22)).compareTo((String)json1.get(att1))==-1)
									{
										headers.add(json1);
									}
									break;
								}
							}
							break;
						}
					
					
					}
				}
				
			}
			else
			{
				if(GlobalData.tablePrimaryKeyMap.get(tbnm2.toLowerCase()).equalsIgnoreCase(att1))
				{
					if(GlobalData.tablePrimaryKeyMap.get(tbnm1.toLowerCase()).equalsIgnoreCase(att2o))
					{
						String op=""+w.operation;
						headers = BPlusTreeIndexing.qBptree(tbnm2,att1,op,tbnm1,att2o);
					}
					else
					{
						//btree
						String op=""+w.operation;
						headers = BPlusTreeIndexing.qBptree(tbnm2,att1,op,tbnm1,att2o);
					}
				}
				else
				{
					if(GlobalData.tablePrimaryKeyMap.get(tbnm1.toLowerCase()).equalsIgnoreCase(att2o))
					{
						String op=""+w.operation;
						headers = BPlusTreeIndexing.qBptree(tbnm2,att1,op,tbnm1,att2o);
					}
					else
					{


						String type = GlobalUtil.getDataType(tbnm1, att2o);
						JSONParser parser = new JSONParser();
						JSONArray tempArray1 =new JSONArray();
						try {
							
							tempArray1 = GlobalData.tableJSonArray.get(tbnm1);
							System.out.println(headers.toString());
							
						}
						catch(Exception E)
						{
						
						}
						JSONArray tempArray2 =new JSONArray();
						try {
							
							tempArray2 = GlobalData.tableJSonArray.get(tbnm2);
							System.out.println(headers.toString());
							
						}
						catch(Exception E)
						{
						
						}
						JSONArray tempArray = crossProduct(tempArray1, tempArray2);
						switch(type)
						{
						case "INT":
							int att2 = Integer.parseInt(w.attribute2);
							Iterator<JSONObject> itr = tempArray.iterator();
							while(itr.hasNext())
							{
								JSONObject json1 = (JSONObject) itr.next();
								switch(w.operation)
								{
									case '=':
									if((long)json1.get(att2) == (long)json1.get(att1))
									{
										headers.add(json1);
									}
									break;
									case '>':
									if((long)json1.get(att2) < (long)json1.get(att1))
									{
										headers.add(json1);
									}
									break;
									case '<':
										if((long)json1.get(att2) > (long)json1.get(att1))
										{
											headers.add(json1);
										}
									break;
								}
							}
							break;
						case "FLOAT":
							float att21 = Float.parseFloat(w.attribute2);
							
							Iterator<JSONObject> itr1 = tempArray.iterator();
							while(itr1.hasNext())
							{
								JSONObject json1 = (JSONObject) itr1.next();
								switch(w.operation)
								{
								case '=':
								if(Float.parseFloat((String)json1.get(att21)) == Float.parseFloat((String)json1.get(att1)))
								{
									headers.add(json1);
								}
								break;
								case '>':
									if(Float.parseFloat((String)json1.get(att21)) < Float.parseFloat((String)json1.get(att1)))
									{
										headers.add(json1);
									}
									break;
								case '<':
									if(Float.parseFloat((String)json1.get(att21)) > Float.parseFloat((String)json1.get(att1)))
									{
										headers.add(json1);
									}
									break;
								}
							}
							break;
						case "VARCHAR":
							String att22 = w.attribute2;
							
							Iterator<JSONObject> itr11 = tempArray.iterator();
							while(itr11.hasNext())
							{
								JSONObject json1 = (JSONObject) itr11.next();
								switch(w.operation)
								{
								case '=':
								if(((String)json1.get(att22)).equalsIgnoreCase((String)json1.get(att1)))
								{
									headers.add(json1);
								}
								break;
								case '<':
									if(((String)json1.get(att22)).compareTo((String)json1.get(att1))==1)
									{
										headers.add(json1);
									}
									break;
								case '>':
									if(((String)json1.get(att22)).compareTo((String)json1.get(att1))==-1)
									{
										headers.add(json1);
									}
									break;
								}
							}
							break;
						}
					
					
					
						
					}
				}
			}
		}
	
		
	}
	private static JSONArray getJSONCond(String tbnm1, String tbnm2, WhereClause w) {
		JSONArray condJ =new JSONArray();
		String att1,att2o;
		headers = new JSONArray();
		if(w.attribute1.contains("."))
		{
			String[] atts = w.attribute1.split("\\.");
			att1 = atts[1];
		}
		else
		{
			att1 = w.attribute1;
		}
		if(w.attribute2value)
		{
			String type,tb;
			if(GlobalUtil.validateColumnNames(att1,tbnm1))
			{
				type = GlobalUtil.getDataType(tbnm1, att1);
				tb=tbnm1;
			}
			else
			{
				type = GlobalUtil.getDataType(tbnm2, att1);
				tb=tbnm2;
			}
			if(GlobalData.tablePrimaryKeyMap.get(tbnm1.toLowerCase()).equalsIgnoreCase(att1) || GlobalData.tablePrimaryKeyMap.get(tbnm2.toLowerCase()).equalsIgnoreCase(att1))
			{
				// btree code
				String op =""+w.operation;
				condJ = BPlusTreeIndexing.qBptree(att1,op,Long.parseLong(w.attribute2));
			}
			else
			{
				
				JSONParser parser = new JSONParser();
				JSONArray tempArray1 =new JSONArray();
				try {
					
					tempArray1 = GlobalData.tableJSonArray.get(tbnm1);
					System.out.println(headers.toString());
					
				}
				catch(Exception E)
				{
				
				}
				JSONArray tempArray2 =new JSONArray();
				try {
					
					tempArray2 = GlobalData.tableJSonArray.get(tbnm2);
					System.out.println(headers.toString());
					
				}
				catch(Exception E)
				{
				
				}
				JSONArray tempArray = crossProduct(tempArray1, tempArray2);
				switch(type)
				{
				case "INT":
					int att2 = Integer.parseInt(w.attribute2);
					Iterator<JSONObject> itr = tempArray.iterator();
					while(itr.hasNext())
					{
						JSONObject json1 = (JSONObject) itr.next();
						switch(w.operation)
						{
							case '=':
							if(att2 == (long)json1.get(att1))
							{
								condJ.add(json1);
							}
							break;
							case '>':
							if(att2 < (long)json1.get(att1))
							{
								condJ.add(json1);
							}
							break;
							case '<':
								if(att2 > (long)json1.get(att1))
								{
									condJ.add(json1);
								}
							break;
						}
					}
					break;
				case "FLOAT":
					float att21 = Float.parseFloat(w.attribute2);
					
					Iterator<JSONObject> itr1 = tempArray.iterator();
					while(itr1.hasNext())
					{
						JSONObject json1 = (JSONObject) itr1.next();
						switch(w.operation)
						{
						case '=':
						if(att21 == Float.parseFloat((String)json1.get(att1)))
						{
							condJ.add(json1);
						}
						break;
						case '>':
							if(att21 < Float.parseFloat((String)json1.get(att1)))
							{
								condJ.add(json1);
							}
							break;
						case '<':
							if(att21 > Float.parseFloat((String)json1.get(att1)))
							{
								condJ.add(json1);
							}
							break;
						}
					}
					break;
				case "VARCHAR":
					String att22 = w.attribute2;
					
					Iterator<JSONObject> itr11 = tempArray.iterator();
					while(itr11.hasNext())
					{
						JSONObject json1 = (JSONObject) itr11.next();
						switch(w.operation)
						{
						case '=':
						if(att22.equalsIgnoreCase((String)json1.get(att1)))
						{
							condJ.add(json1);
						}
						break;
						case '<':
							if(att22.compareTo((String)json1.get(att1))==1)
							{
								condJ.add(json1);
							}
							break;
						case '>':
							if(att22.compareTo((String)json1.get(att1))==-1)
							{
								condJ.add(json1);
							}
							break;
						}
					}
					break;
				}
			}
		}
		else
		{
			
			if(w.attribute2.contains("."))
			{
				String[] atts = w.attribute2.split("\\.");
				att2o = atts[1];
			}
			else
			{
				att2o = w.attribute2;
			}
			if(GlobalUtil.validateColumnNames(att1,tbnm1))
			{
				if(GlobalData.tablePrimaryKeyMap.get(tbnm1.toLowerCase()).equalsIgnoreCase(att1))
				{
					if(GlobalData.tablePrimaryKeyMap.get(tbnm2.toLowerCase()).equalsIgnoreCase(att2o))
					{
						String op=""+w.operation;
						condJ = BPlusTreeIndexing.qBptree(tbnm1,att1,op,tbnm2,att2o);
					}
					else
					{
						//btree
						String op=""+w.operation;
						condJ = BPlusTreeIndexing.qBptree(tbnm1,att1,op,tbnm2,att2o);
					}
				}
				else
				{
					if(GlobalData.tablePrimaryKeyMap.get(tbnm2.toLowerCase()).equalsIgnoreCase(att2o))
					{
						String op=""+w.operation;
						condJ = BPlusTreeIndexing.qBptree(tbnm1,att1,op,tbnm2,att2o);
					}
					else
					{

						String type = GlobalUtil.getDataType(tbnm2, att2o);
						JSONParser parser = new JSONParser();
						JSONArray tempArray1 =new JSONArray();
						try {
							
							tempArray1 = GlobalData.tableJSonArray.get(tbnm1);
							System.out.println(headers.toString());
							
						}
						catch(Exception E)
						{
						
						}
						JSONArray tempArray2 =new JSONArray();
						try {
							
							tempArray2 = GlobalData.tableJSonArray.get(tbnm2);
							System.out.println(headers.toString());
							
						}
						catch(Exception E)
						{
						
						}
						JSONArray tempArray = crossProduct(tempArray1, tempArray2);
						switch(type)
						{
						case "INT":
							int att2 = Integer.parseInt(w.attribute2);
							Iterator<JSONObject> itr = tempArray.iterator();
							while(itr.hasNext())
							{
								JSONObject json1 = (JSONObject) itr.next();
								switch(w.operation)
								{
									case '=':
									if((long)json1.get(att2) == (long)json1.get(att1))
									{
										condJ.add(json1);
									}
									break;
									case '>':
									if((long)json1.get(att2) < (long)json1.get(att1))
									{
										condJ.add(json1);
									}
									break;
									case '<':
										if((long)json1.get(att2) > (long)json1.get(att1))
										{
											condJ.add(json1);
										}
									break;
								}
							}
							break;
						case "FLOAT":
							float att21 = Float.parseFloat(w.attribute2);
							
							Iterator<JSONObject> itr1 = tempArray.iterator();
							while(itr1.hasNext())
							{
								JSONObject json1 = (JSONObject) itr1.next();
								switch(w.operation)
								{
								case '=':
								if(Float.parseFloat((String)json1.get(att21)) == Float.parseFloat((String)json1.get(att1)))
								{
									condJ.add(json1);
								}
								break;
								case '>':
									if(Float.parseFloat((String)json1.get(att21)) < Float.parseFloat((String)json1.get(att1)))
									{
										condJ.add(json1);
									}
									break;
								case '<':
									if(Float.parseFloat((String)json1.get(att21)) > Float.parseFloat((String)json1.get(att1)))
									{
										condJ.add(json1);
									}
									break;
								}
							}
							break;
						case "VARCHAR":
							String att22 = w.attribute2;
							
							Iterator<JSONObject> itr11 = tempArray.iterator();
							while(itr11.hasNext())
							{
								JSONObject json1 = (JSONObject) itr11.next();
								switch(w.operation)
								{
								case '=':
								if(((String)json1.get(att22)).equalsIgnoreCase((String)json1.get(att1)))
								{
									condJ.add(json1);
								}
								break;
								case '<':
									if(((String)json1.get(att22)).compareTo((String)json1.get(att1))==1)
									{
										condJ.add(json1);
									}
									break;
								case '>':
									if(((String)json1.get(att22)).compareTo((String)json1.get(att1))==-1)
									{
										condJ.add(json1);
									}
									break;
								}
							}
							break;
						}
					
					}
				}
				
			}
			else
			{
				if(GlobalData.tablePrimaryKeyMap.get(tbnm2.toLowerCase()).equalsIgnoreCase(att1))
				{
					if(GlobalData.tablePrimaryKeyMap.get(tbnm1.toLowerCase()).equalsIgnoreCase(att2o))
					{
						String op=""+w.operation;
						condJ = BPlusTreeIndexing.qBptree(tbnm2,att1,op,tbnm1,att2o);
					}
					else
					{
						//btree
						String op=""+w.operation;
						condJ = BPlusTreeIndexing.qBptree(tbnm2,att1,op,tbnm1,att2o);
					}
				}
				else
				{
					if(GlobalData.tablePrimaryKeyMap.get(tbnm1.toLowerCase()).equalsIgnoreCase(att2o))
					{
						String op=""+w.operation;
						condJ = BPlusTreeIndexing.qBptree(tbnm2,att1,op,tbnm1,att2o);
					}
					else
					{


						String type = GlobalUtil.getDataType(tbnm1, att2o);
						JSONParser parser = new JSONParser();
						JSONArray tempArray1 =new JSONArray();
						try {
							
							tempArray1 = GlobalData.tableJSonArray.get(tbnm1);
							System.out.println(headers.toString());
							
						}
						catch(Exception E)
						{
						
						}
						JSONArray tempArray2 =new JSONArray();
						try {
							
							tempArray2 = GlobalData.tableJSonArray.get(tbnm2);
							System.out.println(headers.toString());
							
						}
						catch(Exception E)
						{
						
						}
						JSONArray tempArray = crossProduct(tempArray1, tempArray2);
						switch(type)
						{
						case "INT":
							int att2 = Integer.parseInt(w.attribute2);
							Iterator<JSONObject> itr = tempArray.iterator();
							while(itr.hasNext())
							{
								JSONObject json1 = (JSONObject) itr.next();
								switch(w.operation)
								{
									case '=':
									if((long)json1.get(att2) == (long)json1.get(att1))
									{
										condJ.add(json1);
									}
									break;
									case '>':
									if((long)json1.get(att2) < (long)json1.get(att1))
									{
										condJ.add(json1);
									}
									break;
									case '<':
										if((long)json1.get(att2) > (long)json1.get(att1))
										{
											condJ.add(json1);
										}
									break;
								}
							}
							break;
						case "FLOAT":
							float att21 = Float.parseFloat(w.attribute2);
							
							Iterator<JSONObject> itr1 = tempArray.iterator();
							while(itr1.hasNext())
							{
								JSONObject json1 = (JSONObject) itr1.next();
								switch(w.operation)
								{
								case '=':
								if(Float.parseFloat((String)json1.get(att21)) == Float.parseFloat((String)json1.get(att1)))
								{
									condJ.add(json1);
								}
								break;
								case '>':
									if(Float.parseFloat((String)json1.get(att21)) < Float.parseFloat((String)json1.get(att1)))
									{
										condJ.add(json1);
									}
									break;
								case '<':
									if(Float.parseFloat((String)json1.get(att21)) > Float.parseFloat((String)json1.get(att1)))
									{
										condJ.add(json1);
									}
									break;
								}
							}
							break;
						case "VARCHAR":
							String att22 = w.attribute2;
							
							Iterator<JSONObject> itr11 = tempArray.iterator();
							while(itr11.hasNext())
							{
								JSONObject json1 = (JSONObject) itr11.next();
								switch(w.operation)
								{
								case '=':
								if(((String)json1.get(att22)).equalsIgnoreCase((String)json1.get(att1)))
								{
									condJ.add(json1);
								}
								break;
								case '<':
									if(((String)json1.get(att22)).compareTo((String)json1.get(att1))==1)
									{
										condJ.add(json1);
									}
									break;
								case '>':
									if(((String)json1.get(att22)).compareTo((String)json1.get(att1))==-1)
									{
										condJ.add(json1);
									}
									break;
								}
							}
							break;
						}
					
					
					}
				}
			}
			
		}
	
	return condJ;	
	
	}
	public static void applyConditionsOnMainJson(String tableName)
	{
		boolean and=false, or=false;
		//System.out.println(QValidation.conditionArray.toString());
		Iterator<WhereClause> itr= QValidation.conditionArray.iterator();
		while(itr.hasNext())
		{
			WhereClause w = itr.next();
			if(w.bool) // its a AND/OR
			{
				if(w.boolOP)
				{
					and =true;
					or=false;
				}
				else
				{
					and =false;
					or=true;
				}
			}
			else		//its a condition
			{
				if(and)
				{
					JSONArray andJ = getJSONCond(tableName,w);
					JSONArray andArray =new JSONArray();
					String key = (String)GlobalData.tablePrimaryKeyMap.get(tableName.toLowerCase());
					and=false;
					Iterator<JSONObject> andJitr = andJ.iterator();
					while(andJitr.hasNext())
					{
						JSONObject temp1 = andJitr.next();
						Iterator<JSONObject> hitr = headers.iterator();
						while(hitr.hasNext())
						{
							JSONObject temp2 = (JSONObject)hitr.next();
							if(((long)temp2.get(key))==((long)temp1.get(key)))
							{
								andArray.add(temp2);							
							}
						}
					}
					headers = andArray;
				}
				else if(or)
				{
					JSONArray orJ = getJSONCond(tableName,w);
					or=false;
					boolean pre =false;
					JSONArray andArray =new JSONArray();
					String key = (String)GlobalData.tablePrimaryKeyMap.get(tableName.toLowerCase());
					and=false;
					Iterator<JSONObject> andJitr = orJ.iterator();
					while(andJitr.hasNext())
					{
						JSONObject temp1 = andJitr.next();
						Iterator<JSONObject> hitr = headers.iterator();
						while(hitr.hasNext())
						{
							JSONObject temp2 = (JSONObject)hitr.next();
							if(((long)temp2.get(key))==((long)temp1.get(key)))
							{
								pre=true;
								break;
							}
						}
						if(!pre)
						{
							headers.add(temp1);
							pre=false;
						}
					}
				}
				else
				{
					loadJsonCond(tableName,w);
				}
			}
		}
	}
	public static void loadJsonCond(String tableName,WhereClause w)
	{
		String att1;
		headers = new JSONArray();
		if(w.attribute1.contains("."))
		{
			String[] atts = w.attribute1.split("\\.");
			att1 = atts[1];
		}
		else
		{
			att1 = w.attribute1;
		}
		if(w.attribute2value)
		{
			if(GlobalData.tablePrimaryKeyMap.get(tableName.toLowerCase()).equalsIgnoreCase(att1))
			{
				// btree code **************timer*********
				String op =""+w.operation;
				headers = BPlusTreeIndexing.qBptree(att1,op,Long.parseLong(w.attribute2));
			}
			else
			{
				String type = GlobalUtil.getDataType(tableName, att1);
				switch(type)
				{
				case "INT":
					long att2 = Long.parseLong(w.attribute2);
					JSONParser parser = new JSONParser();
					JSONArray tempArray =new JSONArray();
					try {
						
						tempArray = GlobalData.tableJSonArray.get(tableName);
						System.out.println(headers.toString());
						
					}
					catch(Exception E)
					{
					
					}
					Iterator<JSONObject> itr = tempArray.iterator();
					while(itr.hasNext())
					{
						JSONObject json1 = (JSONObject) itr.next();
						switch(w.operation)
						{
							case '=':
							if(att2 == (long)json1.get(att1))
							{
								headers.add(json1);
							}
							break;
							case '>':
							if(att2 < (long)json1.get(att1))
							{
								headers.add(json1);
							}
							break;
							case '<':
								if(att2 > (long)json1.get(att1))
								{
									headers.add(json1);
								}
							break;
						}
					}
					break;
				case "FLOAT":
					float att21 = Float.parseFloat(w.attribute2);
					JSONParser parser1 = new JSONParser();
					JSONArray tempArray1 =new JSONArray();
					try {
						
						tempArray1 = GlobalData.tableJSonArray.get(tableName);
						System.out.println(headers.toString());
						
					}
					catch(Exception E)
					{
					
					}
					Iterator<JSONObject> itr1 = tempArray1.iterator();
					while(itr1.hasNext())
					{
						JSONObject json1 = (JSONObject) itr1.next();
						switch(w.operation)
						{
						case '=':
						if(att21 == Float.parseFloat((String)json1.get(att1)))
						{
							headers.add(json1);
						}
						break;
						case '>':
							if(att21 < Float.parseFloat((String)json1.get(att1)))
							{
								headers.add(json1);
							}
							break;
						case '<':
							if(att21 > Float.parseFloat((String)json1.get(att1)))
							{
								headers.add(json1);
							}
							break;
						}
					}
					break;
				case "VARCHAR":
					String att22 = w.attribute2;
					JSONParser parser11 = new JSONParser();
					JSONArray tempArray11 =new JSONArray();
					try {
						
						tempArray11 = GlobalData.tableJSonArray.get(tableName);
						System.out.println(tempArray11.get(0).toString());
						//System.out.println(headers.toString());
						
					}
					catch(Exception E)
					{
					
					}
					Iterator<JSONObject> itr11 = tempArray11.iterator();
					while(itr11.hasNext())
					{
						JSONObject json1 = (JSONObject) itr11.next();
						switch(w.operation)
						{
						case '=':
						if(att22.equalsIgnoreCase((String)json1.get(att1)))
						{
							headers.add(json1);
						}
						break;
						case '<':
							if(att22.compareTo((String)json1.get(att1))==1)
							{
								headers.add(json1);
							}
							break;
						case '>':
							if(att22.compareTo((String)json1.get(att1))==-1)
							{
								headers.add(json1);
							}
							break;
						}
					}
					break;
				}
			}
		}
	}
	public static JSONArray getJSONCond(String tableName, WhereClause w)
	{
		JSONArray condJ = new JSONArray();

		String att1;
		if(w.attribute1.contains("."))
		{
			String[] atts = w.attribute1.split("\\.");
			att1 = atts[1];
		}
		else
		{
			att1 = w.attribute1;
		}
		if(w.attribute2value)
		{
			if(GlobalData.tablePrimaryKeyMap.get(tableName.toLowerCase()).equalsIgnoreCase(att1))
			{
				// btree code *****************timer*************
				String op =""+w.operation;
				condJ = BPlusTreeIndexing.qBptree(att1,op,Long.parseLong(w.attribute2));
			}
			else
			{
				String type = GlobalUtil.getDataType(tableName, att1);
				switch(type)
				{
				case "INT":
					long att2 = Long.parseLong(w.attribute2);
					JSONParser parser = new JSONParser();
					JSONArray tempArray =new JSONArray();
					try {
						
						tempArray = GlobalData.tableJSonArray.get(tableName);
						
					}
					catch(Exception E)
					{
					
					}
					Iterator<JSONObject> itr = tempArray.iterator();
					while(itr.hasNext())
					{
						JSONObject json1 = (JSONObject) itr.next();
						switch(w.operation)
						{
							case '=':
							if(att2 == (long)json1.get(att1))
							{
								condJ.add(json1);
							}
							break;
							case '>':
							if(att2 < (long)json1.get(att1))
							{
								condJ.add(json1);
							}
							break;
							case '<':
								if(att2 > (long)json1.get(att1))
								{
									condJ.add(json1);
								}
							break;
						}
					}
					break;
				case "FLOAT":
					float att21 = Float.parseFloat(w.attribute2);
					JSONParser parser1 = new JSONParser();
					JSONArray tempArray1 =new JSONArray();
					try {
						
						tempArray1 = GlobalData.tableJSonArray.get(tableName);
						System.out.println(condJ.toString());
						
					}
					catch(Exception E)
					{
					
					}
					Iterator<JSONObject> itr1 = tempArray1.iterator();
					while(itr1.hasNext())
					{
						JSONObject json1 = (JSONObject) itr1.next();
						switch(w.operation)
						{
						case '=':
						if(att21 == Float.parseFloat((String)json1.get(att1)))
						{
							condJ.add(json1);
						}
						break;
						case '>':
							if(att21 < Float.parseFloat((String)json1.get(att1)))
							{
								condJ.add(json1);
							}
							break;
						case '<':
							if(att21 > Float.parseFloat((String)json1.get(att1)))
							{
								condJ.add(json1);
							}
							break;
						}
					}
					break;
				case "VARCHAR":
					String att22 = w.attribute2;
					JSONParser parser11 = new JSONParser();
					JSONArray tempArray11 =new JSONArray();
					try {
						
						tempArray11 = GlobalData.tableJSonArray.get(tableName);
						System.out.println(condJ.toString());
						
					}
					catch(Exception E)
					{
					
					}
					Iterator<JSONObject> itr11 = tempArray11.iterator();
					while(itr11.hasNext())
					{
						JSONObject json1 = (JSONObject) itr11.next();
						switch(w.operation)
						{
						case '=':
						if(att22.equalsIgnoreCase((String)json1.get(att1)))
						{
							condJ.add(json1);
						}
						break;
						case '<':
							if(att22.compareTo((String)json1.get(att1))==1)
							{
								condJ.add(json1);
							}
							break;
						case '>':
							if(att22.compareTo((String)json1.get(att1))==-1)
							{
								condJ.add(json1);
							}
							break;
						}
					}
					break;
				}
			}
		}
	
		return condJ;
	}
	public void projectRecords(String tableName, OrderBy OB, boolean cond) {
		//EventQueue.invokeLater(new Runnable() {
			//public void run() {
		try {

			ProjectionRecords window = new ProjectionRecords();
			System.out.println(tableName);

			// populate records in the table
			window.frame.setTitle(tableName);
			populateMainJson(tableName,cond);
			oderMainJson(tableName,OB);
			window.populateRecords();

			// display all the records of this table

		} catch (Exception e) {
			e.printStackTrace();
		}
   }

	public static void oderMainJson(String tableName,OrderBy OB)
	{
		if(OB.Ordercols.size()==1)
		{
			String key = OB.Ordercols.get(0);
		    System.out.println(OB.toString());
		    JSONArray sortedJsonArray = new JSONArray();

		    List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		    for (int i = 0; i < headers.size(); i++) 
		    {
		        jsonValues.add((JSONObject)headers.get(i));
		    }
		    Collections.sort( jsonValues, new Comparator<JSONObject>() 
		    {
		        @Override
		        public int compare(JSONObject a, JSONObject b) 
		        {		            

					String type = GlobalUtil.getDataType(tableName, key);
					try{
					switch(type)
					{
					case "INT":
						long att1 = (long)a.get(key);
						long att2 = (long)b.get(key);
						if(att1>att2)
						{
							if(OB.order)
							{return 1;}
							return -1;
						}
						if(OB.order)
						{return -1;}
						return 1;
						//return statement
					case "FLOAT":
						float att21 = Float.parseFloat((String)a.get(key));
						float att22 = Float.parseFloat((String)b.get(key));
						if(att21>att22)
						{
							if(OB.order)
							{return 1;}
							return -1;
						}
						if(OB.order)
						{return -1;}
						return 1;
					case "VARCHAR":
						String att31 = (String)a.get(key);
						String att32 = (String)b.get(key);
						if(OB.order)
						{return att31.compareTo(att32);}
						return -att31.compareTo(att32);
					}
			
		            } 
		            catch (Exception e) {
		                //do something
		            }
					return 0;
		            
		            //if you want to change the sort order, simply use the following:
		            //return -valA.compareTo(valB);
		        }
		    });

		    for (int i = 0; i < headers.size(); i++) {
		        sortedJsonArray.add(jsonValues.get(i));
		    }
		    headers = sortedJsonArray;
		}
	}

	public void populateRecords(ArrayList<String> SelectedAttributes1) {
		Iterator<String> itr =SelectedAttributes1.iterator();
		ArrayList<String> SelectedAttributes = new ArrayList<>();
		while(itr.hasNext())
		{
			String temp =itr.next();
			if(temp.contains("."))
			{
				String[] att =temp.split("\\.");
				SelectedAttributes.add(att[1]);
			}
			else
			{
				SelectedAttributes.add(temp);
			}
		}
		JSONParser parser = new JSONParser();
		try {
			

			if (headers.size() == 0) {
				JOptionPane.showMessageDialog(null, "No Records to Display", "Warning",JOptionPane.INFORMATION_MESSAGE);
			} else {

				String[] columnNames = new String[SelectedAttributes.size()];
				SelectedAttributes.toArray(columnNames);
				
//				getDataTypes(frame.getTitle());
				
				table = new JTable();
				table.setModel(new DefaultTableModel(new Object[][] {}, columnNames) {

					@Override
					public boolean isCellEditable(int row, int col) {

						return false;
					}
					
//					public Class<?> getColumnClass(int columnIndex){
//		    			
//						String columnName = getColumnName(columnIndex);
//						
//						String dataType  = columnDataType.get(columnName);
//						
//						if("VARCHAR".equals(dataType))
//							 return String.class;
//						else if("INT".equals(dataType))
//							 return Integer.class;
//						else if("FLOAT".equals(dataType))
//							 return BigDecimal.class;
//
//						return String.class;
//
//					}   

				});

				DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

				for (int i = 0; i < headers.size(); i++) {

					Object temp = parser.parse(headers.get(i).toString());
					JSONObject currJson = (JSONObject) temp;

					Object[] data = new Object[columnNames.length];
					
					int index = 0;
					for (String key : columnNames){	
						
						Object o = currJson.get(key);
						if(o instanceof String)
						{
							data[index]=(String)o;
						}
						else
						{
							data[index]=(Long) o;
						}
						
						
						index++;
					}
					
					tableModel.addRow(data);
				}

				table.setFillsViewportHeight(true);
				table.setAutoCreateRowSorter(true);
				JScrollPane scrollPane = new JScrollPane(table);
				scrollPane.setBounds(50, 50, 1500, 600);
				scrollPane.setViewportView(table);
				scrollPane.setPreferredSize(new Dimension(468, 100));
				frame.getContentPane().add(scrollPane);

				this.frame.setVisible(true);

			}

		}catch(ParseException e) {
			e.printStackTrace();
		}
	}

	public void populateRecords() {
		JSONParser parser = new JSONParser();
		try {

			if (headers.size() == 0) {

				JOptionPane.showMessageDialog(null, "No Records to Display", "Warning",
						JOptionPane.INFORMATION_MESSAGE);

			} else {

				Object temp = parser.parse(headers.get(0).toString());
				JSONObject currJson = (JSONObject) temp;
				System.out.println("test!!!!" + headers.get(0).toString());
				Set<String> keys = currJson.keySet();
				String[] columnNames = keys.toArray(new String[keys.size()]);
				
				//getDataTypes(frame.getTitle());
				
				//for (int i = 0; i < columnNames.length; i++)
					//   System.out.println(i+"th:" + columnNames[i]);

				table = new JTable();

				table.setModel(new DefaultTableModel(new Object[][] {}, columnNames) {

					@Override
					public boolean isCellEditable(int row, int col) {
						return false;
					}
					
					/*public Class<?> getColumnClass(int columnIndex){
		    			
						String columnName = getColumnName(columnIndex);
						
						String dataType  = columnDataType.get(columnName);
						
						if("VARCHAR".equals(dataType))
							 return String.class;
						else if("INT".equals(dataType))
							 return Integer.class;
						else if("FLOAT".equals(dataType))
							 return BigDecimal.class;

						return String.class;

					}   
					
				}*/
				
				});

				DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

				for (int i = 0; i < headers.size(); i++) {
					temp = parser.parse(headers.get(i).toString());
					currJson = (JSONObject) temp;

					Object[] data = new Object[columnNames.length];
					int index = 0;
					for (String key : keys) {
						Object o = currJson.get(key);
						if(o instanceof String)
						{
							data[index]=(String)o;
						}
						else
						{
							data[index]=(Long) o;
						}
						
						
				//		String dataType = columnDataType.get(key);
					//	if("INT".equals(dataType))
							//data[index] = Integer.parseInt((String)currJson.get(key));
						//else if("FLOAT".equals(dataType))
							//data[index] = new BigDecimal((String)currJson.get(key));
						//else 
							//data[index] = (String)currJson.get(key);
						
						index++;
						
					}
					
					tableModel.addRow(data);
				}

				table.setFillsViewportHeight(true);
				table.setAutoCreateRowSorter(true);
				JScrollPane scrollPane = new JScrollPane(table);
				//JScrollPane scrollPane = new JScrollPane(table,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setBounds(50, 50, 1500, 600);
				scrollPane.setViewportView(table);
				//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				scrollPane.setPreferredSize(new Dimension(468, 100));
				frame.getContentPane().add(scrollPane);

				this.frame.setVisible(true);

				
			}

		}catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public ProjectionRecords() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1600, 700);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
	}
	
	private void getDataTypes(String tableName){
		
		 JSONParser parser = new JSONParser();
		 columnDataType = new HashMap<String,String>();
		 		 
			try {
				
				JSONArray headers = GlobalData.tableJSonArray.get(tableName);
			
		    	for(int i = 0 ; i < headers.size(); i++){
		    		
		    		Object temp = parser.parse(headers.get(i).toString());
					JSONObject temp1 = (JSONObject) temp;			
					String dataType = (String) temp1.get("Data Type");
					String columnName = (String) temp1.get("Column Name");
					
					columnDataType.put(columnName, dataType);
					System.out.println("Data Types: "+columnDataType);
						    	       	
		    	}
		    	
			
			} catch (ParseException e){
				e.printStackTrace();
			}	
		}
	
	
}