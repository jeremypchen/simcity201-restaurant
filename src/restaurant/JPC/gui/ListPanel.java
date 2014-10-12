package restaurant.JPC.gui;

import restaurant.JPC.roles.CustomerRoleJPC;
import restaurant.JPC.roles.HostRoleJPC;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class ListPanel extends JPanel implements ActionListener {

	public JScrollPane pane =
			new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JPanel view = new JPanel();
	private List<JButton> list = new ArrayList<JButton>();
	private JButton addPersonB = new JButton("Add");

	private RestaurantPanel restPanel;
	private String type;

	private JCheckBox hungryCheckBox = new JCheckBox();
	private JTextField nameField = new JTextField(5);

	/**
	 * Constructor for ListPanel.  Sets up all the gui
	 *
	 * @param rp   reference to the restaurant panel
	 * @param type indicates if this is for customers or waiters
	 */
	public ListPanel(RestaurantPanel rp, String type) {
		restPanel = rp;
		this.type = type;

		setLayout(new GridBagLayout());//new BoxLayout((Container) this, BoxLayout.Y_AXIS));
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"), c);
		c.gridx = 0;
		c.gridy = 1;
		add(new JLabel("Please enter a name:"), c);
		nameField.getDocument().addDocumentListener(new DocumentListener(){
			public void removeUpdate(DocumentEvent e){
				if(nameField.getText().equals(""))
					hungryCheckBox.setEnabled(false);
				else
					hungryCheckBox.setEnabled(true);
			}
			public void changedUpdate(DocumentEvent e){
				if(nameField.getText().equals(""))
					hungryCheckBox.setEnabled(false);
				else
					hungryCheckBox.setEnabled(true);
			}
			public void insertUpdate(DocumentEvent e){
				if(nameField.getText().equals(""))
					hungryCheckBox.setEnabled(false);
				else
					hungryCheckBox.setEnabled(true);
			}
		});
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(nameField, c);

		if (type == "Customers"){
			c.gridx = 0;
			c.gridy = 2;
			c.fill = GridBagConstraints.NONE;
			add(new JLabel("Hungry?"), c);

			hungryCheckBox.setEnabled(false);
			c.gridx = 1;
			c.gridy = 2;
			add(hungryCheckBox, c); 
		}

		c.gridx = 0;
		c.gridy = 3;
		addPersonB.addActionListener(this);
		add(addPersonB);

		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 100;
		view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
		pane.setViewportView(view);
		add(pane, c);
	}

	/**
	 * Method from the ActionListener interface.
	 * Handles the event of the add button being pressed
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addPersonB) {
			// Chapter 2.19 describes showInputDialog()
			//int result = JOptionPane.showConfirmDialog(null, inputCustomerDialog, "New Customer", JOptionPane.OK_CANCEL_OPTION);
			addPerson(nameField.getText(), hungryCheckBox.isSelected());
		}
		else {
			// Isn't the second for loop more beautiful?
			/*for (int i = 0; i < list.size(); i++) {
                JButton temp = list.get(i);*/
			for (JButton temp:list){
				if (e.getSource() == temp)
					restPanel.showInfo(type, temp.getText());
			}
		}
	}

	/**
	 * If the add button is pressed, this function creates
	 * a spot for it in the scroll pane, and tells the restaurant panel
	 * to add a new person.
	 *
	 * @param name name of new person
	 */
	public void addPerson(String name, boolean isHungry) {
		if (name != null) {
			JButton button = new JButton(name);
			button.setBackground(Color.white);

			Dimension paneSize = pane.getSize();
			Dimension buttonSize = new Dimension(paneSize.width - 20,
					(int) (paneSize.height / 7));
			button.setPreferredSize(buttonSize);
			button.setMinimumSize(buttonSize);
			button.setMaximumSize(buttonSize);
			button.addActionListener(this);
			list.add(button);
			view.add(button);
			restPanel.addPerson(type, name, isHungry);//puts customer on list
			restPanel.showInfo(type, name);//puts hungry button on panel
			validate();
		}
	}
}
