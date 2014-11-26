import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class TestClient {
	private final CountDownLatch closeLatch;

	public TestClient(CountDownLatch closeLatch) {
		this.closeLatch = closeLatch;
	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("connected");
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("connection closed");
		closeLatch.countDown();
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("onMessage: " + message);
	}

	@OnError
	public void onError(Throwable throwable, Session session) {
		throwable.printStackTrace();
	}

	public static void main(String[] args) {
		WebSocketContainer c = ContainerProvider.getWebSocketContainer();
		CountDownLatch close = new CountDownLatch(1);
		TestClient client = new TestClient(close);
		try (Session session = c.connectToServer(client,
				URI.create("ws://echo.websocket.org"))) {
			session.getBasicRemote().sendText("some message");
			close.await();
		} catch (DeploymentException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}