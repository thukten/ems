package bt.gov.ditt.ems.util;


import java.util.*;
import java.io.*;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Log
{
  public final static int TRACE = 1;
  public final static int DEBUG = 2;
  public final static int INFO = 3;
  public final static int WARN = 4;
  public final static int ERROR = 5;
  public final static int FATAL = 6;


  public static void trace(Object obj)
  {
    logIt(TRACE, obj, null);
  }

  public static void trace(Object obj, Throwable t)
  {
    logIt(TRACE, obj, t);
  }

  public static void debug(Object obj)
  {
    logIt(DEBUG, obj, null);
  }

  public static void debug(Object obj, Throwable t)
  {
    logIt(DEBUG, obj, t);
  }

  public static void info(Object obj)
  {
    logIt(INFO, obj, null);
  }

  public static void info(Object obj, Throwable t)
  {
    logIt(INFO, obj, t);
  }

  public static void warn(Object obj)
  {
    logIt(WARN, obj, null);
  }

  public static void warn(Object obj, Throwable t)
  {
    logIt(WARN, obj, t);
  }

  public static void error(Object obj)
  {
    logIt(ERROR, obj, null);
  }

  public static void error(Object obj, Throwable t)
  {
    logIt(ERROR, obj, t);
  }

  public static void fatal(Object obj)
  {
    logIt(FATAL, obj, null);
  }

  public static void fatal(Object obj, Throwable t)
  {
    logIt(FATAL, obj, t);
  }

  private static void logIt(int level, Object obj, Throwable t)
  {
    if(obj == null && t == null)
      return;

    String logString = (obj == null) ? null : obj.toString();
    if( (logString == null || logString.length() == 0 ) && t == null)
      return;

    doLog(level, logString, t);

  }

  private static void doLog(int level, String logString, Throwable t)
  {
    String catName = getCatName(null);
    org.apache.commons.logging.Log log = null;
    
    try 
    {
      log = org.apache.commons.logging.LogFactory.getLog(catName);
    } 
    catch (Exception e) 
    {
      
      @SuppressWarnings("unused")
	long t0 = System.currentTimeMillis();
      try
      {
        Context ctx = new InitialContext();
        ctx.rebind("logging-context", "EMSLoggingContext");
      } catch(Exception e2) 
      {
        
      }
      @SuppressWarnings("unused")
	long t1 = System.currentTimeMillis();
      
      log = org.apache.commons.logging.LogFactory.getLog(catName);
    }

    if(level == TRACE && log.isTraceEnabled())
      log.trace(logString, t);
    else if(level == DEBUG && log.isDebugEnabled())
      log.debug(logString, t);
    else if(level == INFO && log.isInfoEnabled())
      log.info(logString, t);
    else if(level == WARN && log.isWarnEnabled())
      log.warn(logString, t);
    else if(level == ERROR && log.isErrorEnabled())
      log.error(logString, t);
    else if(level == FATAL && log.isFatalEnabled())
      log.fatal(logString, t);
    else if((level < TRACE || level > FATAL) && log.isTraceEnabled()) // by default use the trace level
      log.trace(logString, t);
  }

  private static String getCatName(Object logFor)
  {
    final String STRIPPATH = "bt.gov.";
    String className = "null";

    if(logFor != null)
    {
      if( !(logFor instanceof String))
        className = logFor.getClass().getName();
      else
        className = (String) logFor;
    }
    else 
    {
      className = classGetter.getCallerName();
    }

    if(className.startsWith(STRIPPATH))
    {
        className = className.substring(STRIPPATH.length());
    }

    if(!className.startsWith("EMS"))
    {
      className = "EMS" + "." + className;
    }

    return className;
  }

  private static ClassGetter classGetter = new ClassGetter();

  
  static class ClassGetter extends SecurityManager {

    
    @SuppressWarnings("unchecked")
	public synchronized String getCallerName() {
      
      Class[] callStack = getClassContext();
      String top = callStack[1].getName(); // this should be the Log class
      for(int i = 2; i < callStack.length; i++)
      {
        String curName = callStack[i].getName();
        if(!curName.equals(top)) {
          top = curName;
          break;
        }
      }
      
      return top;
    }

    
    public synchronized String getCallerName2() 
    {
      Throwable t = new Throwable();

      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);

      t.printStackTrace(pw);

      pw.flush();
      sw.flush();

      String trace = sw.toString();

      pw.close();
      try { sw.close(); } catch (Throwable t2) {}

      int i = 0;
      String curLine = "";

      StringTokenizer st = new StringTokenizer(trace, System.getProperty("line.separator"));
      while (st.hasMoreTokens()) {
        curLine = st.nextToken();
        if(i++ == 6) break; // the first 6 lines are useless
      }

      curLine = curLine.substring(4, curLine.length()-1); //skip the initial 'tab' 'a' 't' 'space', and the last ')'

      int index = curLine.indexOf('(');
      String pathToFunc = curLine;
      if(index != -1)
        pathToFunc = curLine.substring(0, index); //get the whole path till function name

      index = pathToFunc.lastIndexOf('.');
      if(index != -1)
        pathToFunc = pathToFunc.substring(0, index); //get rid of func name

      String lineno = "";
      index = curLine.lastIndexOf(':');
      if(index != -1)
        lineno = curLine.substring(index);

      String ctxStr = pathToFunc + lineno;

      
      return ctxStr;
    }
  }
  
  @SuppressWarnings("deprecation")
public static void resetLoggingLevel()
  {
		String catName = getCatName(null);
		@SuppressWarnings("unused")
		org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(catName);
		Category cat = Category.getInstance("EMS");
		if(cat.getLevel().equals(Level.DEBUG))		
			cat.setLevel(Level.INFO);
		else
			cat.setLevel(Level.DEBUG);
		Log.info("logging set to " + (cat.getLevel().equals(Level.DEBUG) ? "DEBUG" : "INFO" ));
  }  
  
  
}

