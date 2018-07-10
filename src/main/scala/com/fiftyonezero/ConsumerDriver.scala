package com.fiftyonezero

import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

import scala.util.control.NonFatal

object ConsumerDriver {

  def main(args: Array[String]): Unit = {
    val numConsumers = args(0).toInt
    val topic = args(1)

    val groupId = s"test-group-$numConsumers"

    val executor: ExecutorService = Executors.newFixedThreadPool(numConsumers)

    val consumers = for (i <- 1 to numConsumers) yield new ConsumerLoop(i, groupId, topic)

    consumers.foreach(consumer => executor.submit(consumer))

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run(): Unit = {
        consumers.foreach { consumer =>
          consumer.shutdown()
        }

        executor.shutdown()

        try {
          executor.awaitTermination(5000, TimeUnit.MILLISECONDS)
        } catch {
          case NonFatal(e) => e.printStackTrace()
        }
      }
    })

  }
}