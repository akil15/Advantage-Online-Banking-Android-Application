package com.hp.advantage.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class LogManager {
    private static String _appName = "ADVANTAGE";

    public static void Debug(Exception exp) {
        if ((exp != null) && (exp.getMessage() != null)) {
            Log.d(_appName, exp.getMessage());
            ReportToAnalytics("Debug", exp.getMessage(), null, null);
        }
    }

    public static void Debug(String string) {
        if (string != null) {
            Log.d(_appName, string);
            ReportToAnalytics("Debug", null, null, string);
        }
    }

    public static void Debug(String paramString1, Exception exp) {
        if ((exp != null) && (exp.getMessage() != null)) {
            Log.d(_appName, paramString1 + " - " + exp.getMessage());
            ReportToAnalytics("Debug", exp.getMessage(), paramString1, null);
        }

    }

    public static void Error(Exception exp) {
        if ((exp != null) && (exp.getMessage() != null)) {
            Log.e(_appName, exp.getMessage());
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            exp.printStackTrace(printWriter);
            String stackTrace = writer.toString();

            ReportToAnalytics("Error", exp.getMessage(), stackTrace, null);
        }
    }

    public static void Error(String message) {
        if (message != null) {
            Log.e(_appName, message);

            ReportToAnalytics("Error", null, null, message);
        }
    }

    public static void Error(String message, Exception exp) {
        if ((exp != null) && (exp.getMessage() != null)) {
            Log.e(_appName, message + " - " + exp.getMessage());
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            exp.printStackTrace(printWriter);
            String stackTrace = writer.toString();

            ReportToAnalytics("Error", exp.getMessage(), stackTrace, message);
        }

    }

    public static void Info(Exception exp) {
        if ((exp != null) && (exp.getMessage() != null)) {
            Log.i(_appName, exp.getMessage());

            //ReportToAnalytics("Info", exp.getMessage(), null , null);
        }
    }

    public static void Info(String message) {
        if (message != null) {
            Log.i(_appName, message);

            //ReportToAnalytics("Info", null, null , message);
        }
    }

    public static void Info(String paramString1, Exception exp) {
        if ((exp != null) && (exp.getMessage() != null)) {
            Log.i(_appName, paramString1 + " - " + exp.getMessage());

            //ReportToAnalytics("Info", exp.getMessage(), paramString1 , null);
        }
    }

    public static void Warning(Exception exp) {
        if ((exp != null) && (exp.getMessage() != null)) {
            Log.w(_appName, exp.getMessage());

            //ReportToAnalytics("Warning", exp.getMessage(), null , null);

        }
    }

    public static void Warning(String string) {
        if (string != null) {
            Log.w(_appName, string);

            //ReportToAnalytics("Warning", null, null , string);

        }
    }

    public static void Warning(String paramString1, Exception exp) {
        if ((exp != null) && (exp.getMessage() != null)) {
            Log.w(_appName, paramString1 + " - " + exp.getMessage());

            //ReportToAnalytics("Warning", exp.getMessage(), paramString1 );
        }
    }

    private static void ReportToAnalytics(String Label, String ExceptionMessage, String Param, String Message) {
        /*

		Map<String, String> exceptionParams = new HashMap<String, String>();
		try
		{
			if (ExceptionMessage != null)
			{
				exceptionParams.put("exception", ExceptionMessage);
			}
			if (Param != null)
			{
				exceptionParams.put("param", Param);
			}
			if (Message != null)
			{
				exceptionParams.put("message", Message);
			}
		}
		catch (Exception exp)
		{

		}
		finally
		{
			//MixPanelHelper mixPanelHelper = new MixPanelHelper(context); 
			//mixPanelHelper.MixPanelTrack(MixPanelHelper.APPLICATION_INSTALLED, referralParams);
			///FlurryAgent.logEvent(Label, exceptionParams);
		}
		*/
    }
}