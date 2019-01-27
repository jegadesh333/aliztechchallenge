package com.aliz.ai;

import com.google.common.collect.Ordering;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class FileIndexer {

  private static final String FILE_OUTPUT_LABEL = "index";
  private static final String DASH_SEPARATOR = "-";
  private static final String DOT_SEPARATOR = ".";
  private static final String BACKSLASH_SEPRATOR = "/";
  private static final String SPECIAL_CHARACTER_REGEX_PATTERN="[+.^:,]";
  private static final String EMPTY_STRING="";
  private static BufferedReader reader = null;
  private static BufferedWriter writer = null;
  private static FileWriter fileWriter = null;

  public static void main(String[] args) {

    SortedSetMultimap<String, Integer> values =
        TreeMultimap.create(Ordering.<String>natural(), Ordering.<Integer>natural());
    String inputFilePath = args[0];
    String outputFilePath = args[1];
    String outputFileName = outputFilePath + BACKSLASH_SEPRATOR + getOutputFileName(inputFilePath);

    try {
      File f = new File(inputFilePath);
      reader = new BufferedReader(new FileReader(f));
      File file = new File(outputFileName);
      fileWriter = new FileWriter(file, true);
      writer = new BufferedWriter(fileWriter);
      String readLine = "";
      int lineNumber = 1;
      while ((readLine = reader.readLine()) != null) {
        updateIndex(readLine, lineNumber, values);
        lineNumber++;
      }

      for (String key : values.keySet()) {
        String indexNumbers = values.get(key).toString();
        String indexLine = key + " " + indexNumbers.substring(1, indexNumbers.length() - 1);
        writer.write(indexLine + "\n");
        System.out.println(indexLine);
      }

    } catch (IOException e) {
      System.err.println("Input/Output file not found. Please input appropriate name");
      e.printStackTrace();
    } finally {
      try {
        reader.close();
        writer.close();
        fileWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static void updateIndex(String readLine, int lineNumber, SortedSetMultimap<String, Integer> values) {
    String normalizedString = readLine.replaceAll(SPECIAL_CHARACTER_REGEX_PATTERN, EMPTY_STRING);
    StringTokenizer st = new StringTokenizer(normalizedString);
    while (st.hasMoreTokens()) {
      values.put(st.nextToken().toLowerCase(), lineNumber);
    }
  }

  private static String getOutputFileName(String inputFile) {
    String inputFileName = getFileNameFromPath(inputFile);
    String inputFileNameArray[] = inputFileName.split("\\.", -1);
    String outputFileName = inputFileNameArray[0] + DASH_SEPARATOR + FILE_OUTPUT_LABEL + DOT_SEPARATOR+ inputFileNameArray[1];
    return outputFileName;
  }

  private static String getFileNameFromPath(String inputFile) {
    String input[] = inputFile.split(BACKSLASH_SEPRATOR);
    return input[input.length - 1];
  }
}
