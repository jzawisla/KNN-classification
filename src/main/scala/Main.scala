import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import pl.zaw.core.config.ConfigUtil
import pl.zaw.core.config.Implicits._

/**
 * Created on 2015-04-28.
 *
 * @author Jakub Zawislak
 */
object Main {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass.getName))

  def main(args: Array[String]): Unit = {
    val knnImpl = new KnnImpl
    logger.info("Reading file.")
    knnImpl.readFile()
    if (ConfigUtil.get[Boolean]("knn_standardization").getOrElse(true)) {
      logger.info("Calculating standardization array.")
      knnImpl.calcStandardizationArray()
    }
    logger.info("Starting KNN algorithm.")
    knnImpl.plotResults()
  }
}
