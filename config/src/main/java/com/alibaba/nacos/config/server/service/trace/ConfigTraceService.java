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
package com.alibaba.nacos.config.server.service.trace;

import com.alibaba.nacos.config.server.utils.LogUtil;
import com.alibaba.nacos.config.server.utils.MD5;
import com.alibaba.nacos.config.server.utils.SystemConfig;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
/**
 * Config trace
 * @author Nacos
 *
 */
@Service
public class ConfigTraceService {
    public static final String  PERSISTENCE_EVENT_PUB = "pub";
    public static final String  PERSISTENCE_EVENT_REMOVE = "remove";
    public static final String  PERSISTENCE_EVENT_MERGE = "merge";

    public static final String  NOTIFY_EVENT_OK = "ok";
    public static final String  NOTIFY_EVENT_ERROR = "error";
	public static final String NOTIFY_EVENT_UNHEALTH = "unhealth";
    public static final String  NOTIFY_EVENT_EXCEPTION = "exception";

    public static final String  DUMP_EVENT_OK = "ok";
    public static final String  DUMP_EVENT_REMOVE_OK = "remove-ok";
    public static final String  DUMP_EVENT_ERROR = "error";

    public static final String  PULL_EVENT_OK = "ok";
    public static final String  PULL_EVENT_NOTFOUND = "not-found";
    public static final String  PULL_EVENT_CONFLICT = "conflict";
    public static final String  PULL_EVENT_ERROR = "error";

    public static void logPersistenceEvent(String dataId, String group, String tenant, String requestIpAppName, long ts, String handleIp, String type , String content) {
		if (!LogUtil.traceLog.isInfoEnabled()) {
			return;
		}
		// ??????tlog??????
		if (StringUtils.isBlank(tenant)) {
			tenant = null;
		}
        //localIp | dataid | group | tenant | requestIpAppName | ts | handleIp | event | type | [delayed = -1] | ext(md5)
        String md5 = content == null ? null : MD5.getInstance().getMD5String(content);
        LogUtil.traceLog.info("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}", new Object[]{SystemConfig.LOCAL_IP, dataId, group, tenant, requestIpAppName, ts, handleIp, "persist", type, -1, md5});
    }

    public static void logNotifyEvent(String dataId, String group, String tenant, String requestIpAppName, long ts, String handleIp, String type,long delayed, String targetIp) {
		if (!LogUtil.traceLog.isInfoEnabled()) {
			return;
		}
		// ??????tlog??????
		if (StringUtils.isBlank(tenant)) {
			tenant = null;
		}
        //localIp | dataid | group | tenant | requestIpAppName | ts | handleIp | event | type | [delayed] | ext(targetIp)
        LogUtil.traceLog.info("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}", new Object[]{SystemConfig.LOCAL_IP, dataId, group, tenant, requestIpAppName, ts, handleIp, "notify", type, delayed, targetIp});
    }

    public static void logDumpEvent(String dataId, String group, String tenant, String requestIpAppName, long ts, String handleIp, String type, long delayed, long length) {
		if (!LogUtil.traceLog.isInfoEnabled()) {
			return;
		}
		// ??????tlog??????
		if (StringUtils.isBlank(tenant)) {
			tenant = null;
		}
        //localIp | dataid | group | tenant | requestIpAppName | ts | handleIp | event | type | [delayed] | length
        LogUtil.traceLog.info("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}", new Object[]{SystemConfig.LOCAL_IP, dataId, group, tenant, requestIpAppName, ts, handleIp, "dump", type, delayed, length});
    }

    public static void logDumpAllEvent(String dataId, String group, String tenant, String requestIpAppName, long ts, String handleIp, String type) {
		if (!LogUtil.traceLog.isInfoEnabled()) {
			return;
		}
		// ??????tlog??????
		if (StringUtils.isBlank(tenant)) {
			tenant = null;
		}
    	//localIp | dataid | group | tenant | requestIpAppName | ts | handleIp | event | type | [delayed = -1]
        LogUtil.traceLog.info("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}", new Object[]{SystemConfig.LOCAL_IP, dataId, group, tenant, requestIpAppName, ts, handleIp, "dump-all", type, -1});
    }

    public static void logPullEvent(String dataId, String group, String tenant, String requestIpAppName, long ts, String type, long delayed, String clientIp) {
		if (!LogUtil.traceLog.isInfoEnabled()) {
			return;
		}
		// ??????tlog??????
		if (StringUtils.isBlank(tenant)) {
			tenant = null;
		}
    	//localIp | dataid | group | tenant| requestIpAppName| ts | event | type | [delayed] | ext(clientIp)
        LogUtil.traceLog.info("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}", new Object[]{SystemConfig.LOCAL_IP, dataId, group, tenant, requestIpAppName, ts, "pull", type, delayed, clientIp});
    }
}
