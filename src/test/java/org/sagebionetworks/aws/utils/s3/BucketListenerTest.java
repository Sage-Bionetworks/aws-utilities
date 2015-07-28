package org.sagebionetworks.aws.utils.s3;

import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.NotificationConfiguration;
import com.amazonaws.services.s3.model.S3Event;
import com.amazonaws.services.s3.model.TopicConfiguration;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicResult;

public class BucketListenerTest {

	String bucketName;
	String topicName;
	String topicArn;
	String configName;
	S3Event event;
	BucketListenerConfiguration config;
	AmazonS3Client mockS3Client;
	AmazonSNSClient mockSNClient;

	@Before
	public void before() {
		mockS3Client = Mockito.mock(AmazonS3Client.class);
		mockSNClient = Mockito.mock(AmazonSNSClient.class);
		topicName = "aTopic";
		topicArn = "aTopicArn";
		bucketName = "aBucketName";
		configName = "aConfigName";
		event = S3Event.ObjectCreated;
		when(mockSNClient.createTopic(topicName)).thenReturn(
				new CreateTopicResult().withTopicArn(topicArn));

		config = new BucketListenerConfiguration();
		config.setBucketName(bucketName);
		config.setTopicName(topicName);
		config.setConfigName(configName);
		config.setEvent(event);
	}

	@Test
	public void testCreateDoesNotExist() {
		when(mockS3Client.getBucketNotificationConfiguration(bucketName))
				.thenReturn(new BucketNotificationConfiguration());
		// call under test
		BucketListener listener = new BucketListener(mockS3Client,
				mockSNClient, config);
		verify(mockSNClient).createTopic(topicName);
		verify(mockS3Client).createBucket(bucketName);
		ArgumentCaptor<String> nameCapture = ArgumentCaptor
				.forClass(String.class);
		ArgumentCaptor<BucketNotificationConfiguration> bucketConfigCapture = ArgumentCaptor
				.forClass(BucketNotificationConfiguration.class);
		verify(mockS3Client).setBucketNotificationConfiguration(
				nameCapture.capture(), bucketConfigCapture.capture());
		assertEquals(bucketName, nameCapture.getValue());
		NotificationConfiguration captureNotification = bucketConfigCapture
				.getValue().getConfigurationByName(configName);
		assertNotNull(captureNotification);
		assertTrue(captureNotification instanceof TopicConfiguration);
		TopicConfiguration topicConfig = (TopicConfiguration) captureNotification;
		assertEquals(topicArn, topicConfig.getTopicARN());
		assertEquals("s3:ObjectCreated:*", topicConfig.getEvents().iterator()
				.next());
		// The policy should be set on the topic
		verify(mockSNClient).setTopicAttributes(anyString(), anyString(), anyString());
	}

	@Test
	public void testCreateDoesExists() {
		// For this call the config already exists.
		when(mockS3Client.getBucketNotificationConfiguration(bucketName))
				.thenReturn(
						new BucketNotificationConfiguration().addConfiguration(
								configName, new TopicConfiguration(topicArn,
										EnumSet.of(event))));
		// call under test
		BucketListener listener = new BucketListener(mockS3Client,
				mockSNClient, config);
		verify(mockSNClient).createTopic(topicName);
		verify(mockS3Client, never()).setBucketNotificationConfiguration(anyString(), any(BucketNotificationConfiguration.class));
		verify(mockSNClient, never()).setTopicAttributes(anyString(), anyString(), anyString());
	}

	@Test (expected=IllegalArgumentException.class)
	public void testSQSClientNull(){
		new BucketListener(null,
				mockSNClient, config);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSNSClientNull(){
		new BucketListener(mockS3Client,
				null, config);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConfogClientNull(){
		new BucketListener(mockS3Client,
				mockSNClient, null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testBucketNameNull(){
		config.setBucketName(null);
		new BucketListener(mockS3Client,
				mockSNClient, config);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testConfigNameNull(){
		config.setConfigName(null);
		new BucketListener(mockS3Client,
				mockSNClient, config);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testTopicNameNull(){
		config.setTopicName(null);
		new BucketListener(mockS3Client,
				mockSNClient, config);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testEventNull(){
		config.setEvent(null);
		new BucketListener(mockS3Client,
				mockSNClient, config);
	}
}
