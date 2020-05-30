import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.DigestException;
import java.util.concurrent.TimeUnit;

public class GUI extends Frame implements Runnable, ActionListener {

	private BasicClient client;
	private JFrame frame;
	StartMenu startMenu;
	UserPage userPage;

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

		Thread thread = new Thread(new ActionListener(this));
		thread.start();

	}

	public void addFriendOption() {
		String username = JOptionPane.showInputDialog(frame, "Enter Friend's Username:");
		System.out.println(username);
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

	public static class UserPage {

		public JPanel panel;
		public JList<String> list;
		public  JButton addFriend;

		private int currentIndex;

		public JLabel testLabel;

		UserPage() {
			panel = new JPanel();
			panel = userPagePanel();
			currentIndex = 0;
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
			DefaultListModel<String> listModel = new DefaultListModel<>();
			for (int i = 0; i < 50; i++)
				listModel.addElement("Test");
			list = new JList<>(listModel);
			JScrollPane scrollPane = new JScrollPane(list);
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

		public void selectedChat(int index)
		{
			testLabel.setText(Integer.toString(index));
		}

		public synchronized void updateTab(int index) {
			if (index != currentIndex)
			{
				currentIndex = index;
				selectedChat(index);
			}
		}

	}

	public static class ActionListener implements Runnable {

		private GUI gui;

		ActionListener(GUI gui) {
			this.gui = gui;
		}

		public void onEvent() {
			while (true) {
				if (gui.userPage != null) {
					gui.userPage.updateTab(gui.userPage.list.getSelectedIndex());
				}

				// if (some other stuff to listen to)

			}
		}

		@Override
		public void run() {
			onEvent();
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
			addFriendOption();
		}

	}
}