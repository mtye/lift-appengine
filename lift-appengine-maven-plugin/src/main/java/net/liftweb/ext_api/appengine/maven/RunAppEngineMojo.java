package net.liftweb.ext_api.appengine.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Starts the Google App Engine development server in a separate process
 * @goal run
 * @execute phase="pre-integration-test"
 */
public class RunAppEngineMojo extends AbstractMojo {

    /**
     * @parameter expression="${appengine.sdk.dir}"
     * @required
     */
    protected File appengineSdkDir;

    /**
     * @parameter expression="${web.app.dir}" default-value="war"
     * @required
     */
    protected File webAppDir;

    public void execute() throws MojoExecutionException, MojoFailureException {
        
        ProcessBuilder builder = buildProcess();
        executeProcess(builder);
    }
    
    protected ProcessBuilder buildProcess() throws MojoFailureException, MojoExecutionException {
        
        validateAppEngineSdkDir();
        validateWebAppDir();
        File toolsJar = new File(appengineSdkDir, "lib/appengine-tools-api.jar");
        validateAppEngineToolJar(toolsJar);
        
        String mainClass = "com.google.appengine.tools.development.DevAppServerMain";
        
        ProcessBuilder builder = new ProcessBuilder("java", "-cp", toolsJar.getAbsolutePath(), mainClass, webAppDir.getAbsolutePath());
        builder.directory(webAppDir);
        
        return builder;
    }
    
    protected int executeProcess(ProcessBuilder builder) throws MojoExecutionException {
        
        Process p;
        try {
            p = builder.start();
        } catch (IOException ioe) {
            throw new MojoExecutionException("Could not start process", ioe);
        }
        
        (new ErrorStreamLogger(p)).start();
        (new InputStreamLogger(p)).start();
        
        getLog().info("Starting Google App Engine development server.");
        
        try {
            p.waitFor();
            getLog().info("Google App Engine development server has stopped.");
        } catch (InterruptedException ie) {
            getLog().warn("Google App Engine development interrupted. Destroying process.", ie);
            p.destroy();
        }
        
        return p.exitValue();
    }
    
    private void validateAppEngineSdkDir() throws MojoFailureException {
      if (! appengineSdkDir.exists() || ! appengineSdkDir.isDirectory()) {
        throw new MojoFailureException("appengineSdkDir property " + appengineSdkDir.getAbsolutePath() + " is not a valid directory");
      }
    }
    
    private void validateAppEngineToolJar(File toolJar) throws MojoFailureException {
      if (! toolJar.exists() || ! toolJar.isFile()) {
        throw new MojoFailureException("Could not locate lib/appengine-tools-api.jar in " + appengineSdkDir.getAbsolutePath());
      }
    }
    
    private void validateWebAppDir() throws MojoFailureException {
      if (! webAppDir.exists() || ! webAppDir.isDirectory()) {
        throw new MojoFailureException("webAppDir property " + webAppDir.getAbsolutePath() + " is not a valid directory");
      }
    }
    
    abstract class StreamLogger extends Thread {
        
        private final BufferedReader buffered;
        private boolean done;
        
        StreamLogger(InputStream in, String name) {
            super(name);
            buffered = new BufferedReader(new InputStreamReader(in), 512);
        }

        public void run() {
            
            String line;
            
            try {
                while ((line = buffered.readLine()) != null) {
                    logLine(line);
                }
            } catch (IOException ioe) {
                getLog().error("Error reading stream", ioe);
            } finally {
                try {
                    buffered.close();
                } catch (IOException ioe) {
                    getLog().error("Error closing stream", ioe);
                }
                done = true;
            }

        }
        
        public boolean isDone() {
            return done;
        }

        abstract void logLine(String line);
    }
    
    class InputStreamLogger extends StreamLogger {
        InputStreamLogger(Process p) {
            super(p.getInputStream(), "InputStreamLogger");
        }

        @Override
        void logLine(String line) {
            getLog().info(line);
        }
    }
    
    class ErrorStreamLogger extends StreamLogger {
        ErrorStreamLogger(Process p) {
            super(p.getErrorStream(), "ErrorStreamLogger");
        }

        @Override
        void logLine(String line) {
            getLog().error(line);
        }
    }
    
}
