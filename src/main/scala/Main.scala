import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

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
    logger.info("Starting KNN algorithm.")
    knnImpl.plotResults()
  }
}
