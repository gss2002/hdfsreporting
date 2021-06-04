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

import java.util.ArrayList;



public class HdfsReportBean {
	public String reportTime;
	ArrayList<HdfsFolders> hdfsFolders;
	
	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}
	
	public String getReportTime() {
		return this.reportTime;
	}
	
	
    public ArrayList<HdfsFolders> getHdfsFolders() {
        return this.hdfsFolders ;
    }

    public void setHdfsFolders(ArrayList<HdfsFolders> hdfsFolders) {
        this.hdfsFolders = hdfsFolders;
    }
	
	public class HdfsFolders {
		public String hdfsFolderPath;
		public String replicatedSize;
		public String unReplicatedSize;
		public String folderFileCount;
		public String quota;
		public String defaultRepFactor;
		public String avgRepFactor;	
		public String owner;
		public String group;
		public String mode;
		public String modifyTime;
		public boolean isSnapShot;
		ArrayList<HdfsFolders> hdfsFolders;
		
		public void setReplicatedSize(String replicatedSize) {
			this.replicatedSize = replicatedSize;
		}
		
		public String getReplicatedSize() {
			return this.replicatedSize;
		}
		
		public void setHdfsFolderPath(String hdfsFolderPath) {
			this.hdfsFolderPath = hdfsFolderPath;
		}
		
		public String getHdfsFolderPath() {
			return this.hdfsFolderPath;
		}
		
		public void setUnReplicatedSize(String unReplicatedSize) {
			this.unReplicatedSize = unReplicatedSize;
		}
		
		public String getUnReplicatedSize() {
			return this.unReplicatedSize;
		}
		
		public void setFolderFileCount(String folderFileCount) {
			this.folderFileCount = folderFileCount;
		}
		
		public String getFolderFileCount() {
			return this.folderFileCount;
		}
		public void setQuota(String quota) {
			this.quota = quota;
		}
		
		public String getQuota() {
			return this.quota;
		}
		public void setOwner(String owner) {
			this.owner = owner;
		}
		public String getOwner() {
			return this.owner;
		}
		public void setGroup(String group) {
			this.group = group;
		}
		public String getGroup() {
			return this.group;
		}
		public void setModifyTime(String modifyTime) {
			this.modifyTime = modifyTime;
		}
		public String getModifyTime() {
			return this.modifyTime;
		}
		public void setMode(String mode) {
			this.mode = mode;
		}
		public String getMode() {
			return this.mode;
		}
				public void setDefaultRepFactor(String defaultRepFactor) {
			this.defaultRepFactor = defaultRepFactor;
		}
		
		public String getDefaultRepFactor() {
			return this.defaultRepFactor;
		}
		public void setAvgRepFactor(String avgRepFactor) {
			this.avgRepFactor = avgRepFactor;
		}
		
		public String getAvgRepFactor() {
			return this.avgRepFactor;
		}
		
	    public ArrayList<HdfsFolders> getHdfsFolders() {
	        return this.hdfsFolders ;
	    }

	    public void setHdfsFolders(ArrayList<HdfsFolders> hdfsFolders) {
	        this.hdfsFolders = hdfsFolders;
	    }
	    
	    public void setIsSnapShot(Boolean issnapshot) {
	    	this.isSnapShot = issnapshot;
	    }
	    public Boolean getIsSnapShot() {
	    	return this.isSnapShot;
	    }	    

	}



}
