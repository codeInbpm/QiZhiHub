package com.pig4cloud.pig.oa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.admin.api.vo.UserExcelVO;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.oa.entity.OaEmployeesEntity;
import com.pig4cloud.pig.oa.vo.OaEmployeesVO;
import org.springframework.validation.BindingResult;

import java.util.List;

import java.util.List;
import java.util.Map;

public interface OaEmployeesService extends IService<OaEmployeesEntity> {

	R importEmployees(List<OaEmployeesVO> excelVOList, BindingResult bindingResult);

	R<Map<Long, String>> getDeptNamesByIds(List<Long> deptIds);


}
