/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
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

package com.alibaba.nacos.plugin.datasource.impl.mysql;

import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.constants.FieldConstant;
import com.alibaba.nacos.plugin.datasource.constants.TableConstant;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigInfoBetaMapperByMysqlTest {
    
    int startRow = 0;
    
    int pageSize = 5;
    
    String appName = "appName";
    
    String tenantId = "tenantId";
    
    String id = "123";
    
    List<Long> ids = Lists.newArrayList(1L, 2L, 3L, 5L, 144L);
    
    Timestamp startTime = new Timestamp(System.currentTimeMillis());
    
    Timestamp endTime = new Timestamp(System.currentTimeMillis());
    
    MapperContext context;
    
    private ConfigInfoBetaMapperByMySql configInfoBetaMapperByMySql;
    
    @BeforeEach
    void setUp() throws Exception {
        configInfoBetaMapperByMySql = new ConfigInfoBetaMapperByMySql();
        
        context = new MapperContext(startRow, pageSize);
        context.putWhereParameter(FieldConstant.APP_NAME, appName);
        context.putWhereParameter(FieldConstant.TENANT_ID, tenantId);
        context.putWhereParameter(FieldConstant.ID, id);
        context.putWhereParameter(FieldConstant.START_TIME, startTime);
        context.putWhereParameter(FieldConstant.END_TIME, endTime);
        context.putWhereParameter(FieldConstant.IDS, ids);
        
    }
    
    @Test
    void testUpdateConfigInfo4BetaCas() {
        String newContent = "new Content";
        String newMD5 = "newMD5";
        String srcIp = "1.1.1.1";
        Object srcUser = "nacos";
        Object appNameTmp = "newAppName";
        Object desc = "description";
        Object use = "use";
        Object effect = "effect";
        Object type = "type";
        Object schema = "schema";
        Object betaIps = "2.2.2.2";
        
        context.putUpdateParameter(FieldConstant.CONTENT, newContent);
        context.putUpdateParameter(FieldConstant.MD5, newMD5);
        context.putUpdateParameter(FieldConstant.BETA_IPS, betaIps);
        context.putUpdateParameter(FieldConstant.SRC_IP, srcIp);
        context.putUpdateParameter(FieldConstant.SRC_USER, srcUser);
        context.putUpdateParameter(FieldConstant.APP_NAME, appNameTmp);
        context.putUpdateParameter(FieldConstant.C_DESC, desc);
        context.putUpdateParameter(FieldConstant.C_USE, use);
        context.putUpdateParameter(FieldConstant.EFFECT, effect);
        context.putUpdateParameter(FieldConstant.TYPE, type);
        context.putUpdateParameter(FieldConstant.C_SCHEMA, schema);
        
        Object dataId = "dataId";
        Object group = "group";
        Object md5 = "md5";
        
        context.putWhereParameter(FieldConstant.DATA_ID, dataId);
        context.putWhereParameter(FieldConstant.GROUP_ID, group);
        context.putWhereParameter(FieldConstant.TENANT_ID, tenantId);
        context.putWhereParameter(FieldConstant.MD5, md5);
        
        MapperResult mapperResult = configInfoBetaMapperByMySql.updateConfigInfo4BetaCas(context);
        
        String sql = mapperResult.getSql();
        assertEquals(sql,
                "UPDATE config_info_beta SET content = ?,md5 = ?,beta_ips = ?,"
                        + "src_ip = ?,src_user = ?,gmt_modified = NOW(3),app_name = ? "
                        + "WHERE data_id = ? AND group_id = ? AND tenant_id = ? AND (md5 = ? OR md5 is null OR md5 = '')");
        assertArrayEquals(new Object[]{newContent, newMD5, betaIps, srcIp, srcUser, appNameTmp, dataId, group, tenantId, md5},
                mapperResult.getParamList().toArray());
    }
    
    @Test
    void testFindAllConfigInfoBetaForDumpAllFetchRows() {
        MapperResult result = configInfoBetaMapperByMySql.findAllConfigInfoBetaForDumpAllFetchRows(context);
        String sql = result.getSql();
        List<Object> paramList = result.getParamList();
        assertEquals(sql, " SELECT t.id,data_id,group_id,tenant_id,app_name,content,md5,gmt_modified,beta_ips,encrypted_data_key "
                + " FROM ( SELECT id FROM config_info_beta  ORDER BY id LIMIT " + startRow + "," + pageSize + " )"
                + "  g, config_info_beta t WHERE g.id = t.id ");
        assertEquals(paramList, Arrays.asList(startRow, pageSize));
    }
    
    @Test
    void testGetTableName() {
        String tableName = configInfoBetaMapperByMySql.getTableName();
        assertEquals(TableConstant.CONFIG_INFO_BETA, tableName);
    }
    
    @Test
    void testGetDataSource() {
        String dataSource = configInfoBetaMapperByMySql.getDataSource();
        assertEquals(DataSourceConstant.MYSQL, dataSource);
    }
}
