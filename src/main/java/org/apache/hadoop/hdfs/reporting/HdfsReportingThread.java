package org.apache.hadoop.hdfs.reporting;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.reporting.HdfsReportBean.HdfsFolders;
import org.apache.hadoop.security.UserGroupInformation;

import com.google.gson.Gson;

public class HdfsReportingThread extends Thread {

	public Configuration conf;
	public UserGroupInformation ugi;
	public FileSystem fs;
	public HdfsReportBean hrb;
	public ArrayList<HdfsFolders> hdfsList;
	public Path inPath;
	public String defaultFs;
	public String output;
	public Integer inDepth;
	public String outputPath;

	public HdfsReportingThread(ThreadGroup tg, String name, String folder, String outputPath, Integer inDepth,
			Configuration conf, boolean setKrb, String keytabupn, String keytab) {
		super(tg, name);

		// Goes to get hadoop configuration files
		this.conf = conf;
		this.inDepth = inDepth;
		this.outputPath = outputPath;
		System.out.println("Output: " + output);
		defaultFs = conf.get("fs.defaultFS");
		UserGroupInformation.setConfiguration(conf);
		if (UserGroupInformation.isSecurityEnabled()) {
			try {
				if (setKrb == true) {
					ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(keytabupn, keytab);
				} else {
					ugi = UserGroupInformation.getCurrentUser();
				}
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				System.out.println("Exception Getting Credentials Exiting!");
				System.exit(1);
			}
		}
		inPath = new Path(folder);
		long currentTime = System.currentTimeMillis();
		if (folder.equalsIgnoreCase("/")) {
			output = outputPath + "/hdfs_root_" + currentTime + ".json";
		} else {
			output = outputPath + "/hdfs_" + folder.replace("/", "") + "_" + currentTime + ".json";
		}
		hrb = new HdfsReportBean();
		hrb.setReportTime(Long.toString(currentTime));
		hdfsList = new ArrayList<HdfsFolders>();

		// Goes to get Hadoop File System Named Node information and such from
		// the configuration.
		getFS();

	}

	public void run() {
		try {
			if (UserGroupInformation.isSecurityEnabled()) {
				ugi.doAs(new PrivilegedExceptionAction<Void>() {
					public Void run() throws Exception {
						getFolderData(fs, inPath, inDepth);
						return null;
					}
				});
			} else {
				getFolderData(fs, inPath, inDepth);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		hrb.setHdfsFolders(hdfsList);
		Gson gson = new Gson();
		String hrbString = gson.toJson(hrb);
		// System.out.println("hrbString: "+hrbString);
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "utf-8"));
			writer.write(hrbString);
		} catch (IOException ex) {
			// report
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
			}
		}
	}

	public void getFolderData(FileSystem fs, Path inPath, int depth) {
		if (depth == 0) {
			return;
		}
		FileStatus[] fsList = null;
		try {
			if (fs.isDirectory(inPath)) {
				HdfsFolders hdfsFolders = getData(fs, inPath, false);
				hdfsList.add(hdfsFolders);
				String snapShotDir = inPath.toString() + "/.snapshot/";
				if (fs.exists(new Path(snapShotDir))) {
					/// HdfsFolders hdfsFoldersSnp = getData(fs, new Path(snapShotDir), true);
					FileStatus[] gStatus = fs.globStatus(new Path(snapShotDir + "/*"));
					for (FileStatus status : gStatus) {
						HdfsFolders hdfsFoldersSnp = getData(fs, status.getPath(), true);
						hdfsList.add(hdfsFoldersSnp);
					}
				}
				try {
					fsList = fs.listStatus(inPath);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int i = 0; fsList != null && i < fsList.length; i++) {
					if (fsList[i].isDirectory()) {
						inPath = fsList[i].getPath();

						getFolderData(fs, inPath, depth - 1);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public HdfsFolders getData(FileSystem fs, Path inPath, Boolean isSnapshotdir) {
		HdfsFolders hdfsFolder = hrb.new HdfsFolders();
		boolean isSnapShot = false;
		String owner = "";
		String group = "";
		String perms = "";
		long replicatedSize = 0;
		long unReplicatedSize = 0;
		long fcount = 0;
		long quota = 0;
		long defaultRepFactor = 0;
		long modifyTime = 0L;
		double avgRepfactor = 0;
		try {
			owner = fs.getFileStatus(inPath).getOwner();
			group = fs.getFileStatus(inPath).getGroup();
			perms = fs.getFileStatus(inPath).getPermission().toString();

			replicatedSize = fs.getContentSummary(inPath).getSpaceConsumed();
			unReplicatedSize = fs.getContentSummary(inPath).getLength();
			fcount = fs.getContentSummary(inPath).getFileCount();
			quota = fs.getContentSummary(inPath).getQuota();
			avgRepfactor = ((double) replicatedSize / unReplicatedSize);
			// }
			defaultRepFactor = fs.getDefaultReplication(inPath);
			modifyTime = fs.getFileStatus(inPath).getModificationTime();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		hdfsFolder.setHdfsFolderPath(inPath.toString().replace(defaultFs, ""));
		hdfsFolder.setAvgRepFactor(Double.toString(avgRepfactor));
		hdfsFolder.setDefaultRepFactor(Long.toString(defaultRepFactor));
		hdfsFolder.setFolderFileCount(Long.toString(fcount));
		hdfsFolder.setReplicatedSize(Long.toString(replicatedSize));
		hdfsFolder.setUnReplicatedSize(Long.toString(unReplicatedSize));
		hdfsFolder.setOwner(owner);
		hdfsFolder.setGroup(group);
		hdfsFolder.setMode(perms);
		hdfsFolder.setQuota(Long.toString(quota));
		hdfsFolder.setModifyTime(Long.toString(modifyTime));
		hdfsFolder.setIsSnapShot(isSnapShot);
		return hdfsFolder;
	}
	// This gets the Hadoop Configuration files loads them into a Configuration
	// object for use throughout the program

	public void getFS() {
		try {
			fs = FileSystem.get(conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
