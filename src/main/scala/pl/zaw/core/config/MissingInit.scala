package pl.zaw.core.config

import com.typesafe.config.ConfigException

/**
 * Created on 2015-04-29.
 *
 * @author Jakub Zawislak
 */
case class MissingInit(message: String = null, cause: Throwable = null) extends ConfigException(message, cause)