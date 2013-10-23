package callcount.lib;

import java.io.*;
import java.util.Iterator;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
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

    public static final String KIEKER_JAR = JAR_PATH + "/" + "kieker-aspectj-1.6.jar";

    public static final String AOP_XML = "aop.xml";

    public static void analyze(File roota, String jar, String cls,
                               String packets, String output, String args) {
        File inFile = new File(jar).getAbsoluteFile();
	File root = inFile.getParentFile();
        try {
            String packagesMerged = packets;
            String pointcut = "";
            String [] packages = packagesMerged.split(";");
            for (int i = 0; i < packages.length; i++) {
                pointcut += "execution(* " + packages[i] +
                        "..*(..)) || call(" + packages[i] + "..*.new(..))";
                if (i < packages.length - 1) {
                    pointcut += " || ";
                }
            }
            String pointcutAn = "execution(* " + cls + ".main(..))";
            InputStream is = Main.class.getResourceAsStream("/META-INF/aop_gen.xml");
            byte [] buf = new byte[700];
            int len = is.read(buf);
            String aopGen = new String(buf, 0, len);
            String aop = aopGen.replace("${pointcut}", pointcut).replace("${pointcutAn}", pointcutAn);
            File aopFile = new File(root + "/" + AOP_XML);
            FileWriter fw = new FileWriter(aopFile);
            fw.write(aop);
            fw.close();
            Project project = new Project();
            project.setBaseDir(root);
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
            //javaTask.setDir(new File("."));
            javaTask.setClassname(cls);
            Argument jvmArgs = javaTask.createJvmarg();

            jvmArgs.setLine("-Xms512m -Xmx512m" + " -javaagent:" + KIEKER_JAR +
                    " -Daj.weaving.verbose=true -Dorg.aspectj.weaver.loadtime.configuration="
                    + AOP_XML);
            Argument taskArgs = javaTask.createArg();
            String argLine = output + " " + args;
            taskArgs.setLine(argLine);
            Path classPath = new Path(project);
            classPath.add(Path.systemClasspath);
            FileSet fileSet = new FileSet();
            fileSet.setDir(inFile.getParentFile());
            fileSet.setIncludes(inFile.getName());
            System.out.println(inFile);
            classPath.addFileset(fileSet);
            fileSet = new FileSet();
            fileSet.setDir(JAR_PATH);
            fileSet.setIncludes(KIEKER_JAR);
            fileSet.setIncludes(JAR_PATH.toString());
            fileSet.setIncludes(JAR_NAME);
            classPath.addFileset(fileSet);
            fileSet = new FileSet();
            fileSet.setDir(root);
            fileSet.setIncludes(AOP_XML);
            classPath.addFileset(fileSet);
            DirSet dirSet = new DirSet();
            dirSet.setDir(JAR_PATH);
            dirSet.setExcludes("*/**");
            classPath.addDirset(dirSet);
            dirSet = new DirSet();
            dirSet.setDir(root);
            dirSet.setExcludes("*/**");
            classPath.addDirset(dirSet);
            for (String s:classPath.list())
                System.out.println(s);
            javaTask.setClasspath(classPath);
            javaTask.init();
            javaTask.executeJava();
            aopFile.deleteOnExit();
        }
        catch  (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String argL = "";
        for (int i = 4; i < args.length; i++) {
            argL += args[i];
            argL += " ";
        }
        analyze(new File("."), args[0], args[1], args[2], args[3], argL);
    }

}
