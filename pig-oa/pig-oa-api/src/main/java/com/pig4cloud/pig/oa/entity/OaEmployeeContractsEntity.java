package com.pig4cloud.pig.oa.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * OA员工合同表
 *
 * @author wangben
 * @date 2025-10-22 09:51:33
 */
@Data
@TableName("oa_employee_contracts")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "OA员工合同表")
public class OaEmployeeContractsEntity extends Model<OaEmployeeContractsEntity> {


	/**
	* 合同主键，自增ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="合同主键，自增ID")
    private Integer id;

	/**
	* 关联员工ID
	*/
    @Schema(description="关联员工ID")
    private Integer employeeId;

	/**
	* 合同编号（如“HT-2021-001”），全局唯一
	*/
    @Schema(description="合同编号（如“HT-2021-001”），全局唯一")
    private String contractNumber;

	/**
	* 合同名称（如“正式劳动合同”）
	*/
    @Schema(description="合同名称（如“正式劳动合同”）")
    private String contractName;

	/**
	* 合同类型（labor=劳动, trial=试用, part_time=兼职, other=其他）
	*/
    @Schema(description="合同类型（labor=劳动, trial=试用, part_time=兼职, other=其他）")
    private String contractType;

	/**
	* 签署单位（如“信呼开发团队”）
	*/
    @Schema(description="签署单位（如“信呼开发团队”）")
    private String signingUnit;

	/**
	* 签订日期（实际签字日）
	*/
    @Schema(description="签订日期（实际签字日）")
    private LocalDate signingDate;

	/**
	* 开始日期
	*/
    @Schema(description="开始日期")
    private LocalDate startDate;

	/**
	* 截止日期
	*/
    @Schema(description="截止日期")
    private LocalDate endDate;

	/**
	* 试用期开始日期
	*/
    @Schema(description="试用期开始日期")
    private LocalDate trialStartDate;

	/**
	* 试用期结束日期
	*/
    @Schema(description="试用期结束日期")
    private LocalDate trialEndDate;

	/**
	* 提前终止日期
	*/
    @Schema(description="提前终止日期")
    private LocalDate earlyTerminationDate;

	/**
	* 状态（pending=待生效, active=有效, expired=已过期, terminated=终止）
	*/
    @Schema(description="状态（pending=待生效, active=有效, expired=已过期, terminated=终止）")
    private String contractStatus;

	/**
	* 合同文件路径
	*/
    @Schema(description="合同文件路径")
    private String filePath;

	/**
	* 甲方（公司）代表人
	*/
    @Schema(description="甲方（公司）代表人")
    private String partyASigner;

	/**
	* 续签次数
	*/
    @Schema(description="续签次数")
    private Integer renewalCount;

	/**
	* 部门
	*/
    @Schema(description="部门")
    private String department;

	/**
	* 说明/备注
	*/
    @Schema(description="说明/备注")
    private String remarks;

	/**
	* 创建人
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建人")
    private String createBy;

	/**
	* 创建时间
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建时间")
    private LocalDateTime createTime;

	/**
	* 修改人
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="修改人")
    private String updateBy;

	/**
	* 更新时间
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="更新时间")
    private LocalDateTime updateTime;

	/**
	* 删除标记
	*/
    @TableLogic
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="删除标记")
    private String delFlag;

	/**
	* 租户ID
	*/
    @Schema(description="租户ID")
    private String tenantId;
}
