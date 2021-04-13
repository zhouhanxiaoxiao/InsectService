package com.cibr.InsectRecognition.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cibr.InsectRecognition.entity.CibrSysAnimal;
import com.cibr.InsectRecognition.service.WxHandleService;
import com.cibr.InsectRecognition.util.CibrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WxHandleController {

    private Logger logger = LoggerFactory.getLogger(WxHandleController.class);

    @Autowired
    private WxHandleService wxHandleService;

    @RequestMapping("/imageUpload")
    public String upload(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        Map<String,Object> ret = new HashMap<>();
        String location = request.getParameter("location");
        String openId = request.getParameter("openId");
        System.out.println("执行upload");
        request.setCharacterEncoding("UTF-8");
        logger.info("执行图片上传");
        CibrSysAnimal animal = wxHandleService.identify(location, openId, file);
        ret.put("animal", animal);
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/getOpenId")
    public String getOpenId(HttpServletRequest request){
        Map<String,String> retMap = new HashMap<>();
        String code = request.getParameter("code");
        String openId = wxHandleService.getOpenId(code);
        retMap.put("openId", openId);
        return JSON.toJSONString(retMap, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }
}
