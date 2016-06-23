package com.redhat.et.patient360;

import org.apache.spark.sql.{DataFrame, Column, SQLContext}
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._


object Queries {
  
  // use this (optionally) to transform strings containing boolean values to boolean values
  def booleanize(df: DataFrame, colnames: Set[String]): DataFrame = {
    val selectCols = df.columns.toList map { 
      case c if colnames.contains(c) => when(lower(col(c)) === "true", true).otherwise(false).as(c)
      case c => col(c)
    }
    df.select(selectCols : _*)
  }
  
  // returns all records with high blood pressure, along with results and warning messages
  // assumes that booleans are encoded as strings.
  def highBP(df: DataFrame): DataFrame = {
    val hasHistory = lower(col("highBpHistory")) === "true"
    val bpIsHigh = (col("sys") >= 140 || col("dia") >= 90)

    // handle cases where medication is null and where it is the string "null"
    val takingMedication = (col("medication").isNotNull && (lower(col("medication")) !== "null"))

    val records = df.filter(hasHistory && bpIsHigh && takingMedication)
    
    val originalCols = List("firstName", "lastName", "medication", "highBpHistory", "sys", "dia").map {c => col(c)}
    val addedCols = List(lit("Continued High BP").as("results"), lit("Check medication dosage and interactions").as("warning"))
    
    records.select((originalCols ++ addedCols) : _*)
  }
  
  // returns all records with low blood pressure, along with results and warning messages
  // assumes that booleans are encoded as strings.
  def lowBP(df: DataFrame): DataFrame = {
    val noHistory = lower(col("highBpHistory")) !== "true"
    val bpIsLow = (col("sys") <= 90 || col("dia") <= 60)

    // handle cases where medication is null and where it is the string "null"
    val takingMedication = (col("medication").isNotNull && (lower(col("medication")) !== "null"))

    val records = df.filter(noHistory && bpIsLow && takingMedication)
    
    val originalCols = List("firstName", "lastName", "medication", "highBpHistory", "sys", "dia").map {c => col(c)}
    val addedCols = List(lit("Low BP").as("results"), lit("Check medication dosage and interactions").as("warning"))
    
    records.select((originalCols ++ addedCols) : _*)
  }
  
  // combine the two above queries into a single data frame
  def bpWarnings(df: DataFrame): DataFrame = highBP(df).unionAll(lowBP(df))
}