import java.io.*;
import java.util.ArrayList;

public class FileHandler extends Thread {
	ArrayList<File> files;
	ArrayList<BufferedWriter> writers;
	ArrayList<String> channels;
	
	FileHandler(ArrayList<String> channels, ArrayList<File> files) {
		this.channels = channels;
		this.files = files;
	}
	
	@Override
	public void start() {
		for (File f: files) {
			try {
				writers.add(new BufferedWriter(new FileWriter(f.getAbsolutePath(), true)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void append(String channelName, String data) {
		int i = channels.indexOf(channelName);
		if (i != -1) {
			BufferedWriter w = writers.get(i);
			synchronized (w) {
				try {
					w.write(data);
					w.write("\n");
					w.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public synchronized ArrayList<String> readAll(String channelName) {
		int i = channels.indexOf(channelName);
		if (i != -1) {
			try {
				ArrayList<String> history = new ArrayList<>();
				BufferedReader r = new BufferedReader(new FileReader(files.get(i).getAbsolutePath()));
				String line;
				while ((line = r.readLine()) != null) {
					history.add(line);
				}
				return history;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	//index 0+ is friends
	//last index is hashedPassword
	public synchronized ArrayList<String> loadUser(String userName){
		return null;
	}
}