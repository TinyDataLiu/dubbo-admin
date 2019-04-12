/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dubbo.admin.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.dubbo.admin.model.dto.MetricDTO;
import org.apache.dubbo.admin.service.ConsumerService;
import org.apache.dubbo.admin.service.ProviderService;
import org.apache.dubbo.admin.service.impl.MetrcisCollectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/api/{env}/metrics")
public class MetricsCollectController {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ConsumerService consumerService;

    @RequestMapping(method = RequestMethod.POST)
    public String metricsCollect(@RequestParam String group) {
        MetrcisCollectServiceImpl service = new MetrcisCollectServiceImpl();
        service.setUrl("dubbo://127.0.0.1:20880?scope=remote&cache=true");

        return service.invoke(group).toString();
    }

    private String getOnePortMessage(String group, String ip, String port, String protocol) {
        MetrcisCollectServiceImpl metrcisCollectService = new MetrcisCollectServiceImpl();
        metrcisCollectService.setUrl(protocol + "://" + ip + ":" + port +"?scope=remote&cache=true");
        String res = metrcisCollectService.invoke(group).toString();
        return res;
    }

    @RequestMapping( value = "/ipAddr", method = RequestMethod.GET)
    public List<MetricDTO> searchService(@RequestParam String ip, @RequestParam String group) {

        Map<String, String> configMap = new HashMap<String, String>();
        //TODO get this message from config file
        //     key:port value:protocol
        configMap.put("54188", "dubbo");
        configMap.put("54199", "dubbo");

        // default value
        if (configMap.size() <= 0) {
            configMap.put("20880", "dubbo");
        }

        List<MetricDTO> metricDTOS = new ArrayList<>();
        for (String port : configMap.keySet()) {
            String protocol = configMap.get(port);
            String res = getOnePortMessage(group, ip, port, protocol);
            metricDTOS.addAll(new Gson().fromJson(res, new TypeToken<List<MetricDTO>>(){}.getType()));
        }

        return metricDTOS;
    }
}
