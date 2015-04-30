package pl.zaw.core.config

import java.io.File

import com.typesafe.config._
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

/**
 * Loads properties files.
 * Created on 2015-04-28.
 *
 * @author Jakub Zawislak
 */
object ConfigUtil {
  private val SUFFIX = ".conf"

  private val logger = Logger(LoggerFactory.getLogger(this.getClass.getName))
  var appName = None: Option[String]
  var config = None: Option[Config]

  //TODO add at the first place, file loaded from the same directory as application
  /**
   * Inits config for application. Should be called once at the beginning.
   *
   * Loads default config from classpath resources and local config from path set in system environments.
   * Default config file should be named [[appName]].conf, [[appName]].json or [[appName]].properties.
   * System variable may be named [[appName]]+[[SUFFIX]]
   *
   * @param appName application's name
   */
  def init(appName: String) = {
    this.appName = Some(appName)

    config = Some({
      val path = Option(System.getProperty(s"$appName${ConfigUtil.SUFFIX}"))
      if (path.isDefined) {
        val configFile = new File(path.get)
        if (configFile.exists()) {
          logger.info("Using default and local config file.")
          ConfigFactory.defaultOverrides()
            .withFallback(ConfigFactory.load(ConfigFactory.parseFile(new File(path.get))))
            .withFallback(ConfigFactory.load(appName))
        } else {
          logger.warn(s"Local config file under ${path.get} not found.")
          ConfigFactory.defaultOverrides()
            .withFallback(ConfigFactory.load(appName))
        }
      } else {
        logger.info("Using only default config file.")
        ConfigFactory.defaultOverrides()
          .withFallback(ConfigFactory.load(appName))
      }
    })
  }

  /**
   * Returns property's value as [[String]]
   * @param property property's name
   */
  @throws[MissingInit]("if the init wasn't called first")
  def getProperty(property: String, default: String = ""): String = {
    if (!config.isDefined) {
      throw new MissingInit("Init properties first.")
    }
    try {
      config.get.getString(property)
    } catch {
      case _:ConfigException => default
    }
  }
}