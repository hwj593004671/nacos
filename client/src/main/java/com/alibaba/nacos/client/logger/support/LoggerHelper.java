/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.nacos.client.logger.support;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.nacos.client.logger.Logger;

/**
 * logger help
 * @author Nacos
 *
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class LoggerHelper {

    private static final String         MORE_URL_POSFIX    = ".ERROR_CODE_MORE_URL";
    private static final String         DEFAULT_MORE_URL   = "http://console.taobao.net/help/";

    private static String               LOG_PATH           = null;
    private static final String         CONVERSION_PATTERN = "01 %d{yyyy-MM-dd HH:mm:ss.SSS} %p [%-5t:%c{2}] %m%n";

    private static Map<String, Boolean> Product_Logger_Info;
    private static Map<String, String>  Product_Logger_Pattern;

    private static Map<String, ResourceBundle> Product_Resource_Bundle;

    static {
        String dpath = System.getProperty("JM.LOG.PATH");
        if (dpath == null || dpath.trim().equals("")) {
            String defaultPath = System.getProperty("user.home");
            LOG_PATH = defaultPath + File.separator + "logs" + File.separator;
        } else {
            if (!new File(dpath).isAbsolute()) {
//                throw new RuntimeException("-DJM.LOG.PATH must be an absolute path.");
                String defaultPath = System.getProperty("user.home");
                dpath = defaultPath + File.separator + dpath;
            }
            if (dpath.endsWith(File.separator)) {
                LOG_PATH = dpath;
            } else {
                LOG_PATH = dpath + File.separator;
            }
        }

        LogLog.info("Log root path: " + LOG_PATH);

        Product_Logger_Info = new ConcurrentHashMap<String, Boolean>();
        Product_Logger_Pattern = new ConcurrentHashMap<String, String>();
        Product_Resource_Bundle = new ConcurrentHashMap<String, ResourceBundle>();
    }

    /**
     * ????????????????????????????????????File.separator??????
     */
    public static String getLogpath() {
        return LOG_PATH;
    }

    /**
     * <pre>
     * ?????????????????????????????????
     * 
     * ????????????-DJM.LOG.PATH?????????????????????????????????
     * ?????????{user.home}/logs/
     * 
     * ??????hsf?????????LoggerHelper.getLogFile("hsf", "hsf.log")????????????{user.home}/logs/hsf/hsf.log
     * </pre>
     * 
     * @param productName ????????????????????????hsf, tddl
     * @param fileName ?????????????????????hsf.log??????????????????????????????????????? subDir + File.separator + *.log
     */
    public static String getLogFile(String productName, String fileName) {
        String file = LOG_PATH + productName + File.separator + fileName;

        if (Product_Logger_Info.get(productName) == null) {
            Product_Logger_Info.put(productName, true);
            LogLog.info("Set " + productName + " log path: " + LOG_PATH + productName);
        }

        return file;
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????logback/log4j??????
     */
    public static String getPattern(String productName) {
        String pattern = Product_Logger_Pattern.get(productName);
        if (pattern == null) {
            return CONVERSION_PATTERN;
        }

        return pattern;
    }

    /**
     * ?????????????????????????????????
     */
    public static String getPattern() {
        return CONVERSION_PATTERN;
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????? log4j/logback ?????????????????????????????????????????????????????????????????????????????????????????????
     * 
     * @param productName ????????????????????????hsf, tddl
     * @param pattern ????????????
     */
    public static void setPattern(String productName, String pattern) {
        Product_Logger_Pattern.put(productName, pattern);
    }

    /**
     * ??????????????????????????????properties??????
     *
     * @param productName ????????????????????????hsf, tddl
     * @param bundleName bundleName
     */
    public static void setResourceBundle(String productName, String bundleName) {
        try {
            ResourceBundle rb = ResourceBundle.getBundle(bundleName);
            Product_Resource_Bundle.put(productName, rb);
        } catch (Exception e) {
            LogLog.error("Failed to set " + productName + " resource bundle for: " + bundleName, e);
        }
    }

    /**
     * ??????????????????message???????????????????????????????????????code
     *
     * @param productName ????????????????????????hsf, tddl
     * @param code code
     */
    public static String getResourceBundleString(String productName, String code) {
        if (Product_Resource_Bundle.isEmpty() || code == null || productName == null) {
            return code;
        }

        ResourceBundle rs = Product_Resource_Bundle.get(productName);
        if (rs != null) {
            try {
                String value = rs.getString(code);
                return value;
            } catch (MissingResourceException e) {
                return code;
            }
        }

        return code;
    }

    /**
     * ?????????????????????ErrorCode??????
     * 
     * @param errorCode
     */
    @Deprecated
    public static String getErrorCodeStr(String errorCode) {
        return "ERR-CODE: [" + errorCode + "], More: [" + "http://console.taobao.net/jm/" + errorCode + "]";
    }

    /**
     * ??????productName?????????????????????ErrorCode??????
     * 
     * @param productName ??? HSF???????????? HSF.ErrorCodeMoreUrl ??? System??????????????? more url ????????????http://console.taobao.net/jm/
     * @param errorCode ???????????????HSF-001
     * @param errorType ????????????
     * @param message ??????????????????
     */
    public static String getErrorCodeStr(String productName, String errorCode, String errorType, String message) {
        String moreUrl = DEFAULT_MORE_URL;
        if (productName != null) {
            String customUrl = System.getProperty(productName.toUpperCase() + MORE_URL_POSFIX);

            if (customUrl != null) {
                moreUrl = customUrl;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(message);
        sb.append(" ERR-CODE: [");
        sb.append(errorCode);
        sb.append("], Type: [");
        sb.append(errorType);
        sb.append("], More: [");
        sb.append(moreUrl);
        sb.append(errorCode);
        sb.append("]");

        return sb.toString();
    }

	@SuppressFBWarnings(value = { "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE" })
	public static String getLogFileP(String productName, String fileName) {
		String file = getLogFile(productName, fileName);
		File logfile = new File(file);
		logfile.getParentFile().mkdirs();
		return file;
	}

    /**
     * When prudent is set to true, file appenders from multiple JVMs can safely
     * write to the same file.
     *
     * Only support by logback
     *
     * @param prudent
     * @since 0.1.8
     */
    public static void activePrudent(Logger logger, boolean prudent) {
        if (logger != null && logger.getDelegate() != null) {
            if (!(logger.getDelegate() instanceof ch.qos.logback.classic.Logger)) {
                throw new IllegalArgumentException("logger must be ch.qos.logback.classic.Logger, but it's "
                        + logger.getDelegate().getClass());
            }

            Iterator<Appender<ILoggingEvent>> iter = ((ch.qos.logback.classic.Logger) logger.getDelegate()).iteratorForAppenders();
            while (iter.hasNext()) {
                ch.qos.logback.core.Appender<ILoggingEvent> appender = iter.next();
                if (appender instanceof FileAppender) {
                    ((FileAppender) appender).setPrudent(prudent);
                } else {
                    continue;
                }
            }
        }
    }
}
