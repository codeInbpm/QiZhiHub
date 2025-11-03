package com.pig4cloud.pig.oa.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.oa.entity.OaEmployeeContractsEntity;
import com.pig4cloud.pig.oa.entity.OaEmployeesEntity;
import com.pig4cloud.pig.oa.mapper.OaEmployeeContractsMapper;
import com.pig4cloud.pig.oa.service.OaEmployeeContractsService;
import com.pig4cloud.pig.oa.service.OaEmployeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OA员工合同表
 *
 * @author wangben
 * @date 2025-10-22 09:51:33
 */
@Service
public class OaEmployeeContractsServiceImpl extends ServiceImpl<OaEmployeeContractsMapper, OaEmployeeContractsEntity> implements OaEmployeeContractsService {

	@Autowired
	private OaEmployeesService oaEmployeesService;

	@Override
	public R<Map<Long, String>> getEmployeeNamesByIds(List<Long> longEmployeeIds) {
		// 参数校验：如果 ID 列表为空，直接返回空 Map
		if (CollUtil.isEmpty(longEmployeeIds)) {
			return R.ok(Collections.emptyMap());
		}

		// 查询员工表中指定 ID 的员工姓名
		List<OaEmployeesEntity> employeeList = oaEmployeesService.list(
				new LambdaQueryWrapper<OaEmployeesEntity>()
						.in(OaEmployeesEntity::getId, longEmployeeIds)
						.select(OaEmployeesEntity::getId, OaEmployeesEntity::getEnpName));

		// 转换为 Map，键是员工 ID（Long），值是员工姓名
		Map<Long, String> employeeNameMap = employeeList.stream()
				.collect(Collectors.toMap(
						OaEmployeesEntity::getId,
						OaEmployeesEntity::getEnpName,
						(existing, replacement) -> existing  // 处理潜在重复键（虽 unlikely）
				));

		return R.ok(employeeNameMap);
	}
}