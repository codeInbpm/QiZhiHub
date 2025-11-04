package com.pig4cloud.pig.oa.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pig4cloud.pig.admin.api.entity.SysDept;
import com.pig4cloud.pig.admin.api.feign.RemoteDeptService;
import com.pig4cloud.pig.common.core.constant.CommonConstants;
import com.pig4cloud.pig.common.core.exception.ValidateCodeException;
import com.pig4cloud.pig.common.core.util.MsgUtils;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.oa.constant.OaErrorConstants;
import com.pig4cloud.pig.oa.entity.OaEmployeesEntity;
import com.pig4cloud.pig.oa.mapper.OaEmployeesMapper;
import com.pig4cloud.pig.oa.service.OaEmployeesService;
import com.pig4cloud.pig.oa.vo.OaEmployeesVO;
import com.pig4cloud.plugin.excel.vo.ErrorMessage;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OA员工档案主表
 *
 * @author wangben
 * @date 2025-10-12 21:59:38
 */
@Service
public class OaEmployeesServiceImpl extends ServiceImpl<OaEmployeesMapper, OaEmployeesEntity> implements OaEmployeesService {

	@Autowired
	private RemoteDeptService remoteDeptService;

	// 远程部门服务
	private final ObjectMapper objectMapper;

	// 用于校验字典值的常量
	private static final Set<String> GENDER_SET = Set.of("男", "女", "其他");
	private static final Set<String> HIRE_TYPE_SET = Set.of("全职", "兼职", "远程"); // 示例值
	private static final Set<String> EMP_TYPE_SET = Set.of("正式", "合同", "实习"); // 示例值

	public OaEmployeesServiceImpl(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public R<Map<Long, String>> getDeptNamesByIds(List<Long> deptIds) {
		// 直接委托 Feign 调用，如果 deptIds 为空，返回空 Map
		if (deptIds == null || deptIds.isEmpty()) {
			return R.ok(Map.of());
		}
		return remoteDeptService.getDeptNamesByIds(deptIds);
	}

	@Override
	public R<Map<Long, String>> getEmployeeNamesByIds(List<Long> ids) {
		// 1. 参数校验
		if (CollUtil.isEmpty(ids)) {
			return R.ok(Collections.emptyMap());
		}

		try {
			// 2. 查询员工ID和姓名映射（调用自定义 Mapper SQL）
			Map<Long, String> employeeNameMap = baseMapper.getEmployeeNamesByIds(ids);

			// 3. 如果 Mapper 返回 null，兜底空 Map
			if (employeeNameMap == null) {
				employeeNameMap = Collections.emptyMap();
			}

			// 4. 返回标准封装结果
			return R.ok(employeeNameMap);

		} catch (Exception e) {
			log.error("通过员工ID批量查询员工名称失败: {}");
			return R.failed("查询员工名称失败");
		}
	}

	@Override
	public boolean save(OaEmployeesEntity oaEmployees) {
		// 仅保留系统审计字段处理（移除用户校验/默认值）
		preProcessSystemFields(oaEmployees);

		int result = baseMapper.insert(oaEmployees);
		return result > 0;
	}

	/**
	 *  预处理系统字段
	 */
	private void preProcessSystemFields(OaEmployeesEntity entity) {
		// 通用：设置审计字段（如果未设）
		if (StringUtils.isBlank(entity.getCreateBy())) {
			entity.setCreateBy("系统");  // 或从 SecurityContext 获取当前用户
		}
		entity.setCreateTime(LocalDateTime.now());
		entity.setUpdateTime(LocalDateTime.now());
		entity.setDelFlag("0");  // 默认未删除

		// tenant_id: 若多租户，设当前租户
		if (StringUtils.isBlank(entity.getTenantId())) {
			entity.setTenantId("");
		}
		if(ObjectUtils.isEmpty(entity.getEntryDate())){
			throw new ValidateCodeException(MsgUtils.getMessage(OaErrorConstants.ENTRY_DATE_NOT_NULL));
		}
		// 其他系统字段如 userId 等，若需默认，可添加
	}

	// 完善 getAllDepts() 方法
	private List<SysDept> getAllDepts() {
		// 调用远程服务获取部门列表
		R result = remoteDeptService.getDeptList();

		// 检查调用是否成功
		if (result != null && result.getCode() == CommonConstants.SUCCESS) {
			Object data = result.getData();
			if (data instanceof List) {
				// **关键：使用 ObjectMapper 将 List<LinkedHashMap> 转换为 List<SysDept>**
				try {
					return objectMapper.convertValue(data, new TypeReference<List<SysDept>>() {});
				} catch (IllegalArgumentException e) {
					log.error("远程部门列表数据类型转换失败: {}");
					return Collections.emptyList();
				}
			}
		}

		// 如果调用失败或者返回数据为空/类型不正确
		log.error("调用远程部门服务获取部门列表失败或数据为空: {}");
		return Collections.emptyList();
	}

	/**
	 * 导入员工档案
	 * @param excelVOList Excel用户数据对象
	 * @param bindingResult 通用校验结果
	 * @return 导入结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public R importEmployees(List<OaEmployeesVO> excelVOList, BindingResult bindingResult) {
		// 1. 通用校验获取失败的数据
		List<ErrorMessage> errorMessageList = (List<ErrorMessage>) bindingResult.getTarget();

		// 2. 数据准备：获取所有部门列表
		List<SysDept> deptList = getAllDepts();
		if (CollUtil.isEmpty(deptList)) {
			// 如果部门数据都获取不到，则无法进行部门ID校验，直接返回失败（或者根据业务决定是否允许继续）
			return R.failed("无法获取部门数据，请检查部门服务状态或网络连接。");
		}
		Set<Long> existingDeptIds = deptList.stream()
				.map(SysDept::getDeptId)
				.collect(Collectors.toSet());

		// 3. 执行数据插入操作和个性化校验
		// 预先获取所有员工工号、身份证、手机号、邮箱，用于唯一性校验
		List<OaEmployeesEntity> existingEmployees = this.list(
				Wrappers.<OaEmployeesEntity>lambdaQuery()
						.select(OaEmployeesEntity::getEmployeeNo, OaEmployeesEntity::getIdCard,
								OaEmployeesEntity::getMobilePhone, OaEmployeesEntity::getEmail)
		);
		Set<String> existingEmployeeNos = existingEmployees.stream()
				.map(OaEmployeesEntity::getEmployeeNo).collect(Collectors.toSet());
		// 对数据库中为 NULL 的唯一字段进行过滤，以防空指针或错误比对
		Set<String> existingIdCards = existingEmployees.stream()
				.map(OaEmployeesEntity::getIdCard).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
		Set<String> existingMobiles = existingEmployees.stream()
				.map(OaEmployeesEntity::getMobilePhone).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
		Set<String> existingEmails = existingEmployees.stream()
				.map(OaEmployeesEntity::getEmail).filter(StrUtil::isNotBlank).collect(Collectors.toSet());

		for (OaEmployeesVO excel : excelVOList) {
			Set<String> errorMsg = new HashSet<>();

			// 3.1 校验员工工号是否存在（唯一性）
			if (existingEmployeeNos.contains(excel.getEmployeeNo())) {
				errorMsg.add(MsgUtils.getMessage(OaErrorConstants.OA_EMPLOYEES_NO_EXISTING, excel.getEmployeeNo()));
			}

			// 3.2 校验部门ID是否存在
			if (!existingDeptIds.contains(excel.getDepartmentId())) {
				errorMsg.add(MsgUtils.getMessage(OaErrorConstants.SYS_DEPT_DEPTID_INEXISTENCE, excel.getDepartmentId()));
			}

			// 3.3 校验性别是否合法
			if (StrUtil.isNotBlank(excel.getGender()) && !GENDER_SET.contains(excel.getGender())) {
				errorMsg.add(MsgUtils.getMessage(OaErrorConstants.OA_EMPLOYEES_GENDER_ILLEGAL, excel.getGender()));
			}

			// 3.4 校验聘用类型是否合法
			if (StrUtil.isNotBlank(excel.getHireType()) && !HIRE_TYPE_SET.contains(excel.getHireType())) {
				errorMsg.add(MsgUtils.getMessage(OaErrorConstants.OA_EMPLOYEES_HIRE_TYPE_ILLEGAL, excel.getHireType()));
			}

			// 3.5 校验员工类型是否合法
			if (StrUtil.isNotBlank(excel.getEmpType()) && !EMP_TYPE_SET.contains(excel.getEmpType())) {
				errorMsg.add(MsgUtils.getMessage(OaErrorConstants.OA_EMPLOYEES_EMP_TYPE_ILLEGAL, excel.getEmpType()));
			}

			// 3.6 校验身份证号是否唯一（如果数据库字段允许NULL，Excel中可以为空）
			if (StrUtil.isNotBlank(excel.getIdCard())) {
				if (existingIdCards.contains(excel.getIdCard())) {
					errorMsg.add(MsgUtils.getMessage(OaErrorConstants.OA_EMPLOYEES_IDCARD_EXISTING, excel.getIdCard()));
				}
			}

			// 3.7 校验手机号是否唯一
			if (StrUtil.isNotBlank(excel.getMobilePhone())) {
				if (existingMobiles.contains(excel.getMobilePhone())) {
					errorMsg.add(MsgUtils.getMessage(OaErrorConstants.OA_EMPLOYEES_MOBILE_EXISTING, excel.getMobilePhone()));
				}
			}

			// 3.8 校验邮箱是否唯一
			if (StrUtil.isNotBlank(excel.getEmail())) {
				if (existingEmails.contains(excel.getEmail())) {
					errorMsg.add(MsgUtils.getMessage(OaErrorConstants.OA_EMPLOYEES_EMAIL_EXISTING, excel.getEmail()));
				}
			}

			// 4. 数据合法情况
			if (CollUtil.isEmpty(errorMsg)) {
				insertExcelEmployee(excel);
				// 导入成功后，更新唯一性校验集合，防止同一批次内重复数据插入
				existingEmployeeNos.add(excel.getEmployeeNo());
				if (StrUtil.isNotBlank(excel.getIdCard())) existingIdCards.add(excel.getIdCard());
				if (StrUtil.isNotBlank(excel.getMobilePhone())) existingMobiles.add(excel.getMobilePhone());
				if (StrUtil.isNotBlank(excel.getEmail())) existingEmails.add(excel.getEmail());
			} else {
				// 数据不合法情况
				errorMessageList.add(new ErrorMessage(excel.getLineNum(), errorMsg));
			}
		}

		// 5. 返回结果
		if (CollUtil.isNotEmpty(errorMessageList)) {
			return R.failed(errorMessageList);
		}
		return R.ok();
	}

	/**
	 * 插入Excel导入的员工档案信息
	 * @param excel Excel员工数据对象
	 */
	private void insertExcelEmployee(OaEmployeesVO excel) {
		OaEmployeesEntity employeesEntity = new OaEmployeesEntity();

		// 1. VO -> Entity 属性拷贝
		BeanUtils.copyProperties(excel, employeesEntity);

		// 2. 设置默认值和常量
		employeesEntity.setDelFlag(CommonConstants.STATUS_NORMAL); // 0-未删除
		employeesEntity.setEmpStatus("试用"); // 默认人员状态：试用 (根据你的表定义)
		employeesEntity.setBirthdayType("阳历"); // 默认生日类型：阳历 (根据你的表定义)
		employeesEntity.setMaritalStatus("单身"); // 默认婚姻：单身 (根据你的表定义)

		// 注意：OaEmployeesVO 中有 departmentId，这里直接拷贝了，不需要额外处理。
		// OaEmployeesVO 中没有 postId，默认会是 null，符合表定义。

		// 3. 执行插入
		this.save(employeesEntity);
	}
}
