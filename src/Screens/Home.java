package Screens;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JTextArea;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class Home extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Home frame = new Home();
					frame.setVisible(true);
					GlobalData.initTableArray();
					GlobalData.initprimaryKey();

					// newly added
					GlobalData.initTableJSonArray();
					GlobalData.initAttTableMap();
					GlobalData.initAttBTreeIndex();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}

		/**
	 * Create the frame.
	 * 
	 */
	public static <E> void saveAllBtrees()
	{
		Iterator<String> itr = GlobalData.allTables.iterator();
		while(itr.hasNext())
		{
			String tableName= itr.next();
			String att = GlobalData.tablePrimaryKeyMap.get(tableName.toLowerCase());
			if(!tableName.equalsIgnoreCase("complaints"))
			{
				SaveBtree B = new SaveBtree(att, tableName);
			}
			
		}
	}
	

	

	private void saveMainTable() {
		
		// TODO Auto-generated method stub

		try{

			Iterator<String> itr = GlobalData.allTables.iterator();

			while(itr.hasNext()){

				String tableName= itr.next();

				//String actualTableName = GlobalData.getTableName(tableName);
				JSONArray maintable = GlobalData.tableJSonArray.get(tableName);

				JSONObject newJson = new JSONObject();
				newJson.put("Records", maintable);

				File file = new File("Data/Records/" + tableName + ".json");

				if(file.exists()){
					file.delete();
				}

				FileWriter fw = null;
				BufferedWriter bw = null;

				fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);

				bw.write(newJson.toJSONString());
				bw.flush();
				bw.close();
				fw.close();

				System.out.println("data saved back to disk on close");

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	
	public Home() {
		setTitle("SQL");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
		        @Override
		        public void windowClosing(WindowEvent event) {
		        	saveMainTable();
		        	saveAllBtrees();
		            System.exit(0);
		        }

		});
		setBounds(100, 100, 900, 500);
		getContentPane().setLayout(null);

		JLabel lblSqlQuery = new JLabel("SQL Query : ");
		lblSqlQuery.setBounds(10, 44, 116, 24);
		lblSqlQuery.setFont(new Font("Times New Roman", Font.BOLD, 20));
		getContentPane().add(lblSqlQuery);

		JTextArea textArea = new JTextArea();
		textArea.setBounds(135, 30, 700, 274);

		textArea.setFont(new Font("Times New Roman", Font.PLAIN, 20));

		getContentPane().add(textArea);
		JButton btnCreate = new JButton("Create Table");
		btnCreate.setBounds(449, 400, 138, 31);
		btnCreate.setFont(new Font("Times New Roman", Font.BOLD, 15));
		btnCreate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {

					CreateTable frame = new CreateTable();
					frame.setTitle("Create Table");
					frame.setVisible(true);

				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		});
		getContentPane().add(btnCreate);
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(597, 400, 75, 31);
		btnOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String query = textArea.getText();
				if (query.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please enter a Query", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					QValidation.validateQ1(query);
				}
			}
		});
		btnOk.setFont(new Font("Times New Roman", Font.BOLD, 18));
		getContentPane().add(btnOk);

		JButton btnClear = new JButton("Clear");
		btnClear.setBounds(682, 400, 84, 31);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textArea.setText("");
			}
		});
		btnClear.setFont(new Font("Times New Roman", Font.BOLD, 15));
		getContentPane().add(btnClear);

		JButton btnLoadComplaintdb = new JButton("Load complaint.db");
		btnLoadComplaintdb.setFont(new Font("Times New Roman", Font.BOLD, 15));

		btnLoadComplaintdb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				LoadData ld = new LoadData();

				// GlobalData.allTables.add("complaints");
				try {
					// aGlobalData.updateTableFile();
					GlobalData.addTableJSonArray("complaints");
					GlobalData.addAttTableMap("complaints");
					GlobalData.addAttBTreeIndex("complaints", "id");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		btnLoadComplaintdb.setBounds(289, 400, 150, 29);
		getContentPane().add(btnLoadComplaintdb);
		
		JButton btnNewButton = new JButton("List Index");
		btnNewButton.setFont(new Font("Times New Roman", Font.BOLD, 15));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ListBtree frame = new ListBtree();
					frame.setVisible(true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(97, 400, 117, 29);
		getContentPane().add(btnNewButton);
		contentPane = new JPanel(new GridLayout(5, 5));

	}
}
