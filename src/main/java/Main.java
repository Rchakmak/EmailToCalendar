import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.Gmail;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) throws IOException {

        // Build a new authorized API client service.
        Gmail serviceMail = Credentials.getGmailService();

        com.google.api.services.calendar.Calendar serviceCal =
                Credentials.getCalendarService();

        // Create a List containing all emails in your inbox
        List<Message> messages = MessageMethods.listMessagesMatchingQuery(serviceMail, "cmceventschedule@gmail.com", "");

        for (Message message : messages) {
            String id = message.getId();

            // Replace the following middle parameter with your desired email
            Message email = MessageMethods.getMessage(serviceMail, "cmceventschedule@gmail.com", id);

            // Gets the message body of the email and converts it from Google's Base64
            List<MessagePart> content = email.getPayload().getParts();
            Scanner read = new Scanner(new String(Base64.decodeBase64(content.get(0).getBody().getData())));

            // I've set it up so that all the emails will come in this specific format
            // You can change it as you need
            String eventName = read.nextLine();
            String eventLocation = read.nextLine();
            DateTime startTime = new DateTime(read.next());
            EventDateTime start = new EventDateTime()
                    .setDateTime(startTime)
                    //replace with your time zone
                    .setTimeZone("America/Los_Angeles");
            read.nextLine();
            DateTime endTime = new DateTime(read.next());
            EventDateTime end = new EventDateTime()
                    .setDateTime(endTime)
                    //replace with your time zone
                    .setTimeZone("America/Los_Angeles");
            read.nextLine();

            // Consumes the rest of the email as the description
            String eventDescription = "";
            while (read.hasNextLine()) {
                eventDescription += read.nextLine() + '\n';
            }

            // Sets up the new event
            Event event = new Event()
                    .setSummary(eventName)
                    .setLocation(eventLocation)
                    .setDescription(eventDescription)
                    .setStart(start)
                    .setEnd(end);

            read.close();

            // Replace with your calendar link
            String calendarId = "students.claremontmckenna.edu_rrsuvko8qa6enrp9vge8no3veo@group.calendar.google.com";

            // Creates the event in the calender
            serviceCal.events().insert(calendarId, event).execute();

            // Replace with your desired email
            MessageMethods.trashMessage(serviceMail, "cmceventschedule@gmail.com", id);
        }
    }

}