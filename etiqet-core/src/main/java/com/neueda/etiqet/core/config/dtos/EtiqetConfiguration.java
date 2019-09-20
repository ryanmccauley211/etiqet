package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * XPath: /etiqetConfiguration
 */
@XmlType(propOrder = {"aftermathConfig", "protocols", "clients", "servers"}, namespace = EtiqetConstants.NAMESPACE)
@XmlRootElement(name = "etiqetConfiguration", namespace = EtiqetConstants.NAMESPACE)
public class EtiqetConfiguration implements Serializable {

    private AftermathConfig aftermathConfig;

    private List<Protocol> protocols = new ArrayList<>();

    private List<ClientImpl> clients = new ArrayList<>();

    private List<ServerImpl> servers = new ArrayList<>();

    /**
     * Gets configuration for aftermath test suite viewer
     *
     * XPath: /etiqetConfiguration/aftermathConfig
     *
     * @return configuration for aftermath
     */
    @XmlElement(name = "aftermathConfig", namespace = EtiqetConstants.NAMESPACE)
    public AftermathConfig getAftermathConfig() {
        return aftermathConfig;
    }

    /**
     * Set aftermath configuration
     *
     * @param aftermathConfig
     */
    public void setAftermathConfig(AftermathConfig aftermathConfig) {
        this.aftermathConfig = aftermathConfig;
    }

    /**
     * Gets all defined protocols in the configuration.
     *
     * XPath: /etiqetConfiguration/protocols
     *
     * @return list of protocol DTOs to be prepared for use
     */
    @XmlElementWrapper(name = "protocols", namespace = EtiqetConstants.NAMESPACE)
    @XmlElement(name = "protocol", namespace = EtiqetConstants.NAMESPACE)
    public List<Protocol> getProtocols() {
        return protocols;
    }

    /**
     * Sets the protocols in the configuration
     *
     * @param protocols list of protocol DTOs to be prepared for use
     */
    public void setProtocols(List<Protocol> protocols) {
        this.protocols = protocols;
    }

    /**
     * Gets all clients pre-defined for use in test steps
     *
     * XPath: /etiqetConfiguration/clients
     *
     * @return all client names and implementations
     */
    @XmlElementWrapper(name = "clients", namespace = EtiqetConstants.NAMESPACE, required = false)
    @XmlElement(name = "client", namespace = EtiqetConstants.NAMESPACE, required = false)
    public List<ClientImpl> getClients() {
        return clients;
    }

    /**
     * Sets pre-defined clients for use in test steps
     *
     * @param clients list of client names / implementations
     */
    public void setClients(List<ClientImpl> clients) {
        this.clients = clients;
    }

    /**
     * Gets all servers pre-defined for use in test steps
     *
     * XPath: /etiqetConfiguration/servers
     *
     * @return all server names, implementations and configurations
     */
    @XmlElementWrapper(name = "servers", namespace = EtiqetConstants.NAMESPACE, required = false)
    @XmlElement(name = "server", namespace = EtiqetConstants.NAMESPACE, required = false)
    public List<ServerImpl> getServers() {
        return servers;
    }

    /**
     * Sets pre-defined servers to be used in test steps
     *
     * @param servers list of server names, implementations and configurations
     */
    public void setServers(List<ServerImpl> servers) {
        this.servers = servers;
    }
}
