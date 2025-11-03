package com.pig4cloud.pig.oa.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pig4cloud.pig.oa.entity.OaEmployeesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OaEmployeesMapper extends BaseMapper<OaEmployeesEntity> {

	Map<Long, String> getEmployeeNamesByIds(@Param("ids") List<Long> ids);
}
