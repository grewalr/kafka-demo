package com.fiftyonezero

import java.util.{Date, Properties}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.util.Random

object ProducerExample extends App {
  val events = 1

  val topic = "grewalr-test"

  // Min define 2 as the producer will obtain more info post initial connection
  val brokers = "ec2-18-130-168-20.eu-west-2.compute.amazonaws.com:9092," +
    "ec2-18-130-185-43.eu-west-2.compute.amazonaws.com:9092"

  val rnd = new Random()
  val props = new Properties()

  props.put("bootstrap.servers", brokers)
//  props.put("client.id", "ProducerExample")

  // Define key/value serialisers here.

  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  // Values of the records e.g. String
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  // Key/Value types for the KafkaProducer here are Strings
  // Kafka brokers expect byte arrays as keys and values of messages,
  // Yet the producer interface allows using parameterized types
  val producer = new KafkaProducer[String, String](props)

  val t = System.currentTimeMillis()

  for (nEvents <- Range(0, events)) {
    val runtime = new Date().getTime
    val ip = s"192.168.1.${rnd.nextInt(255)}"
    val msg = s"$runtime,$nEvents,www.51zero.com,$ip"

    // Always create one of these to send messages. These are placed onto a buffer and sent via another thread
    val data = new ProducerRecord[String, String](topic, ip, msg)

    // 3 ways to send the message:
    // 1) F & F - Message sent and not concerned whether arrives successfully. Kafka HA ensures that it
    //            will arrive successfully and producer will retry sending automatically. Be warned that some
    //            messages may get lost using this method
    // 2) Sync - send() returns Future object then using get() to wait on the Future. This will determine whether
    //            the send was successful or not
    // 3) Async - call send() with a registered callback function, which is triggered when a response is received
    //            from the Kafka broker
    producer.send(data)

    if (nEvents == 1000) println(s"$nEvents throughput per second: ${events * 1000 / (System.currentTimeMillis() - t)}")

    // Sync
    // producer.send(data).get()

    // Async
    // producer.send(data, new Callback() { override def onCompletion(recordMetadata: RecordMetadata, e: Exception) {} } )
  }

  println(s"Total sent per second: ${events * 1000 / (System.currentTimeMillis() - t)}")
  producer.close()
}
