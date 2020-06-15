import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;

/* creates a bucket WithStorageClassAndLocation */
public class CreateBucket {
  public static void createBucketWithStorageClassAndLocation(String projectId, String bucketName) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID to give your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    // See the StorageClass documentation for other valid storage classes:
    // https://googleapis.dev/java/google-cloud-clients/latest/com/google/cloud/storage/StorageClass.html
    StorageClass storageClass = StorageClass.COLDLINE;

    // See this documentation for other valid locations:
    // http://g.co/cloud/storage/docs/bucket-locations#location-mr
    String location = "asia";

    Bucket bucket = storage.create(BucketInfo.newBuilder(bucketName)
                                       .setStorageClass(storageClass)
                                       .setLocation(location)
                                       .build());

    System.out.println("Created bucket " + bucket.getName() + " in " + bucket.getLocation()
        + " with storage class " + bucket.getStorageClass());
  }
}