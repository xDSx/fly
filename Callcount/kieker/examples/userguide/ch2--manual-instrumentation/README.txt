This directory contains a pre-compiled Jar file which can be started with 
the 'java' tool using the following commands:
 
Monitoring:

-UNIX-like systems:
 $ java -cp BookstoreMonitoring.jar:lib/kieker-1.6_emf.jar kieker.examples.userguide.ch2bookstore.manual.BookstoreStarter
- Windows:
 $ java -cp BookstoreMonitoring.jar;lib\kieker-1.6_emf.jar kieker.examples.userguide.ch2bookstore.manual.BookstoreStarter

Analysis:

-UNIX-like systems:
 $ java -cp BookstoreAnalysis.jar:lib/kieker-1.6_emf.jar kieker.examples.userguide.ch2bookstore.manual.BookstoreAnalysisStarter </path/to/monitoring-log/>
- Windows:
 $ java -cp BookstoreAnalysis.jar;lib\kieker-1.6_emf.jar kieker.examples.userguide.ch2bookstore.manual.BookstoreAnalysisStarter <path\to\monitoring-log\>

Alternatively, you can compile the sources and start the application using
'ant' with the given 'build.xml' file:

Monitoring:
 $ ant run-monitoring

Analysis:
 $ ant run-analysis -Danalysis.directory=</path/to/monitoring-log/>

Please see Chapter 2 of the Kieker User Guide for details on this example.
