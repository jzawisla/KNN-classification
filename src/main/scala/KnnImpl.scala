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

  val trainingList = new ListBuffer[KnnElement]
  val testList = new ListBuffer[KnnElement]
  val random = new scala.util.Random(1)

  /**
   * Reads classification data from the specified file. Splits data into two sets: training and test.
   * @param fileName file to be read
   * @param trainingPercentage how much data should become training data (in percentage)
   */
  def readFile(fileName: String = ConfigUtil.getProperty("csv_filename").get,
               trainingPercentage: Int = ConfigUtil.getPropertyAsInt("knn_training_percentage").getOrElse(70)) = {
    val url = getClass.getResource(fileName)
    // if(url == null) throw new java.io.FileNotFoundException
    val it = Source.fromURL(url).getLines()

    val typesList = ConfigUtil.getPropertyAsStringList("csv_col_types")
    if (ConfigUtil.getPropertyAsBoolean("csv_skip_headers").getOrElse(false)) it.next()

    while (it.hasNext) {
      val newElement = new KnnElement
      val lineSplit = it.next().split(ConfigUtil.getProperty("csv_delimiter").getOrElse(","))
      for {
        field <- lineSplit.zip(typesList.get)
      } yield {
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

  def resolveTestSet(kParam:Int = ConfigUtil.getPropertyAsInt("knn_k").get) = {
    for {
      k <- testList
    } yield {
      val closeClassesWithDistance = trainingList.map(t => t.calculateDistance(k) -> t.classVal)
      val mostCommonClass = closeClassesWithDistance.sortBy(_._1).take(kParam).groupBy(_._2).toList.sortBy( _._2.length).reverse.head._1
      k.setDeterminedClassVal(mostCommonClass)
    }
  }

  def printResults() = {
    val correctlyClassified = testList.count(a => a.classVal == a.determinedClassVal)
    println(s"Accuracy is: ${correctlyClassified.toDouble/testList.length}")
  }

  def plotResults() = {
    import org.jfree.chart._
    import org.jfree.data.xy._

    val results = for(i <- 1 to ConfigUtil.getPropertyAsInt("knn_k").getOrElse(100)) yield {
      resolveTestSet(i)
      i.toDouble -> testList.count(a => a.classVal == a.determinedClassVal).toDouble/testList.length
    }

    val dataset = new DefaultXYDataset
    dataset.addSeries("Series 1",Array(results.map(_._2).toArray,results.map(_._1).toArray))

    val frame = new ChartFrame(
      "KNN-Classification",
      ChartFactory.createXYLineChart(
        "KNN-Classification results",
        "Accuracy",
        "K-parameter",
        dataset,
        org.jfree.chart.plot.PlotOrientation.HORIZONTAL,
        false,false,false
      )
    )
    frame.pack()
    frame.setVisible(true)
  }
}
