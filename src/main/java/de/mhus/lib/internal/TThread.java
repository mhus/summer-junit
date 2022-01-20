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

/**
 * @author hummel
 *     <p>To change the template for this generated type comment go to
 *     Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TThread {

    protected static Log log = Log.getLog(TThread.class);

    /**
     * Sleeps _millisec milliseconds. On Interruption it will throw an RuntimeInterruptedException
     *
     * @param _millisec
     */
    public static void sleep(long _millisec) {
        try {
            Thread.sleep(_millisec);
        } catch (InterruptedException e) {
            throw new RuntimeInterruptedException(e);
        }
    }

    /**
     * Sleeps _millisec milliseconds. On Interruption it will throw an InterruptedException. If
     * thread is already interrupted, it will throw the exception directly.
     *
     * <p>This can be used in loops if a interrupt should be able to stop the loop.
     *
     * @param _millisec
     * @throws InterruptedException on interrupt
     */
    public static void sleepInLoop(long _millisec) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        Thread.sleep(_millisec);
    }

    /**
     * Sleeps _millisec milliseconds. On Interruption it will print a debug stack trace but not
     * break. It will leave the Thread.interrupted state to false see
     * https://docs.oracle.com/javase/tutorial/essential/concurrency/interrupt.html
     *
     * @param _millisec
     * @return true if the thread was interrupted in the sleep time
     */
    public static boolean sleepForSure(long _millisec) {
        boolean interrupted = false;
        while (true) {
            long start = System.currentTimeMillis();
            try {
                Thread.sleep(_millisec);
                return interrupted;
            } catch (InterruptedException e) {
                interrupted = true;
                try {
                    Thread.sleep(1); // clear interrupted state
                } catch (InterruptedException e1) {
                }
                log.d(e);
                long done = System.currentTimeMillis() - start;
                _millisec = _millisec - done;
                if (_millisec <= 0) return interrupted;
            }
        }
    }


}
