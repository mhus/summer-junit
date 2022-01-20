/**
 * Copyright (C) 2002 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.internal;

import java.io.PrintStream;

public class TStopWatch {

    private static final long MONTH_AVERAGE_MILLISECONDS = 2629746000l; // for 10.000 years
    private static final long YEAR_AVERAGE_MILLISECONDS =
            MONTH_AVERAGE_MILLISECONDS * 12; // for 10.000 years

    private static final int STATUS_INITIAL = 0;
    private static final int STATUS_RUNNING = 1;
    private static final int STATUS_STOPPED = 2;

    private long count = 0;
    private long start = 0;
    private long stop = 0;

    private String name;

    public TStopWatch() {
        name = "StopWatch";
    }

    public TStopWatch(String name) {
        this.name = name;
    }

    public TStopWatch start() {
        synchronized (this) {
            if (start != 0 && stop != 0) {
                start = System.currentTimeMillis() - (stop - start);
                stop = 0;
                count++;
            } else if (start == 0) {
                start = System.currentTimeMillis();
                count++;
            }
        }
        return this;
    }

    public TStopWatch stop() {
        synchronized (this) {
            if (start != 0 && stop == 0) {
                stop = System.currentTimeMillis();

                //			try {
                //				if (MApi.instance().isPersistence()) {
                //					IConfig persistence =
                // MApi.instance().getPersistenceManager().sessionScope().getPersistence("de.mhus.lib");
                //					long uid = MApi.instance().nextUniqueId();
                //					persistence.setString(getJmxName() + "_" + name + "_" + uid,
                // getCurrentTimeAsString());
                //					persistence.save();
                //				}
                //			} catch (Throwable t) {
                //				log().t(t);
                //			}

            }
        }
        return this;
    }

    public long getCurrentTime() {
        if (start == 0) return 0;
        if (stop == 0) return System.currentTimeMillis() - start;
        return stop - start;
    }

    public TStopWatch reset() {
        synchronized (this) {
            start = 0;
            stop = 0;
        }
        return this;
    }

    public int getStatus() {
        if (start == 0 && stop == 0) return STATUS_INITIAL;
        if (stop == 0) return STATUS_RUNNING;
        return STATUS_STOPPED;
    }

    public boolean isRunning() {
        return getStatus() == STATUS_RUNNING;
    }

    public String getStatusAsString() {
        switch (getStatus()) {
            case STATUS_INITIAL:
                return "initial";
            case STATUS_RUNNING:
                return "running";
            case STATUS_STOPPED:
                return "stopped";
            default:
                return "unknown";
        }
    }

    public long getCurrentSeconds() {
        return getCurrentTime() / 1000;
    }

    public long getCurrentMinutes() {
        return getCurrentSeconds() / 60;
    }

    public String getCurrentMinutesAsString() {
        long sec = getCurrentSeconds();
        return String.valueOf(sec / 60) + ':' + TCast.toString((int) (sec % 60), 2);
    }

    public String getCurrentTimeAsString() {
        return getIntervalAsString(getCurrentTime());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "=" + getCurrentTimeAsString();
    }

    public long getCount() {
        return count;
    }

    public long getStart() {
        return start;
    }

    public long getStop() {
        return stop;
    }

    public void print() {
        print(System.out);
    }

    public void print(PrintStream out) {
        out.println(toString());
    }
    
    private static String getIntervalAsString(long msec) {

        boolean negative = false;
        if (msec < 0) {
            negative = true;
            msec = -msec;
        }

        long sec = msec / 1000;
        long min = sec / 60;
        long hours = min / 60;
        long days = hours / 24;
        long months = (msec / MONTH_AVERAGE_MILLISECONDS) % 12;
        long years = msec / YEAR_AVERAGE_MILLISECONDS;

        return (negative ? "-" : "")
                + (years > 0 ? TCast.toString(years) + "y " : "")
                + (years > 0 || months > 0 ? TCast.toString(months) + "m " : "")
                + TCast.toString((int) (days % 365), 2)
                + ' '
                + TCast.toString((int) (hours % 24), 2)
                + ':'
                + TCast.toString((int) (min % 60), 2)
                + ':'
                + TCast.toString((int) (sec % 60), 2)
                + '.'
                + TCast.toString((int) (msec % 1000), 3);
    }

}
