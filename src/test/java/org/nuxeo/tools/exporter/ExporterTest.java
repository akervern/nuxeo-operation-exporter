package org.nuxeo.tools.exporter;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationType;
import org.nuxeo.ecm.automation.jaxrs.io.JsonWriter;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.RuntimeHarness;

@RunWith(FeaturesRunner.class)
@Features({ AutomationFeature.class, WebEngineFeature.class })
public class ExporterTest {

    private static final String PARAM_OPERATION_ID = "nuxeo.exporter.operation";

    private static final String PARAM_COMPONENT = "nuxeo.exporter.component";

    private static final String PARAM_OUTPUT = "nuxeo.exporter.output";

    @Inject
    private AutomationService automationService;

    @Inject
    private RuntimeHarness runtimeHarness;

    @Test
    public void testLocalOperation() throws Exception {
        // Assume there is not parameter
        Assume.assumeFalse("Test runned without parameters", runnedWithParameters());

        File tempFile = newTempFile();
        generateOperationJson("Document.GetParent", null, tempFile.getAbsolutePath());
        assertNotEquals(0, FileUtils.readBytes(tempFile).length);
    }

    @Test
    public void testLocalOperationWithCustomComponent() throws Exception {
        // Assume there is not parameter
        Assume.assumeFalse("Test runned without parameters", runnedWithParameters());

        File tempFile = newTempFile();
        generateOperationJson("Empty.Operation", "test-contrib.xml", tempFile.getAbsolutePath());
        assertNotEquals(0, FileUtils.readBytes(tempFile).length);
    }

    @Test
    public void testWithParameters() throws Exception {
        // Assume we have at least one parameter
        Assume.assumeTrue("Test runner with parameters", runnedWithParameters());
        generateOperationJson(null, null, null);
    }

    private File newTempFile() throws IOException {
        File tempFile = File.createTempFile("exporter-", ".json");
        assertEquals(0, FileUtils.readBytes(tempFile).length);
        System.out.println(tempFile.getAbsolutePath());
        return tempFile;
    }

    private boolean runnedWithParameters() {
        return Stream.of(PARAM_OPERATION_ID, PARAM_OUTPUT).anyMatch(s -> Framework.getProperty(s, null) != null);
    }

    private void generateOperationJson(String operationId, String component, String output) throws Exception {
        operationId = Framework.getProperty(PARAM_OPERATION_ID, operationId);
        assertTrue(format("Missing parameter '%s'.", PARAM_OPERATION_ID), isNotBlank(operationId));

        component = Framework.getProperty(PARAM_COMPONENT, component);
        if (isNotBlank(component)) {
            runtimeHarness.deployTestContrib("org.nuxeo.ecm.automation.core", component);
        }

        output = Framework.getProperty(PARAM_OUTPUT, output);
        assertTrue(format("Missing parameter '%s'.", PARAM_OUTPUT), isNotBlank(output));
        File f = new File(output);
        if (!f.exists()) {
            assertTrue(f.createNewFile());
        }
        assertTrue(format("File '%s' is not writable", f.getAbsolutePath()), f.canWrite());
        FileOutputStream outputStream = new FileOutputStream(f);

        OperationType operation = automationService.getOperation(operationId);
        assertNotNull(format("Operation '%s' is not deployed.", operation), operation);
        JsonWriter.writeOperation(outputStream, operation.getDocumentation());
    }
}
