package bankmanagementsystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AuditLogController {
	@FXML private TextArea logArea;

	@FXML
	private void initialize() {
		loadLog();
	}

	@FXML
	private void handleRefresh() {
		loadLog();
	}

	@FXML
	private void handleClose() {
		Stage stage = (Stage) logArea.getScene().getWindow();
		stage.close();
	}

	private void loadLog() {
		File file = new File("data/audit.log");
		if (!file.exists()) {
			logArea.setText("No audit entries yet.");
			return;
		}
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			logArea.setText(sb.toString());
			logArea.positionCaret(logArea.getText().length());
		} catch (IOException e) {
			logArea.setText("Error reading audit log: " + e.getMessage());
		}
	}
}
