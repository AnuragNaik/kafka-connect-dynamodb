/*
 * Copyright 2016 Shikhar Bhushan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dynamok.sink;

import dynamok.Version;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DynamoDbSinkConnector extends SinkConnector {

    private Map<String, String> props;

    @Override
    public Class<? extends Task> taskClass() {
        return DynamoDbSinkTask.class;
    }

    @Override
    public void start(Map<String, String> props) {
        /*
        Taking parameters from either environment variables or java system properties.
        In the configuration files env variable names are passed as ${Variable_name} format instead of the original value
        and the variable value are fetched from environment.
         */
        for(Map.Entry<String, String> entry : props.entrySet()){
            if(entry.getValue().startsWith("${") && entry.getValue().endsWith("}")){
                String variable = entry.getValue().replace("${","").replace("}","");
                if(System.getenv().containsKey(variable)){
                    String newValue = System.getenv(variable);
                    entry.setValue(newValue);
                }
                else if (System.getProperties().containsKey(variable)){
                    String newValue = System.getProperty(variable);
                    entry.setValue(newValue);
                }
            }
        }
        this.props = props;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return Collections.nCopies(maxTasks, props);
    }

    @Override
    public void stop() {
    }

    @Override
    public ConfigDef config() {
        return ConnectorConfig.CONFIG_DEF;
    }

    @Override
    public String version() {
        return Version.get();
    }

}
