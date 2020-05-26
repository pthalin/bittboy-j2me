package sdljava.util;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;

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
 * Scheduler class
 * 
 * @author Bart LEBOEUF
 * @version $Id: Scheduler.java,v 1.1 2005/03/13 09:26:39 doc_alton Exp $
 */
public class Scheduler {

	/**
	 * Class instance
	 */
	private static Scheduler _instance = null;
	/**
	 * Timers for each tasks
	 */
	private Hashtable _timers = null;
	
	private final static Object _lock = new Object();
	
	static {
		synchronized(_lock) {
			_instance = new Scheduler();
		}
	}

	/**
	 * Constructor
	 */
	private Scheduler() {
		_timers = new Hashtable();
	}
	
	/**
	 * Get Scheduler instance
	 * @return Scheduler return instance of this class
	 */
	public static synchronized Scheduler getInstance() {
		return _instance;
	}

	/**
	 * Add a new task in scheduler planning
	 * @param period time in milliseconds between successive task executions. 
	 * @param task task to be scheduled.
	 */
	public void addTask(long period, Task task) {
		Timer _timer = new Timer();
		System.out.println(_timer);
		_timer.schedule(task,new Date(), period);
		_timers.put(task.getUniqueId(),_timer);
	}
	
	/**
	 * Add a new task in scheduler planning
	 * @param time time at which task is to be executed.
	 * @param task task to be scheduled.
	 */
	public void addTask(Date time, Task task) {
		Timer _timer = new Timer();
		_timer.schedule(task,time);
		_timers.put(task.getUniqueId(),_timer);
	}
	
	/**
	 * Add a new task in scheduler planning
	 * @param delay delay in milliseconds before task is to be executed.
	 * @param period time in milliseconds between successive task executions. 
	 * @param task task to be scheduled.
	 */
	public void addTask(long delay, long period, Task task) {
		Timer _timer = new Timer();
		_timer.schedule(task,delay,period);
		_timers.put(task.getUniqueId(),_timer);
	}

	/**
	 * Schedules the specified task for repeated fixed-rate execution,
     * beginning at the specified time. Subsequent executions take place at
     * approximately regular intervals, separated by the specified period.
	 * @param firstTime First time at which task is to be executed.
	 * @param period time in milliseconds between successive task executions.
	 * @param task task to be scheduled.
	 */
	public void addPeriodicTask(Date firstTime, long period, Task task) {
		Timer _timer = new Timer();
		_timer.scheduleAtFixedRate(task,firstTime,period);
		_timers.put(task.getUniqueId(),_timer);
	}

	/**
	 * Cancel all Tasks in the array
	 * @param tasks[] array of task to cancel
	 */
	public void cancelTasks(Task[] tasks) {
		for (int v = 0; v < tasks.length; v++) {
			cancelTask(tasks[v]);
		}
	}
	
	/**
	 * Cancels this task.  If the task has been scheduled for one-time
     * execution and has not yet run, or has not yet been scheduled, it will
     * never run.  If the task has been scheduled for repeated execution, it
     * will never run again.  (If the task is running when this call occurs,
     * the task will run to completion, but will never run again.)
	 * @param task Task to cancel
	 */
	public void cancelTask(Task task) {
		task.cancel();
		((Timer)_timers.get(task.getUniqueId())).cancel();
		_timers.remove(task.getUniqueId());
	}
	
	/**
	 * Cancel all scheduled tasks
	 */
	public void cancelAllTasks() {
		Enumeration ite = _timers.keys();
		while (ite.hasMoreElements()) {
			((Task)ite.nextElement()).cancel();
		}
		_timers.clear();
	}
	
	/**
	 * Count how many task are schedule
	 * @return int return number of scheduled task
	 */
	public int countTask() {
		return _timers.size();
	}

	public void destroy() {
		cancelAllTasks();
		_timers = null;
	}
	
	public String toString() {
		return "Scheduler="+_timers.toString();
	}
}