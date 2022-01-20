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

import java.util.LinkedList;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Link;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports.Binding;

import de.mhus.lib.internal.TCast;
import de.mhus.lib.internal.TNet;
import de.mhus.lib.internal.TRuntimeException;
import de.mhus.lib.internal.TString;

import com.github.dockerjava.api.model.Volume;

public class ContainerBuilder {

    public final DockerScenario scenario;
    public final DockerContainer cont;

    public final LinkedList<String> links = new LinkedList<>();
    public final LinkedList<String> cmd = new LinkedList<>();
    public final LinkedList<String> env = new LinkedList<>();
    public final LinkedList<String> entrypoint = new LinkedList<>();
    public final LinkedList<String> volumes = new LinkedList<>();
    public final LinkedList<String> ports = new LinkedList<>();
    public final HostConfig hostBuilder;
    public final CreateContainerCmd builder;
    public final LinkedList<Volume> volumesDef = new LinkedList<>();
    public final LinkedList<Bind> volumesBind = new LinkedList<>();
    public final LinkedList<PortBinding> portConfig = new LinkedList<>();
    public final LinkedList<Link> linksBind = new LinkedList<>();
    public final LinkedList<ExposedPort> exposedPorts = new LinkedList<>();

    public ContainerBuilder(
            DockerScenario scenario, DockerContainer cont, CreateContainerCmd builder) {
        this.scenario = scenario;
        this.builder = builder;
        this.cont = cont;
        this.hostBuilder = HostConfig.newHostConfig();
    }

    public void build() {

        if (ports.size() > 0) {
            for (String port : ports) {
                if (port.endsWith("/udp")) {
                    port = TString.beforeIndex(port, '/');
                    if (port.indexOf(':') > 0) {
                        ExposedPort udp = ExposedPort.udp(TCast.toint(TString.afterIndex(port, ':'), 0));
                        Binding bind = Binding.bindPort(TCast.toint(TString.beforeIndex(port, ':'), 0));
                        PortBinding ports = new PortBinding(bind, udp);
                        exposedPorts.add(udp);
                        portConfig.add(ports);
                    } else {
                        ExposedPort udp = ExposedPort.udp(TCast.toint(port, 0));
                        PortBinding ports = new PortBinding(Binding.empty(), udp);
                        exposedPorts.add(udp);
                        portConfig.add(ports);
                    }
                } else {
                    if (port.endsWith("/tcp")) port = TString.beforeIndex(port, '/');
                    if (port.indexOf(':') > 0) {
                        String bindPort = TString.beforeIndex(port, ':');
                        if (bindPort.endsWith("+")) {
                            // only localhost !!
                            int start = TCast.toint(bindPort.substring(0, bindPort.length() - 1), 0);
                            start = start + scenario.cnt();
                            if (start == 0) throw new TRuntimeException("port malformed", bindPort);
                            while (!TNet.availablePort(start)) {
                                start++;
                                scenario.cnt();
                            }
                            bindPort = String.valueOf(start);
                        }
                        ExposedPort tcp = ExposedPort.tcp(TCast.toint(TString.afterIndex(port, ':'), 0));
                        Binding bind = Binding.bindPort(TCast.toint(bindPort, 0));
                        PortBinding ports = new PortBinding(bind, tcp);
                        exposedPorts.add(tcp);
                        portConfig.add(ports);
                    } else {
                        ExposedPort tcp = ExposedPort.tcp(TCast.toint(port, 0));
                        PortBinding ports = new PortBinding(Binding.empty(), tcp);
                        exposedPorts.add(tcp);
                        portConfig.add(ports);
                    }
                }
            }
            System.out.println("--- Port Binding: " + portConfig);
            hostBuilder.withPortBindings(portConfig);
        }

        if (entrypoint.size() > 0) builder.withEntrypoint(entrypoint);

        if (volumes.size() > 0) {
            for (String vol : volumes) {
                AccessMode mode = AccessMode.DEFAULT;
                String src = TString.beforeIndex(vol, ':');
                String trg = TString.afterIndex(vol, ':');
                if (TString.isIndex(trg, ':')) {
                    String modeStr = TString.afterIndex(trg, ':');
                    trg = TString.beforeIndex(trg, ':');
                    if (modeStr.equals("ro")) mode = AccessMode.ro;
                    if (modeStr.equals("rw")) mode = AccessMode.rw;
                }
                Volume v = new Volume(trg);
                volumesDef.add(v);
                volumesBind.add(new Bind(src, v, mode));
            }
            builder.withVolumes(volumesDef);
            hostBuilder.withBinds(volumesBind);
        }
        if (env.size() > 0) builder.withEnv(env);

        if (cmd.size() > 0) builder.withCmd(cmd);

        if (links.size() > 0) {
            for (String link : links) {
                linksBind.add(
                        new Link(TString.beforeIndex(link, ':'), TString.afterIndex(link, ':')));
            }
            hostBuilder.withLinks(linksBind);
        }
        builder.withHostConfig(hostBuilder);
        builder.withExposedPorts(exposedPorts);
    }
}
