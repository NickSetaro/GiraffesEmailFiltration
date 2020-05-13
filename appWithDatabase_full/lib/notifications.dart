

class Notif{
  String address;
  String subject;
  String bodySnip;
  String date;
  String uuid;
  bool pinned = false;
  bool snoozed = false;
  int snoozeHours = 0;
  int snoozeDays = 0;

  Notif(this.address,this.subject,this.bodySnip,this.date);

  void toggle() {
    pinned = !pinned;
  }
}