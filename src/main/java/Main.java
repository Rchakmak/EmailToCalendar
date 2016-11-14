
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.Gmail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;

import java.util.List;
import java.util.Scanner;


public class Main {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME_GMAIL =
            "Gmail API Java Quickstart";

    private static final String APPLICATION_NAME_GCAL =
            "GCal API Java Quickstart";


    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR_GMAIL = new java.io.File(
            System.getProperty("user.home"), ".credentials/gmail-java-quickstart");

    private static final java.io.File DATA_STORE_DIR_GCAL = new java.io.File(
            System.getProperty("user.home"), ".credentials/gcal-java-quickstart");


    /**
     * Global instance of the {link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY_GMAIL;
    private static FileDataStoreFactory DATA_STORE_FACTORY_GCAL;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     * <p>
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/gmail-java-quickstart
     */
    private static final List<String> SCOPES_GMAIL =
            Arrays.asList(GmailScopes.GMAIL_MODIFY);

    private static final List<String> SCOPES_GCAL =
            Arrays.asList(CalendarScopes.CALENDAR);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY_GMAIL = new FileDataStoreFactory(DATA_STORE_DIR_GMAIL);
            DATA_STORE_FACTORY_GCAL = new FileDataStoreFactory(DATA_STORE_DIR_GCAL);

        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * return an authorized Credential object.
     * throws IOException
     */
    public static Credential authorizeGMAIL() throws IOException {
        // Load client secrets.
        InputStream in =
                Main.class.getResourceAsStream("/client_secret_GMAIL.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES_GMAIL)
                        .setDataStoreFactory(DATA_STORE_FACTORY_GMAIL)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR_GMAIL.getAbsolutePath());
        return credential;
    }

    public static Credential authorizeGCAL() throws IOException {
        // Load client secrets.
        InputStream in =
                Main.class.getResourceAsStream("/client_secret_GCAL.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES_GCAL)
                        .setDataStoreFactory(DATA_STORE_FACTORY_GCAL)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR_GCAL.getAbsolutePath());
        return credential;
    }


    /**
     * Build and return an authorized Gmail client service.
     * return an authorized Gmail client service
     * throws IOException
     */
    public static Gmail getGmailService() throws IOException {
        Credential credential = authorizeGMAIL();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME_GMAIL)
                .build();
    }

    /**
     * Build and return an authorized Calendar client service.
     *
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar
    getCalendarService() throws IOException {
        Credential credential = authorizeGCAL();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME_GCAL)
                .build();
    }


    /**
     * List all Messages of the user's mailbox matching the query.
     * <p>
     * param service Authorized Gmail API instance.
     * param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * param query String used to filter the Messages listed.
     * throws IOException
     */

    public static List<Message> listMessagesMatchingQuery(Gmail service, String userId,
                                                          String query) throws IOException {
        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setQ(query)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }

        return messages;
    }

    /**
     * Get Message with given ID.
     * <p>
     * param service Authorized Gmail API instance.
     * param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * param messageId ID of Message to retrieve.
     * return Message Retrieved Message.
     * throws IOException
     */
    public static Message getMessage(Gmail service, String userId, String messageId)
            throws IOException {
        Message message = service.users().messages().get(userId, messageId).execute();

        return message;
    }

    /**
     * Trash the specified message.
     * <p>
     * param service Authorized Gmail API instance.
     * param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * param msgId ID of Message to trash.
     * throws IOException
     */
    public static void trashMessage(Gmail service, String user, String msgId)
            throws IOException {
        service.users().messages().trash(user, msgId).execute();
        System.out.println("Message with id: " + msgId + " has been trashed.");
    }


    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
        Gmail serviceMail = getGmailService();
        com.google.api.services.calendar.Calendar serviceCal =
                getCalendarService();

        List<Message> messages = listMessagesMatchingQuery(serviceMail, "cmceventschedule@gmail.com", "");

        for (Message message : messages) {
            String id = message.getId();

            // Replace with your desired email
            Message email = getMessage(serviceMail, "cmceventschedule@gmail.com", id);
            List<MessagePart> content = email.getPayload().getParts();
            Scanner read = new Scanner(new String(Base64.decodeBase64(content.get(0).getBody().getData())));
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
            String eventDescription = "";
            while (read.hasNextLine()) {
                eventDescription += read.nextLine() + '\n';
            }
            Event event = new Event()
                    .setSummary(eventName)
                    .setLocation(eventLocation)
                    .setDescription(eventDescription)
                    .setStart(start)
                    .setEnd(end);

            read.close();

            // Replace with your calendar link
            String calendarId = "students.claremontmckenna.edu_rrsuvko8qa6enrp9vge8no3veo@group.calendar.google.com";
            event = serviceCal.events().insert(calendarId, event).execute();
            System.out.printf("Event created: %s\n", event.getHtmlLink());

            // Replace with your desired email
            trashMessage(serviceMail, "cmceventschedule@gmail.com", id);
        }
    }

}