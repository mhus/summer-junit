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

public class TConsoleFactory extends TLogFactory {

    public static int FIX_NAME_LENGTH = 30;
    // public static boolean tracing = true;
    private boolean traces = true;
    private boolean printTime = true;
	private PrintStream out;

    public TConsoleFactory() {
        out = System.out;
    }

    @Override
    public TLogEngine createInstance(String name) {
        return new ConsoleLog(name);
    }

    public String printTime() {
        if (printTime) {
            return TDate.toIso8601(System.currentTimeMillis()) + " ";
        }
        return "";
    }

    public TLog.LEVEL getLevel() {
        return level;
    }

    public void setLevel(TLog.LEVEL level) {
        this.level = level;
    }

    private class ConsoleLog extends TLogEngine {

        private String fixName;

        public ConsoleLog(String name) {
            super(name);
        }

        @Override
        public void debug(Object message) {
            if (!isDebugEnabled()) return;
            out.print(printTime());
            out.print("DEBUG ");
            out.print(getFixName());
            out.print(" ");
            out.println(message);
            if (message != null && message instanceof Throwable)
                ((Throwable) message).printStackTrace(out);
        }

        @Override
        public void debug(Object message, Throwable t) {
            if (!isDebugEnabled()) return;
            out.print(printTime());
            out.print("DEBUG ");
            out.print(getFixName());
            out.print(" ");
            out.println(message);
            if (t != null && traces) t.printStackTrace(out);
        }

        @Override
        public void error(Object message) {
            if (!isErrorEnabled()) return;
            out.print(printTime());
            out.print("ERROR ");
            out.print(getFixName());
            out.print(" ");
            out.println(message);
            if (message != null && message instanceof Throwable && traces)
                ((Throwable) message).printStackTrace(out);
        }

        @Override
        public void error(Object message, Throwable t) {
            if (!isErrorEnabled()) return;
            out.print(printTime());
            out.print("ERROR ");
            out.print(getFixName());
            out.print(" ");
            out.println(message);
            if (t != null && traces) t.printStackTrace(out);
        }

        @Override
        public void fatal(Object message) {
            if (!isFatalEnabled()) return;
            out.print(printTime());
            out.print("FATAL ");
            out.print(getFixName());
            out.print(" ");
            out.println(message);
            if (message != null && message instanceof Throwable && traces)
                ((Throwable) message).printStackTrace(out);
        }

        @Override
        public void fatal(Object message, Throwable t) {
            if (!isFatalEnabled()) return;
            out.print(printTime());
            out.print("FATAL ");
            out.print(getFixName());
            out.print(" ");
            out.println(message);
            if (t != null && traces) t.printStackTrace(out);
        }

        @Override
        public void info(Object message) {
            if (!isInfoEnabled()) return;
            out.print(printTime());
            out.print("INFO  ");
            out.print(getFixName());
            out.print(" ");
            out.println(message);
            if (message != null && message instanceof Throwable && traces)
                ((Throwable) message).printStackTrace(out);
        }

        @Override
        public void info(Object message, Throwable t) {
            if (!isInfoEnabled()) return;
            out.print(printTime());
            out.print("INFO  ");
            out.print(getFixName());
            out.print(" ");
            out.println(message);
            if (t != null && traces) t.printStackTrace(out);
        }

        @Override
        public boolean isDebugEnabled() {
            return getLevel().ordinal() <= TLog.LEVEL.DEBUG.ordinal();
        }

        @Override
        public boolean isErrorEnabled() {
            return getLevel().ordinal() <= TLog.LEVEL.ERROR.ordinal();
        }

        @Override
        public boolean isFatalEnabled() {
            return getLevel().ordinal() <= TLog.LEVEL.FATAL.ordinal();
        }

        @Override
        public boolean isInfoEnabled() {
            return getLevel().ordinal() <= TLog.LEVEL.INFO.ordinal();
        }

        @Override
        public boolean isTraceEnabled() {
            return getLevel().ordinal() <= TLog.LEVEL.TRACE.ordinal();
        }

        @Override
        public boolean isWarnEnabled() {
            return getLevel().ordinal() <= TLog.LEVEL.WARN.ordinal();
        }

        @Override
        public void trace(Object message) {
            if (!isTraceEnabled()) return;
            out.print(printTime());
            out.print("TRACE ");
            out.print(getFixName());
            out.print(" ");
            out.println(message);
            if (message != null && message instanceof Throwable && traces)
                ((Throwable) message).printStackTrace(out);
        }

        @Override
        public void trace(Object message, Throwable t) {
            if (!isTraceEnabled()) return;
            out.print(printTime());
            out.print("TRACE ");
            out.print(getFixName());
            out.print(" ");
            out.println(message);
            if (t != null && traces) t.printStackTrace(out);
        }

        @Override
        public void warn(Object message) {
            if (!isWarnEnabled()) return;
            out.print(printTime());
            out.print("WARN  ");
            out.print(getFixName());
            out.print(" ");
            out.println(message);
            if (message != null && message instanceof Throwable && traces)
                ((Throwable) message).printStackTrace(out);
        }

        @Override
        public void warn(Object message, Throwable t) {
            if (!isWarnEnabled()) return;
            out.print(printTime());
            out.print("WARN  ");
            out.print(getFixName());
            out.print(" ");
            out.println(message);
            if (t != null && traces) t.printStackTrace(out);
        }

        private String getFixName() {
            if (fixName == null) {
                String n = getName();
                if (n.length() > FIX_NAME_LENGTH) n = n.substring(0, FIX_NAME_LENGTH);
                else if (n.length() < FIX_NAME_LENGTH)
                    n = n + TString.rep(' ', FIX_NAME_LENGTH - n.length());
                fixName = n;
            }
            return fixName;
        }

        @Override
        public void doInitialize(TLogFactory logFactory) {}

        @Override
        public void close() {}
    }
}
