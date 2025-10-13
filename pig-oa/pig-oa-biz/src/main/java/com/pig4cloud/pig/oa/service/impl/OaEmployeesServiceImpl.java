package com.pig4cloud.pig.oa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.oa.entity.OaEmployeesEntity;
import com.pig4cloud.pig.oa.mapper.OaEmployeesMapper;
import com.pig4cloud.pig.oa.service.OaEmployeesService;
import com.pig4cloud.pig.oa.vo.OaEmployeesVO;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * OA员工档案主表
 *
 * @author wangben
 * @date 2025-10-12 21:59:38
 */
@Service
public class OaEmployeesServiceImpl extends ServiceImpl<OaEmployeesMapper, OaEmployeesEntity> implements OaEmployeesService {

	@Override
	public R importEmployees(List<OaEmployeesVO> excelVOList, BindingResult bindingResult) {
		return null;
	}
}
