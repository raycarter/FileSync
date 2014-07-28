package processing;

import java.io.File;
import java.text.SimpleDateFormat;

public class FileInfo {
	String path;
	String name;
	long lastModified;
	boolean isFolder;

	public FileInfo(String path, String name, long lm, boolean isFolder) {
		this.path = path;
		this.lastModified = lm;
		this.name = name;
		this.isFolder = isFolder;
	}

	@Override
	public String toString() {
		return getPathName()
				+ "\t\t"
				+ (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(lastModified) + "\n");
	}

	public String getPathName() {

		if (isFolder) {
			return path;
		} else {
			return path + File.separator + name;
		}
	}
}
