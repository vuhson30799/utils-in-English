package utils.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TableGenerator {
	private static final String LINE_RESULT_FORMAT = "%.2f | %s";
	public Map<Float, List<String>> generateStudentWrongAnswers(String filePath) throws IOException {
		Map<Float, List<String>> result = new HashMap<>();
		Files.readAllLines(Paths.get(filePath))
				.forEach(generatorRecords(result));
		return result.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	public void putResultIntoFile(Map<Float, List<String>> result, String filePath) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8);
		result.forEach((key, value) -> {
			String line = String.format(LINE_RESULT_FORMAT, key, value);
			try {
				writer.write(line);
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		writer.flush();
		writer.close();
	}

	private Consumer<String> generatorRecords(Map<Float, List<String>> result) {
		return line -> {
			String[] params = line.split("\\|");
			if (params.length != 2) {
				return;
			}
			String student = params[0];
			String[] questions = params[1].trim().split(",");
			Arrays.stream(questions)
					.forEach(question -> {
						Float questionNumber = Float.valueOf(question);
						if (result.containsKey(questionNumber)) {
							result.get(questionNumber).add(student);
						} else {
							List<String> students = new ArrayList<>();
							students.add(student);
							result.put(questionNumber, students);
						}
					});
		};
	}

}
