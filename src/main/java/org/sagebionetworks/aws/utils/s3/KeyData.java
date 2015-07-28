package org.sagebionetworks.aws.utils.s3;

/**
 * Data extracted from a formated S3 Key.
 *
 */
public class KeyData {
	
	int stackInstanceNumber;
	long timeMS;
	boolean rolling;
	String fileName;
	String path;
	String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getStackInstanceNumber() {
		return stackInstanceNumber;
	}
	public void setStackInstanceNumber(int stackInstanceNumber) {
		this.stackInstanceNumber = stackInstanceNumber;
	}
	public long getTimeMS() {
		return timeMS;
	}
	public void setTimeMS(long timeMS) {
		this.timeMS = timeMS;
	}
	public boolean isRolling() {
		return rolling;
	}
	public void setRolling(boolean rolling) {
		this.rolling = rolling;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + (rolling ? 1231 : 1237);
		result = prime * result + stackInstanceNumber;
		result = prime * result + (int) (timeMS ^ (timeMS >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyData other = (KeyData) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (rolling != other.rolling)
			return false;
		if (stackInstanceNumber != other.stackInstanceNumber)
			return false;
		if (timeMS != other.timeMS)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "KeyData [stackInstanceNumber=" + stackInstanceNumber
				+ ", timeMS=" + timeMS + ", rolling=" + rolling + ", fileName="
				+ fileName + ", path=" + path + ", type=" + type + "]";
	}
	
	
}
