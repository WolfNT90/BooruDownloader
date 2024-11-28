package booruDownloader;

import java.util.List;

import booruCLI.CommandLine;

public class MainProgram {

	private static long startExecTimeMilis;
	private static BooruManager booruManager;

	public static void main(String[] args) {
		startExecTimeMilis = System.currentTimeMillis();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (booruManager != null)
				booruManager.onFinishedInterpretingQuery();
			BooruDownloader.onProgramShutdown();
			BooruDownloader.printFailedDownloads();
		}));

		// Handle CLI
		if (args.length > 0)
			CommandLine.handle(args);
		else
			downloadBoorus(QueriesFileParser.parseConfigFile());
	}

	public static void downloadBoorus(List<BooruQueries> boorusQueries) {
		for (BooruQueries booruQueries : boorusQueries) {
			String booru = booruQueries.getBooruURL();
			List<Query> queries = booruQueries.getBooruQueries();

			if (queries.isEmpty()) {
				continue;
			}

			System.out.println("Starting " + booru + " downloads");
			booruManager = new BooruManager(booru);
			for (Query query : queries) {
				System.out.println(query.getQuery().toLowerCase());
				booruManager.interpretQuery(query);
				booruManager.onFinishedInterpretingQuery();
				System.out.println();
			}
			System.out.println();
		}

		System.out.printf("All downloads finished. (took %o sec)%n",
				(System.currentTimeMillis() - startExecTimeMilis) / 1000);
		BooruDownloader.onProgramShutdown();
	}

}
