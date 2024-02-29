package com.example.multidatasource.controller;

import com.example.multidatasource.config.HikariProperties;
import com.example.multidatasource.datasource.DataSourceMap;
import com.example.multidatasource.datasource.DataSourceRouter;
import com.zaxxer.hikari.HikariConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/hikari")
@Slf4j
@AllArgsConstructor
public class HikariConfigController {
    private final HikariProperties hikariProperties;
    private final DataSourceMap dataSourceMap;
    private final DataSourceRouter dataSourceRouter;

    //  curl -X GET -i -H "Content-Type:application/json" http://localhost:8080/hikari/config
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public ResponseEntity<Map<String, HikariConfig>> getHikariConfig() {
        return ResponseEntity.ok(hikariProperties.getConfig());
    }

    //  curl -X GET -i -H "Content-Type:application/json" http://localhost:8080/hikari/datasources
    @RequestMapping(value = "/datasources", method = RequestMethod.GET)
    public ResponseEntity<Map<Object, Object>> getAllHikariDataSources() {
        return ResponseEntity.ok(dataSourceMap.getMap());
    }

    //  curl -X GET -i -H "Content-Type:application/json" http://localhost:8080/hikari/poolcount
    @RequestMapping(value = "/poolcount", method = RequestMethod.GET)
    public ResponseEntity<Integer> getPoolCount() {
        return ResponseEntity.ok(dataSourceMap.size());
    }

    //  curl -X GET -i -H "Content-Type:application/json" http://localhost:8080/hikari/init
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public ResponseEntity<Map<Object, Object>> init() {
        return ResponseEntity.ok(dataSourceMap.initMap());
    }
}
