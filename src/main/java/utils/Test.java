package utils;

import utils.service.TableGenerator;

import java.io.IOException;

public class Test {
	private static final TableGenerator tableGenerator = new TableGenerator();
	private static final String FILE_PATH_PATTERN = "./%s";

	public static void main(String[] args) throws IOException {
		String inputFileName = args[0];
		String inputFilePath = String.format(FILE_PATH_PATTERN, inputFileName);

		String outputFileName = args[1];
		String outputFilePath = String.format(FILE_PATH_PATTERN, outputFileName);

		tableGenerator.putResultIntoFile(tableGenerator.generateStudentWrongAnswers(inputFilePath), outputFilePath);
	}
}
