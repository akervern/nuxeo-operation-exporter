package org.nuxeo.tools.exporter;

import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;

@Operation(id = "Empty.Operation")
public class EmptyOperation {
    @Param(name = "useless")
    String param;

    @OperationMethod
    public void run() {
        // Empty method
    }
}
