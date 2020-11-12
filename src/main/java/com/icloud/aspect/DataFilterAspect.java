/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.icloud.aspect;


import com.icloud.annotation.DataFilter;
import com.icloud.common.Constant;
import com.icloud.exceptions.BaseException;
import com.icloud.modules.sys.entity.SysUserEntity;
import com.icloud.modules.sys.service.SysDeptService;
import com.icloud.modules.sys.service.SysRoleDeptService;
import com.icloud.modules.sys.service.SysRoleShopService;
import com.icloud.modules.sys.service.SysUserRoleService;
import com.icloud.modules.sys.shiro.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据过滤，切面处理类
 *
 * @author Mark sunlightcs@gmail.com
 */
@Slf4j
@Aspect
@Component
public class DataFilterAspect {
//    @Autowired
//    private SysDeptService sysDeptService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleDeptService sysRoleDeptService;
    @Autowired
    private SysRoleShopService sysRoleShopService;

    @Pointcut("@annotation(com.icloud.annotation.DataFilter)")
//    @Pointcut("execution(* com.icloud.modules..controller..*.*(..))")
    public void dataFilterCut() {

    }

    @Before("dataFilterCut()")
    public void dataFilter(JoinPoint point) throws Throwable {
        Object params = point.getArgs()[0];
        if(params != null && params instanceof Map){
            SysUserEntity user = ShiroUtils.getUserEntity();
            //如果不是超级管理员，则进行数据过滤(由原来的部门过滤 改成 店铺过滤)
            if(user.getUserId() != Constant.SUPER_ADMIN){
                Map map = (Map)params;
                map.put(Constant.SQL_FILTER, getSQLFilter(user, point));
            }

            return ;
        }

        throw new BaseException("数据权限接口，只能是Map类型参数，且不能为NULL");
    }

    /**
     * 获取数据过滤的SQL
     */
    private String getSQLFilter(SysUserEntity user, JoinPoint point){
        MethodSignature signature = (MethodSignature) point.getSignature();
        DataFilter dataFilter = signature.getMethod().getAnnotation(DataFilter.class);
        //获取表的别名
        String tableAlias = dataFilter.tableAlias();
        if(StringUtils.isNotBlank(tableAlias)){
            tableAlias +=  ".";
        }

        //dianpu ID列表
        Set<Long> shopIdList = new HashSet<>();
//        shopIdList.add(user.getShopId());

        //用户拥有角色
//        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(user.getUserId());
//        if(roleIdList.size() > 0){
//            //用户角色对应的店铺ID列表
//            List<Long> userShopIdList = sysRoleShopService.queryShopIdList(roleIdList.toArray(new Long[roleIdList.size()]));
//            shopIdList.addAll(userShopIdList);
//        }

        //用户子店铺ID列表
    /*    if(dataFilter.subDept()){
            List<Long> subShopIdList = shopService.getSubShopIdList(user.getShopId());
            shopIdList.addAll(subShopIdList);
        }*/

        StringBuilder sqlFilter = new StringBuilder();
        sqlFilter.append(" (");

        if(shopIdList.size() > 0){
            sqlFilter.append(tableAlias).append(dataFilter.shopId()).append(" in(").append(StringUtils.join(shopIdList, ",")).append(")");
        }

        //没有本部门数据权限，也能查询本人所在数据
//        if(dataFilter.user()){
//            if(shopIdList.size() > 0){
//                sqlFilter.append(" or ");
//            }
//            sqlFilter.append(tableAlias).append(dataFilter.userId()).append("=").append(user.getUserId());
//        }

        sqlFilter.append(")");

        if(sqlFilter.toString().trim().equals("()")){
            return null;
        }
        log.info("Aspact_getSQLFilter====="+sqlFilter.toString());
        return sqlFilter.toString();
    }

//    /**
//     * 获取数据过滤的SQL
//     */
//    private String getSQLFilter_bak(SysUserEntity user, JoinPoint point){
//        MethodSignature signature = (MethodSignature) point.getSignature();
//        DataFilter dataFilter = signature.getMethod().getAnnotation(DataFilter.class);
//        //获取表的别名
//        String tableAlias = dataFilter.tableAlias();
//        if(StringUtils.isNotBlank(tableAlias)){
//            tableAlias +=  ".";
//        }
//
//        //部门ID列表
//        Set<Long> deptIdList = new HashSet<>();
//
//        //用户拥有角色
//        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(user.getUserId());
//        if(roleIdList.size() > 0){
//            //用户角色对应的部门ID列表
//            List<Long> userDeptIdList = sysRoleDeptService.queryDeptIdList(roleIdList.toArray(new Long[roleIdList.size()]));
//            deptIdList.addAll(userDeptIdList);
//        }
//
//        //用户子部门ID列表
//        if(dataFilter.subDept()){
//            List<Long> subDeptIdList = sysDeptService.getSubDeptIdList(user.getDeptId());
//            deptIdList.addAll(subDeptIdList);
//        }
//
//        StringBuilder sqlFilter = new StringBuilder();
//        sqlFilter.append(" (");
//
//        if(deptIdList.size() > 0){
//            sqlFilter.append(tableAlias).append(dataFilter.deptId()).append(" in(").append(StringUtils.join(deptIdList, ",")).append(")");
//        }
//
//        //没有本部门数据权限，也能查询本人数据
//        if(dataFilter.user()){
//            if(deptIdList.size() > 0){
//                sqlFilter.append(" or ");
//            }
//            sqlFilter.append(tableAlias).append(dataFilter.userId()).append("=").append(user.getUserId());
//        }
//
//        sqlFilter.append(")");
//
//        if(sqlFilter.toString().trim().equals("()")){
//            return null;
//        }
//
//        return sqlFilter.toString();
//    }
}
