import 'bucket.dart';
import 'notifications.dart';

class ListOfBuckets {

  List<Bucket> bucketList = new List<Bucket>();

  ListOfBuckets() {
    Bucket b = new Bucket('Myers', 'myers@rowan.edu');
    Bucket c = new Bucket('Bursar', 'bursar@rowan.edu');
    b.addNotification(new Notif('Homework', 'Do you\'re homework', '04/06/2020',''));
    b.addNotification(new Notif('Sprint Review', 'Do you\'re sprint review', '04/01/2020',''));
    c.addNotification(new Notif('Money', 'Give me your money', '04/08/2020',''));
    c.addNotification(new Notif('Late Fee', 'Give me more of your money', '04/10/2020',''));
    bucketList.add(b);
    bucketList.add(c);
  }
}