package org.sagebionetworks.aws.utils.s3;

import java.util.Iterator;

import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * An abstraction for an S3 buck Data Access Object (dao).
 * 
 * 
 */
public interface BucketDao {

	/**
	 * Create new iterator to iterator over all of the objects in the bucket
	 * with the given prefix.
	 * 
	 * @param prefix
	 *            An optional parameter to filter keys that start with the given
	 *            prefix.
	 * @return
	 */
	public Iterator<S3ObjectSummary> summaryIterator(String prefix);
	
	/**
	 * Create new iterator to iterator over all of the objects in the bucket
	 * with the given prefix.
	 * 
	 * @param prefix
	 *            An optional parameter to filter keys that start with the given
	 *            prefix.
	 * @return
	 */
	public Iterator<String> keyIterator(String prefix);

	/**
	 * Delete all object in this bucket with keys that start with the given
	 * prefix.
	 * 
	 * @param prefix
	 *            An optional parameter to filter keys that start with the given
	 *            prefix. If null, then all objects will be deleted from the
	 *            bucket.
	 */
	public void deleteAllObjectsWithPrefix(String prefix);

}
