package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * XPath element: /etiqetConfiguration/aftermathConfig
 */
@XmlRootElement(name = "aftermathConfig", namespace = EtiqetConstants.NAMESPACE)
public class AftermathConfig implements Serializable {

    private boolean enabled;
    private String host;
    private String projectName;
    private String testSuiteName;

    @XmlElement(name = "enabled", namespace = EtiqetConstants.NAMESPACE)
    public boolean isEnabled() {
        return enabled;
    }

    @XmlElement(name = "host", namespace = EtiqetConstants.NAMESPACE)
    public String getHost() {
        return host;
    }

    @XmlElement(name = "project_name", namespace = EtiqetConstants.NAMESPACE)
    public String getProjectName() {
        return projectName;
    }

    @XmlElement(name = "test_suite_name", namespace = EtiqetConstants.NAMESPACE)
    public String getTestSuiteName() {
        return testSuiteName;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }
}
