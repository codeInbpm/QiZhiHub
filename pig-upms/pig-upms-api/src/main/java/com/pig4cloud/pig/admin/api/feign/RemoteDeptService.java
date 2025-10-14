package com.pig4cloud.pig.admin.api.feign;

import com.pig4cloud.pig.admin.api.dto.UserDTO;
// ... (其他导入)
import com.pig4cloud.pig.admin.api.entity.SysDept;
import com.pig4cloud.pig.common.core.constant.ServiceNameConstants;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(contextId = "remoteDeptService", value = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteDeptService {

	// 保持原有的错误定义 (R<SysDept>) 不变
	@NoToken
	@GetMapping("/dept/list")
	R<SysDept> list();

	// 新增一个通用的方法来获取列表，返回 R<Object> 或 R，用于Service层处理
	// 注意：Feign/Jackson 可能会将列表反序列化为 List<LinkedHashMap>，
	// 在 Service 层需要手动转换为 List<SysDept>。
	@NoToken
	@GetMapping("/dept/list")
	R getDeptList(); // 返回 R，通用类型

	// 如果是获取单个部门，可以使用这个
	 @GetMapping("/dept/{id}")
	 R<SysDept> getById(@PathVariable("id") Long id);
}