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
package com.alibaba.nacos.config.server.controller;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.nacos.config.server.constant.Constants;
import com.alibaba.nacos.config.server.service.DataSourceService;
import com.alibaba.nacos.config.server.service.DynamicDataSource;
import com.alibaba.nacos.config.server.service.ServerListService;
import com.alibaba.nacos.config.server.utils.SystemConfig;

/**
 * health service
 * 
 * @author Nacos
 *
 */
@Controller
@RequestMapping(Constants.HEALTH_CONTROLLER_PATH)
public class HealthController {
	
	@Autowired
	private DynamicDataSource dynamicDataSource;
	private DataSourceService dataSourceService;
	private String heathUpStr = "UP";
	private String heathDownStr = "DOWN";
	private String heathWarnStr = "WARN";

	@PostConstruct
	public void init() {
		dataSourceService = dynamicDataSource.getDataSource();
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public String getHealth() {
		// TODO UP DOWN WARN
		StringBuilder sb = new StringBuilder();
		String dbStatus = dataSourceService.getHealth();
		if (dbStatus.contains(heathUpStr) && ServerListService.isAddressServerHealth() && ServerListService.isInIpList()) {
			sb.append(heathUpStr);
		} else if (dbStatus.contains(heathWarnStr) && ServerListService.isAddressServerHealth() && ServerListService.isInIpList()){
			sb.append("WARN:");
			sb.append("???????????? ").append(dbStatus.split(":")[1]).append(" down. ");
		} else {
			sb.append("DOWN:");
			if (dbStatus.indexOf(heathDownStr) != -1) {
				sb.append("???????????? ").append(dbStatus.split(":")[1]).append(" down. ");
			}
			if (!ServerListService.isAddressServerHealth()) {
				sb.append("??????????????? down. ");
			}
			if (!ServerListService.isInIpList()) {
				sb.append("server ").append(SystemConfig.LOCAL_IP).append(" ????????????????????????IP?????????. ");
			}
		}

		return sb.toString();
	}

}
