package com.pig4cloud.pig.oa.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.pig.oa.entity.OaEmployeeCertificatesEntity;
import com.pig4cloud.pig.oa.service.OaEmployeeCertificatesService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * OA员工证书表
 *
 * @author wangben
 * @date 2025-10-22 14:22:53
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/oaEmployeeCertificates" )
@Tag(description = "oaEmployeeCertificates" , name = "OA员工证书表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class OaEmployeeCertificatesController {

    private final  OaEmployeeCertificatesService oaEmployeeCertificatesService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param oaEmployeeCertificates OA员工证书表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("oa_oaEmployeeCertificates_view")
    public R getOaEmployeeCertificatesPage(@ParameterObject Page page, @ParameterObject OaEmployeeCertificatesEntity oaEmployeeCertificates) {
        LambdaQueryWrapper<OaEmployeeCertificatesEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(Objects.nonNull(oaEmployeeCertificates.getEmployeeId()),OaEmployeeCertificatesEntity::getEmployeeId,oaEmployeeCertificates.getEmployeeId());
		wrapper.like(StrUtil.isNotBlank(oaEmployeeCertificates.getCertificateName()),OaEmployeeCertificatesEntity::getCertificateName,oaEmployeeCertificates.getCertificateName());
		wrapper.like(StrUtil.isNotBlank(oaEmployeeCertificates.getCertificateNumber()),OaEmployeeCertificatesEntity::getCertificateNumber,oaEmployeeCertificates.getCertificateNumber());
        return R.ok(oaEmployeeCertificatesService.page(page, wrapper));
    }


    /**
     * 通过条件查询OA员工证书表
     * @param oaEmployeeCertificates 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("oa_oaEmployeeCertificates_view")
    public R getDetails(@ParameterObject OaEmployeeCertificatesEntity oaEmployeeCertificates) {
        return R.ok(oaEmployeeCertificatesService.list(Wrappers.query(oaEmployeeCertificates)));
    }

    /**
     * 新增OA员工证书表
     * @param oaEmployeeCertificates OA员工证书表
     * @return R
     */
    @Operation(summary = "新增OA员工证书表" , description = "新增OA员工证书表" )
    @SysLog("新增OA员工证书表" )
    @PostMapping
    @HasPermission("oa_oaEmployeeCertificates_add")
    public R save(@RequestBody OaEmployeeCertificatesEntity oaEmployeeCertificates) {
        return R.ok(oaEmployeeCertificatesService.save(oaEmployeeCertificates));
    }

    /**
     * 修改OA员工证书表
     * @param oaEmployeeCertificates OA员工证书表
     * @return R
     */
    @Operation(summary = "修改OA员工证书表" , description = "修改OA员工证书表" )
    @SysLog("修改OA员工证书表" )
    @PutMapping
    @HasPermission("oa_oaEmployeeCertificates_edit")
    public R updateById(@RequestBody OaEmployeeCertificatesEntity oaEmployeeCertificates) {
        return R.ok(oaEmployeeCertificatesService.updateById(oaEmployeeCertificates));
    }

    /**
     * 通过id删除OA员工证书表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除OA员工证书表" , description = "通过id删除OA员工证书表" )
    @SysLog("通过id删除OA员工证书表" )
    @DeleteMapping
    @HasPermission("oa_oaEmployeeCertificates_del")
    public R removeById(@RequestBody Integer[] ids) {
        return R.ok(oaEmployeeCertificatesService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param oaEmployeeCertificates 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("oa_oaEmployeeCertificates_export")
    public List<OaEmployeeCertificatesEntity> exportExcel(OaEmployeeCertificatesEntity oaEmployeeCertificates,Integer[] ids) {
        return oaEmployeeCertificatesService.list(Wrappers.lambdaQuery(oaEmployeeCertificates).in(ArrayUtil.isNotEmpty(ids), OaEmployeeCertificatesEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param oaEmployeeCertificatesList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("oa_oaEmployeeCertificates_export")
    public R importExcel(@RequestExcel List<OaEmployeeCertificatesEntity> oaEmployeeCertificatesList, BindingResult bindingResult) {
        return R.ok(oaEmployeeCertificatesService.saveBatch(oaEmployeeCertificatesList));
    }
}
