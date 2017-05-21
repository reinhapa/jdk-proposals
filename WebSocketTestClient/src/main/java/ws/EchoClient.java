package ws;

import java.util.function.Consumer;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint
public final class EchoClient {
	private final Consumer<String> messageConsumer;

	EchoClient(Consumer<String> messageConsumer) {
		this.messageConsumer = messageConsumer;
	}

	@OnOpen
	public void onOpen(Session session) {
	}

	@OnClose
	public void onClose(Session session) {
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		messageConsumer.accept(message);
	}

	@OnError
	public void onError(Throwable throwable, Session session) {
		throwable.printStackTrace();
	}
}