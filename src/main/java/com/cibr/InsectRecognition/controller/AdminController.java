package com.cibr.InsectRecognition.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cibr.InsectRecognition.entity.*;
import com.cibr.InsectRecognition.service.AdminService;
import com.cibr.InsectRecognition.util.CibrUtil;
import com.cibr.InsectRecognition.util.RedisUtil;
import com.cibr.InsectRecognition.util.RetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request,
                        HttpServletResponse response,
                        @RequestBody Map requestBody){
        ReturnData ret = new ReturnData();
        try {
            Map<String,Object> retMap = new HashMap<>();
            String userStr = (String) requestBody.get("user");
            CibrSysUser loginUser = JSONObject.parseObject(userStr, CibrSysUser.class);
            String result = adminService.login(loginUser);
            if (result.equals(RetUtil.SUCCESS)){
                String token = CibrUtil.getUUID();
                CibrSysUser user = adminService.getUserByEmail(loginUser.getEmail());
                redisUtil.set(token, user);
                retMap.put("token", token);
                retMap.put("user", user);
            }
            ret.setCode(result);
            ret.setRetMap(retMap);
        }catch (Exception e){
            ret.setCode("err.systemErr");
            ret.setErrMsg("系统异常！");
            e.printStackTrace();
        }
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/admin/user/initPage")
    public String userInit(HttpServletRequest request,
                        HttpServletResponse response){
        ReturnData ret = new ReturnData();
        try {
            Map<String,Object> retMap = new HashMap<>();
            List<CibrSysUser> users = adminService.userInit();
            retMap.put("users", users);
            ret.setRetMap(retMap);
            ret.setCode(RetUtil.SUCCESS);
        }catch (Exception e){
            ret.setCode("err.systemErr");
            ret.setErrMsg("系统异常！");
            e.printStackTrace();
        }
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/admin/user/update")
    public String userUpdate(HttpServletRequest request,
                           HttpServletResponse response,
                             @RequestBody Map requestBody
                             ){
        ReturnData ret = new ReturnData();
        try {
            Map<String,Object> retMap = new HashMap<>();
            String userStr = (String) requestBody.get("user");
            CibrSysUser user = JSONObject.parseObject(userStr, CibrSysUser.class);
            String result = adminService.userUpdate(user);
            ret.setRetMap(retMap);
            ret.setCode(result);
        }catch (Exception e){
            ret.setCode("err.systemErr");
            ret.setErrMsg("系统异常！");
            e.printStackTrace();
        }
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/admin/user/addNew")
    public String userAddNew(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestBody Map requestBody
    ){
        ReturnData ret = new ReturnData();
        try {
            Map<String,Object> retMap = new HashMap<>();
            String userStr = (String) requestBody.get("user");
            CibrSysUser user = JSONObject.parseObject(userStr, CibrSysUser.class);
            String result = adminService.userAddNew(user);
            ret.setRetMap(retMap);
            ret.setCode(result);
        }catch (Exception e){
            ret.setCode("err.systemErr");
            ret.setErrMsg("系统异常！");
            e.printStackTrace();
        }
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/admin/type/new")
    public String insectTypeNew(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestBody Map requestBody
    ){
        ReturnData ret = new ReturnData();
        try {
            Map<String,Object> retMap = new HashMap<>();
            String type = (String) requestBody.get("type");
            CibrSysType cibrSysType = JSONObject.parseObject(type, CibrSysType.class);
            String result = adminService.insectTypeNew(cibrSysType);
            ret.setRetMap(retMap);
            ret.setCode(result);
        }catch (Exception e){
            ret.setCode("err.systemErr");
            ret.setErrMsg("系统异常！");
            e.printStackTrace();
        }
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/admin/insect/addNew")
    public String insectAddNew(HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestBody Map requestBody
    ){
        ReturnData ret = new ReturnData();
        try {
            Map<String,Object> retMap = new HashMap<>();
            String inesctStr = (String) requestBody.get("inesct");
            String fileIdsStr = (String) requestBody.get("fileIds");
            String alertFlag = (String) requestBody.get("alertFlag");

            CibrSysAnimal insects = JSONObject.parseObject(inesctStr, CibrSysAnimal.class);
            List<String> fileIds = JSONObject.parseArray(fileIdsStr, String.class);

            String result = adminService.insectAddNew(insects,fileIds, alertFlag);
            ret.setRetMap(retMap);
            ret.setCode(result);
        }catch (Exception e){
            ret.setCode("err.systemErr");
            ret.setErrMsg("系统异常！");
            e.printStackTrace();
        }
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/admin/buss/update")
    public String bussUpdate(HttpServletRequest request,
                               HttpServletResponse response,
                               @RequestBody Map requestBody
    ){
        ReturnData ret = new ReturnData();
        try {
            Map<String,Object> retMap = new HashMap<>();
            String idt = (String) requestBody.get("idt");
            String status = (String) requestBody.get("status");
            CibrBussIdentify bussIdentify = JSONObject.parseObject(idt, CibrBussIdentify.class);
            String result = adminService.bussUpdate(bussIdentify,status);
            ret.setRetMap(retMap);
            ret.setCode(result);
        }catch (Exception e){
            ret.setCode("err.systemErr");
            ret.setErrMsg("系统异常！");
            e.printStackTrace();
        }
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/admin/insect/showImages")
    public String showImages(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestBody Map requestBody
    ){
        ReturnData ret = new ReturnData();
        try {
            Map<String,Object> retMap = new HashMap<>();
            String detailId = (String) requestBody.get("detailId");
            List<CibrSysFile> images = adminService.findImages(detailId);
            if (images.size() == 0){
                ret.setCode(RetUtil.NO_IMAGES);
            }else {
                retMap.put("images",images);
                ret.setCode(RetUtil.SUCCESS);
            }
            ret.setRetMap(retMap);
        }catch (Exception e){
            ret.setCode("err.systemErr");
            ret.setErrMsg("系统异常！");
            e.printStackTrace();
        }
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/admin/animal/init")
    public String getBussByAnimalId(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestBody Map requestBody
    ){
        ReturnData ret = new ReturnData();
        try {
            Map<String,Object> retMap = new HashMap<>();
            String animalId = (String) requestBody.get("animalId");
            List<CibrBussIdentify> buss = adminService.getBussByAnimalId(animalId);
            List<CibrSysFile> images = adminService.findImages(animalId);
            retMap.put("buss",buss);
            retMap.put("images",images);
            ret.setRetMap(retMap);
            ret.setCode(RetUtil.SUCCESS);
        }catch (Exception e){
            ret.setCode("err.systemErr");
            ret.setErrMsg("系统异常！");
            e.printStackTrace();
        }
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/admin/insect/typeInitPage")
    public String typeInitPage(HttpServletRequest request,
                                HttpServletResponse response
    ){
        ReturnData ret = new ReturnData();
        try {
            Map<String,Object> retMap = new HashMap<>();
            List<CibrSysType> types = adminService.typeInitPage();
            retMap.put("types", types);
            ret.setRetMap(retMap);
            ret.setCode(RetUtil.SUCCESS);
        }catch (Exception e){
            ret.setCode("err.systemErr");
            ret.setErrMsg("系统异常！");
            e.printStackTrace();
        }
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/admin/insect/initPage")
    public String insectInitPage(HttpServletRequest request,
                               HttpServletResponse response
    ){
        ReturnData ret = new ReturnData();
        try {
            Map<String,Object> retMap = new HashMap<>();
            List<CibrSysType> types = adminService.typeInitPage();
            List<CibrSysAnimal> animals = adminService.insectInitPage();
            retMap.put("types", types);
            retMap.put("insects", animals);
            ret.setRetMap(retMap);
            ret.setCode(RetUtil.SUCCESS);
        }catch (Exception e){
            ret.setCode("err.systemErr");
            ret.setErrMsg("系统异常！");
            e.printStackTrace();
        }
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/admin/insect/noFinds/init")
    public String noFindInit(HttpServletRequest request,
                                 HttpServletResponse response
    ){
        ReturnData ret = new ReturnData();
        try {
            Map<String,Object> retMap = new HashMap<>();
            List<CibrBussIdentify> cibrBussIdentifies = adminService.noFindInit();
            List<CibrSysAnimal> allAnimal = adminService.findAllAnimal();
            retMap.put("noFinds", cibrBussIdentifies);
            retMap.put("allAnimal", allAnimal);
            ret.setRetMap(retMap);
            ret.setCode(RetUtil.SUCCESS);
        }catch (Exception e){
            ret.setCode("err.systemErr");
            ret.setErrMsg("系统异常！");
            e.printStackTrace();
        }
        return JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/file/upload")
    public String upload(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        System.out.println("执行upload");
        request.setCharacterEncoding("UTF-8");
        String s = adminService.saveFile(file);
        Map<String,String> retMap = new HashMap<>();
        retMap.put("id", s);
        return JSON.toJSONString(retMap, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping(value = "/file/image/{fileId}")
    public void userHead(HttpServletRequest request,
                         HttpServletResponse response,
                         @PathVariable String fileId) throws IOException {
        File userHead = adminService.getImage(fileId);
        FileInputStream fileInputStream = new FileInputStream(userHead);
        ServletOutputStream outputStream = response.getOutputStream();
        //创建存放文件内容的数组
        byte[] buff = new byte[1024];
        //所读取的内容使用n来接收
        int n;
        //当没有读取完时,继续读取,循环
        while ((n = fileInputStream.read(buff)) != -1) {
            //将字节数组的数据全部写入到输出流中
            outputStream.write(buff, 0, n);
        }
        outputStream.flush();
        outputStream.close();
        fileInputStream.close();
    }
}
