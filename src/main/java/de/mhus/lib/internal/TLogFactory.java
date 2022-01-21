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

import java.util.List;
import java.util.WeakHashMap;

import de.mhus.lib.internal.TLog.LEVEL;

public abstract class TLogFactory {

    WeakHashMap<String, TLogEngine> buffer = new WeakHashMap<String, TLogEngine>();
    protected LEVEL level = LEVEL.INFO;
    // protected LevelMapper levelMapper;
    private int maxMsgSize = 10000; // default max length
    private List<String> maxMsgSizeExceptions = null;

    /**
     * Convenience method to derive a name from the specified class and call <code>
     * getInstance(String)</code> with it.
     *
     * @param clazz Class for which a suitable Log name will be derived
     * @return The current log engine
     */
    public TLogEngine getInstance(Class<?> clazz) {
        return getInstance(clazz.getCanonicalName());
    }

    /**
     * Construct (if necessary) and return a <code>Log</code> instance, using the factory's current
     * set of configuration attributes.
     *
     * <p><strong>NOTE</strong> - Depending upon the implementation of the <code>LogFactory</code>
     * you are using, the <code>Log</code> instance you are returned may or may not be local to the
     * current application, and may or may not be returned again on a subsequent call with the same
     * name argument.
     *
     * @param name Logical name of the <code>Log</code> instance to be returned (the meaning of this
     *     name is only known to the underlying logging implementation that is being wrapped)
     * @return the log engine
     */
    public synchronized TLogEngine getInstance(String name) {
        TLogEngine inst = buffer.get(name);
        if (inst == null) {
            inst = createInstance(name);
            inst.doInitialize(this);
            buffer.put(name, inst);
        }
        return inst;
    }

    /**
     * Construct and return a <code>Log</code> instance, using the factory's current set of
     * configuration attributes.
     *
     * <p><strong>NOTE</strong> - Depending upon the implementation of the <code>LogFactory</code>
     * you are using, the <code>Log</code> instance you are returned may or may not be local to the
     * current application, and may or may not be returned again on a subsequent call with the same
     * name argument.
     *
     * @param name Logical name of the <code>Log</code> instance to be returned (the meaning of this
     *     name is only known to the underlying logging implementation that is being wrapped)
     * @return the log engine
     */
    public abstract TLogEngine createInstance(String name);

    public TLogEngine getLog(Class<?> class1) {
        return getInstance(class1);
    }

    public void setDefaultLevel(LEVEL level) {
        this.level = level;
    }

    public LEVEL getDefaultLevel() {
        return level;
    }
    
    public int getMaxMessageSize() {
        return maxMsgSize;
    }

    public void setMaxMessageSize(int max) {
        maxMsgSize = max;
    }

    public List<String> getMaxMessageSizeExceptions() {
        return maxMsgSizeExceptions;
    }

    public void setMaxMessageSizeExceptions(List<String> maxMsgSizeExceptions) {
        this.maxMsgSizeExceptions = maxMsgSizeExceptions;
    }
    
}
