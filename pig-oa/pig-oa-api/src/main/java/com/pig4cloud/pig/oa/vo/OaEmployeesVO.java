package com.pig4cloud.pig.oa.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.pig4cloud.plugin.excel.annotation.ExcelLine;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 员工档案导入 VO
 *
 * @author wangben
 * @date 2025-10-13
 */
@Data
@ColumnWidth(25)
public class OaEmployeesVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 导入时回显行号 */
	@ExcelLine
	@ExcelIgnore
	private Long lineNum;

	/** 员工工号 */
	@NotBlank(message = "员工工号不能为空")
	@ExcelProperty("员工工号")
	private String employeeNo;

	/** 员工姓名 */
	@NotBlank(message = "员工姓名不能为空")
	@ExcelProperty("员工姓名")
	private String enpName;

	/** 性别（男/女/其他） */
	@ExcelProperty("性别")
	private String gender;

	/** 所属部门ID（可通过名称匹配） */
	@NotBlank(message = "所属部门不能为空")
	@ExcelProperty("所属部门ID")
	private Long departmentId;

	/** 职位 */
	@NotBlank(message = "职位不能为空")
	@ExcelProperty("职位")
	private String position;

	/** 聘用类型（全职/兼职/远程等） */
	@NotBlank(message = "聘用类型不能为空")
	@ExcelProperty("聘用类型")
	private String hireType;

	/** 员工类型（正式/合同/实习等） */
	@ExcelProperty("员工类型")
	private String empType;

	/** 入职日期 */
	@NotBlank(message = "入职日期不能为空")
	@ExcelProperty("入职日期")
	private LocalDate entryDate;

	/** 试用期结束日期 */
	@ExcelProperty("试用期到")
	private LocalDate probationEndDate;

	/** 转正日期 */
	@ExcelProperty("转正日期")
	private LocalDate regularizationDate;

	/** 学历 */
	@ExcelProperty("学历")
	private String education;

	/** 民族 */
	@ExcelProperty("民族")
	private String ethnicity;

	/** 籍贯 */
	@ExcelProperty("籍贯")
	private String nativePlace;

	/** 婚姻状况（已婚/单身/离异等） */
	@ExcelProperty("婚姻状况")
	private String maritalStatus;

	/** 身份证号 */
	@ExcelProperty("身份证号")
	private String idCard;

	/** 手机号 */
	@ExcelProperty("手机号")
	private String mobilePhone;

	/** 邮箱 */
	@ExcelProperty("邮箱")
	private String email;

	/** 紧急联系人 */
	@ExcelProperty("紧急联系人")
	private String emergencyContact;

	/** 紧急联系人电话 */
	@ExcelProperty("紧急联系人电话")
	private String emergencyPhone;

	/** 开户行 */
	@ExcelProperty("开户行")
	private String bankName;

	/** 工资卡账号 */
	@ExcelProperty("工资卡账号")
	private String bankAccount;

	/** 备注 */
	@ExcelProperty("备注")
	private String remarks;
}