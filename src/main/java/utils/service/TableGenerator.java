package utils.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TableGenerator {
	private static final Logger logger = LoggerFactory.getLogger(TableGenerator.class);
	private static final String LINE_RESULT_FORMAT = "%s | %s";
	public Map<String, List<String>> generateStudentWrongAnswers(String filePath) throws IOException {
		Map<String, List<String>> result = new HashMap<>();
		Files.readAllLines(Paths.get(filePath))
				.forEach(generatorRecords(result));
		return result.entrySet()
				.stream()
				.sorted(getEntryComparator())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	private Comparator<Map.Entry<String, List<String>>> getEntryComparator() {
		return (question1, question2) -> {
			float questionNumber1;
			float questionNumber2;
			try {
				questionNumber1 = Float.parseFloat(question1.getKey());
			} catch (NumberFormatException e) {
				logger.info(String.format("Format is wrong on student: %s", question1.getValue()));
				return -1;
			}

			try {
				questionNumber2 = Float.parseFloat(question2.getKey());
			} catch (NumberFormatException e) {
				logger.info(String.format("Format is wrong on student: %s", question2.getValue()));
				return 1;
			}

			if (questionNumber1 > questionNumber2) {
				return 1;
			}
			if (questionNumber1 < questionNumber2){
				return -1;
			}
			return question1.getKey().compareTo(question2.getKey());
		};
	}

	public void putResultIntoFile(Map<String, List<String>> result, String filePath) throws IOException {
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

	private Consumer<String> generatorRecords(Map<String, List<String>> result) {
		return line -> {
			String[] params = line.split("\\|");
			if (params.length != 2) {
				return;
			}
			String student = params[0];
			String[] questions = params[1].trim().split(",");
			Arrays.stream(questions)
					.map(String::trim)
					.forEach(question -> {
						if (result.containsKey(question)) {
							result.get(question).add(student);
						} else {
							List<String> students = new ArrayList<>();
							students.add(student);
							result.put(question, students);
						}
					});
		};
	}

}
