package data.mariadb

import play.api.Configuration
import java.sql.{DriverManager}

object MariaDB{
  case class Connection(config: Configuration) extends Config {
    def getConnection(): java.sql.Connection = {
      val url = s"jdbc:mariadb://${server}:${port}/${database}"
      try {
        Class.forName(driver)
        DriverManager.getConnection(url, user, password)

      }catch{
        case exc:Exception =>
          exc.printStackTrace()
          throw exc
      }
    }
  }
  trait Config {
    val config: Configuration
    val serverConfig   = "db.mariadb.server"
    val databaseConfig = "db.mariadb.database"
    val userConfig     = "db.mariadb.user"
    val passwordConfig = "db.mariadb.password"
    private[MariaDB] val portConfig = "db.mariadb.port"
    private[MariaDB] val driverConfig = "db.mariadb.driver"

    val server = config.getString(serverConfig).getOrElse(throw new IllegalArgumentException(ServerErrorMessage))
    val database = config.getString(databaseConfig).getOrElse(throw new IllegalArgumentException(DatabaseErrorMessage))
    val user = config.getString(userConfig).getOrElse(throw new IllegalArgumentException(UserErrorMessage))
    val password = config.getString(passwordConfig).getOrElse(throw new IllegalArgumentException(PasswordErrorMessage))
    val port = config.getString(portConfig).getOrElse(throw new IllegalArgumentException(PortErrorMessage))
    val driver = config.getString(driverConfig).getOrElse(throw new IllegalArgumentException(DriverErrorMessage))

    //"jdbc:mysql://localhost:3306/test"
    //val driver = "com.mysql.jdbc.Driver"
    //val username = "root"
    //val password = "demo"

    val ServerErrorMessage =
        s"MariaDBConfiguration Error. Configure server with $serverConfig "
    val DatabaseErrorMessage =
        s"MariaDBConfiguration Error. Configure database with $databaseConfig"
    val UserErrorMessage =
      s"MariaDBConfiguration Error. Configure username with $userConfig"
    val PasswordErrorMessage =
      s"MariaDBConfiguration Error. Configure user password with $passwordConfig"
    val PortErrorMessage =
      s"MariaDBConfiguration Error. Configure port database with $portConfig"
    val DriverErrorMessage =
      s"MariaDBConfiguration Error. Configure driver with $driverConfig"
    }

}
