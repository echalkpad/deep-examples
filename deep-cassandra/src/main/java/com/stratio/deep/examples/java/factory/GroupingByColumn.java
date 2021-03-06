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

package com.stratio.deep.examples.java.factory;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;

import com.google.common.collect.Lists;
import com.stratio.deep.cassandra.config.CassandraConfigFactory;
import com.stratio.deep.cassandra.config.CassandraDeepJobConfig;
import com.stratio.deep.core.context.DeepSparkContext;
import com.stratio.deep.examples.java.entities.TweetEntity;
import com.stratio.deep.examples.utils.ContextProperties;

import scala.Tuple2;

// !!Important!!

/**
 * Author: Emmanuelle Raffenne
 * Date..: 13-feb-2014
 */
public final class GroupingByColumn {

    private static final Logger LOG = Logger.getLogger(GroupingByColumn.class);

    private static List<Tuple2<String, Integer>> results;

    private GroupingByColumn() {
    }

    /**
     * Application entry point.
     *
     * @param args the arguments passed to the application.
     */
    public static void main(String[] args) {
        doMain(args);
    }

    /**
     * This is the method called by both main and tests.
     *
     * @param args
     */
    public static void doMain(String[] args) {
        String job = "java:groupingByColumn";

        String keyspaceName = "test";
        String tableName = "tweets";

        // Creating the Deep Context
        ContextProperties p = new ContextProperties(args);
        DeepSparkContext deepContext = new DeepSparkContext(p.getCluster(), job, p.getSparkHome(), p.getJars());

        // Create a configuration for the RDD and initialize it
        CassandraDeepJobConfig<TweetEntity> config = CassandraConfigFactory.create(TweetEntity.class)
                .host(p.getCassandraHost()).cqlPort(p.getCassandraCqlPort()).rpcPort(p.getCassandraThriftPort())
                .keyspace(keyspaceName).table(tableName)
                .initialize();

        // Creating the RDD
        JavaRDD<TweetEntity> rdd = deepContext.createJavaRDD(config);

        // grouping
        JavaPairRDD<String, Iterable<TweetEntity>> groups = rdd.groupBy(new Function<TweetEntity, String>() {
            @Override
            public String call(TweetEntity tableEntity) {
                return tableEntity.getAuthor();
            }
        });

        // counting elements in groups
        JavaPairRDD<String, Integer> counts = groups.mapToPair(new PairFunction<Tuple2<String,
                Iterable<TweetEntity>>, String,
                Integer>() {
            @Override
            public Tuple2<String, Integer> call(Tuple2<String, Iterable<TweetEntity>> t) {
                return new Tuple2<>(t._1(), Lists.newArrayList(t._2()).size());
            }
        });

        // fetching the results
        results = counts.collect();

        LOG.info("Este es el resultado con groupBy: ");
        for (Tuple2 t : results) {
            LOG.info(t._1() + ": " + t._2().toString());
        }

        deepContext.stop();
    }

    public static List<Tuple2<String, Integer>> getResults() {
        return results;
    }
}
