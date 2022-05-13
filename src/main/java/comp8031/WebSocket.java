package comp8031;

import comp8031.model.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.websocket.DecodeException;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket/device",
                decoders = MessageDecoder.class,
                encoders = MessageEncoder.class)
public class WebSocket {

    private Session session;
    private static Set<WebSocket> connection = new HashSet<>();
    static private final int QUORUM = 3;
    private static List<String> users;

    public WebSocket() {
        users = new ArrayList<>(QUORUM);
    }    
      
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        connection.add(this);
        users.add(session.getId());
        System.out.printf("New User gets connected %s \n",session.getId());
    }

    private static void broadcaset(Message message, Session session) {
        try{
            System.out.println(message.toString());
            connection.stream()
                    .collect(Collectors.toList())
                    .get(0)
                    .getSession().getBasicRemote().sendObject(message);
        }catch (IOException | EncodeException exc ) {
            System.out.println(exc.getLocalizedMessage());
        }
        
    }

    public Session getSession() {
        return session;
    }

    @OnMessage
    public void onMessage(String string, Session session) throws IOException, DecodeException {
        MessageDecoder messageDecoder = new MessageDecoder();
        System.out.println(string);
        Message message = messageDecoder.decode(string);
        broadcaset(message, session);
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        Message message = new Message();
        this.users.removeIf(user -> user.equals(session.getId())); 
        broadcaset(message, session);
    }

} 
