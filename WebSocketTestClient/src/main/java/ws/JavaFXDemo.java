package ws;

import java.io.IOException;
import java.net.URI;

import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

// Simple Hello World JavaFX program
public class JavaFXDemo extends Application {
	private final WebSocketContainer container;
	private Session session;

	public static void main(String[] args) {
		launch(args);
	}

	public JavaFXDemo() {
		container = ContainerProvider.getWebSocketContainer();
	}

	// JavaFX entry point
	@Override
	public void start(Stage primaryStage) throws Exception {
		FlowPane root = new FlowPane(Orientation.VERTICAL);
		root.setPadding(new Insets(5, 5, 5, 5));

		TextField textField = new TextField();
		TextArea echoResponse = new TextArea();
		root.getChildren().add(textField);
		root.getChildren().add(echoResponse);

		Slider slider = new Slider();
		root.getChildren().add(slider);
		for (SkinType type : SkinType.values()) {
			Gauge gauge = GaugeBuilder.create().skinType(type).minValue(0).maxValue(180).prefSize(150, 150)
					.unit(type.name()).build();
			slider.maxProperty().bind(gauge.maxValueProperty());
			gauge.valueProperty().bind(slider.valueProperty());
			root.getChildren().add(gauge);
		}

		session = container.connectToServer(new EchoClient(message -> {
			echoResponse.appendText(message.concat("\n"));
		}), URI.create("ws://echo.websocket.org"));
		textField.setOnAction(value -> {
			try {
				session.getBasicRemote().sendText(textField.getText());
				textField.clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		// Top level container for all view content
		Scene scene = new Scene(root);

		// primaryStage is the main top level window created by platform
		primaryStage.setTitle("test");
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		if (session != null) {
			session.close();
		}
		super.stop();
	}
}