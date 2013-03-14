/***************************************************************************
 * Copyright 2012 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package callcount.lib.record;

import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;

/**
 * @author Andre van Hoorn, Jan Waller
 */
public final class OperationExecutionStopRecord extends AbstractMonitoringRecord implements IMonitoringRecord.Factory {

	/**
	 * Constant to be used if no hostname required.
	 */
	public static final String NO_HOSTNAME = "<default-host>";

	/**
	 * Constant to be used if no session ID required.
	 */
	public static final String NO_SESSION_ID = "<no-session-id>";

	/**
	 * Constant to be used if no trace ID required.
	 */
	public static final long NO_TRACEID = -1;

	/**
	 * Constant to be used if no timestamp required.
	 */
	public static final long NO_TIMESTAMP = -1;

	/**
	 * Constant to be used if no eoi or ess required.
	 */
	public static final int NO_EOI_ESS = -1;

	/**
	 * This field should not be exported, because it makes little sense to have no associated operation
	 */
	private static final String NO_OPERATION_SIGNATURE = "noOperation";

	private static final long serialVersionUID = 8028082734210614968L;
	private static final Class<?>[] TYPES = {
		String.class, // sessionId
		long.class, // traceId
		long.class, // tout
		String.class, // hostname
		int.class, // eoi
		int.class, // ess
	};

	private final String hostname;
	private final String sessionId;
	private final long traceId;
	private final long tout;
	private final int eoi;
	private final int ess;

	/**
	 * Creates a new {@link OperationExecutionRecord} with the given parameters.
	 *
	 * @param sessionId
	 *            the session ID; must not be null, use {@link #NO_SESSION_ID} if no session ID desired.
	 * @param traceId
	 *            the trace ID; use {@link #NO_TRACEID} if no trace ID desired.
	 * @param tout
	 *            the execution stop timestamp; use {@link #NO_TIMESTAMP} if no timestamp desired.
	 * @param hostname
	 *            the host name; must not be null, use {@link #NO_HOSTNAME} if no host name desired.
	 * @param eoi
	 *            the execution order index (eoi); use {@link #NO_EOI_ESS} if no eoi desired.
	 * @param ess
	 *            the execution order index (ess); use {@link #NO_EOI_ESS} if no ess desired.
	 */
	public OperationExecutionStopRecord(final String sessionId, final long traceId, final long tout,
			final String hostname, final int eoi, final int ess) {
		this.traceId = traceId;
		this.tout = tout;
		this.sessionId = (sessionId == null) ? NO_SESSION_ID : sessionId; // NOCS
		this.hostname = (hostname == null) ? NO_HOSTNAME : hostname; // NOCS
		this.eoi = eoi;
		this.ess = ess;
	}

	public OperationExecutionStopRecord(final Object[] values) { // NOPMD (values stored directly)
		AbstractMonitoringRecord.checkArray(values, TYPES); // throws IllegalArgumentException
		this.sessionId = (String) values[1];
		this.traceId = (Long) values[2];
		this.tout = (Long) values[4];
		this.hostname = (String) values[5];
		this.eoi = (Integer) values[6];
		this.ess = (Integer) values[7];
	}

	public final Object[] toArray() {
		return new Object[] {
			this.sessionId,
			this.traceId,
			this.tout,
			this.hostname,
			this.eoi,
			this.ess, };
	}

	@Deprecated
	public void initFromArray(final Object[] values) {
		throw new UnsupportedOperationException();
	}

	public Class<?>[] getValueTypes() {
		return TYPES.clone();
	}

	/**
	 * @return the hostname
	 */
	public final String getHostname() {
		return this.hostname;
	}

	/**
	 * @return the sessionId
	 */
	public final String getSessionId() {
		return this.sessionId;
	}

	/**
	 * @return the traceId
	 */
	public final long getTraceId() {
		return this.traceId;
	}

	/**
	 * @return the tout
	 */
	public final long getTout() {
		return this.tout;
	}

	/**
	 * @return the eoi
	 */
	public final int getEoi() {
		return this.eoi;
	}

	/**
	 * @return the ess
	 */
	public final int getEss() {
		return this.ess;
	}

}
