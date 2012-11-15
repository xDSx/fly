building:
requires maven
download the kieker binary release: http://se.informatik.uni-kiel.de/kieker/
extract and go to the dist subfolder in the command prompt
run:
mvn install:install-file -Dfile=kieker-1.6_aspectj.jar -DgroupId=net.kieker-monitoring -DartifactId=kieker -Dversion=1.6 -Dclassifier=aspectj
mvn install:install-file -Dfile=kieker-1.6_emf.jar -DgroupId=net.kieker-monitoring -DartifactId=kieker -Dversion=1.6 -Dclassifier=emf

build with mvn package

usage:
java -jar Callcount Callcount-1.0.jar <jar> <class> <packets> <output>

packets - list of packets separated by semicolons

example:

java -cp "Callcount-1.0.jar;JUNIT_PATH" callcount.Main JUNIT_PATH/junit-4.10.jar org.junit.runner.JUnitCore org.junit out.csv