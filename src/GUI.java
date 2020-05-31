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
	public static String currentId;

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
		userPage.sendMessage.addActionListener(this);
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


	private class UserPage {

		public JPanel panel;

		public JList<String> list;
		public  JButton addFriend, sendMessage;
		public JTextArea message;
		public JScrollPane scrollPane;
		DefaultListModel<String> listModel;

		public JLabel chatInfo;

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

			// Banner
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
			//--------------------------------------

			// List of Chats
			JPanel channels = new JPanel();
			channels.setLayout(new BoxLayout(channels, BoxLayout.PAGE_AXIS));
			channels.add(scrollPane);
			c.weighty = 1;
			c.gridx = 0;
			c.gridy = 1;
			panel.add(channels, c);
			// ----------------------------------

			// Chat Panel
			JPanel chat = new JPanel(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.weighty = 1;
			constraints.weightx = 1;
			constraints.fill = GridBagConstraints.BOTH;
			chatInfo = new JLabel("No Chat Selected", SwingConstants.CENTER);
			chatInfo.setFont(new Font("Serif", Font.PLAIN, 20));
			JScrollPane jsp1 = new JScrollPane(chatInfo);
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 2;
			constraints.weighty = 20;
			chat.add(jsp1, constraints);

			JPanel messages = new JPanel();
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.gridwidth = 2;
			constraints.weighty = 500;
			chat.add(messages, constraints);

			message = new JTextArea();
			JScrollPane jsp2 = new JScrollPane(message);
			constraints.gridx = 0;
			constraints.gridy = 2;
			constraints.gridwidth = 1;
			constraints.weighty = 0;
			constraints.weightx = 100;
			chat.add(jsp2, constraints);

			sendMessage = new JButton("Send");
			constraints.gridx = 1;
			constraints.gridy = 2;
			constraints.weightx = 10;
			chat.add(sendMessage, constraints);

			c.weightx = 100;
			c.gridheight = 2;
			c.gridx = 1;
			c.gridy = 0;
			panel.add(chat, c);
			//----------------------------------------------------------

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
			chatInfo.setText(client.chats.get(index).name);
			currentId = client.chats.get(index).id;
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
		} else if (e.getSource() == userPage.sendMessage) {
			Message message = new Message(client.username, userPage.message.getText());
			client.sendMessage(message, currentId);
			userPage.message.setText("");
		}

	}
}