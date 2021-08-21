package com.lin.mapper.clickhouse;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClickhouseMapper {

    /**
     * test
     * @return
     */
    Integer test();

    /**
     * 查询
     * @param LogIdList
     * @return
     */
    List<Map<String, Object>> getOriginAlarmLogReportData(@Param("LogIdList") List<String> LogIdList);

    /**
     * 根据告警日志，查询
     * @param LogIdList
     * @return
     */
    List<Map<String, Object>> getRawMsgById(@Param("LogIdList") List<String> LogIdList);
}
