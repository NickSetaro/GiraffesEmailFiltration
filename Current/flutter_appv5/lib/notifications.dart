

class Notif{
  String address;
  String subject;
  String bodySnip;
  String date;
  String dateAndBody;
  String url;
  String uuid;
  bool pinned = false;
  bool snoozed = false;
  int snoozeHours = 0;
  int snoozeDays = 0;

  Notif(this.address);

  void toggle() {
    pinned = !pinned;
  }
}