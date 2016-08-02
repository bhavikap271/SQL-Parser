package Screens;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.html.HTMLDocument.Iterator;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TableModelEvent;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class ListBtree extends JFrame {

	private JPanel contentPane;
	private final JButton btnDisplayRecords = new JButton("Display BTree");

	/**
	 * Create the frame.
	 */

	public ListBtree() {

		setTitle("List of BTree");
		setBounds(100, 100, 558, 275);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JTable table = new JTable() {

			public void tableChanged(TableModelEvent e) {
				super.tableChanged(e);
				repaint();
			}

		};

		table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Attribute Name", "Table Name" }));
		table.setBackground(Color.WHITE);

		int rowCount = GlobalData.allTables.size();
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		for (String attName : GlobalData.AttBTreeIndex.keySet()) {

			Object[] data = { attName, GlobalData.AttTableMap.get(attName) };
			tableModel.addRow(data);

		}

		// table.setPreferredScrollableViewportSize(new Dimension(400, 30));
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(37, 5, 468, 100);
		scrollPane.setViewportView(table);
		scrollPane.setPreferredSize(new Dimension(468, 100));
		contentPane.add(scrollPane);

		btnDisplayRecords.setBounds(379, 116, 128, 25);
		btnDisplayRecords.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		btnDisplayRecords.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				DefaultTableModel dm = (DefaultTableModel) table.getModel();
				int rowIndex = table.getSelectedRow();
				if (rowIndex == -1) {
					JOptionPane.showMessageDialog(null, "Please select a btree to display", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					String tableName = GlobalData.allTables.get(rowIndex);

					displayJTree display = new displayJTree((String) table.getValueAt(rowIndex, 0));
				}
			}
		});

		contentPane.add(btnDisplayRecords);
	}

}
