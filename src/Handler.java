import java.io.Serializable;

public class Handler implements Serializable {

	private String name;
	private String msg;

	public Handler(String name, String msg) {
		this.name = name;
		this.msg = msg;
		System.out.println("created");
	}

	public String getName() {
		return name;
	}

}
