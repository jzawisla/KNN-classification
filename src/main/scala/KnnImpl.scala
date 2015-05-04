import java.io.{FileInputStream, FileNotFoundException, InputStream}

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import pl.zaw.core.config.ConfigUtil

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
 * Created on 2015-04-28.
 *
 * @author Jakub Zawislak
 */
class KnnImpl {
  ConfigUtil.init("KNN")
  private val logger = Logger(LoggerFactory.getLogger(this.getClass.getName))

  val trainingList = new ListBuffer[KnnElement]
  val testList = new ListBuffer[KnnElement]
  val random = new scala.util.Random(1)
  private var standardizationArray = None: Option[Array[Double]]

  /**
   * Reads classification data from the specified file. Splits data into two sets: training and test.
   * @param fileName file to be read
   * @param trainingPercentage how much data should become training data (in percentage)
   */
  @throws[FileNotFoundException]("if the init wasn't called first")
  def readFile(fileName: String = ConfigUtil.getProperty("csv_filename").get,
               trainingPercentage: Int = ConfigUtil.getPropertyAsInt("knn_training_percentage").getOrElse(70)) = {
    var sourceFile: InputStream = null
    try {
      sourceFile = new FileInputStream(fileName)
    }
    catch {
      case _: FileNotFoundException =>
        logger.warn(s"File $fileName not found. Loading from resources.")
        sourceFile = getClass.getResourceAsStream(fileName)
        if (sourceFile == null) {
          throw new java.io.FileNotFoundException(s"$fileName was not found.")
        }
    }

    val it = Source.fromInputStream(sourceFile).getLines()

    val typesList = ConfigUtil.getPropertyAsStringList("csv_col_types")
    if (ConfigUtil.getPropertyAsBoolean("csv_skip_headers").getOrElse(false)) it.next()

    while (it.hasNext) {
      val newElement = new KnnElement
      val lineSplit = it.next().split(ConfigUtil.getProperty("csv_delimiter").getOrElse(","))
      for (field <- lineSplit.zip(typesList.get)) {
        field._2 match {
          case "Double" => newElement.addElement(field._1.toDouble)
          case "Boolean" => newElement.addElement(field._1.toBoolean)
          case "Class" => newElement.setClassVal(field._1.toString)
          case _ => newElement.addElement(field._1.toString)
        }
      }

      random.nextInt(100) match {
        case x if x <= trainingPercentage => trainingList.append(newElement)
        case _ => testList.append(newElement)
      }
    }

    //println(trainingList.length)
    //println(testList.toString())
  }

  def calcStandardizationArray(): Unit = {
    val attributeMinsMaxs = {
      Array.fill[Option[Tuple2[Double, Double]]](ConfigUtil.getPropertyAsStringList("csv_col_types").get.count(col => col != "Class"))(None: Option[Tuple2[Double, Double]])
    }

    for (knnElem <- trainingList) {
      var i = 0
      for (attribute <- knnElem.stringList) {
        setMinMax(i, attribute.length)
        i = i + 1
      }
      for (attribute <- knnElem.doubleList) {
        setMinMax(i, attribute)
        i = i + 1
      }
      for (attribute <- knnElem.booleanList) {
        setMinMax(i, if (attribute) 1D else 0D)
        i = i + 1
      }
    }
    def setMinMax(position: Int, value: Double) = {
      attributeMinsMaxs(position) = Some({
        val attributeMin = Math.min(attributeMinsMaxs(position).getOrElse(Tuple2(value, value))._1, value)
        val attributeMax = Math.max(attributeMinsMaxs(position).getOrElse(Tuple2(value, value))._2, value)
        Tuple2(attributeMin, attributeMax)
      })
    }

    standardizationArray = Some(Array.fill[Double](attributeMinsMaxs.length)(0))

    for (k <- attributeMinsMaxs.zipWithIndex) {
      for {
        opt <- standardizationArray
      } {
        opt(k._2) = k._1.get._2 - k._1.get._1
      }
    }

    logger.info("Calculated standardization coefficients:")
    for (k <- standardizationArray.get) {
      logger.info(k.toString)
    }
  }

  def resolveTestSet(kParam: Int) = {
    for (k <- testList) {
      val closeClassesWithDistance = trainingList.map(t => t.calculateDistance(k, standardizationArray) -> t.classVal).sortBy(_._1).take(kParam)
      val mostCommonClass = closeClassesWithDistance.groupBy(_._2).toList.sortBy(_._2.length).reverse.head._1
      k.setDeterminedClassVal(mostCommonClass)
    }
  }

  def plotResults() = {
    import org.jfree.chart._
    import org.jfree.data.xy._

    val results = for (i <- ConfigUtil.getPropertyAsInt("knn_k_start").getOrElse(1) to ConfigUtil.getPropertyAsInt("knn_k_end").getOrElse(50)) yield {
      logger.info(s"Running for k=$i")
      resolveTestSet(i)
      i.toDouble -> testList.count(a => a.classVal == a.determinedClassVal).toDouble / testList.length
    }

    val dataSet = new DefaultXYDataset
    dataSet.addSeries("Series 1", Array(results.map(_._2).toArray, results.map(_._1).toArray))

    val frame = new ChartFrame(
      "KNN-Classification",
      ChartFactory.createXYLineChart(
        "KNN-Classification results",
        "Accuracy",
        "K-parameter",
        dataSet,
        org.jfree.chart.plot.PlotOrientation.HORIZONTAL,
        false, false, false
      )
    )
    frame.pack()
    frame.setVisible(true)
  }
}
