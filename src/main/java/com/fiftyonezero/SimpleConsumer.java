package com.fiftyonezero;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Arrays;
import java.util.concurrent.*;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.PartitionInfo;

public class SimpleConsumer
{
    public static void main( String[] args ) throws Exception
    {
//        if ( args.length == 0 )
//        {
//            System.out.println( "Enter topic name" );
//            return;
//        }
        //Kafka consumer configuration settings
        String topicName = "grewalr-test";
        Properties props = new Properties();

        props.put( "bootstrap.servers", "ec2-18-130-168-20.eu-west-2.compute.amazonaws.com:9092,ec2-18-130-185-43.eu-west-2.compute.amazonaws.com:9092" );
        props.put( "group.id", "grewalr-test-group" );
        props.put( "enable.auto.commit", "true" );
        props.put( "auto.commit.interval.ms", "1000" );
        props.put( "session.timeout.ms", "30000" );
//        props.put("auto.offset.reset", "earliest");
        props.put( "key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer" );
        props.put( "value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer" );
        KafkaConsumer<String,String> consumer = new KafkaConsumer
                <String,String>( props );

        //Kafka Consumer subscribes list of topics here.
        consumer.subscribe( Arrays.asList( topicName ) );

        //print the topic name
        System.out.println( "Subscribed to topic " + topicName );
        System.out.println( "Subscribed to topic " + topicName );
        int i = 0;


        Map<String,List<PartitionInfo>> x = consumer.listTopics();

        for (Map.Entry<String, List<PartitionInfo>> entry : x.entrySet())
        {
            System.out.println(entry.getKey() + "/" + entry.getValue());

            for(int j=0; j<entry.getValue().size(); j++){
                System.out.println(entry.getValue().get(j));
            }

        }



        while ( true )
        {
            ConsumerRecords<String,String> records = consumer.poll( 100 );
            for ( ConsumerRecord<String,String> record : records )

            // print the offset,key and value for the consumer records.
            {
                System.out.printf( "offset = %d, key = %s, value = %s\n",
                        record.offset(), record.key(), record.value() );
            }

            consumer.commitSync();
        }
    }
}
