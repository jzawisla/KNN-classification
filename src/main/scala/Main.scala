/**
 * Created on 2015-04-28.
 *
 * @author Jakub Zawislak
 */
object Main {
  def main(args: Array[String]): Unit = {
    val knnImpl = new KnnImpl
    knnImpl.readFile()
    knnImpl.resolveTestSet()
    //knnImpl.printResults()
    knnImpl.plotResults()
  }
}
