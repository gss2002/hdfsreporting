/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hdfs.reporting;

import java.io.File;

import java.io.IOException;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.GenericOptionsParser;


public class HdfsReporting {

	public static int inDepth;
	public static String folder;
	public static String outputPath;
	static boolean setKrb = false;
	static String keytab = null;
	static String keytabupn = null;
	// public static Configuration hdpConfig;

	public static String[] folderList;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Configuration conf = new Configuration();
		String[] otherArgs = null;
		try {
			otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		} catch (IOException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}

		Options options = new Options();
		options.addOption("depth", true, "Depth of Report per Folder ex. --depth 3");
		options.addOption("folders", true,
				"List of Folders to Report ex. --folders /apps/hive/warehouse:/source:/landing:/users:/tmp or /apps/hive/warehouse=4:/source=2:/landing:/users:/tmp");
		options.addOption("krb_keytab", true, "Keytab for Krb5 HDFS --krb_keytab $HOME/S00000.keytab");
		options.addOption("krb_upn", true,
				"Kerberos Princpial for Keytab for Krb5 HDFS --krb_upn S00000@EXAMP.EXAMPLE.COM");
		options.addOption("output", true, "Output Folder and FileName ex. --output /hdfs_usage/hourly/filename.json");
		options.addOption("help", false, "Display help");
		CommandLineParser parser = new HdfsRptParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, otherArgs);
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String folders = null;
		String depth = null;
		if (((cmd.hasOption("folders") && cmd.hasOption("depth") && cmd.hasOption("output"))
				|| (cmd.hasOption("help")))) {
			if (cmd.hasOption("folders") && cmd.hasOption("depth")) {
				folders = cmd.getOptionValue("folders");
				depth = cmd.getOptionValue("depth");
				outputPath = cmd.getOptionValue("output");

			}
			if (cmd.hasOption("help")) {
				String header = "Do something useful with an input file\n\n";
				String footer = "\nPlease report issues at http://example.com/issues";
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("get", header, options, footer, true);
				System.exit(0);
			}
		} else {
			String header = "Do something useful with an input file\n\n";
			String footer = "\nPlease report issues at http://example.com/issues";
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("get", header, options, footer, true);
			System.exit(0);
		}
		if (cmd.hasOption("krb_keytab") && cmd.hasOption("krb_upn")) {
			setKrb = true;
			keytab = cmd.getOptionValue("krb_keytab");
			keytabupn = cmd.getOptionValue("krb_upn");
			File keytabFile = new File(keytab);
			if (keytabFile.exists()) {
				if (!(keytabFile.canRead())) {
					System.out.println("Keytab  exists but cannot read it - exiting");
					System.exit(1);
				}
			} else {
				System.out.println("Keytab doesn't exist  - exiting");
				System.exit(1);
			}
		}
		folderList = folders.split(":");
		inDepth = Integer.parseInt(depth);

		UserGroupInformation.setConfiguration(conf);
		System.out.println("Config: " + conf.get("hadoop.security.authentication"));
		System.out.println("Config: " + conf.get("dfs.namenode.kerberos.principal"));
		System.out.println("Config: " + conf.get("fs.defaultFS"));

		File outputPathFile = new File(outputPath);
		if (!(outputPathFile.exists())) {
			outputPathFile.mkdirs();
		}
		
		for (String folderIn : folderList) {
			HdfsReportingThread sc = null;
			if (folderIn.contains("=")) {
				folder = folderIn.split("=")[0];
				depth = folderIn.split("=")[1];
				inDepth = Integer.parseInt(depth);
			} else {
				folder = folderIn;
			}
			System.out.println("Folder: " + folder + ":: " + inDepth);
			ThreadGroup hdfsRtg = new ThreadGroup("HdfsReportingThreadGroup");
			String threadName = "HdfsReporting" + folder;
			sc = new HdfsReportingThread(hdfsRtg, threadName, folder, outputPath, inDepth, conf, setKrb, keytabupn,
					keytab);
			sc.start();
		}

	}

}
