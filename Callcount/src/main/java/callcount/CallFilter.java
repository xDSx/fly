
package callcount;

import callcount.record.OperationExecutionStartRecord;
import callcount.record.OperationExecutionStopRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import kieker.analysis.plugin.annotation.InputPort;
import kieker.analysis.plugin.annotation.Plugin;
import kieker.analysis.plugin.filter.AbstractFilterPlugin;
import kieker.common.configuration.Configuration;

@Plugin(description = "A filter to convert CallOperationEvents")
public class CallFilter extends AbstractFilterPlugin{

    public static final String INPUT_PORT_NAME_EVENTS = "receivedEvents";

    private final Map<List<String>, Integer> calls = new HashMap<List<String>, Integer>();
    private final Map<Long, Stack<String> > trees = new HashMap<Long, Stack<String> >();

    public CallFilter(final Configuration configuration) {
        super(configuration);
    }

    public Configuration getCurrentConfiguration() {
        return configuration;
    }

    public Map<List<String>, Integer> getMap() {
        return calls;
    }

    @InputPort(name = INPUT_PORT_NAME_EVENTS, description = "Receives call operation events", eventTypes = { OperationExecutionStartRecord.class })
    public final void inputEvent(final Object object) {
        OperationExecutionStartRecord e = (OperationExecutionStartRecord) object;
        if (!trees.containsKey(e.getTraceId())) {
            Stack<String> ns = new Stack<String>();
            ns.push(e.getOperationSignature());
            trees.put(e.getTraceId(), ns);
        }
        else {
            Stack<String> tree = trees.get(e.getTraceId());
            while (e.getEss() <= tree.size() - 1) {
                tree.pop();
            }
            List<String> key = new ArrayList<String>();
            key.add(tree.peek());
            key.add(e.getOperationSignature());
            if (calls.containsKey(key)) {
                calls.put(key, calls.get(key) + 1);
            }
            else {
                calls.put(key, 1);
            }
            tree.push(e.getOperationSignature());
        }
        //System.err.println(e.getOperationSignature() + " " + e.getEss());
    }

}
