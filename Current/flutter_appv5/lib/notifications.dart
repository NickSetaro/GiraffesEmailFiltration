class Notif{
  String address;
  String subject;
  String bodySnip;
  String date;
  String dateAndBody;
  String url;
  String uuid;
  bool pinned = false;
  Notif(this.address);

  void toggle() {
    pinned = !pinned;
  }
}