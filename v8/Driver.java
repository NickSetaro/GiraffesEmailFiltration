package v8;

import java.io.IOException;

public class Driver {

	public static void main(String[] args) 
	{
		String fileName = User.getFilename();
		User user;

//		user.addBucket("callih56@students.rowan.edu", "test|homework");
//		user.addBucket("edward.callihan@yahoo.com", "pizza|cake");
//		User.serialize(user, fileName);
		user = User.deserialize(fileName);
		user.resetLastCheck();
		
		user.addAddress("myersjac@rowan.edu");
//		user.addKeyword("edward.callihan@gmail.com", "fads");
//		user.removeKeyword("edward.callihan@gmail.com", "");
//		user.removeKeyword("edward.callihan@gmail.com", "fads");
//		user.removeKeyword("edward.callihan@gmail.com", "sent");
//		user.removeKeyword("edward.callihan@gmail.com", "bark");
//		user.addAddress("someone@something.com", keywords);
//		user.addAddress("callih56@students.rowan.edu", "homework");
//		user.deleteBucket("edward.callihan@gmail.com");
//		EmailReceiver receiver = new EmailReceiver(user.getFilter());
//		
//		try {
//			receiver.checkEmail(user.getUserName(), user.getPassword());
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
		System.out.println("Names of buckets: ");
		for(EmailBucket b: user.getBuckets())
		{
			System.out.println(b.getBucketName());
		}
		for(EmailBucket b: user.getBuckets())
		{
			for(MailObject m: b.getMessages())
			{
				String text = EmailReceiver.textFromHtml(m.getContentText());
				System.out.println("From: " + m.getFrom() + 
						"\n\t" + "Subject: " + m.getSubject() + 
						"\n\t" +  "Text: " + text +
						"\n\t" + "Excerpt at " + User.getTagBuffer() + 
						" characters: " + user.createExcerpt(text, user.getTags(m.getFrom())));
			}
			
		}
		
		user.getFilter().getEmailFilter().forEach((k,v) -> {
			System.out.println(k + "\n" + v);
		});
		
		System.out.println("Recent Messages: ");
		
		user.getRecents().getMessages().forEach(e -> {
			System.out.println("\n" + e.getFrom() + "\n" + e.getSentDate().toString());
		});
		
		User.serialize(user, fileName);	
	}

}
