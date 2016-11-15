import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.Gmail;
import java.io.IOException;


// Handles the creation of the Calendar and GMail Object
// which requires authorization by Google's Auth2.0
public class Credentials {

    // Application name.
    private static final String APPLICATION_NAME_GMAIL =
            "Gmail API Java Quickstart";

    private static final String APPLICATION_NAME_GCAL =
            "GCal API Java Quickstart";


    // Directory to store user credentials for this application.
    private static final java.io.File DATA_STORE_DIR_GMAIL = new java.io.File(
            System.getProperty("user.home"), ".credentials/gmail-java-quickstart");

    private static final java.io.File DATA_STORE_DIR_GCAL = new java.io.File(
            System.getProperty("user.home"), ".credentials/gcal-java-quickstart");


    // Global instance of the {link FileDataStoreFactory}.
    private static FileDataStoreFactory DATA_STORE_FACTORY_GMAIL;
    private static FileDataStoreFactory DATA_STORE_FACTORY_GCAL;


    //Global instance of the JSON factory.
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();


    // Global instance of the HTTP transport.
    private static HttpTransport HTTP_TRANSPORT;


    /**
     * Global instance of the scopes required by this quickstart.
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
        } catch(Throwable t)
        {
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

}