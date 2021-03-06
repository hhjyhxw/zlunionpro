package com.icloud.modules.sys.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色与部门对应关系
 *
 */
@Data
@TableName("sys_role_shop")
public class SysRoleShopEntity implements Serializable {
	private static final long serialVersionUID = 1L;
    /*  */
    @TableId(value="id", type= IdType.AUTO)
	private Long id;

	/**
	 * 角色ID
	 */
	private Long roleId;


	/**
	 * 店铺ID
	 */
	private Long shopId;
	
}
