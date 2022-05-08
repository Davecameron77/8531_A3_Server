package comp8031;

import comp8031.model.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
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

@ServerEndpoint(value = "/websocket/{userName}", 
                decoders = MessageDecoder.class,
                encoders = MessageEncoder.class)
public class WebSocket {

    private Session session;
    private static Set<WebSocket> connection = new HashSet<>();
    private static Map<String, String> users = new HashMap<>();

    public WebSocket() {
        users = new HashMap<>();
    }    
      
    @OnOpen
    public void onOpen(Session session, @PathParam("userName") String userName) {
        this.session = session;
        connection.add(this);
        users.put(session.getId(), userName); 
        Message message = new Message();
        message.setFrom("Server");
        message.setTo(userName);
        message.setContent(String.format("Welcomet %s join chat room", userName));
        broadcaset(message, null);
    }

    private static void broadcaset(Message message, Session session) {
        if(session == null) {
            connection.forEach(connection -> {
                try {
                    System.out.println(message);
                    connection.getSession().getBasicRemote().sendObject(message);
                } catch (IOException | EncodeException exc) {
                    System.out.println(exc.getLocalizedMessage());
                }
            });
        } else {
            try{
                System.out.println(message);
                connection.stream()
                          .filter(c -> c.getSession() != session ).collect(Collectors.toList())
                          .get(0)
                          .getSession().getBasicRemote().sendObject(message);
            }catch (IOException | EncodeException exc ) {
                System.out.println(exc.getLocalizedMessage());
            }
        }

    }

    public Session getSession() {
        return session;
    }

    @OnMessage
    public void onMessage(String string, Session session) throws IOException, DecodeException {
        MessageDecoder messageDecoder = new MessageDecoder();
        
        String toUserName = null;
        for(Map.Entry<String, String> entry: users.entrySet()){
           if(!entry.getKey().equalsIgnoreCase(session.getId())){
               toUserName = entry.getValue();
           }
        }
        Message message = messageDecoder.decode(string);
        broadcaset(message, session);
        System.out.println(string);
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Session cloused");
    }

} 
