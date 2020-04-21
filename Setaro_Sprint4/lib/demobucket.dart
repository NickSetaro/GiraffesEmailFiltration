import 'bucket.dart';
import 'notifications.dart';

class ListOfBuckets {

  List<Bucket> buckets;

  ListOfBuckets() {
    buckets = new List<Bucket>();
  }

  addBucket(Bucket b) {
    buckets.add(b);
  }

  insertBucket(int index, Bucket b) {
    buckets.insert(index,b);
  }

  Bucket removeAt(int index) {
    return buckets.removeAt(index);
  }
}