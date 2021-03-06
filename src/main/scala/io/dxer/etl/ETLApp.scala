package io.dxer.etl

import com.google.common.base.Strings
import com.google.gson.Gson
import org.apache.log4j.{Level, Logger}

object ETLApp {

  Logger.getLogger("org").setLevel(Level.ERROR)


  def main(args: Array[String]): Unit = {
    val processor = new CommandProcessor()
    processor.process(args)

    if (processor.hasOption('e') || processor.hasOption('f')) {
      val sqls = processor.getSQLs()
      sqls.foreach(sql => {
        var start = System.currentTimeMillis()
        val operResult = SQLEngine.execute(SparkSessionHolder.sparkSession, sql)
        val end = System.currentTimeMillis()
        if (operResult != null) {
          if (!Strings.isNullOrEmpty(operResult.content)) {
            println(operResult.content)
          }
          println(operResult.msg)
        }
      })
    } else if (processor.hasOption('s')) { //
      val portStr = processor.getValue('p')
      var port: Int = 8090
      if (!Strings.isNullOrEmpty(portStr)) {
        port = portStr.toInt
      }
      // 创建SparkSession
      SparkSessionHolder.build
      new HttpServer().start(port)
    }

  }
}
