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
package com.alibaba.nacos.client.utils;

import java.util.List;
import java.util.Map;

import com.alibaba.nacos.client.config.utils.LogUtils;
import com.alibaba.nacos.client.logger.Logger;

/**
 * env util.
 * 
 * @author Nacos
 *
 */
public class EnvUtil {

	final static public Logger log = LogUtils.logger(EnvUtil.class);
	
	public static void setSelfEnv(Map<String, List<String>> headers) {
		if (headers != null) {
			List<String> amorayTagTmp = headers.get(AMORY_TAG);
			if (amorayTagTmp == null) {
				if (selfAmorayTag != null) {
					selfAmorayTag = null;
					log.warn("selfAmoryTag:null");
				}
			} else {
				String amorayTagTmpStr = listToString(amorayTagTmp);
				if (!amorayTagTmpStr.equals(selfAmorayTag)) {
					selfAmorayTag = amorayTagTmpStr;
					log.warn("selfAmoryTag:{}", selfAmorayTag);
				}
			}

			List<String> vipserverTagTmp = headers.get(VIPSERVER_TAG);
			if (vipserverTagTmp == null) {
				if (selfVipserverTag != null) {
					selfVipserverTag = null;
					log.warn("selfVipserverTag:null");
				}
			} else {
				String vipserverTagTmpStr = listToString(vipserverTagTmp);
				if (!vipserverTagTmpStr.equals(selfVipserverTag)) {
					selfVipserverTag = vipserverTagTmpStr;
					log.warn("selfVipserverTag:{}", selfVipserverTag);
				}
			}
			List<String> locationTagTmp = headers.get(LOCATION_TAG);
			if (locationTagTmp == null) {
				if (selfLocationTag != null) {
					selfLocationTag = null;
					log.warn("selfLocationTag:null");
				}
			} else {
				String locationTagTmpStr = listToString(locationTagTmp);
				if (!locationTagTmpStr.equals(selfLocationTag)) {
					selfLocationTag = locationTagTmpStr;
					log.warn("selfLocationTag:{}", selfLocationTag);
				}
			}
		}
	}

	public static String getSelfAmorayTag() {
		return selfAmorayTag;
	}

	public static String getSelfVipserverTag() {
		return selfVipserverTag;
	}

	public static String getSelfLocationTag() {
		return selfLocationTag;
	}
	
	public static String listToString(List<String> list) {
		if (list == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		boolean first = true;
		// ????????????????????????","
		for (String string : list) {
			if (first) {
				first = false;
			} else {
				result.append(",");
			}
			result.append(string);
		}
		return result.toString();
	}

	private static String selfAmorayTag;
	private static String selfVipserverTag;
	private static String selfLocationTag;
	public final static String AMORY_TAG = "Amory-Tag";
	public final static String VIPSERVER_TAG = "Vipserver-Tag";
	public final static String LOCATION_TAG = "Location-Tag";
}
