package processing;

import gui.FilesSync;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class SyncSrcDes implements Runnable {

	FilesSync fileSync;

	public SyncSrcDes(FilesSync fs) {
		fileSync = fs;
	}

	@Override
	public void run() {
		fileSync.desLocTextField.setEditable(false);
		fileSync.srcLocTextField.setEditable(false);

		fileSync.desFileList = new ArrayList<FileInfo>();
		fileSync.srcFileList = new ArrayList<FileInfo>();

		// read file list in src and des
		File srcFolderPath = new File(fileSync.srcLocTextField.getText());
		File desFolderPath = new File(fileSync.desLocTextField.getText());
		listFilesInFolder(desFolderPath, fileSync.desFileList,
				fileSync.desLocTextField.getText());
		listFilesInFolder(srcFolderPath, fileSync.srcFileList,
				fileSync.srcLocTextField.getText());

		// showFileLists();

		// compare
		compareFolder(fileSync.desLocTextField.getText());
		try {
			compareFiles(fileSync.srcLocTextField.getText(),
					fileSync.desLocTextField.getText());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		fileSync.desFileList = new ArrayList<FileInfo>();
		fileSync.srcFileList = new ArrayList<FileInfo>();
		fileSync.desLocTextField.setEditable(true);
		fileSync.srcLocTextField.setEditable(true);
		fileSync.confirmButton.setEnabled(true);
	}

	protected void compareFiles(String srcPre, String desPre)
			throws IOException {
		for (int i = 0; i < fileSync.srcFileList.size(); i++) {
			if (fileSync.srcFileList.get(i).isFolder) {
				continue;
			}
			boolean ident = false;
			String srcPath = fileSync.srcFileList.get(i).path;
			String srcName = fileSync.srcFileList.get(i).name;
			for (int j = 0; j < fileSync.desFileList.size(); j++) {
				if (fileSync.desFileList.get(j).isFolder) {
					continue;
				}
				String desPath = fileSync.desFileList.get(j).path;
				String desName = fileSync.desFileList.get(j).name;
				if (srcPath.equals(desPath)) {
					if (srcName.compareTo(desName) == 0
							&& fileSync.srcFileList.get(i).lastModified == fileSync.desFileList
									.get(j).lastModified) {
						ident = true;
						break;
					} else if (srcName.compareTo(desName) > 0) {
						continue;
					} else if (srcName.compareTo(desName) < 0) {
						continue;
					}
				} else {
					continue;
				}

			}
			if (ident) {
				continue;
			} else {
				File srcFile = new File(srcPre
						+ fileSync.srcFileList.get(i).getPathName());
				File desFile = new File(desPre
						+ fileSync.srcFileList.get(i).getPathName());
				if (desFile.exists()) {
					Files.delete(desFile.toPath());
				}
				Files.copy(srcFile.toPath(), desFile.toPath(),
						StandardCopyOption.COPY_ATTRIBUTES);
				System.out.println("copy: "
						+ fileSync.srcFileList.get(i).getPathName());
			}
		}

	}

	protected void compareFolder(String pre) {
		for (int i = 0; i < fileSync.srcFileList.size(); i++) {
			boolean ident = false;
			if (fileSync.srcFileList.get(i).isFolder == false) {
				continue;
			}
			String srcPathName = fileSync.srcFileList.get(i).getPathName();
			for (int j = 0; j < fileSync.desFileList.size(); j++) {
				if (fileSync.desFileList.get(j).isFolder == false) {
					continue;
				}
				String desPathName = fileSync.desFileList.get(j).getPathName();
				if (srcPathName.compareTo(desPathName) == 0) {
					ident = true;
					break;
				} else if (srcPathName.compareTo(desPathName) > 0) {
					continue;
				} else if (srcPathName.compareTo(desPathName) < 0) {
					continue;
				}
			}
			if (ident) {
				continue;
			} else {
				File desFolder = new File(pre + srcPathName);
				desFolder.mkdir();
				System.out.println("copy: " + pre + srcPathName);
			}
		}

	}

	protected void listFilesInFolder(File folder,
			ArrayList<FileInfo> fileInfoList, String pre) {
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				fileInfoList.add(new FileInfo(fileEntry.getPath().replace(pre,
						""), fileEntry.getName(), fileEntry.lastModified(),
						true));
				listFilesInFolder(fileEntry, fileInfoList, pre);
			} else {

				fileInfoList.add(new FileInfo(fileEntry.getParent().replace(
						pre, ""), fileEntry.getName(),
						fileEntry.lastModified(), false));
			}
		}
		/*
		 * for (FileInfo fileInfo : fileInfoList) {
		 * System.out.println(fileInfo.toString()); }
		 */

	}

	void showFileLists() {
		System.out.println("src list: " + fileSync.srcFileList.size());
		for (FileInfo fileInfo : fileSync.srcFileList) {
			System.out.print(fileInfo.toString());
		}
		System.out.println("des list: " + fileSync.desFileList.size());
		for (FileInfo fileInfo : fileSync.desFileList) {
			System.out.print(fileInfo.toString());
		}

	}

}
