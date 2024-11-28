package booruCLI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import booruDownloader.BooruQueries;
import booruDownloader.MainProgram;
import booruDownloader.Query;

public class CommandLine {

	private CommandLine() {

	}

	/**
	 * Usage: <code>
	 * <b>-b</b> <i>booru</i> <b>-t</b> <i>tag1 tag2 tag3 ...</i>
	 * </code>
	 */ // TODO
	public static void handle(final String[] args) {
		final String argsAsStringLineString = String.join(" ", args);

		final HashMap<String, List<String>> booruQueryPairs = new HashMap<>();

		int cacheMode = 0;
		try (Scanner scanner = new Scanner(argsAsStringLineString)) {
			System.out.printf("CommandLine.handle(%s); ...%n", argsAsStringLineString);

			// Int keeps track of the current operation, defined by the last switch that was
			// passed to the program
			int operation = 0;
			String booru = "";
			do {
				final String currentNext = scanner.next();

//				if (currentNext.startsWith("--target-directory=")) {
//					final String dir = currentNext.split("=", 2)[1];
//					return;
//				}

				// Process current

				switch (operation) {
				case 0:
					// most opmodes should reset to 0 after their logic is done, if they're unable
					// to process multiple inputs
//					System.out.printf("CommandLine.handle() has erroneous opmode '0' for current '%s'%n", currentNext);
					break;
				case 1: // current is a booru specification
					booru = currentNext;
					operation = 0;// TODO allow multiple booru specifications
					break;
				case 2:
					if (!booruQueryPairs.containsKey(booru)) {
						booruQueryPairs.put(booru, new ArrayList<>());
					}
					booruQueryPairs.get(booru).add(currentNext);
					break;
				default:
					System.out.printf("CommandLine.handle() has undefined opmode '%o' for current '%s'%n", operation,
							currentNext);
					break;
				}

				// Change opmodes

				if (cacheMode == 0) {
					if ("--cache-only".equalsIgnoreCase(currentNext))
						cacheMode = 1;

					if ("--no-cache".equalsIgnoreCase(currentNext))
						cacheMode = 2;
				}

				if ("-b".equalsIgnoreCase(currentNext) || "--booru".equalsIgnoreCase(currentNext)) {
					operation = 1;
				}

				if ("-t".equalsIgnoreCase(currentNext) || "--tags".equalsIgnoreCase(currentNext)) {
					operation = 2;
				}

				if ("-u".equalsIgnoreCase(currentNext) || "--uploader".equalsIgnoreCase(currentNext)) {
					operation = 3;
				}

				if ("-iw".equalsIgnoreCase(currentNext) || "--image-width".equalsIgnoreCase(currentNext)) {
					operation = 4;
				}

				if ("-ih".equalsIgnoreCase(currentNext) || "--image-height".equalsIgnoreCase(currentNext)) {
					operation = 5;
				}

				if ("-r".equalsIgnoreCase(currentNext) || "--rating".equalsIgnoreCase(currentNext)) {
					operation = 6;
				}

			} while (scanner.hasNext());
		} catch (final Exception e) {
			e.printStackTrace();
		}

		// Compile queries
		final List<BooruQueries> queries = new ArrayList<>();

		for (Entry<String, List<String>> set : booruQueryPairs.entrySet())
			queries.add(new BooruQueries(set.getKey()).addQuery(new Query(String.join(" ", set.getValue()),
					cacheMode == 2 ? true : false, cacheMode == 1 ? true : false)));

		MainProgram.downloadBoorus(queries);
	}
}
