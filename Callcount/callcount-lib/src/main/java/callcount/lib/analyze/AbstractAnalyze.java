package callcount.lib.analyze;


import callcount.lib.CallFilter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import callcount.lib.ExecutionTimeFilter;
import kieker.analysis.AnalysisController;
import kieker.analysis.plugin.reader.namedRecordPipe.PipeReader;
import kieker.common.configuration.Configuration;
import kieker.common.namedRecordPipe.Broker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public abstract class AbstractAnalyze {

    @Pointcut
    public abstract void main();

    @Around("main()")
    public Object startAnalyzeThread(final ProceedingJoinPoint thisJoinPoint) throws Throwable {
        Object[] args = thisJoinPoint.getArgs();
        Object[] arg = (Object[])args[0];
        final String outFile = (String)arg[0];
        final AnalysisController analysisInstance = new AnalysisController();
        List al = Arrays.asList(arg);
        al = al.subList(1, al.size());
        String[] nArg = new String[al.size()];
        int i = 0;
        for (Object o: al) {
            nArg[i] = (String)o;
            i++;
        }
        args[0] = nArg;
        PipeReader reader;
        {
            final Configuration conf = new Configuration();
            conf.setProperty(PipeReader.CONFIG_PROPERTY_NAME_PIPENAME, PipeReader.CONFIG_PROPERTY_VALUE_PIPENAME_DEFAULT);
            reader = new PipeReader(conf);
            analysisInstance.registerReader(reader);
        }
        final CallFilter callFilter;
        final ExecutionTimeFilter etFilter;
        {
            final Configuration conf = new Configuration();
            callFilter = new CallFilter(conf);
            final Configuration conf2 = new Configuration();
            etFilter = new ExecutionTimeFilter(conf2);

            analysisInstance.registerFilter(callFilter);
            analysisInstance.registerFilter(etFilter);
        }
        analysisInstance.connect(reader, PipeReader.OUTPUT_PORT_NAME_RECORDS, callFilter, CallFilter.INPUT_PORT_NAME_EVENTS);
        analysisInstance.connect(reader, PipeReader.OUTPUT_PORT_NAME_RECORDS, etFilter, ExecutionTimeFilter.INPUT_PORT_NAME_EVENTS);
        final Thread analysis = new Thread(new Runnable() {
            public void run() {
        try {
            analysisInstance.run();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
            }});
        Thread shutdown = new Thread(new Runnable() {
            public void run() {
                try {
                    analysis.join();
                    PrintWriter fout = new PrintWriter(new FileWriter(outFile));
                    //fout.println("Source;Target;Weight");
                    etFilter.postProcess();
                    for (List<String> s: callFilter.getMap().keySet()) {
                        fout.println(s.get(0) + ";" + s.get(1) + ";" +callFilter.getMap().get(s) + ";" +
                                (int)(Math.max(etFilter.getMap().get(s) / 1000000, 1)));
                        //fout.println("\"" + s.get(0) + "\";\"" + s.get(1) + "\";" +callFilter.getMap().get(s));
                    }
                    fout.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }});
        Runtime.getRuntime().addShutdownHook(shutdown);
        analysis.start();
        Object ret = thisJoinPoint.proceed(args);
        Broker.INSTANCE.acquirePipe(PipeReader.CONFIG_PROPERTY_VALUE_PIPENAME_DEFAULT).close();
        return ret;
    }
}
