package com.no.crowd.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.no.crowd.constant.CrowdConstant;
import com.no.crowd.entity.Admin;
import com.no.crowd.entity.AdminExample;
import com.no.crowd.entity.RoleExample;
import com.no.crowd.exception.LoginAcctAlreadyInUseException;
import com.no.crowd.exception.LoginAcctAlreadyInUseForUpdateException;
import com.no.crowd.exception.LoginFailedException;
import com.no.crowd.mapper.AdminMapper;
import com.no.crowd.service.AdminService;
import com.no.crowd.util.CrowdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.lang.model.element.AnnotationMirror;
import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.Objects;

/**
 * @author NO
 * @create 2022-06-08-15:44
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Override
    public void saveAdmin(Admin admin) {

        //密码加密
        String userPswd = admin.getUserPswd();
       //String md5 = CrowdUtil.md5(userPswd);
        String encode = passwordEncoder.encode(userPswd);
        admin.setUserPswd(encode);

        //创建时间
        Date data = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = simpleDateFormat.format(data);
        admin.setCreateTime(createTime);

        try {
            adminMapper.insert(admin);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof DuplicateKeyException) {
                throw new LoginAcctAlreadyInUseException(CrowdConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
            }
        }

    }

    @Override
    public List<Admin> getAll() {
        List<Admin> admins = adminMapper.selectByExample(new AdminExample());
        return admins;
    }

    @Override
    public Admin getAdminByLoginAcct(String loginAcct, String userPswd) {

        // 根据LoginAcct账号查询出admin对象
        AdminExample adminExample = new AdminExample();

        AdminExample.Criteria criteria = adminExample.createCriteria();
        //封装查询条件
        criteria.andLoginAcctEqualTo(loginAcct);

        List<Admin> admins = adminMapper.selectByExample(adminExample);

        // 判断admin对象是否为空
        if (admins == null || admins.size() == 0) {
            // 如果admin对象不存在，则抛出异常
            throw new LoginFailedException(CrowdConstant.MESSAGE_LOGIN_FAILED);
        }
        if (admins.size() > 1) {
            throw new RuntimeException(CrowdConstant.MESSAGE_SYSTEM_ERROR_LOGIN_NOT_UNIQUE);
        }

        // 如果admin对象存在，则将admin对象的密码从数据库中取出
        Admin admin = admins.get(0);
        String userPswdDB = admin.getUserPswd();

        // 将传入的明文密码加密，得到加密结果
        String userPswdForm = CrowdUtil.md5(userPswd);

        // 加密结果与数据库取出的密码比较
        if (!Objects.equals(userPswdForm, userPswdDB)) {
            // 如果密码不一致，抛出异常
            throw new LoginFailedException();
        }

        // 如果密码一致，返回admin对象
        return admin;

    }

    @Override
    public Admin getAdminByLoginAcct(String username) {
        AdminExample example = new AdminExample();
        AdminExample.Criteria criteria = example.createCriteria();
        criteria.andLoginAcctEqualTo(username);
        List<Admin> list = adminMapper.selectByExample(example);
        Admin admin = list.get(0);
        return admin;
    }

    @Override
    public PageInfo<Admin> getPageInfo(String keyword, Integer pageNum, Integer pageSize) {

        //调用pageHelper的静态方法开启分页
        PageHelper.startPage(pageNum, pageSize);

        //执行查询
        List<Admin> admins = adminMapper.selectAdminByKeyword(keyword);

        //封装到PageInfo中
        return new PageInfo<>(admins);

    }

    @Override
    public void remove(Integer adminId) {
        adminMapper.deleteByPrimaryKey(adminId);

    }

    @Override
    public Admin getAdminById(Integer adminId) {

        Admin admin = adminMapper.selectByPrimaryKey(adminId);
        return admin;
    }

    @Override
    public void update(Admin admin) {

        try {
            //Selective表示有选择的更新，对于null的字段不更新
            adminMapper.updateByPrimaryKeySelective(admin);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof DuplicateKeyException) {
                throw new LoginAcctAlreadyInUseForUpdateException(CrowdConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
            }
        }

    }

    @Override
    public void saveAdminRoleRelationShip(Integer adminId, List<Integer> roleIdList) {

        // 删除所有旧数据
        adminMapper.deleteOldRelationShip(adminId);

        // 保存新的关联关系
        if (roleIdList != null && roleIdList.size() > 0) {
            adminMapper.InsertNewRelationShip(adminId, roleIdList);
        }
    }

    @Override
    public void removeAdmin(List<Integer> adminIdList) {

        AdminExample example = new AdminExample();
        AdminExample.Criteria criteria = example.createCriteria();
        //where id in (5,3,2)
        criteria.andIdIn(adminIdList);

        adminMapper.deleteByExample(example);
    }
}
