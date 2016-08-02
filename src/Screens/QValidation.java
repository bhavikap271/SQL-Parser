package Screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

public class QValidation {
	static boolean cond_flag;
	static boolean order_flag;
	static ArrayList<WhereClause> conditionArray;
	public static void validateQ1(String query)
	{
		String[] words = query.toLowerCase().split("\\s+");
		int i=0;
		while(true)
		{
			System.out.println(words[i].toLowerCase().toString());
			i++;
			if(i == words.length)
			{
				break;
			}
		}
		String type= words[0].toLowerCase();
		switch(type)
		{
		case "select":
			selectValidation(words,query.toLowerCase()+" ");
			break;
		case "update":
			updateValidation(query);
			break;
		case "insert":
			insertValidation(query);
			break;
		case "delete":
			deleteValidation(query);
			break;
		default:
			JOptionPane.showMessageDialog(null, "Invalid database operation", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	public static void selectValidation(String[] words, String query)
	{
		cond_flag = false;
		order_flag =false;
		OrderBy OB = new OrderBy();
		conditionArray = new ArrayList<>();
		ArrayList<String> projection = new ArrayList<>();
		ArrayList<String> tables = new ArrayList();
		//WhereClause w1 =new WhereClause();
		ArrayList<String> alias = new ArrayList<>();
		int i=1;
		while( i< words.length && !(words[i].equals("from")))
		{
			projection.add(words[i]);
			i++;
		}
		if(i==words.length)
		{
			JOptionPane.showMessageDialog(null, "Please Specify from clause", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		i++;
		while(i< words.length && (!(words[i].equals("where")) || (words[i].equals("order")) ))
		{
			tables.add(words[i]);
			i++;
		}
		if(i!= words.length)
		{
			cond_flag =true;
			
		}
		System.out.println(tables.toString());
		if(!tableValidation(query, tables, alias))
		{
			System.out.println(tables.toString());
			return;
		}
		
			//System.out.println(projection.toString());
		if(!projectionValid(projection))
		{
			return;
		}
		int len = projection.size();
		int ind =0;
		int ind1 = query.indexOf("select");
		int ind2 = query.indexOf("from");
		String query1 = query.substring(ind1+6, ind2);
		while(ind<len-1)
		{
			int i1 = query1.indexOf((String)projection.get(ind));
			int i2 = query1.indexOf((String)projection.get(ind+1));
			String sub = query1.substring(i1+1, i2);
			System.out.println(sub);
			if(sub.trim().length()==0)
			{
				JOptionPane.showMessageDialog(null, "Invalid projection attributes", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			ind++;
		}
		if(cond_flag)
		{
			if(!conditionSplit(query))
			{
				return;
			}
			displayConditions();
		}
		if(query.contains(" order by "))
		{
			order_flag =true;
		}
		if(order_flag)
		{
			
			if(!orderValidation(OB, query))
			{
				return;
			}
			
			System.out.println(OB.Ordercols.toString()+ OB.order);
			
		}
			if(order_flag)
			{
				if(!columnValidation(projection,tables,alias,OB,cond_flag))
				{
					return;
				}
			}
			else
			{
				if(!columnValidation(projection,tables,alias,cond_flag))
				{
					return;
				}
			}
			if(order_flag)
			{
				Select.displaySelection(projection,tables,alias,OB,cond_flag);
			}
			else
			{
				Select.displaySelection(projection,tables,alias,cond_flag);
			}
			
	}
	private static boolean columnValidation(ArrayList<String> projection,ArrayList<String> tables,ArrayList<String> alias,OrderBy OB,boolean cond)
	{
		Iterator<String> itr = tables.iterator();
		while(itr.hasNext())
		{
			if(!GlobalUtil.validateTableName(itr.next()))
			{
				JOptionPane.showMessageDialog(null, "Invalid table name", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		if(projection.size()==1 && projection.get(0).equals("*"))
		{
			
		}
		else{
		Iterator<String> itr1 = projection.iterator();
		while(itr1.hasNext())
		{
			String att = itr1.next();
			if(att.contains("."))
			{
				String[] att2 = att.trim().split("\\.");
				String tnm;
				if(att2.length> 2 || att2[0].isEmpty() || att2[1].isEmpty())
				{
					JOptionPane.showMessageDialog(null, "Invalid projection attribute name", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if(alias.contains(att2[0]))
				{
					int index = alias.indexOf(att2[0]);
					tnm = tables.get(index);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Invalid projection table name"+att2[0], "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				
				if(!GlobalUtil.validateColumnNames(att2[1],tnm))
				{
					JOptionPane.showMessageDialog(null, "Invalid projection attribute name", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				att = GlobalUtil.getTableColumnName(tnm,att2[1]);
			}
			else
			{
				boolean valid = false;
				Iterator<String> itr2 = tables.iterator();
				while(itr2.hasNext())
				{
					String tb =itr2.next();
					if(GlobalUtil.validateColumnNames(att,tb))
					{
						att = GlobalUtil.getTableColumnName(tb, att);
						valid=true;
					}
				}
				if(!valid)
				{
					JOptionPane.showMessageDialog(null, "Projection attribute name not in tables", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		}
		if(cond)
		{
			Iterator it = conditionArray.iterator();
			while(it.hasNext())
			{
				WhereClause w = (WhereClause) it.next();
				if(!w.bool)
				{
					String att = w.attribute1;
					if(att.contains("."))
					{
						String[] att2 = att.trim().split("\\.");
						String tnm;
						if(att2.length> 2 || att2[0].isEmpty() || att2[1].isEmpty())
						{
							JOptionPane.showMessageDialog(null, "Invalid condition attribute name", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						if(alias.contains(att2[0]))
						{
							int index = alias.indexOf(att2[0]);
							tnm = tables.get(index);
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Invalid condition table name"+att2[0], "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						
						if(!GlobalUtil.validateColumnNames(att2[1],tnm))
						{
							JOptionPane.showMessageDialog(null, "Invalid condition attribute name", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						w.attribute1 = GlobalUtil.getTableColumnName(tnm,att2[1]);
						String dataType = GlobalUtil.getDataType(tnm,att2[1]);
						if((dataType.equals("INT") || dataType.equals("FLOAT")) && w.attribute2.contains("\'"))
						{
							JOptionPane.showMessageDialog(null, "Invalid condition", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						if(w.attribute2.contains("\'"))
						{
							String temp =w.attribute2.trim();
							String[] att21 = temp.split("\'");
							int cnt=0,index=0,value=-1;
							if(att21.length > 2 && !att21[0].isEmpty() && !att21[1].isEmpty())
							{
								JOptionPane.showMessageDialog(null, "Invalid condition", "Error", JOptionPane.ERROR_MESSAGE);
								return false;
							}
							w.attribute2 = att21[1];
							w.attribute2value = true;
						}
						else
						{
							if(GlobalUtil.validateDataType(dataType, w.attribute2))
							{
								w.attribute2value = true;
							}
							else
							{
								w.attribute2value = false;
							}
							
						}
					}
					else
					{
						String tnm = null;
						boolean valid = false;
						Iterator<String> itr2 = tables.iterator();
						while(itr2.hasNext())
						{
							tnm=itr2.next();
							if(GlobalUtil.validateColumnNames(att,tnm))
							{
								w.attribute1 = GlobalUtil.getTableColumnName(tnm, att);
								valid=true;
								break;
							}
						}
						if(!valid)
						{
							JOptionPane.showMessageDialog(null, "Condition attribute name not in tables", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						String dataType = GlobalUtil.getDataType(tnm,att);
						if((dataType.equals("INT") || dataType.equals("FLOAT")) && w.attribute2.contains("\'"))
						{
							JOptionPane.showMessageDialog(null, "Invalid condition", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						if(w.attribute2.contains("\'"))
						{
							String temp =w.attribute2.trim();
							String[] att21 = temp.split("\'");
							int cnt=0,index=0,value=-1;
							if(att21.length > 2 && !att21[0].isEmpty() && !att21[1].isEmpty())
							{
								JOptionPane.showMessageDialog(null, "Invalid condition", "Error", JOptionPane.ERROR_MESSAGE);
								return false;
							}
							w.attribute2 = att21[1];
							w.attribute2value = true;
						}
						else
						{
							if(GlobalUtil.validateDataType(dataType, w.attribute2))
							{
								w.attribute2value = true;
							}
							else
							{
								w.attribute2value = false;
							}
							
						}
					}
					if(!w.attribute2value){
					String att1 = w.attribute2;
					if(att1.contains("."))
					{
						String[] att2 = att1.trim().split("\\.");
						String tnm;
						if(att2.length> 2 || att2[0].isEmpty() || att2[1].isEmpty())
						{
							JOptionPane.showMessageDialog(null, "Invalid condition attribute name", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						if(alias.contains(att2[0]))
						{
							int index = alias.indexOf(att2[0]);
							tnm = tables.get(index);
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Invalid condition table name"+att2[0], "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						
						if(!GlobalUtil.validateColumnNames(att2[1],tnm))
						{
							JOptionPane.showMessageDialog(null, "Invalid condition attribute name", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						w.attribute2 = GlobalUtil.getTableColumnName(tnm, att2[1]);
					}
					else
					{
						boolean valid = false;
						Iterator<String> itr2 = tables.iterator();
						while(itr2.hasNext())
						{
							String tb =itr2.next();
							if(GlobalUtil.validateColumnNames(att1,tb))
							{
								w.attribute2 = GlobalUtil.getTableColumnName(tb, att1);
								valid=true;
							}
						}
						if(!valid)
						{
							JOptionPane.showMessageDialog(null, "Condition attribute name not in tables", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
					}
					}
				
				}
			}
		}
		Iterator it1 = OB.Ordercols.iterator();
		while(it1.hasNext())
		{
			String att =(String) it1.next();
			if(att.contains("."))
			{
				String[] att2 = att.trim().split("\\.");
				String tnm;
				if(att2.length> 2 || att2[0].isEmpty() || att2[1].isEmpty())
				{
					JOptionPane.showMessageDialog(null, "Invalid order attribute name", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if(alias.contains(att2[0]))
				{
					int index = alias.indexOf(att2[0]);
					tnm = tables.get(index);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Invalid order table name"+att2[0], "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				
				if(!GlobalUtil.validateColumnNames(att2[1],tnm))
				{
					JOptionPane.showMessageDialog(null, "Invalid order attribute name", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				att = GlobalUtil.getTableColumnName(tnm,att2[1]);
			}
			else
			{
				boolean valid = false;
				Iterator<String> itr2 = tables.iterator();
				while(itr2.hasNext())
				{
					String tb= itr2.next();
					if(GlobalUtil.validateColumnNames(att,tb))
					{
						att = GlobalUtil.getTableColumnName(tb, att);
						valid=true;
						break;
					}
				}
				if(!valid)
				{
					JOptionPane.showMessageDialog(null, "Order attribute name not in tables", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		
		}
		updateProjectionAtt(projection,tables);
		updateOrderAtt(OB,tables);
		System.out.println("Valid");
		return true;
	}
	public static void updateProjectionAtt(ArrayList<String> projection,ArrayList<String> tables)
	{
		if(projection.size()==1 && projection.get(0).equals("*"))
		{
			return;
		}
		int ind = projection.size();
		for(int i=0;i<ind;i++)
		{
			String tb = null;
			Iterator<String> itr = tables.iterator();
			while(itr.hasNext())
			{
				String tnm=itr.next();
				if(GlobalUtil.validateColumnNames(projection.get(i), tnm))
				{
					tb = tnm;
					break;
				}
			}
			String att = GlobalUtil.getTableColumnName(tb, projection.get(i));
			projection.set(i, att);
		}
	}
	public static void updateOrderAtt(OrderBy OB,ArrayList<String> tables)
	{
		int ind = OB.Ordercols.size();
		for(int i=0;i<ind;i++)
		{
			String tb = null;
			Iterator<String> itr = tables.iterator();
			while(itr.hasNext())
			{
				String tnm=itr.next();
				if(GlobalUtil.validateColumnNames(OB.Ordercols.get(i), tnm))
				{
					tb = tnm;
					break;
				}
			}
			String att = GlobalUtil.getTableColumnName(tb, OB.Ordercols.get(i));
			OB.Ordercols.set(i, att);
		}
	}
	private static boolean columnValidation(ArrayList<String> projection,ArrayList<String> tables,ArrayList<String> alias,boolean cond)
	{
		Iterator<String> itr = tables.iterator();
		while(itr.hasNext())
		{
			if(!GlobalUtil.validateTableName(itr.next()))
			{
				JOptionPane.showMessageDialog(null, "Invalid table name", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		if(projection.size()==1 && projection.get(0).equals("*"))
		{
			
		}
		else{
		Iterator<String> itr1 = projection.iterator();
		while(itr1.hasNext())
		{
			String att = itr1.next();
			if(att.contains("."))
			{
				String[] att2 = att.trim().split("\\.");
				String tnm;
				if(att2.length> 2 || att2[0].isEmpty() || att2[1].isEmpty())
				{
					JOptionPane.showMessageDialog(null, "Invalid projection attribute name", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if(alias.contains(att2[0]))
				{
					int index = alias.indexOf(att2[0]);
					tnm = tables.get(index);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Invalid projection table name"+att2[0], "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				
				if(!GlobalUtil.validateColumnNames(att2[1],tnm))
				{
					JOptionPane.showMessageDialog(null, "Invalid projection attribute name", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				att = GlobalUtil.getTableColumnName(tnm,att2[1]);
			}
			else
			{
				boolean valid = false;
				Iterator<String> itr2 = tables.iterator();
				while(itr2.hasNext())
				{
					String tb =itr2.next();
					if(GlobalUtil.validateColumnNames(att,tb))
					{
						att = GlobalUtil.getTableColumnName(tb, att);
						valid=true;
					}
				}
				if(!valid)
				{
					JOptionPane.showMessageDialog(null, "Projection attribute name not in tables", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		}
		if(cond)
		{
			Iterator it = conditionArray.iterator();
			while(it.hasNext())
			{
				WhereClause w = (WhereClause) it.next();
				if(!w.bool)
				{
					String att = w.attribute1;
					if(att.contains("."))
					{
						String[] att2 = att.trim().split("\\.");
						String tnm;
						if(att2.length> 2 || att2[0].isEmpty() || att2[1].isEmpty())
						{
							JOptionPane.showMessageDialog(null, "Invalid condition attribute name", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						if(alias.contains(att2[0]))
						{
							int index = alias.indexOf(att2[0]);
							tnm = tables.get(index);
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Invalid condition table name"+att2[0], "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						
						if(!GlobalUtil.validateColumnNames(att2[1],tnm))
						{
							JOptionPane.showMessageDialog(null, "Invalid condition attribute name", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						w.attribute1 = GlobalUtil.getTableColumnName(tnm,att2[1]);
						String dataType = GlobalUtil.getDataType(tnm,att2[1]);
						if((dataType.equals("INT") || dataType.equals("FLOAT")) && w.attribute2.contains("\'"))
						{
							JOptionPane.showMessageDialog(null, "Invalid condition", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						if(w.attribute2.contains("\'"))
						{
							String temp =w.attribute2.trim();
							String[] att21 = temp.split("\'");
							int cnt=0,index=0,value=-1;
							if(att21.length > 2 && !att21[0].isEmpty() && !att21[1].isEmpty())
							{
								JOptionPane.showMessageDialog(null, "Invalid condition", "Error", JOptionPane.ERROR_MESSAGE);
								return false;
							}
							w.attribute2 = att21[1];
							w.attribute2value = true;
						}
						else
						{
							if(GlobalUtil.validateDataType(dataType, w.attribute2))
							{
								w.attribute2value = true;
							}
							else
							{
								w.attribute2value = false;
							}
							
						}
					}
					else
					{
						String tnm = null;
						boolean valid = false;
						Iterator<String> itr2 = tables.iterator();
						while(itr2.hasNext())
						{
							tnm=itr2.next();
							if(GlobalUtil.validateColumnNames(att,tnm))
							{
								w.attribute1 = GlobalUtil.getTableColumnName(tnm, att);
								valid=true;
								break;
							}
						}
						if(!valid)
						{
							JOptionPane.showMessageDialog(null, "Condition attribute name not in tables", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						String dataType = GlobalUtil.getDataType(tnm,att);
						if((dataType.equals("INT") || dataType.equals("FLOAT")) && w.attribute2.contains("\'"))
						{
							JOptionPane.showMessageDialog(null, "Invalid condition", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						if(w.attribute2.contains("\'"))
						{
							String temp =w.attribute2.trim();
							String[] att21 = temp.split("\'");
							int cnt=0,index=0,value=-1;
							if(att21.length > 2 && !att21[0].isEmpty() && !att21[1].isEmpty())
							{
								JOptionPane.showMessageDialog(null, "Invalid condition", "Error", JOptionPane.ERROR_MESSAGE);
								return false;
							}
							w.attribute2 = att21[1];
							w.attribute2value = true;
						}
						else
						{
							if(GlobalUtil.validateDataType(dataType, w.attribute2))
							{
								w.attribute2value = true;
							}
							else
							{
								w.attribute2value = false;
							}
							
						}
					}
					if(!w.attribute2value){
					String att1 = w.attribute2;
					if(att1.contains("."))
					{
						String[] att2 = att1.trim().split("\\.");
						String tnm;
						if(att2.length> 2 || att2[0].isEmpty() || att2[1].isEmpty())
						{
							JOptionPane.showMessageDialog(null, "Invalid condition attribute name", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						if(alias.contains(att2[0]))
						{
							int index = alias.indexOf(att2[0]);
							tnm = tables.get(index);
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Invalid condition table name"+att2[0], "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						
						if(!GlobalUtil.validateColumnNames(att2[1],tnm))
						{
							JOptionPane.showMessageDialog(null, "Invalid condition attribute name", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
						w.attribute2 = GlobalUtil.getTableColumnName(tnm, att2[1]);
					}
					else
					{
						boolean valid = false;
						Iterator<String> itr2 = tables.iterator();
						while(itr2.hasNext())
						{
							String tb =itr2.next();
							if(GlobalUtil.validateColumnNames(att1,tb))
							{
								w.attribute2 = GlobalUtil.getTableColumnName(tb, att1);
								valid=true;
							}
						}
						if(!valid)
						{
							JOptionPane.showMessageDialog(null, "Condition attribute name not in tables", "Error", JOptionPane.ERROR_MESSAGE);
							return false;
						}
					}
					}
				
				}
			}
		}
		updateProjectionAtt(projection,tables);
		System.out.println("Valid");
		return true;
	}
	private static boolean orderValidation(OrderBy OB, String query)
	{
		query+=" ";
		int ind1 = query.indexOf("order by"); 
		int ind2 =query.indexOf("asc");
		boolean order=true;
		String qtable;
		if(ind2 <= 0)
		{
			ind2 =query.indexOf("desc");
			if(ind2 <= 0)
			{
				qtable = query.substring(ind1+8);
			}
			else
			{
				order=false;
				qtable = query.substring(ind1+8, ind2);
			}
		}
		else
		{
			qtable = query.substring(ind1+8, ind2);
		}
		String[] attributes = qtable.split(",");
		int index = 0;
		while(index < attributes.length)
		{
			if(attributes[index].trim().length()==0 || attributes[index].isEmpty())
			{
				JOptionPane.showMessageDialog(null, "Invalid order by attributes", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			OB.Ordercols.add(attributes[index].trim());
			index++;
		}
		OB.order=order;
		return true;
	}
	private static void displayConditions() {
		Iterator<WhereClause> itr = conditionArray.iterator();
		while(itr.hasNext())
		{
			WhereClause temp = itr.next();
			if(temp.bool)
			{
				if(temp.boolOP)
					System.out.print(" AND ");
				else
					System.out.print(" OR ");
			}
			else
			{
				System.out.print(temp.attribute1+" "+temp.operation+" "+temp.attribute2);
			}
		}
		
	}
	public static boolean conditionSplit(String query)
	{
		//query = query.trim();
		int ind1 = query.indexOf("where"); 
		int ind2 =query.indexOf("order by");
		String qtable;
		if(ind2 <= 0)
		{
			qtable = query.substring(ind1+5);
		}
		else
		{
			qtable = query.substring(ind1+5, ind2);
		}
		String[] andString = qtable.split("and");
		int len = andString.length;
		int ind =0 ;
		while(ind < len)
		{
			if(ind!=0)
			{
				WhereClause wr1 =new WhereClause();
				wr1.bool = true;
				wr1.boolOP = true;
				conditionArray.add(wr1);
			}
			if(andString[ind].isEmpty() || andString[ind].trim().length()==0)
			{
				JOptionPane.showMessageDialog(null, "Invalid projection attributes", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			String[] orString = andString[ind].split(" or ");
			int len1 = orString.length;
			int ind11 =0 ;
			while(ind11 < len1)
			{
				if(ind11!=0)
				{
					WhereClause wr =new WhereClause();
					wr.bool = true;
					wr.boolOP = false;
					conditionArray.add(wr);
				}
				if(orString[ind11].isEmpty() || orString[ind11].trim().length()==0)
				{
					JOptionPane.showMessageDialog(null, "Invalid projection attributes", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				WhereClause tempWr = new WhereClause();
				if(!conditionValidation(orString[ind11],tempWr))
				{
					return false;
				}
				conditionArray.add(tempWr);
				ind11++;
			}
			ind++;
		}
		return true;
	}
	public static boolean conditionValidation(String qtable, WhereClause w1)
	{
		qtable+=" ";
		char ops = 0;
		ArrayList<String> cond =new ArrayList<>();
		String[] q1 = qtable.split(">");
		int len = q1.length;
		int ind =0;
		while(ind<len)
		{
			if(q1[ind].isEmpty() || q1[ind].trim().length()==0)
			{
				JOptionPane.showMessageDialog(null, "Invalid conditions in where clause", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			ind++;
		}
		if(q1.length>2)
		{
			JOptionPane.showMessageDialog(null, "Invalid conditions in where clause", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else if(q1.length==2)
		{
			cond.add(q1[0].trim());
			cond.add(q1[1].trim());
			ops ='>';
		}
		else if(q1.length==1)
		{
			String[] q11 = qtable.split("<");
			int len1 = q11.length;
			int ind11 =0;
			while(ind11<len1)
			{
				if(q11[ind11].isEmpty() || q11[ind11].trim().length()==0)
				{
					JOptionPane.showMessageDialog(null, "Invalid conditions in where clause", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				ind11++;
			}
			if(q11.length>2)
			{
				JOptionPane.showMessageDialog(null, "Invalid conditions in where clause", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			else if(q11.length==2)
			{
				cond.add(q11[0].trim());
				cond.add(q11[1].trim());
				ops ='<';
			}
			else if(q11.length==1)
			{
				String[] q111 = qtable.split("=");
				int len11 = q111.length;
				int ind111 =0;
				while(ind111<len11)
				{
					if(q111[ind111].isEmpty() || q111[ind111].trim().length()==0)
					{
						JOptionPane.showMessageDialog(null, "Invalid conditions in where clause", "Error", JOptionPane.ERROR_MESSAGE);
						return false;
					}
					ind111++;
				}
				if(q111.length>2)
				{
					JOptionPane.showMessageDialog(null, "Invalid conditions in where clause", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				else if(q111.length==2)
				{
					cond.add(q111[0].trim());
					cond.add(q111[1].trim());
					ops ='=';
				}
				else if(q111.length==1)
				{
					JOptionPane.showMessageDialog(null, "Please Specify a condition", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		System.out.println("conditions :"+cond.toString()+ "operation "+ ops);
		if(cond.size()>2)
		{
			JOptionPane.showMessageDialog(null, "Invalid condition", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		w1.attribute1 = cond.get(0);
		w1.attribute2 = cond.get(1);
//		if(cond.get(1).contains("\'"))
//		{
//			String temp =cond.get(1).trim();
//			String[] att2 = temp.split("\'");
//			int cnt=0,index=0,value=-1;
//			if(att2.length > 2 && !att2[0].isEmpty() && !att2[1].isEmpty())
//			{
//				JOptionPane.showMessageDialog(null, "Invalid condition", "Error", JOptionPane.ERROR_MESSAGE);
//				return false;
//			}
//			w1.attribute2 = att2[1];
//			w1.attribute2value = true;
//		}
//		else
//		{
//			try 
//			{
//				w1.attribute2 = cond.get(1);
//				w1.attribute2value= true;
//			}
//			catch()
//			{
//				w1.attribute2 = cond.get(1);
//				w1.attribute2value= false;
//			}
//			
//		}
		w1.bool =false;
		w1.operation = ops;
		//System.out.println("conditions :"+conditions.toString());
		return true;
	}
	public static boolean tableValidation(String query, ArrayList<String> tables, ArrayList< String> alias)
	{
		tables.clear();
		int ind1 = query.indexOf("from"); 
		int ind2 =query.indexOf("where");
		String qtable;
		if(ind2 < 0)
		{
			ind2 = query.indexOf("order by");
			if(ind2<0)
			{
				qtable = query.substring(ind1+4);
			}
			else
			{
				qtable = query.substring(ind1+4, ind2);
			}
		}
		else
		{
			qtable = query.substring(ind1+4, ind2);
		}
		String[] tabs = qtable.split(",");
		int len = tabs.length;
		int ind =0;
		while(ind < len)
		{
			if(tabs[ind].isEmpty() || tabs[ind].trim().length()==0)
			{
				JOptionPane.showMessageDialog(null, "Invalid tables", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			ind++;
		}
		ind = 0;
		while(ind < len)
		{
			String[] tempQ = tabs[ind].split(" ");
			ArrayList<String> tQ= new ArrayList<>(); 
			int tlen = tempQ.length;
			int i1= 0;
			while(i1<tlen)
			{
				if(!tempQ[i1].isEmpty())
				{
					tQ.add(tempQ[i1]);
				}
				i1++;
			}
			if(tQ.size() > 2)
			{
				JOptionPane.showMessageDialog(null, "Invalid tables", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			else if(tQ.size() == 2)
			{
				if(alias.contains(tQ.get(1)))
				{
					JOptionPane.showMessageDialog(null, "Table alias name repeted", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				tables.add(tQ.get(0));
				alias.add(tQ.get(1));
			}
			else if(tQ.size() == 1)
			{
				tables.add(tQ.get(0));
				alias.add("NULL");
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Invalid tables", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			ind++;
		}
		System.out.println("tables :"+tables.toString());
		System.out.println("Alias :"+alias.toString());
		return true;
	}
	

	public static boolean projectionValid(ArrayList<String> projection)
	{
		if(projection.size()==1 && projection.get(0).equals("*"))
		{
			return true;
		}
		if(projection.contains(",") && (projection.get(projection.size()-1).equals(",") || projection.get(0).equals(",")))
		{
			JOptionPane.showMessageDialog(null, "Invalid location of \",\"", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		while(projection.contains(","))
		{
			int index = projection.indexOf(",");
			projection.remove(index);
		}
		Iterator itr1 =projection.iterator();
		String tempProj1="";
		while(itr1.hasNext())
		{
			tempProj1+=itr1.next();
			
		}
		if(tempProj1.contains(",,"))
		{
			JOptionPane.showMessageDialog(null, "Invalid sequence \",,\"", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		Iterator itr =projection.iterator();
		String tempProj="";
		while(itr.hasNext())
		{
			tempProj+=itr.next()+" ";
			
		}
		if(tempProj.charAt(0)==',' || tempProj.charAt(tempProj.length()-1)==',')
		{
			JOptionPane.showMessageDialog(null, "Invalid location of \",\"", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		//tempProj.replace(",", " ");
		System.out.println(tempProj);
		String[] proj = tempProj.split(",");
		
		projection.clear();
		System.out.println("old : "+projection.toString());;
		int len =0 ;
		while(len < proj.length)
		{
			if(proj[len].contains(" "))
			{
				String[] tempS = proj[len].split(" ");
				int ind = 0;
				while(ind<tempS.length)
				{
					if(!tempS[ind].isEmpty())
					{
						projection.add(tempS[ind]);
					}
					ind++;
				}
				
			}
			else
			{
				projection.add(proj[len]);
			}
			System.out.println(proj[len]);
			len++;
		}
		
		System.out.println(projection.toString());;
		return true;
 	}
	

	
	public static void updateValidation(String statement)
	{
		
		Update update = new Update();
		boolean result = update.parse(statement);
		
		if(result){			
			 JOptionPane.showMessageDialog(null, "Records updated successfully", "Message", JOptionPane.INFORMATION_MESSAGE);
			 return;
		}
		
	}
	
	public static void insertValidation(String statement)
	{				
	     Insert insert = new Insert();
	     insert.parse(statement);
	     		
	}
	
	
	
	
	public static void deleteValidation(String query)
	{
		
		Delete delete = new Delete();
		//validate the syntax and semantics
		boolean isValid = delete.parse(query);
		
		if(isValid){
			 delete.deleteRecords();
			 JOptionPane.showMessageDialog(null, "Records deleted successfully", "Message", JOptionPane.INFORMATION_MESSAGE);
			 return;
		}	
		
		
	}
}
	
	
	

