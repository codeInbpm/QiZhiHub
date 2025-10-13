package com.pig4cloud.pig.oa.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * OA员工档案主表
 *
 * @author wangben
 * @date 2025-10-12 21:59:38
 */
@Data
@TableName("oa_employees")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "OA员工档案主表")
public class OaEmployeesEntity extends Model<OaEmployeesEntity> {


	/**
	* 员工ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="员工ID")
    private Long id;

	/**
	* 关联系统用户ID（sys_user.user_id）
	*/
    @Schema(description="关联系统用户ID（sys_user.user_id）")
    private Long userId;

	/**
	* 员工工号
	*/
    @Schema(description="员工工号")
    private String employeeNo;

	/**
	* 姓名
	*/
    @Schema(description="姓名")
    private String enpName;

	/**
	* 用户头像
	*/
    @Schema(description="用户头像")
    private String avatar;

	/**
	* 性别（男/女/其他）
	*/
    @Schema(description="性别（男/女/其他）")
    private String gender;

	/**
	* 所属部门ID（sys_dept.dept_id）
	*/
    @Schema(description="所属部门ID（sys_dept.dept_id）")
    private Long departmentId;

	/**
	* 职位
	*/
    @Schema(description="职位")
    private String position;

	/**
	* 岗位ID（sys_post.post_id，如果有岗位表）
	*/
    @Schema(description="岗位ID（sys_post.post_id，如果有岗位表）")
    private Long postId;

	/**
	* 人员状态
	*/
    @Schema(description="人员状态")
    private String empStatus;

	/**
	* 密码过期时间
	*/
    @Schema(description="密码过期时间")
    private LocalDate passwordExpireDate;

	/**
	* 最近登录时间（从sys_user同步）
	*/
    @Schema(description="最近登录时间（从sys_user同步）")
    private LocalDateTime lastLoginTime;

	/**
	* 聘用类型（全职/兼职/远程等）
	*/
    @Schema(description="聘用类型（全职/兼职/远程等）")
    private String hireType;

	/**
	* 员工类型（正式/合同/实习等）
	*/
    @Schema(description="员工类型（正式/合同/实习等）")
    private String empType;

	/**
	* 入职日期
	*/
    @Schema(description="入职日期")
    private LocalDate entryDate;

	/**
	* 试用期到
	*/
    @Schema(description="试用期到")
    private LocalDate probationEndDate;

	/**
	* 转正日期
	*/
    @Schema(description="转正日期")
    private LocalDate regularizationDate;

	/**
	* 离职日期
	*/
    @Schema(description="离职日期")
    private LocalDate exitDate;

	/**
	* 学历
	*/
    @Schema(description="学历")
    private String education;

	/**
	* 民族
	*/
    @Schema(description="民族")
    private String ethnicity;

	/**
	* 生日类型（阳历/阴历）
	*/
    @Schema(description="生日类型（阳历/阴历）")
    private String birthdayType;

	/**
	* 生日
	*/
    @Schema(description="生日")
    private LocalDate birthday;

	/**
	* 籍贯
	*/
    @Schema(description="籍贯")
    private String nativePlace;

	/**
	* 婚姻（已婚/单身/离异等）
	*/
    @Schema(description="婚姻（已婚/单身/离异等）")
    private String maritalStatus;

	/**
	* 身份证号
	*/
    @Schema(description="身份证号")
    private String idCard;

	/**
	* 现住址
	*/
    @Schema(description="现住址")
    private String currentAddress;

	/**
	* 家庭住址
	*/
    @Schema(description="家庭住址")
    private String familyAddress;

	/**
	* 电话
	*/
    @Schema(description="电话")
    private String landlinePhone;

	/**
	* 手机号
	*/
    @Schema(description="手机号")
    private String mobilePhone;

	/**
	* 邮箱
	*/
    @Schema(description="邮箱")
    private String email;

	/**
	* 备用联系人
	*/
    @Schema(description="备用联系人")
    private String emergencyContact;

	/**
	* 备用联系人电话
	*/
    @Schema(description="备用联系人电话")
    private String emergencyPhone;

	/**
	* 开户行
	*/
    @Schema(description="开户行")
    private String bankName;

	/**
	* 工资卡账号
	*/
    @Schema(description="工资卡账号")
    private String bankAccount;

	/**
	* 备注
	*/
    @Schema(description="备注")
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
	* 删除标记，0未删除，1已删除
	*/
    @TableLogic
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="删除标记，0未删除，1已删除")
    private String delFlag;

	/**
	* 租户字段
	*/
    @Schema(description="租户字段")
    private String tenantId;
}
