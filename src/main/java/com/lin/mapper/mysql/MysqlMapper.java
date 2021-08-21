package com.lin.mapper.mysql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MysqlMapper {

    /**
     * test
     * @return
     */
    Integer test();

    /**
     * 查询安全时间新增(tc_alarm表数据， 原始日志从clickhouse中查询)
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String, Object>> getSecurityAddReportData(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 查询告警对应的原始日志的id
     * @param startTime
     * @param endTime
     * @return
     */
    List<String> getSecurityAddLogId(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 保存集团上报信息
     * @param reportDataMapList
     */
    void saveAlarmData(@Param("reportDataMapList") List<Map<String, Object>> reportDataMapList);
}
