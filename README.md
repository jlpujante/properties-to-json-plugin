# properties-to-json-plugin

## How to use the plugin

```
<plugin>
    <groupId>com.numbytes.maven.plugins</groupId>
    <artifactId>properties-to-json-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <id>properties-json-assets</id>
            <phase>process-resources</phase>
            <goals>
                <goal>propertiestojson</goal>
            </goals>
            <configuration>
                <fileOutput>${path_to}/properties.json</fileOutput>
                <fileJsOutput>${path_to}/properties.js</fileJsOutput>
                <propertiesUnifiedPath>${path_to}/languages.json</propertiesUnifiedPath>
                <propertiesUnifiedJsPath>${path_to}/languages.js</propertiesUnifiedJsPath>
                <fileSets>
                    <fileSet>
                        <propertiesKey>en_US</propertiesKey>
                        <propertiesSourcePath>${path_to}/audit_en.properties</propertiesSourcePath>
                        <jsonTargetPath>${path_to}/${maven.build.timestamp}_audit_en.json</jsonTargetPath>
                        <propertiesToMergePath>${path_to}/audit.properties</propertiesToMergePath>
                        <assetName>/${path_to}/audit_en.properties</assetName>
                        <assetValue>${path_to}/${maven.build.timestamp}_audit_en.json</assetValue>
                    </fileSet>
                    ...
                </fileSets>
            </configuration>
        </execution>
    </executions>
</plugin>
```
