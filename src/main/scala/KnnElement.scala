import scala.collection.mutable.ListBuffer

/**
 * Created on 2015-05-02.
 *
 * @author Jakub Zawislak
 */
class KnnElement {
  private val stringList = new ListBuffer[String]()
  private val doubleList = new ListBuffer[Double]()
  private val booleanList = new ListBuffer[Boolean]()
  private var classValue: String = _
  private var determinedClassValue: String = _

  def addElement(newVal: Any): Unit = {
    newVal match {
      case s: String => stringList.append(s)
      case d: Double => doubleList.append(d)
      case b: Boolean => booleanList.append(b)
    }
  }

  def classVal = classValue
  def setClassVal(newVal: String) = classValue = newVal
  def determinedClassVal = determinedClassValue
  def setDeterminedClassVal(newVal: String) = determinedClassValue = newVal

  def calculateDistance(toCompare: KnnElement): Double = {
    var distance = 0D
    //TODO implement hamming distance or other
    distance += toCompare.stringList.zip(this.stringList).map(a => scala.math.abs(a._1.length - a._2.length).toDouble).sum
    distance += toCompare.booleanList.zip(this.booleanList).map(a => if(a._1 != a._2) 1D else 0D).sum
    distance += toCompare.doubleList.zip(this.doubleList).map(a => scala.math.abs(a._1 - a._2)).sum
    distance
  }

  override def toString = {
    s""" StringList: $stringList
        |DoubleList: $doubleList
        |BooleanList: $booleanList
        |ClassValue: $classValue
     """.stripMargin
  }
}
