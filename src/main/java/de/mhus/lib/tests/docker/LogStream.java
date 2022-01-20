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
package de.mhus.lib.tests.docker;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;

import com.github.dockerjava.api.model.Frame;

import de.mhus.lib.internal.TCast;
import de.mhus.lib.internal.TException;
import de.mhus.lib.internal.TString;
import de.mhus.lib.internal.TThread;
import de.mhus.lib.internal.PipedStream;

public class LogStream extends com.github.dockerjava.api.async.ResultCallback.Adapter<Frame> {

    private PipedStream output = new PipedStream();
    private DockerContainer cont;
    private boolean print = true;
    private boolean closed = false;
    private LinkedList<Byte> capture = null;
    private LogFilter filter = null;
    private boolean completed = false;

    public LogStream(DockerScenario scenario, String contName) throws TException {
        this.cont = scenario.get(contName);
    }

    public LogStream(DockerContainer cont) {
        this.cont = cont;
    }

    public PipedStream getOutput() {
        return output;
    }

    @Override
    public void onStart(Closeable closeable) {
        completed = false;
        super.onStart(closeable);
    }

    @Override
    public void onComplete() {
        // output.close();
        // if (input != null)
        //     input.close();
        completed = true;
        super.onComplete();
    }

    @Override
    public void onNext(Frame item) {
        if (closed) return;
        try {
            String logStr = null;
            if (print || capture != null) {
                LinkedList<Byte> sb = new LinkedList<>();
                for (byte b : item.getPayload())
                    if (b != 0) {
                        sb.add(b);
                        if (capture != null)
                            synchronized (this) {
                                capture.add(b);
                            }
                    }
                logStr = new String(TCast.toByteArray(sb), TString.CHARSET_CHARSET_UTF_8);
            }
            if (print) {
                System.out.print(logStr);
            }
            output.getOut().write(item.getPayload());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * No UTF8 encoding supported !!!! Use: LinkedList<Byte> logArray = logStream.readLine();
     * logArray.removeIf(v -> v == 0); String logStr = new String(MCast.toByteArray(logArray),
     * MString.CHARSET_CHARSET_UTF_8);
     *
     * @return The next line until and inclusive LF \n, also inclusive 0 characters and CR \r
     */
    public LinkedList<Byte> readLineRaw() {
        LinkedList<Byte> sb = new LinkedList<>();
        try {
            while (true) {
                int c =
                        output.getIn()
                                .read(); // no utf8 !! - utf8 fails because of a lot of 0 bytes
                if (c < 0) return sb;
                sb.add((byte) c);
                if (c == '\n') {
                    //                    String out = sb.toString();
                    //                    System.err.print(step + ": " + sb );
                    return sb;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //            System.err.print(step + ": " + sb );
            return sb;
        }
    }

    public String readLine() {
        LinkedList<Byte> array = readLineRaw();
        array.removeIf(v -> v == 0);
        if (filter != null) filter.doFilter(array);
        String logStr = new String(TCast.toByteArray(array), TString.CHARSET_CHARSET_UTF_8);
        return logStr;
    }

    public LinkedList<Byte> readAllRaw() {
        LinkedList<Byte> sb = new LinkedList<>();
        try {
            while (true) {
                int av = output.getIn().available();
                if (av > 0) {
                    int c =
                            output.getIn()
                                    .read(); // no utf8 !! - utf8 fails because of a lot of 0 bytes
                    if (c < 0) return sb;
                    sb.add((byte) c);
                } else {
                    TThread.sleep(100);
                    if (sb.size() > 0 && (av < 0 || closed || completed)) return sb;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }

    public String readAll() {
        LinkedList<Byte> array = readAllRaw();
        array.removeIf(v -> v == 0);
        if (filter != null) filter.doFilter(array);
        String logStr = new String(TCast.toByteArray(array), TString.CHARSET_CHARSET_UTF_8);
        return logStr;
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        super.close();
        output.close();
    }

    public DockerContainer getContainer() {
        return cont;
    }

    public boolean isPrint() {
        return print;
    }

    public LogStream setPrint(boolean print) {
        this.print = print;
        return this;
    }

    public boolean isClosed() {
        return closed;
    }

    public LogStream setCapture(boolean capt) {
        synchronized (this) {
            if (capt) capture = new LinkedList<>();
            else capture = null;
            return this;
        }
    }

    public String getCaptured() {
        synchronized (this) {
            if (capture == null) return null;
            if (filter != null) filter.doFilter(capture);
            return new String(TCast.toByteArray(capture), TString.CHARSET_CHARSET_UTF_8);
        }
    }

    public LogFilter getFilter() {
        return filter;
    }

    public void setFilter(LogFilter filter) {
        this.filter = filter;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
