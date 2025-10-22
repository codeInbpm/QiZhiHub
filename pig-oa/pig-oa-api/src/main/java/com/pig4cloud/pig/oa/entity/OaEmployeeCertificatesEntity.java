package com.pig4cloud.pig.oa.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * OA员工证书表
 *
 * @author wangben
 * @date 2025-10-22 14:22:53
 */
@Data
@TableName("oa_employee_certificates")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "OA员工证书表")
public class OaEmployeeCertificatesEntity extends Model<OaEmployeeCertificatesEntity> {


	/**
	* 证书主键，自增ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="证书主键，自增ID")
    private Integer id;

	/**
	* 关联员工ID
	*/
    @Schema(description="关联员工ID")
    private Integer employeeId;

	/**
	* 证书名称（如“身份证”、“学历证书”）
	*/
    @Schema(description="证书名称（如“身份证”、“学历证书”）")
    private String certificateName;

	/**
	* 证书编号（如“ID-2025001”）
	*/
    @Schema(description="证书编号（如“ID-2025001”）")
    private String certificateNumber;

	/**
	* 证书图片路径
	*/
    @Schema(description="证书图片路径")
    private String certImage;

	/**
	* 证书文件路径
	*/
    @Schema(description="证书文件路径")
    private String filePath;

	/**
	* 颁发日期
	*/
    @Schema(description="颁发日期")
    private LocalDate issueDate;

	/**
	* 有效期（NULL表示无期限）
	*/
    @Schema(description="有效期（NULL表示无期限）")
    private LocalDate expiryDate;

	/**
	* 颁发机构（如“教育部”）
	*/
    @Schema(description="颁发机构（如“教育部”）")
    private String issuer;

	/**
	* 证书状态（valid=有效, expired=过期, invalid=无效）
	*/
    @Schema(description="证书状态（valid=有效, expired=过期, invalid=无效）")
    private String status;

	/**
	* 备注（如“复印件”）
	*/
    @Schema(description="备注（如“复印件”）")
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
	* 删除标记（0未删，1已删）
	*/
    @TableLogic
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="删除标记（0未删，1已删）")
    private String delFlag;

	/**
	* 租户ID
	*/
    @Schema(description="租户ID")
    private String tenantId;
}
