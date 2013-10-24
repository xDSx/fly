package callcount.lib;

import callcount.lib.record.OperationExecutionStartRecord;
import callcount.lib.record.OperationExecutionStopRecord;
import kieker.analysis.plugin.annotation.InputPort;
import kieker.analysis.plugin.annotation.Plugin;
import kieker.analysis.plugin.filter.AbstractFilterPlugin;
import kieker.common.configuration.Configuration;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.timer.ITimeSource;

import java.util.*;

@Plugin(description = "A filter to calculate execution time.")
public class ExecutionTimeFilter extends AbstractFilterPlugin {
    public static final String INPUT_PORT_NAME_EVENTS = "receivedEventsAll";

    public static final double DIV = 1000000.;

    private static final IMonitoringController CTRLINST = MonitoringController.getInstance();
    private static final ITimeSource TIME = CTRLINST.getTimeSource();

    private final Map<List<String>, Long> calls = new HashMap<List<String>, Long>();
    private final Map<Long, Stack<OperationExecutionStartRecord>> trees = new HashMap<Long, Stack<OperationExecutionStartRecord> >();

    public ExecutionTimeFilter(final Configuration configuration) {
        super(configuration);
    }

    public Configuration getCurrentConfiguration() {
        return configuration;
    }

    public Map<List<String>, Long> getMap() {
        return calls;
    }

    @InputPort(name = INPUT_PORT_NAME_EVENTS, description = "Receives call operation events", eventTypes =
            { OperationExecutionStartRecord.class, OperationExecutionStopRecord.class })
    public final void inputEvent(final Object object) {
        if (object instanceof  OperationExecutionStartRecord) {
            OperationExecutionStartRecord e = (OperationExecutionStartRecord) object;
            if (!trees.containsKey(e.getTraceId())) {
                Stack<OperationExecutionStartRecord> ns = new Stack<OperationExecutionStartRecord>();
                ns.push(e);
                trees.put(e.getTraceId(), ns);
            }
            else {
                Stack<OperationExecutionStartRecord> tree = trees.get(e.getTraceId());
                while (e.getEss() <= tree.size() - 1) {
                    tree.pop();
                    System.out.println("Shouldn't reach");
                }
                List<String> key = new ArrayList<String>();
                key.add(tree.peek().getOperationSignature());
                key.add(e.getOperationSignature());
                tree.push(e);
            }
        }
        else {
            OperationExecutionStopRecord e = (OperationExecutionStopRecord) object;
            Stack<OperationExecutionStartRecord> tree = trees.get(e.getTraceId());
            if (tree.size() > 1) {
                List<String> key = new ArrayList<String>();
                OperationExecutionStartRecord m2 = tree.peek();
                tree.pop();
                OperationExecutionStartRecord m1 = tree.peek();
                key.add(m1.getOperationSignature());
                key.add(m2.getOperationSignature());
                if (calls.containsKey(key)) {
                    calls.put(key, calls.get(key) + (e.getTout() - m2.getTin()));
                }
                else {
                    calls.put(key, (e.getTout() - m2.getTin()));
                }
            }
            else {
                tree.pop();
            }
        }
    }

    public void postProcess() {
        final long tend = TIME.getTime();
        for (Stack<OperationExecutionStartRecord> tree: trees.values()) {
            while (tree.size() > 1) {
                List<String> key = new ArrayList<String>();
                OperationExecutionStartRecord m2 = tree.peek();
                tree.pop();
                OperationExecutionStartRecord m1 = tree.peek();
                key.add(m1.getOperationSignature());
                key.add(m2.getOperationSignature());
                if (calls.containsKey(key)) {
                    calls.put(key, calls.get(key) + (tend - m2.getTin()));
                }
                else {
                    calls.put(key, (tend - m2.getTin()));
                }
            }
        }
    }
}
