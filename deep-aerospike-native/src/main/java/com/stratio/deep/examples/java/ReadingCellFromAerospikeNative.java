/*
 * Copyright 2014 Aerospike, Inc.
 *
 * Portions may be licensed to Aerospike, Inc. under one or more
 * contributor license agreements.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.stratio.deep.examples.java;

import com.stratio.deep.aerospike.direct.config.AerospikeConfigFactory;
import com.stratio.deep.aerospike.direct.config.AerospikeDeepJobConfig;
import com.stratio.deep.aerospike.direct.extractor.AerospikeNativeCellExtractor;
import com.stratio.deep.examples.utils.ContextProperties;
import org.apache.log4j.Logger;
import org.apache.spark.rdd.RDD;

import com.stratio.deep.core.context.DeepSparkContext;

/**
 * Example of reading Cells objects with Aerospike Native connector.
 */
public class ReadingCellFromAerospikeNative {

    private static final Logger LOG = Logger.getLogger(ReadingCellFromAerospikeNative.class);

    private ReadingCellFromAerospikeNative() {
    }

    public static void main(String[] args) {
        doMain(args);
    }

    public static void doMain(String[] args) {
        String job = "java:readingCellFromAerospikeNative";

        String host = "127.0.0.1";
        int port = 3000;

        String namespace = "book";
        String set = "input";

        // Creating the Deep Context where args are Spark Master and Job Name
        ContextProperties p = new ContextProperties(args);
        DeepSparkContext deepContext = new DeepSparkContext(p.getCluster(), job, p.getSparkHome(),
                p.getJars());

        AerospikeDeepJobConfig inputConfigCell = AerospikeConfigFactory.createAerospike().host(host).port(port)
                .namespace(namespace)
                .set(set);
        inputConfigCell.setExtractorImplClass(AerospikeNativeCellExtractor.class);

        RDD inputRDDCell = deepContext.createRDD(inputConfigCell);
        LOG.info("count : " + inputRDDCell.count());
        LOG.info("prints first cell  : " + inputRDDCell.first());

        deepContext.stop();

    }
}
