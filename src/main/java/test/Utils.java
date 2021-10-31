package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Utils {
	private static final Logger logger = Logger.getLogger(Utils.class.getName());
	private static final String FILE_PATH_PATTERN = "./%s";
	private static final int MAX_OPTION = 5;

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Let's input file name (file should be put in resources folder):");
		String fileName = scanner.nextLine();
		String filePath = String.format(FILE_PATH_PATTERN, fileName);
		printMenu();
		int option = scanner.nextInt();
		List<String> results = Collections.emptyList();
		switch (option) {
			case 0:
				break;
			case 1:
				results = handleFileWithTabs(filePath);
				break;
			case 2:
				results = handleFileWithColon(filePath);
				break;
			default:
				throw new IllegalArgumentException("This operation is not supported.");
		}

		logger.info("=========RESULT=========");
		results.forEach(System.out::println);
	}

	private static void printMenu() {
		System.out.println("Menu: ");
		System.out.println("0. Exit");
		System.out.println("1. Handle file containing tabs");
		System.out.println("2. Handle file containing colon");
		System.out.println("3. Handle file to generate table of the people who get wrong answer in question");
	}

	private static List<String> handleFileWithColon(String filePath) throws IOException {
		return Files.readAllLines(Paths.get(filePath))
				.stream()
				.filter(line -> !line.isBlank())
				.map(String::trim)
				.map(Utils::addColonIfNotExist)
				.map(Utils::handleWordType)
				.map(Utils::moveVNAsLeading)
				.map(Utils::createDataTable)
				.map(String::trim)
				.collect(Collectors.toList());
	}

	private static String createDataTable(String line) {
		String[] wordsByColon = line.split(":");
		if (wordsByColon.length == 2) {
			String[] wordsByEqualSign = wordsByColon[1].split("=");
			String result = wordsByEqualSign[0];

			if (wordsByEqualSign.length > 1) {
				for (int i = 1; i< wordsByEqualSign.length; i++) {
					result = result.concat(";").concat(wordsByEqualSign[i]);
				}
			}

			if (MAX_OPTION - wordsByEqualSign.length > 0) {
				for (int i = 0; i < MAX_OPTION - wordsByEqualSign.length; i++) {
					result = result.concat(";");
				}
			}
			return wordsByColon[0].concat(";").concat(result);
		}
		logger.log(Level.SEVERE, line);
		return line;
	}

	private static String moveVNAsLeading(String line) {
		String[] words = line.split(":");
		if (words.length == 2) {
			return words[1].concat(":").concat(words[0]);
		}
		logger.log(Level.SEVERE, line);
		return line;
	}

	private static String addColonIfNotExist(String line) {
		if (line.contains(":")) {
			return line;
		}
		return line.replace(")", "):");
	}

	private static List<String> handleFileWithTabs(String filePath) throws IOException {
		return Files.readAllLines(Paths.get(filePath))
				.stream()
				.filter(line -> !line.isBlank())
				.map(String::trim)
				.map(Utils::handleWordType)
				.map(Utils::handleTabs)
				.collect(Collectors.toList());
	}


	private static String handleTabs(String line) {
		line = line.replace("\t", "");
		if (!line.matches(".* {2,}.*")) {
			logger.log(Level.SEVERE, line);
		}
		return line.replaceAll(" {2,}", "\t");
	}

	private static String handleWordType(String line) {
		String wordType = Arrays.stream(Type.values())
				.filter(type -> line.contains(type.pattern))
				.map(type -> type.pattern)
				.findAny()
				.orElse("");
		if (!wordType.isBlank()) {
			return line.replace(wordType, "")
					.concat(" ")
					.concat(wordType);
		} else {
			logger.log(Level.SEVERE, line);
		}

		return line.replaceAll(" {2,}", " ");
	}
}
