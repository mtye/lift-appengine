import com.google.appengine.tools.development.DevAppServer
import com.google.appengine.tools.development.DevAppServerFactory

import java.io.File

object RunAppEngine {
  
  def main(args: Array[String]) {
    
    for ((arg, i) <- args.zipWithIndex) println("arg[" + i + "]=" + arg)
    val dir = if (args.isEmpty) new File(".") else new File(args(0))
    println(dir.getAbsolutePath)
    
    validateSdkRoot match {
      case Right(_) => startServer(dir)
      case Left(error) => System.err.println(error)
    }
  }
  
  def startServer(dir: File) {
    
    val factory = new DevAppServerFactory
    val server = factory.createDevAppServer(dir, DevAppServer.DEFAULT_HTTP_ADDRESS, DevAppServer.DEFAULT_HTTP_PORT)
    
    try {
      println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
      server.start();
      while (System.in.available() == 0) {
        Thread.sleep(5000)
      }
      server.shutdown()
    } catch {
      case exc : Exception => {
        exc.printStackTrace()
        System.exit(100)
      }
    }
  }
  
  def validateSdkRoot: Either[String, String] = {

    val sdkRootKey = "appengine.sdk.root"
    
    def sdkRoot: Either[String, String] = System.getProperty(sdkRootKey) match {
      case null =>
        Left("ERROR: appengine.sdk.root system property has not been set.\n" +
             "Use -Dappengine.sdk.root=<directory> to set the property to the " +
             "directory where you unzipped your App Engine Java SDK.\n" + 
             "See http://code.google.com/appengine/docs/java/gettingstarted/installing.html")
      case sdkRoot => {
        Right(sdkRoot)
      }
    }
    
    def isValidDirectory (either: Either[String, String]): Either[String, String] = either match {
      case Left(errMsg) => either
      case Right(dir) => {
        val file = new File(dir)
        if (file.isDirectory) either else
          Left("ERROR: The value of appengine.sdk.root system property is not valid.\n" +
               "The invalid value is: " + dir + "\n" +
               "Use -Dappengine.sdk.root=<directory> to set the property to the " +
               "directory where you unzipped your App Engine Java SDK.\n" + 
               "See http://code.google.com/appengine/docs/java/gettingstarted/installing.html")
      }
    }
      
    isValidDirectory(sdkRoot)
  }
  

}
