package com.redhat.et.patient360;

import com.redhat.et.silex.app.AppCommon

object QueryApp extends AppCommon {
  override def appName = "patient360-demo"
  
  // the args to main should be the JDBC url and the table name
  override def appMain(args: Array[String]) {
    require(args.length == 2, "usage:  QueryApp JDBC_URL TABLE_NAME")
    val source = sqlContext.read.format("jdbc").options(Map("url" -> args(0),"dbtable" -> args(1))).load()
    val results = Queries.bpWarnings(source)
    results.show(results.count.toInt, false)
  }
}
