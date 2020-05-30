import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		frame.setLayout(new BorderLayout());
		frame.add(userPage.panel, BorderLayout.CENTER);
		frame.validate();
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

		UserPage() {
			panel = new JPanel();
			panel = userPagePanel();
		}

		public JPanel userPagePanel() {
			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.weightx = 1.0;
			c.weighty = 1.0;
			c.fill = GridBagConstraints.BOTH;

			JPanel channels = new JPanel();
			channels.setLayout(new BoxLayout(channels, BoxLayout.PAGE_AXIS));
			DefaultListModel<String> listModel = new DefaultListModel<>();
			for (int i = 0; i < 50; i++)
				listModel.addElement("Test");
			list = new JList<>(listModel);
			JScrollPane scrollPane = new JScrollPane(list);
			channels.add(scrollPane);
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 0;
			panel.add(channels, c);

			JPanel chat = new JPanel();
			chat.add(new Label("Chat"));
			c.weightx = 6;
			c.gridx = 1;
			c.gridy = 0;
			panel.add(chat, c);

			return panel;
		}

	}

	@Override
	public void run() {
		while (true) {
			if (userPage != null)
			{
				System.out.println(userPage.list.getSelectedIndex());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == startMenu.createAccount) {
			if (startMenu.c_rPassword.getText().equals(startMenu.c_password.getText()))
				client.createAccount(startMenu.c_name.getText(), startMenu.c_password.getText());
		} else if (e.getSource() == startMenu.logIn) {
			client.logIn(startMenu.l_name.getText(), startMenu.l_password.getText());
		}

	}
}