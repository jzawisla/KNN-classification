import pl.zaw.core.config.ConfigUtil

/**
 * Created on 2015-04-28.
 *
 * @author Jakub Zawislak
 */
object KnnImpl {
  def main(args: Array[String]): Unit = {
    ConfigUtil.init("KNN")
    println(ConfigUtil.getProperty("csv_delimiter"))
    println(ConfigUtil.getProperty("col_number"))
    println(ConfigUtil.getProperty("col_number2", "not_found"))
  }
}
