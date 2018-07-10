package com.fiftyonezero

import java.util.concurrent._
import java.util.{Collections, Properties}
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import scala.collection.JavaConversions._

class ConsumerExample {

  val topic = "grewalr-test"
  val brokers: String = "ec2-18-130-168-20.eu-west-2.compute.amazonaws.com:9092,ec2-18-130-185-43.eu-west-2.compute.amazonaws.com:9092"
  val groupId: String = ""

  val props: Properties = createConsumerConfig(brokers, groupId)
  val consumer = new KafkaConsumer[String, String](props)

  //  var executor: ExecutorService = _
  //
  //  def shutdown(): Unit = {
  //    if (consumer != null) consumer.close()
  //    if (executor != null) executor.shutdown()
  //  }

  def createConsumerConfig(brokers: String, groupId: String): Properties = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
//    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId) // Not mandatory for demo purposes
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("auto.offset.reset","earliest")
    props
  }

  def run(): Unit = {
    // Can list multiple topics here as a regex e.g. Pattern.compile("test.*")
    consumer.subscribe(Collections.singletonList(topic))

    Executors.newFixedThreadPool(1).execute(new Runnable {
      override def run(): Unit = {
        while (true) {
          // Continuously poll the consumer buffer until timeout interval
          val records = consumer.poll(10000)

          for (record <- records) {
            println(s"Received message: (${record.key()},${record.value()}) at offset ${record.offset()}")
          }
        }
      }
    })
  }
}

object ConsumerExample extends App {
  val ex = new ConsumerExample
  ex.run()
}
