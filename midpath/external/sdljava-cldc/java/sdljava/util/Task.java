package sdljava.util;

import java.util.TimerTask;

/**
 *  sdljava - a java binding to the SDL API
 *
 *  Copyright (C) 2004  Ivan Z. Ganza
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA
 *
 *  Ivan Z. Ganza (ivan_ganza@yahoo.com)
 *  Bart LEBOEUF  (bartleboeuf@yahoo.fr)
 */

/**
 * A task that can be scheduled for one-time or repeated execution by the Scheduler.
 * @author Bart LEBOEUF
 * @version $Id: Task.java,v 1.1 2005/03/13 09:26:39 doc_alton Exp $
 */
public class Task extends TimerTask { //implements Comparable {

	/**
	 * Creation time
	 */
	private long created;

	/**
	 * Job to execute when timer call task
	 */
	private TaskPerformer performer;

	/**
	 * Is task cancelled ?
	 */
	private boolean cancelled;
	
	/**
	 * Internal counter id
	 */
	private static long counter = 0L;

	/**
	 * Current Task unique id
	 */
	private long unique_id = 0L;

	/**
	 * Constructor
	 * @param _performer TaskPerformer which contains the job
	 */
	public Task(TaskPerformer _performer) {
		unique_id = counter++;
		performer = _performer;
		created = System.currentTimeMillis();
	}

	/**
	 * Get creation time
	 * @return long return creation time
	 */
	public long getCreatedTime() {
		return (created);
	}

	protected Runnable getRunnable() {
		return (this);
	}

	/**
	 * Method call by Timer when period is over
	 */
	public void run() {
		performer.perform();
	}
	
	/**
	 * Cancel task
	 */
	public boolean cancel() {
		return (cancelled=super.cancel());
	}

	/**
	 * Get if task is cancelled
	 * @return boolean return true if tash is cancelled, else false
	 */
	public synchronized boolean isCancelled() {
		return( cancelled );
	}	
	
	/**
	 * Return unique id of curretn task
	 * @return Long unique id of current task
	 */
	public Long getUniqueId() {
		return new Long(unique_id);
	}

	public int compareTo(Object other) {
		long res = created - ((Task) other).getCreatedTime();
		if (res == 0) {
			return ((int) (unique_id - ((Task) other).getUniqueId().longValue()));
		} else {
			return ((int) res);
		}
	}
}