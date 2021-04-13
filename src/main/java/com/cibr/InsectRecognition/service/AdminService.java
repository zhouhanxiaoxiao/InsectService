package com.cibr.InsectRecognition.service;

import com.cibr.InsectRecognition.dao.*;
import com.cibr.InsectRecognition.entity.*;
import com.cibr.InsectRecognition.util.CibrUtil;
import com.cibr.InsectRecognition.util.RetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private CibrSysUserMapper userMapper;

    @Autowired
    private CibrSysTypeMapper typeMapper;

    @Value("${insect.image.path}")
    private String filePath;

    @Autowired
    private CibrSysAnimalMapper animalMapper;

    @Autowired
    private CibrSysFileMapper fileMapper;

    @Autowired
    private CibrBussIdentifyMapper identifyMapper;

    public String login(CibrSysUser loginUser) {
        CibrSysUserExample userExample = new CibrSysUserExample();
        userExample.createCriteria().andEmailEqualTo(loginUser.getEmail());
        List<CibrSysUser> dbUsers = userMapper.selectByExample(userExample);
        if (dbUsers == null || dbUsers.size() == 0){
            return RetUtil.NO_USER;
        }
        if (!loginUser.getPassword().equals(dbUsers.get(0).getPassword())){
            return RetUtil.PASSWORD_ERR;
        }
        return RetUtil.SUCCESS;
    }

    public CibrSysUser getUserByEmail(String email){
        CibrSysUserExample userExample = new CibrSysUserExample();
        userExample.createCriteria().andEmailEqualTo(email);
        List<CibrSysUser> dbUsers = userMapper.selectByExample(userExample);
        return dbUsers.get(0);
    }

    public List<CibrSysUser> userInit() {
        List<CibrSysUser> users = userMapper.selectByExample(new CibrSysUserExample());
        return users;
    }

    public String userUpdate(CibrSysUser user) {
        userMapper.updateByPrimaryKey(user);
        return RetUtil.SUCCESS;
    }

    public String userAddNew(CibrSysUser user) {

        CibrSysUserExample userExample = new CibrSysUserExample();
        userExample.createCriteria().andEmailEqualTo(user.getEmail());
        List<CibrSysUser> users = userMapper.selectByExample(userExample);
        if (users != null && users.size() > 0){
            return RetUtil.USER_EXIST;
        }
        user.setId(CibrUtil.getUUID());
        user.setUserstatus("01");
        userMapper.insert(user);
        return RetUtil.SUCCESS;
    }

    public String insectTypeNew(CibrSysType type) {
        CibrSysTypeExample typeExample = new CibrSysTypeExample();
        typeExample.createCriteria().andVdomainEqualTo(type.getVdomain())
                .andVkingdomEqualTo(type.getVkingdom())
                .andVphylumEqualTo(type.getVphylum())
                .andVclassEqualTo(type.getVclass())
                .andVorderEqualTo(type.getVorder())
                .andVfamilyEqualTo(type.getVfamily())
                .andVgenusEqualTo(type.getVgenus())
                .andVspeciesEqualTo(type.getVspecies());
        List<CibrSysType> types = typeMapper.selectByExample(typeExample);
        if (types != null && types.size() > 0){
            return RetUtil.INSECT_TYPE_EXIST;
        }
        type.setId(CibrUtil.getUUID());
        typeMapper.insert(type);
        return RetUtil.SUCCESS;
    }

    public List<CibrSysType> typeInitPage() {
        return typeMapper.selectByExample(new CibrSysTypeExample());
    }

    public List<CibrSysAnimal> insectInitPage() {
        return animalMapper.selectByExample(new CibrSysAnimalExample());
    }


    public String saveFile(MultipartFile file) throws Exception {
        String realPath = filePath + "default" + File.separator + CibrUtil.getUUID() +file.getOriginalFilename();
        File saveFile = new File(realPath);
        File path = new File(filePath + "default" + File.separator);
        if (!path.exists()){
            path.mkdirs();
        }
        saveFile.createNewFile();
        file.transferTo(saveFile);
        CibrSysFile img = new CibrSysFile();
        img.setId(CibrUtil.getUUID());
        img.setCreatetime(new Date());
        img.setName(saveFile.getName());
        img.setPath(realPath);
        img.setRealname(file.getOriginalFilename());
        fileMapper.insert(img);
        return img.getId();
    }

    public File getImage(String fileId) {
        CibrSysFile cibrSysFile = fileMapper.selectByPrimaryKey(fileId);
        File file = new File(cibrSysFile.getPath());
        return file;
    }


    public String insectAddNew(CibrSysAnimal insects, List<String> fileIds,String alertFlag) {
        if ("edit".equals(alertFlag)){
            animalMapper.updateByPrimaryKey(insects);
        }else {
            insects.setId(CibrUtil.getUUID());
            insects.setCreatetime(new Date());
            animalMapper.insert(insects);
        }
        if (fileIds != null && fileIds.size() > 0){
            CibrSysFileExample fileExample = new CibrSysFileExample();
            fileExample.createCriteria().andIdIn(fileIds);
            List<CibrSysFile> files = fileMapper.selectByExample(fileExample);
            if (files != null && files.size() > 0){
                files.forEach(item ->{
                    if (item != null){
                        item.setDetailid(insects.getId());
                        fileMapper.updateByPrimaryKey(item);
                    }
                });
            }
        }
        return  RetUtil.SUCCESS;
    }

    public List<CibrBussIdentify> noFindInit() {
        CibrBussIdentifyExample identifyExample = new CibrBussIdentifyExample();
        identifyExample.createCriteria().andRecstatusEqualTo("02").andAnimalidIsNull();
        List<CibrBussIdentify> cibrBussIdentifies = identifyMapper.selectByExample(identifyExample);
        return cibrBussIdentifies;
    }

    public List<CibrSysAnimal>  findAllAnimal() {
        CibrSysAnimalExample animalExample = new CibrSysAnimalExample();
        animalExample.setOrderByClause("name");
        return animalMapper.selectByExample(animalExample);
    }

    @Transactional(rollbackFor = Exception.class)
    public String bussUpdate(CibrBussIdentify bussIdentify, String status) {
        CibrBussIdentify dbItem = identifyMapper.selectByPrimaryKey(bussIdentify.getId());
        if (dbItem != null && "02".equals(dbItem.getRecstatus())){
            dbItem.setRecstatus("03");
            dbItem.setAnimalid(bussIdentify.getAnimalid());
            identifyMapper.updateByPrimaryKey(dbItem);
        }else {
            dbItem.setRecstatus(status);
            dbItem.setAnimalid(bussIdentify.getAnimalid());
            identifyMapper.updateByPrimaryKey(dbItem);
        }

        CibrSysFile dbfile = fileMapper.selectByPrimaryKey(dbItem.getFileid());
        File oldImg = new File(dbfile.getPath());
        String animalTransPath = filePath + File.separator + dbItem.getId() + File.separator + "trans" + File.separator;
        File transPath = new File(animalTransPath);
        if (!transPath.exists()){
            transPath.mkdirs();
        }

        File transImg = new File(animalTransPath + dbfile.getName());
        oldImg.renameTo(transImg);
        dbfile.setPath(animalTransPath + dbfile.getName());
        fileMapper.updateByPrimaryKey(dbfile);
        return RetUtil.SUCCESS;
    }

    public List<CibrBussIdentify> getBussByAnimalId(String animalId) {
        CibrBussIdentifyExample identifyExample = new CibrBussIdentifyExample();
        identifyExample.createCriteria().andAnimalidEqualTo(animalId);
        return identifyMapper.selectByExample(identifyExample);
    }

    public List<CibrSysFile>  findImages(String detailId) {
        CibrSysFileExample fileExample = new CibrSysFileExample();
        fileExample.createCriteria().andDetailidEqualTo(detailId);
        List<CibrSysFile> cibrSysFiles = fileMapper.selectByExample(fileExample);
        if (cibrSysFiles == null){
            return new ArrayList<>();
        }
        return cibrSysFiles;
    }
}



























