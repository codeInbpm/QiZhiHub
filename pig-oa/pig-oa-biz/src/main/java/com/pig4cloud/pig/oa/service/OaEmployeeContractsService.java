package com.pig4cloud.pig.oa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.oa.entity.OaEmployeeContractsEntity;

import java.util.List;
import java.util.Map;

public interface OaEmployeeContractsService extends IService<OaEmployeeContractsEntity> {

    R<Map<Long, String>> getEmployeeNamesByIds(List<Long> longEmployeeIds);
}
