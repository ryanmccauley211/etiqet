package com.neueda.q4j.message;

import com.neueda.etiqet.core.EtiqetOptions;
import com.neueda.etiqet.core.EtiqetTestRunner;
import org.junit.runner.RunWith;

@RunWith(EtiqetTestRunner.class)
@EtiqetOptions(plugin = {"pretty", "html:C:\\Users\\Neueda\\Documents\\Workspace\\etiqet\\etiqet-core\\src\\main\\java\\com\\neueda\\etiqet\\core/cucumber"}, features = "src/test/resources/scenarios/NeuedaFix.feature")
public class RunNeuedaFixCreateXNosTest {

}
