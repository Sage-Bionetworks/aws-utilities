package org.sagebionetworks.aws.utils.s3;

import java.util.Iterator;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.sqs.model.UnsupportedOperationException;

public class BucketDaoImpl implements BucketDao {

	private AmazonS3Client awsS3Client;
	private String bucketName;

	public BucketDaoImpl(AmazonS3Client awsS3Client, String bucketName) {
		super();
		this.awsS3Client = awsS3Client;
		this.bucketName = bucketName;
	}

	@Override
	public Iterator<S3ObjectSummary> summaryIterator(String prefix) {
		return new PagingListingIterator(prefix);
	}

	@Override
	public void deleteAllObjectsWithPrefix(String prefix) {
		Iterator<String> iterator = keyIterator(prefix);
		while (iterator.hasNext()) {
			this.awsS3Client.deleteObject(bucketName, iterator.next());
		}
	}

	@Override
	public Iterator<String> keyIterator(String prefix) {
		// simple wrapper of the Iterator<S3ObjectSummary> iterator.
		final Iterator<S3ObjectSummary> iterator = summaryIterator(prefix);
		return new Iterator<String>() {

			@Override
			public void remove() {
				iterator.remove();
			}

			@Override
			public String next() {
				S3ObjectSummary summary = iterator.next();
				return summary.getKey();
			}

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
		};
	}

	/**
	 * Iterator implementation that will fetch one page of S3 object listing
	 * results at a time.
	 * 
	 */
	private class PagingListingIterator implements Iterator<S3ObjectSummary> {

		private String prefix;
		ObjectListing lastPage;
		int currentIndex;

		public PagingListingIterator(String prefix) {
			super();
			this.prefix = prefix;
			this.currentIndex = 0;
			this.lastPage = awsS3Client.listObjects(new ListObjectsRequest()
					.withBucketName(bucketName).withPrefix(prefix));
		}

		@Override
		public boolean hasNext() {
			if (lastPage == null) {
				return false;
			}
			if (lastPage.getObjectSummaries() == null) {
				return false;
			}
			// is this page done
			if (lastPage.getObjectSummaries().size() - 1 < currentIndex) {
				// this page is done.
				if (lastPage.getNextMarker() == null) {
					// there are no more results
					return false;
				} else {
					// get the next page
					this.lastPage = awsS3Client
							.listObjects(new ListObjectsRequest()
									.withBucketName(bucketName)
									.withPrefix(prefix)
									.withMarker(lastPage.getNextMarker()));
					this.currentIndex = 0;
					return hasNext();
				}
			}
			return true;
		}

		@Override
		public S3ObjectSummary next() {
			S3ObjectSummary summary = lastPage.getObjectSummaries().get(
					currentIndex);
			currentIndex++;
			return summary;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Not supported");
		}
	}

}
