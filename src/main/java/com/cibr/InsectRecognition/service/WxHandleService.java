package com.cibr.InsectRecognition.service;

import com.alibaba.fastjson.JSONObject;
import com.cibr.InsectRecognition.dao.CibrBussIdentifyMapper;
import com.cibr.InsectRecognition.dao.CibrSysAnimalMapper;
import com.cibr.InsectRecognition.dao.CibrSysFileMapper;
import com.cibr.InsectRecognition.entity.CibrBussIdentify;
import com.cibr.InsectRecognition.entity.CibrSysAnimal;
import com.cibr.InsectRecognition.entity.CibrSysFile;
import com.cibr.InsectRecognition.util.CibrUtil;
import com.cibr.InsectRecognition.util.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WxHandleService {

    private Logger logger = LoggerFactory.getLogger(WxHandleService.class);

    @Value("${wx.appId}")
    private String appId;

    @Value("${wx.appSecret}")
    private String appSecret;

    @Value("${insect.image.path}")
    private String filePath;

    @Value("${insect.recognition}")
    private String recognitionUrl;

    @Autowired
    private CibrBussIdentifyMapper bussIdentifyMapper;

    @Autowired
    private CibrSysAnimalMapper animalMapper;

    @Autowired
    private CibrSysFileMapper fileMapper;


    public String getOpenId(String code) {
        String openIdUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + appSecret
                + "&js_code=" + code + "&grant_type=authorization_code";
        String get = httpRequest(openIdUrl, "GET", null);
        JSONObject jsonObject = JSONObject.parseObject(get);
        String openid = jsonObject.get("openid").toString();
        return openid;
    }

    public static String httpRequest(String requestUrl,String requestMethod,String output){
        try{
            URL url = new URL(requestUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            if(null != output){
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(output.getBytes("utf-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null){
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            connection.disconnect();
            return buffer.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public CibrSysAnimal identify(String location, String openId, MultipartFile file) throws IOException {
        BASE64Encoder encoder = new BASE64Encoder();
        String encode = encoder.encode(file.getBytes());
        Map<String, String> postData = new HashMap<>();
        postData.put("img", encode);
        String result = HttpRequestUtils.doPost(recognitionUrl, postData);
        logger.info("识别结果：" + result);
        String curstatus = "02";
        Map retMap = JSONObject.parseObject(result, Map.class);
        String category = String.valueOf(retMap.get("category"));
        String animalId = null;
        String imgDir = "noFind";
        CibrSysAnimal ani = animalMapper.selectByPrimaryKey(category);
        if (ani != null){
            animalId = ani.getId();
            imgDir = animalId;
            curstatus = "01";
        }

        Map map = JSONObject.parseObject(location, Map.class);
        String savePath = filePath + imgDir + File.separator + CibrUtil.getUUID() + file.getOriginalFilename();
        File saveFile = new File(savePath);
        File path = new File(filePath + imgDir + File.separator);

        if (!path.exists()){
            logger.info("mkdir path");
            path.mkdirs();
        }

        logger.info("转存文件 start");
        saveFile.createNewFile();
        file.transferTo(saveFile);
        logger.info("转存文件 end");

        CibrSysFile dbFile = new CibrSysFile();
        dbFile.setId(CibrUtil.getUUID());
        dbFile.setRealname(file.getOriginalFilename());
        dbFile.setName(saveFile.getName());
        dbFile.setPath(savePath);
        dbFile.setCreatetime(new Date());

        CibrBussIdentify bussIdentify = new CibrBussIdentify();
        bussIdentify.setId(CibrUtil.getUUID());
        bussIdentify.setAnimalid(animalId);
        bussIdentify.setCreatetime(new Date());
        bussIdentify.setFilepath(saveFile.getAbsolutePath());
        bussIdentify.setVadress((String) map.get("address"));
        bussIdentify.setLatitude(CibrUtil.objectNumberToString(map.get("latitude")));
        bussIdentify.setAltitude(CibrUtil.objectNumberToString(map.get("altitude")));
        bussIdentify.setLongitude(CibrUtil.objectNumberToString(map.get("longitude")));
        bussIdentify.setOpenid(openId);
        bussIdentify.setRecstatus(curstatus);
        bussIdentify.setFileid(dbFile.getId());
        bussIdentifyMapper.insert(bussIdentify);

        dbFile.setDetailid(bussIdentify.getId());
        fileMapper.insert(dbFile);

        return ani;
    }
}
































