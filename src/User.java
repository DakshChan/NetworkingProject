public class User {

	private String name;
	private String password;

	public User(String name, String password) {
		this.name = name;
		this.password = password;
	}

	@Override
	public boolean equals(Object obj) {
		User user = (User) obj;
		return user.name.equals(this.name) && user.password.equals(this.password);
	}

	public String  getName() { return name; }
	public String getPassword() { return password; }

}
