/*
 * Copyright 2014, Stratio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.deep.examples.java.save;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.rdd.RDD;

import com.stratio.deep.aerospike.config.AerospikeConfigFactory;
import com.stratio.deep.aerospike.config.AerospikeDeepJobConfig;
import com.stratio.deep.core.context.DeepSparkContext;
import com.stratio.deep.examples.entities.MessageEntity;
import com.stratio.deep.examples.utils.ContextProperties;

import scala.Tuple2;

/**
 * Example of writing an Entity to Aerospike.
 */
public class WritingEntityToAerospike {

    private static final Logger LOG = Logger.getLogger(WritingEntityToAerospike.class);
    public static List<Tuple2<String, Integer>> results;

    public static void main(String[] args) {
        doMain(args);
    }

    public static void doMain(String[] args) {
        String job = "java:writingEntityToAerospike";

        String host = "localhost";
        Integer port = 3000;

        String namespace = "test";
        String inputCollection = "message";
        String outputCollection = "messageOutput";

        // Creating the Deep Context where args are Spark Master and Job Name
        ContextProperties p = new ContextProperties(args);
        DeepSparkContext deepContext = new DeepSparkContext(p.getCluster(), job, p.getSparkHome(),
                p.getJars());

        AerospikeDeepJobConfig<MessageEntity> inputConfigEntity =
                AerospikeConfigFactory.createAerospike(MessageEntity.class).host(host).port(port).namespace(namespace)
                        .set(inputCollection);

        RDD<MessageEntity> inputRDDEntity = deepContext.createRDD(inputConfigEntity);

        AerospikeDeepJobConfig<MessageEntity> outputConfigEntity =
                AerospikeConfigFactory.createAerospike(MessageEntity.class).host(host).port(port).namespace(namespace)
                        .set(outputCollection);

        deepContext.saveRDD(inputRDDEntity, outputConfigEntity);

        deepContext.stop();
    }
}
