
package Screens;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.json.simple.JSONObject;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;

public class CreateTable extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private ButtonGroup group = new ButtonGroup();

	// static List<String> tableNames = new ArrayList<String>();

	/**
	 * Create the frame.
	 */
	public CreateTable() {

		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 800, 300);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.setOpaque(true);
		setContentPane(contentPane);

		// add table here
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 41, 584, 111);

		JRadioButton radioButton = new JRadioButton();
		radioButton.setOpaque(false);
		group.add(radioButton);

		String[] columnNames = { "Key", "Column Name", "Data Type" };

		DefaultTableModel model = new DefaultTableModel();
		model.setDataVector(new Object[][] { { radioButton, "", "Choose.." } }, columnNames);

		final JTable table = new JTable(model) {
			public void tableChanged(TableModelEvent e) {
				super.tableChanged(e);
				repaint();
			}
		};

		table.setBackground(Color.WHITE);

		// default values for dataType:
		table.setBounds(20, 53, 584, 100);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);

		// add Jcombobox:
		String[] dataTypes = { "VARCHAR", "INT", "FLOAT" };
		JComboBox comboBox = new JComboBox(dataTypes);

		/// our combo column
		TableColumn col = table.getColumnModel().getColumn(2);
		col.setCellEditor(new DefaultCellEditor(comboBox));

		// set renderer and editor for radioButtons
		TableColumn radioColumn = table.getColumnModel().getColumn(0);
		radioColumn.setCellEditor(new RadioEditor(new JCheckBox()));
		radioColumn.setCellRenderer(new RadioRenderer());
		radioColumn.setMaxWidth(50);

		scrollPane.setViewportView(table);
		contentPane.add(scrollPane);

		textField = new JTextField();
		textField.setBounds(107, 10, 497, 20);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Table Name:");
		lblNewLabel.setFont(new Font("Perpetua", Font.BOLD, 13));
		lblNewLabel.setBounds(20, 14, 77, 14);
		contentPane.add(lblNewLabel);

		JButton btnNewButton = new JButton("OK");

		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// get data from the table and save it in json format:
				// check if atleast one radio button is selected.

				String tableName = textField.getText();

				int rowIndex = getSelectedRadioButton();
				System.out.println("Row selected: " + rowIndex);

				if ("".equals(tableName)) {
					JOptionPane.showMessageDialog(null, "Please enter Table Name", "Error", JOptionPane.ERROR_MESSAGE);
				} else if (GlobalData.allTables.contains(tableName)) {
					JOptionPane.showMessageDialog(null, "Table Name already Exists", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {

					if (rowIndex == -1) {
						JOptionPane.showMessageDialog(null, "Please select atleaset one column as Key", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
					/*
					 * else if(check){ check =false;
					 * JOptionPane.showMessageDialog(null,
					 * "Cannot have duplicate column names", "Error",
					 * JOptionPane.ERROR_MESSAGE); }
					 */
					else {
						// System.out.println(rowIndex);
						DefaultTableModel dm = (DefaultTableModel) table.getModel();
						int rowCount = dm.getRowCount();
						int colCount = dm.getColumnCount();

						File file = new File("Data/Metadata/" + tableName + ".json");
						File file2 = new File("Data/Records/" + tableName + ".json");

						FileWriter fw1;

						try {

							fw1 = new FileWriter(file2.getAbsoluteFile());
							BufferedWriter bw1 = new BufferedWriter(fw1);
							bw1.write("{\"Records\":[]}");
							bw1.flush();
							bw1.close();
							fw1.close();

						} catch (IOException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						}

						FileWriter fw = null;
						BufferedWriter bw = null;

						try {

							fw = new FileWriter(file.getAbsoluteFile());
							bw = new BufferedWriter(fw);

						} catch (IOException e1) {

							e1.printStackTrace();
						}

						String records = "{\"headers\":[";
						for (int i = 0; i < rowCount; i++) {
							JSONObject json = new JSONObject();
							for (int j = 0; j < colCount; j++) {
								if (j == 0 && i == rowIndex) {
									json.put(table.getColumnName(j), Boolean.TRUE);
								}
								else {
									if (j == 0) {
										json.put(table.getColumnName(j), Boolean.FALSE);
									} else {

										if (j == 2) {

											// System.out.println("j:"+j);
											String dataType = (String) table.getModel().getValueAt(i, j);
											// String valueSelected =
											if ("Choose..".equals(dataType)) {
												// System.out.println("I am
												// here");
												table.getModel().setValueAt("VARCHAR", i, j);
											}

										}

										json.put(table.getColumnName(j), table.getModel().getValueAt(i, j));
									}
								}
							}

							if (i == (rowCount - 1)) {
								records += json.toString();
							} else {
								records += json.toString() + ",";
							}

							// write json data to file
							/*
							 * try { //System.out.println(json.toJSONString());
							 * bw.write(json.toString()); } catch (IOException
							 * e1) { // TODO Auto-generated catch block
							 * e1.printStackTrace(); }
							 */
						}

						records += "]}";
						System.out.println(records);
						try {
							bw.write(records);
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						try {
							bw.flush();
							bw.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// save the tableName in globalList
						GlobalData.allTables.add(tableName);

						try {
							GlobalData.updateTableFile();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							GlobalData.addAttTableMap(tableName);
							GlobalData.addTableJSonArray(tableName);
							System.out.println("test!!!"+GlobalData.tableJSonArray.get(tableName).toString());
							GlobalData.addAttBTreeIndex(tableName, GlobalData.tablePrimaryKeyMap.get(tableName.toLowerCase()));
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						
						JOptionPane.showMessageDialog(null, "Table created successfully", "Success",
								JOptionPane.INFORMATION_MESSAGE);
						dispose();
					}
				}
			}
		});

		btnNewButton.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		btnNewButton.setBounds(557, 227, 89, 23);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Cancel");
		btnNewButton_1.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		btnNewButton_1.setBounds(678, 227, 96, 23);
		contentPane.add(btnNewButton_1);

		JButton btnAddColumn = new JButton("Add Column");
		btnAddColumn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel dm = (DefaultTableModel) table.getModel();
				JRadioButton radioButton = new JRadioButton();
				group.add(radioButton);
				Object[] rowData = { radioButton, "", "Choose.." };
				dm.addRow(rowData);

			}
		});

		btnAddColumn.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		btnAddColumn.setBounds(651, 52, 123, 23);
		contentPane.add(btnAddColumn);

		JButton btnDeleteColumn = new JButton("Remove Column");
		btnDeleteColumn.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		btnDeleteColumn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int rowNum = table.getSelectedRow();
				int count = 0;
				for (Enumeration e1 = group.getElements(); e1.hasMoreElements();) {
					JRadioButton b = (JRadioButton) e1.nextElement();
					if (count == rowNum) {
						group.remove(b);
						break;
					} else
						count++;
				}

				DefaultTableModel dm = (DefaultTableModel) table.getModel();
				dm.removeRow(table.getSelectedRow());
			}
		});

		btnDeleteColumn.setBounds(651, 94, 123, 23);
		contentPane.add(btnDeleteColumn);

	}

	public int getSelectedRadioButton() {
		int rowIndex = 0;

		boolean flag = false;
		for (Enumeration e = group.getElements(); e.hasMoreElements();) {
			JRadioButton b = (JRadioButton) e.nextElement();
			if (b.isSelected()) {
				flag = true;
				break;
				// return rowIndex;
			}
			rowIndex++;
		}

		if (!flag) {
			// show a dialog box
			rowIndex = -1;
		}

		return rowIndex;
	}

}

class RadioRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		// TODO Auto-generated method stub
		if (value == null)
			return null;
		return (Component) value;
	}

}

class RadioEditor extends DefaultCellEditor implements ItemListener {

	JRadioButton radio;

	public RadioEditor(JCheckBox checkBox) {
		super(checkBox);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		// TODO Auto-generated method stub

		if (value == null)
			return null;
		radio = (JRadioButton) value;
		radio.addItemListener(this);
		return (Component) value;
	}

	@Override
	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		radio.removeItemListener(this);
		return radio;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		super.fireEditingStopped();
	}

}
