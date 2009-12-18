/*
* Copyright 2009 Mark Tye
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions
* and limitations under the License.
*/
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
