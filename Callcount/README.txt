building:
requires maven
build with mvn package

usage:
java -jar Callcount Callcount-1.0.jar <jar> <class> <packets> <output>

packets - list of packets separated by semicolons

example:

java -cp "Callcount-1.0.jar;JUNIT_PATH" Main JUNIT_PATH/junit-4.10.jar org.junit.runner.JUnitCore org.junit out.csv