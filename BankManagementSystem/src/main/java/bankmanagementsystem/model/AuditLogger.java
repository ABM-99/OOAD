package bankmanagementsystem.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLogger {
	private static final String DATA_DIR = "data";
	private static final String AUDIT_FILE = DATA_DIR + "/audit.log";
	private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	static {
		File dataDir = new File(DATA_DIR);
		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}
	}

	public static synchronized void log(String category, String actor, String subjectId, String action, String details, boolean success) {
		String timestamp = LocalDateTime.now().format(TS);
		String line = String.join("|",
				timestamp,
				"category=" + safe(category),
				"actor=" + safe(actor),
				"subject=" + safe(subjectId),
				"action=" + safe(action),
				"success=" + success,
				"details=" + safe(details)
		);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(AUDIT_FILE, true))) {
			writer.write(line);
			writer.newLine();
		} catch (IOException e) {
			System.err.println("❌ Error writing audit log: " + e.getMessage());
		}
	}

	private static String safe(String value) {
		if (value == null) return "";
		return value.replace('\n', ' ').replace('\r', ' ');
	}
}
