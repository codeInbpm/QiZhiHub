package com.pig4cloud.pig.oa.constant; // 假设的包名，实际请根据项目结构调整

/**
 * OA 模块错误码常量
 * * @author wangben
 * @date 2025-10-13
 */
public interface OaErrorConstants {

	// ==================== 员工档案导入相关错误码 ====================

	/**
	 * 员工工号已存在: 员工工号 %s 已存在
	 */
	String OA_EMPLOYEES_NO_EXISTING = "OA_EMPLOYEES_10001";

	/**
	 * 部门ID不存在: 所属部门ID %s 不存在
	 * 注意：如果SysUser导入的错误码可以直接使用，可以保留SYS_DEPT_DEPTID_INEXISTENCE
	 */
	String SYS_DEPT_DEPTID_INEXISTENCE = "SYS_DEPT_10004"; // 沿用系统部门错误码，假设它存在

	/**
	 * 性别值不合法: 性别 %s 不合法，必须是: 男/女/其他
	 */
	String OA_EMPLOYEES_GENDER_ILLEGAL = "OA_EMPLOYEES_10002";

	/**
	 * 聘用类型不合法: 聘用类型 %s 不合法
	 */
	String OA_EMPLOYEES_HIRE_TYPE_ILLEGAL = "OA_EMPLOYEES_10003";

	/**
	 * 员工类型不合法: 员工类型 %s 不合法
	 */
	String OA_EMPLOYEES_EMP_TYPE_ILLEGAL = "OA_EMPLOYEES_10004";

	/**
	 * 身份证号已存在: 身份证号 %s 已存在
	 */
	String OA_EMPLOYEES_IDCARD_EXISTING = "OA_EMPLOYEES_10005";

	/**
	 * 手机号已存在: 手机号 %s 已存在
	 */
	String OA_EMPLOYEES_MOBILE_EXISTING = "OA_EMPLOYEES_10006";

	/**
	 * 邮箱已存在: 邮箱 %s 已存在
	 */
	String OA_EMPLOYEES_EMAIL_EXISTING = "OA_EMPLOYEES_10007";

	// ==================== 其他可能用到的错误码，这里仅作示例 ====================

	/**
	 * 岗位名称不存在: 岗位名称 %s 不存在
	 */
	String SYS_POST_POSTNAME_INEXISTENCE = "SYS_POST_10003";

	/**
	 * 用户名已存在: 用户名 %s 已存在
	 */
	String SYS_USER_USERNAME_EXISTING = "SYS_USER_10001";

	/**
	 * 入职时间不能为空: 入职时间不能为空
	 */
	String ENTRY_DATE_NOT_NULL = "SYS_USER_10008";
}