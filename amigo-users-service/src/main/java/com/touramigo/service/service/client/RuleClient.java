package com.touramigo.service.service.client;

import com.touramigo.service.model.RuleViewModel;
import java.util.UUID;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("amigo-commercial-service")
public interface RuleClient {
    @RequestMapping(method = RequestMethod.GET, path = "/rules/{id}")
    RuleViewModel getRuleById(@RequestHeader("Authorization") String authorization, @PathVariable("id") UUID id);

}
