import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageMethods {

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

}
