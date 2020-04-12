
class Notif{
  String subject;
  String bodySnip;
  String date;
  String dateAndBody;
  String url;

  Notif(String subject, String bodySnip, String date, String url) {
    this.subject = subject;
    this.bodySnip = bodySnip;
    this.date = date;
    this.url = url;
    dateAndBody = date + '\n' + bodySnip;
  }
}