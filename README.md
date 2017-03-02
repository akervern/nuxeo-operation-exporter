# About

Export a Nuxeo Operation documentation JSON

# Usage

```bash
mvn test -Dnuxeo.extractor.operation=<OperationID> -Dnuxeo.extractor.output=<destinationFile> [-Dnuxeo.extractor.component=<componentPath>]
```

# Use it from Another Project

Add those lines to a `pom.xml` file

```xml
<project>
    ...
    <build>
        <plugins>
            ...
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <dependenciesToScan>
                        <dependency>org.nuxeo.tools.exporter:operation-exporter-core</dependency>
                    </dependenciesToScan>
                </configuration>
            </plugin>
        </plugins>
    </build>
    <dependencies>
        ...
        <dependency>
            <groupId>org.nuxeo.tools.exporter</groupId>
            <artifactId>operation-exporter-core</artifactId>
            <version>1.0-SNAPSHOT</version>
            <type>test-jar</type>
        </dependency>
    </dependencies>
</project>
```

And run for instance:
```bash
mvn test -Dtest=ExporterTest#testWithParameters -Dnuxeo.exporter.operation=Document.MyOperation -Dnuxeo.exporter.component=OSGI-INF/my-operation-operation-contrib.xml -Dnuxeo.exporter.output=/tmp/exported.json
```