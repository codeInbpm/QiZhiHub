package com.pig4cloud.pig.oa.feign;

import com.pig4cloud.pig.common.core.constant.ServiceNameConstants;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.feign.annotation.NoToken;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteEmployeeService", value = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteEmployeeService {

	@NoToken
	@GetMapping("/oaEmployees/remote/names/{ids}")
	R<Map<Long, String>> getEmployeeNamesByIds(@RequestParam("ids") List<Long> ids);

}
