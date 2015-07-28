package org.sagebionetworks.aws.utils.s3;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class BucketDaoImplTest {

	private AmazonS3Client mockS3Client;
	private String bucketName;
	private BucketDaoImpl dao;

	@Before
	public void setUp() {
		bucketName = "someBucket";
		mockS3Client = Mockito.mock(AmazonS3Client.class);

		
		// simulate two pages of results
		ObjectListing one = new ObjectListing();
		S3ObjectSummary sum = new S3ObjectSummary();
		sum.setKey("one");
		one.getObjectSummaries().add(sum);
		sum = new S3ObjectSummary();
		sum.setKey("two");
		one.getObjectSummaries().add(sum);
		one.setNextMarker("nextMarker");
		
		ObjectListing two = new ObjectListing();
		sum = new S3ObjectSummary();
		sum.setKey("three");
		two.getObjectSummaries().add(sum);
		sum = new S3ObjectSummary();
		sum.setKey("four");
		two.getObjectSummaries().add(sum);
		two.setMarker(null);
		
		when(mockS3Client.listObjects(any(ListObjectsRequest.class))).thenReturn(one, two);
		
		dao = new BucketDaoImpl(mockS3Client, bucketName);
	}

	@Test
	public void testSummaryIterator(){
		String prefix = "aPrefix";
		Iterator<S3ObjectSummary> it = dao.summaryIterator(prefix);
		assertNotNull(it);
		assertTrue(it.hasNext());
		assertEquals("one", it.next().getKey());
		assertTrue(it.hasNext());
		assertEquals("two", it.next().getKey());
		assertTrue(it.hasNext());
		assertEquals("three", it.next().getKey());
		assertTrue(it.hasNext());
		assertEquals("four", it.next().getKey());
		assertFalse(it.hasNext());
	}

}
