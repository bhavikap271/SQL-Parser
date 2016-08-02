package Screens;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;

public class Select {
	
	public static void displaySelection(ArrayList<String> projection,ArrayList<String> tables,ArrayList<String> alias,OrderBy OB,boolean cond_flag)
	{
		String tableName = null;
		if(tables.size()==1)
		{
			Iterator< String> itr = GlobalData.allTables.iterator();
			while(itr.hasNext())
			{
				String tnm= itr.next();
				if(tables.get(0).equalsIgnoreCase(tnm))
				{
					tableName = tnm;
					break;
				}
			}
			if(projection.size()==1 && projection.get(0).equals("*"))
			{
				ProjectionRecords display = new ProjectionRecords();
				display.projectRecords(tableName,OB,cond_flag);
			}
			else
			{
				ProjectionRecords display = new ProjectionRecords();
				display.projectRecords(tableName, projection,OB,cond_flag);
			}

			
		}
		else
		{
			if(projection.size()==1 && projection.get(0).equals("*"))
			{
				ProjectionRecords display = new ProjectionRecords();
				display.projectRecords(tables,alias,cond_flag,OB);
			}
			else
			{
				ProjectionRecords display = new ProjectionRecords();
				display.projectRecords(tables,alias, projection,cond_flag,OB);
			}
		}
	}
	public static void displaySelection(ArrayList<String> projection,ArrayList<String> tables,ArrayList<String> alias,boolean cond_flag)
	{
		String tableName = null;
		if(tables.size()==1)
		{
			Iterator< String> itr = GlobalData.allTables.iterator();
			while(itr.hasNext())
			{
				String tnm= itr.next();
				if(tables.get(0).equalsIgnoreCase(tnm))
				{
					tableName = tnm;
					break;
				}
			}	
			if(projection.size()==1 && projection.get(0).equals("*"))
			{
				ProjectionRecords display = new ProjectionRecords();
				display.projectRecords(tableName,cond_flag);
			}
			else
			{
				ProjectionRecords display = new ProjectionRecords();
				display.projectRecords(tableName, projection,cond_flag);
			}
			
		}
		else
		{
			if(projection.size()==1 && projection.get(0).equals("*"))
			{
				ProjectionRecords display = new ProjectionRecords();
				display.projectRecords(tables,alias,cond_flag);
			}
			else
			{
				ProjectionRecords display = new ProjectionRecords();
				display.projectRecords(tables,alias, projection,cond_flag);
			}
		}
	}

}
