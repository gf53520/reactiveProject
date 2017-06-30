package com.reactive.stream.protocol

/**
  * Created by guifeng on 2017/6/25.
  */

case class StudentScore(id: String, sex: String, math: Double, chinese: Double, english: Double)

object StudentScore {
  def apply(arr: Array[String]): Option[StudentScore] =
    try {
      Some(new StudentScore(arr(0), arr(1), arr(2).toDouble, arr(3).toDouble, arr(4).toDouble))
    } catch {
      case _: Exception => None
    }
}


case class StudentAvgScore(studentScore: StudentScore, avgScore: Double) extends Comparable[StudentAvgScore] {
  override def compareTo(o: StudentAvgScore) = avgScore.compareTo(o.avgScore)
}

object StudentAvgScore {
  def apply(studentScore: StudentScore): Option[StudentAvgScore] = {
    if (studentScore.math > 0 &&
        studentScore.chinese > 0 && studentScore.english > 0) {
      Some(StudentAvgScore(studentScore,
        (studentScore.math + studentScore.chinese + studentScore.english) / 3))
    } else None
  }
}

object LargestAvgScore
