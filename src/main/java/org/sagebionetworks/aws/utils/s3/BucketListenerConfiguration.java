package org.sagebionetworks.aws.utils.s3;

import com.amazonaws.services.s3.model.S3Event;

/**
 * Configuration for establishing a bucket listener such that bucket events are
 * published to the provided SNS topic.
 * 
 */
public class BucketListenerConfiguration {

	private String bucketName;
	private String topicName;
	private S3Event event;
	private String configName;

	/**
	 * The name of the bucket to listen to.
	 * 
	 * @return
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * The name of the bucket to listen to.
	 * @param bucketName
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * The name of the topic where bucket events should be published.
	 * @return
	 */
	public String getTopicName() {
		return topicName;
	}

	/**
	 * The name of the topic where bucket events should be published.
	 * @param topicName
	 */
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	/**
	 * The type of S3 Event that should be published to the topic.
	 * 
	 * @return
	 */
	public S3Event getEvent() {
		return event;
	}

	/**
	 * The type of S3 Event that should be published to the topic.
	 * @param event
	 */
	public void setEvent(S3Event event) {
		this.event = event;
	}

	/**
	 * A unique name for this listener configuration.
	 * @return
	 */
	public String getConfigName() {
		return configName;
	}

	/**
	 * A unique name for this listener configuration.
	 * @param configName
	 */
	public void setConfigName(String configName) {
		this.configName = configName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bucketName == null) ? 0 : bucketName.hashCode());
		result = prime * result
				+ ((configName == null) ? 0 : configName.hashCode());
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result
				+ ((topicName == null) ? 0 : topicName.hashCode());
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
		BucketListenerConfiguration other = (BucketListenerConfiguration) obj;
		if (bucketName == null) {
			if (other.bucketName != null)
				return false;
		} else if (!bucketName.equals(other.bucketName))
			return false;
		if (configName == null) {
			if (other.configName != null)
				return false;
		} else if (!configName.equals(other.configName))
			return false;
		if (event != other.event)
			return false;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BucketListenerConfiguration [bucketName=" + bucketName
				+ ", topicName=" + topicName + ", event=" + event
				+ ", configName=" + configName + "]";
	}
}
