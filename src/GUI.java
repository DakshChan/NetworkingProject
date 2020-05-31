import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends Frame implements Runnable, ActionListener {

	private BasicClient client;
	private JFrame frame;
	StartMenu startMenu;
	UserPage userPage;

	public enum OptionType {
		INPUT_NAME,
		NO_MATCH,
		FOUND_MATCH
	}

	public GUI(BasicClient client) {

		this.client = client;

		frame = new JFrame("Piper Chat");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setLayout(new GridLayout(1, 2));

		startMenu = new StartMenu();

		startMenu.logIn.addActionListener(this);
		startMenu.createAccount.addActionListener(this);

		frame.add(startMenu.panel);
		frame.validate();

	}

	public void logIn() {
		frame.remove(startMenu.panel);
		userPage = new UserPage();
		userPage.addFriend.addActionListener(this);
		frame.setLayout(new BorderLayout());
		frame.add(userPage.panel, BorderLayout.CENTER);
		frame.validate();
	}

	public void addFriendOption(OptionType type, String name) {

		switch (type) {
			case INPUT_NAME:
				String username = JOptionPane.showInputDialog(frame, "Enter Friend's Username:");
				if (username.length() > 0)
					client.addFriend(username);
				break;
			case FOUND_MATCH:
				userPage.listModel.addElement(name);
				userPage.updateChats();
				revalidate();
				JOptionPane.showMessageDialog(frame, "Friend Added Successfully");
				break;
			case NO_MATCH:
				JOptionPane.showMessageDialog(frame, "Friend Could Not Be Added", "Friend Not Found", JOptionPane.ERROR_MESSAGE);
				break;
		}

	}

	private static class StartMenu {

		public JPanel panel;
		public JButton createAccount, logIn;
		public JTextField l_password, l_name, c_password, c_rPassword, c_name;

		StartMenu() {
			panel = new JPanel();
			panel.add(createAccountPanel());
			panel.add(logInPanel());
		}

		public JPanel logInPanel() {

			JPanel panel = new JPanel();

			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			Border border = BorderFactory.createTitledBorder("Log In");
			panel.setBorder(border);

			panel.add(new JLabel("Username:"));
			l_name = new JTextField();
			l_name.setMaximumSize(new Dimension(300, 25));
			panel.add(l_name);

			panel.add(new JLabel("Password:"));
			l_password = new JPasswordField();
			l_password.setMaximumSize(new Dimension(300, 25));
			panel.add(l_password);

			logIn = new JButton("Log In");
			panel.add(logIn);

			return panel;
		}

		public JPanel createAccountPanel() {

			JPanel panel = new JPanel();

			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			Border border = BorderFactory.createTitledBorder("Create Account");
			panel.setBorder(border);

			panel.add(new JLabel("Username:"));
			c_name = new JTextField();
			c_name.setMaximumSize(new Dimension(300, 25));
			panel.add(c_name);

			panel.add(new JLabel("Password:"));
			c_password = new JPasswordField();
			c_password.setMaximumSize(new Dimension(300, 25));
			panel.add(c_password);

			panel.add(new JLabel("Repeat Password:"));
			c_rPassword = new JPasswordField();
			c_rPassword.setMaximumSize(new Dimension(300, 25));
			panel.add(c_rPassword);

			createAccount = new JButton("Create Account");
			panel.add(createAccount);

			return panel;
		}

	}



	// ---------------------------------------------



	private class UserPage {

		public JPanel panel;

		public JList<String> list;
		public  JButton addFriend;
		public JScrollPane scrollPane;
		DefaultListModel<String> listModel;

		public JLabel testLabel;

		UserPage() {
			panel = new JPanel();
			listModel = new DefaultListModel<>();
			updateChats();
			panel = userPagePanel();
		}

		public JPanel userPagePanel() {
			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.weightx = 1.0;
			c.weighty = 1.0;
			c.fill = GridBagConstraints.BOTH;

			JPanel banner = new JPanel();
			banner.setLayout(new BoxLayout(banner, BoxLayout.PAGE_AXIS));
			JLabel icon = new JLabel();
			icon.setIcon(new ImageIcon(getClass().getResource("assets/logo.png")));
			icon.setAlignmentX(Component.CENTER_ALIGNMENT);
			banner.add(icon);
			//banner.add(Box.createRigidArea(new Dimension(0, 10))); add space between the elements
			addFriend = new JButton("Add Friend");
			addFriend.setAlignmentX(Component.CENTER_ALIGNMENT);
			banner.add(addFriend);
			c.weighty = 0;
			c.gridy = 0;
			c.gridx = 0;
			panel.add(banner, c);

			JPanel channels = new JPanel();
			channels.setLayout(new BoxLayout(channels, BoxLayout.PAGE_AXIS));

			channels.add(scrollPane);
			c.weighty = 1;
			c.gridx = 0;
			c.gridy = 1;
			panel.add(channels, c);

			JPanel chat = new JPanel();
			testLabel = new JLabel("0");
			chat.add(testLabel);
			c.weightx = 100;
			c.gridheight = 2;
			c.gridx = 1;
			c.gridy = 0;
			panel.add(chat, c);

			return panel;
		}

		public void updateChats() {
			list = new JList<>(listModel);
			ListSelectionModel listSelectionModel = list.getSelectionModel();
			listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
			scrollPane = new JScrollPane(list);
		}

		public void selectedChat(int index)
		{
			testLabel.setText(Integer.toString(index));
		}

		class SharedListSelectionHandler implements ListSelectionListener {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
				{
					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					selectedChat(lsm.getSelectedIndices()[0]);
				}
			}
		}

	}

	@Override
	public void run() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == startMenu.createAccount) {
			if (startMenu.c_rPassword.getText().equals(startMenu.c_password.getText()))
				client.createAccount(startMenu.c_name.getText(), startMenu.c_password.getText());
		} else if (e.getSource() == startMenu.logIn) {
			client.logIn(startMenu.l_name.getText(), startMenu.l_password.getText());
		} else if (e.getSource() == userPage.addFriend) {
			addFriendOption(OptionType.INPUT_NAME, null);
		}

	}
}