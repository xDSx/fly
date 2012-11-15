package callcount;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import kieker.analysis.AnalysisController;
import kieker.analysis.plugin.reader.filesystem.FSReader;
import kieker.common.configuration.Configuration;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.DemuxInputStream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.input.DefaultInputHandler;
import org.apache.tools.ant.input.GreedyInputHandler;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

public class Main {

    public static final File JAR_PATH = new File(
            Main.class.getProtectionDomain().getCodeSource(
            ).getLocation().getPath()).getParentFile();

    public static final String JAR_NAME = new File(
            Main.class.getProtectionDomain().getCodeSource(
            ).getLocation().getPath()).getName().toString();

    public static final String KIEKER_JAR = "lib/kieker-1.6-aspectj.jar";

    public static final String AOP_XML = "aop.xml";

    public static void main(String[] args) {
        File inFile = new File(args[0]);
        try {
            String packagesMerged = args[2];
            String pointcut = "";
            String [] packages = packagesMerged.split(";");
            for (int i = 0; i < packages.length; i++) {
                pointcut += "execution(* " + packages[i] +
                        "..*(..)) || call(" + packages[i] + "..*.new(..))";
                if (i < packages.length - 1) {
                    pointcut += " || ";
                }
            }
            String pointcutAn = "execution(* " + args[1] + ".main(..))";
            InputStream is = Main.class.getResourceAsStream("/META-INF/aop_gen.xml");
            byte [] buf = new byte[648];
            is.read(buf);
            String aopGen = new String(buf);
            String aop = aopGen.replace("${pointcut}", pointcut).replace("${pointcutAn}", pointcutAn);
            File aopFile = new File(AOP_XML);
            FileWriter fw = new FileWriter(aopFile);
            fw.write(aop);
            fw.close();
            Project project = new Project();
            project.setBaseDir(JAR_PATH);
            project.init();
            DefaultLogger logger = new DefaultLogger();
            project.addBuildListener(logger);
            logger.setOutputPrintStream(System.out);
            logger.setErrorPrintStream(System.err);
            logger.setMessageOutputLevel(Project.MSG_INFO);
            logger.setEmacsMode(true);

            final Java javaTask = new Java();
            javaTask.setNewenvironment(true);
            javaTask.setTaskName("runjava");
            javaTask.setProject(project);
            javaTask.setFork(true);
            javaTask.setFailonerror(true);

            javaTask.setClassname(args[1]);
            Argument jvmArgs = javaTask.createJvmarg();

            jvmArgs.setLine("-Xms512m -Xmx512m" + " -javaagent:" + KIEKER_JAR +
                    " -Daj.weaving.verbose=true -Dorg.aspectj.weaver.loadtime.configuration=" + AOP_XML);
            Argument taskArgs = javaTask.createArg();
            List<String> al = Arrays.asList(args);
            String argLine = "";
            for (String s: al.subList(3, al.size())) {
                argLine += s;
                argLine += " ";
            }
            taskArgs.setLine(argLine);
            Path classPath = new Path(project);
            classPath.add(Path.systemClasspath);
            FileSet fileSet = new FileSet();
            fileSet.setDir(inFile.getParentFile());
            fileSet.setIncludes(inFile.getName());
            classPath.addFileset(fileSet);
            fileSet = new FileSet();
            fileSet.setDir(JAR_PATH);
            fileSet.setIncludes(KIEKER_JAR);
            fileSet.setIncludes(JAR_PATH.toString());
            fileSet.setIncludes(JAR_NAME);
            classPath.addFileset(fileSet);
            DirSet dirSet = new DirSet();
            dirSet.setDir(JAR_PATH);
            dirSet.setExcludes("*/**");
            classPath.addDirset(dirSet);
            javaTask.setClasspath(classPath);
            javaTask.init();
            javaTask.executeJava();
            aopFile.deleteOnExit();
        }
        catch  (Exception e) {
            e.printStackTrace();
        }

    }

}
