package pl.zaw.core.config

import com.typesafe.config.Config

import scala.collection.JavaConversions._

/**
 * Created on 2015-05-19.
 *
 * @author Jakub Zawislak
 */
object Implicits {
  implicit val configGetInt: (Config, String) => Int =
    (config: Config, property: String) => config.getInt(property)
  implicit val configGetString: (Config, String) => String =
    (config: Config, property: String) => config.getString(property)
  /**
   * Implicit conversion from [[java.util.List]] to [[scala.collection.immutable.List]]
   */
  implicit val configGetStringList: (Config, String) => List[String] =
    (config: Config, property: String) => config.getStringList(property).toList
  implicit val configGetBoolean: (Config, String) => Boolean =
    (config: Config, property: String) => config.getBoolean(property)
}
