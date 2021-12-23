package com.allenyll.sw.system.service.member.impl;

import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.enums.dict.IsOrNoDict;
import com.allenyll.sw.common.enums.dict.StatusDict;
import com.allenyll.sw.common.exception.BusinessException;
import com.allenyll.sw.common.util.*;
import com.allenyll.sw.system.mapper.member.CustomerAddressMapper;
import com.allenyll.sw.system.service.member.ICustomerAddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.customer.CustomerAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-06-25 15:58:58
 */
@Slf4j
@Service("customerAddressService")
public class CustomerAddressServiceImpl extends ServiceImpl<CustomerAddressMapper, CustomerAddress> implements ICustomerAddressService {

    @Autowired
    CustomerAddressMapper customerAddressMapper;
    
    private static final String ADD = "add";

    private static final String UPDATE = "update";
    
    @Override
    public Result setAddress(User user, Map<String, Object> params) {
        Result result = new Result();
        String addOrUpdate = MapUtil.getString(params, "addOrUpdate");
        String isDefault = MapUtil.getString(params, "isDefault");
        if(ADD.equals(addOrUpdate)){
            CustomerAddress customerAddress = new CustomerAddress();
            customerAddress.setId(SnowflakeIdWorker.generateId());
            setData(customerAddress, params);
            if(IsOrNoDict.YES.getCode().equals(isDefault)){
                setDefault(customerAddress);
            }
            customerAddress.setIsDelete(0);
            customerAddress.setAddTime(DateUtil.getCurrentDateTime());
            customerAddress.setAddUser(user.getId());
            customerAddress.setUpdateTime(DateUtil.getCurrentDateTime());
            customerAddress.setUpdateUser(user.getId());
            customerAddressMapper.insert(customerAddress);
        }else if(UPDATE.equals(addOrUpdate)){
            String id = MapUtil.getString(params, "id");
            QueryWrapper<CustomerAddress> wrapper = new QueryWrapper<>();
            wrapper.eq("ID", id);
            wrapper.eq("IS_DELETE", 0);
            CustomerAddress customerAddress = customerAddressMapper.selectOne(wrapper);
            if(customerAddress == null) {
                log.error("用户：{}更新地址失败, 未找到对应地址", user.getAccount());
                result.fail("更新失败, 未找到对应地址");
                return result;
            }
            String _isDefault = customerAddress.getIsDefault();
            if(!_isDefault.equals(isDefault) && IsOrNoDict.YES.getCode().equals(isDefault)){
                setDefault(customerAddress);
            }
            setData(customerAddress, params);
            customerAddress.setUpdateTime(DateUtil.getCurrentDateTime());
            customerAddress.setUpdateUser(user.getId());
            customerAddressMapper.updateById(customerAddress);
        }
        return result;
    }

    @Override
    public Result<List<CustomerAddress>> getAddressList(Map<String, Object> params) {
        Result<List<CustomerAddress>> result = new Result<>();
        Long customerId = MapUtil.getLong(params, "customerId");
        if(StringUtil.isEmpty(customerId)){
            log.error("查询地址集合用户不能为空");
            result.fail("用户不能为空");
            return result;
        }
        QueryWrapper<CustomerAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("CUSTOMER_ID", customerId);
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("STATUS", StatusDict.START.getCode());
        List<CustomerAddress> list = customerAddressMapper.selectList(wrapper);
        result.setData(list);
        return result;
    }

    @Override
     public Result deleteAddress(User user, Map<String, Object> params) {
        Result result = new Result();
        Long id = MapUtil.getLong(params, "id");
        CustomerAddress obj = customerAddressMapper.selectById(id);
        QueryWrapper<CustomerAddress> delWrapper = new QueryWrapper<>();
        delWrapper.eq("ID", id);
        obj.setIsDelete(1);
        obj.setUpdateUser(user.getId());
        obj.setUpdateTime(DateUtil.getCurrentDateTime());
        int flag = customerAddressMapper.update(obj, delWrapper);
        if(flag == 0){
            result.fail("删除失败");
            return result;
        }
        return result;
    }

    @Override
    public void updateAddress(String id) {
        if(StringUtil.isEmpty(id)){
            throw new BusinessException("收货地址不能为空");
        }
        QueryWrapper<CustomerAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("ID", id);
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("STATUS", StatusDict.START.getCode());
        CustomerAddress customerAddress = customerAddressMapper.selectOne(wrapper);
        if(customerAddress == null){
            throw new BusinessException("获取收货地址异常");
        }

        List<CustomerAddress> list = getList(customerAddress);

        if(CollectionUtil.isNotEmpty(list)){
            for(CustomerAddress _customerAddress:list){
                _customerAddress.setIsSelect(IsOrNoDict.NO.getCode());
                customerAddressMapper.updateById(_customerAddress);
            }
            customerAddress.setIsSelect(IsOrNoDict.YES.getCode());
            customerAddressMapper.updateById(customerAddress);
        }
    }

    /**
     * 设置默认地址
     * @param customerAddress
     */
    private void setDefault(CustomerAddress customerAddress) {
        List<CustomerAddress> list = getList(customerAddress);
        if(CollectionUtil.isNotEmpty(list)){
            for(CustomerAddress _customerAddress:list){
                _customerAddress.setIsDefault(IsOrNoDict.NO.getCode());
                _customerAddress.setIsSelect(IsOrNoDict.NO.getCode());
                customerAddressMapper.updateById(_customerAddress);
            }
        }
    }

    /**
     * 封装数据
     * @param customerAddress
     * @param params
     */
    public void setData(CustomerAddress customerAddress, Map<String, Object> params){
        customerAddress.setName(MapUtil.getString(params, "name"));
        customerAddress.setPhone(MapUtil.getString(params, "phone"));
        customerAddress.setCustomerId(MapUtil.getLong(params, "customerId"));
        customerAddress.setPostCode(MapUtil.getString(params, "postCode"));
        customerAddress.setStatus(MapUtil.getString(params, "status"));
        customerAddress.setProvince(MapUtil.getString(params, "province"));
        customerAddress.setCity(MapUtil.getString(params, "city"));
        customerAddress.setRegion(MapUtil.getString(params, "region"));
        customerAddress.setDetailAddress(MapUtil.getString(params, "detailAddress"));
        customerAddress.setIsDefault(MapUtil.getString(params, "isDefault"));
        customerAddress.setIsSelect(MapUtil.getString(params, "isSelect"));
    }

    /**
     * 获取地址列表
     * @param customerAddress
     * @return
     */
    public List<CustomerAddress> getList(CustomerAddress customerAddress){
        QueryWrapper<CustomerAddress> entityWrapper = new QueryWrapper<>();
        entityWrapper.eq("CUSTOMER_ID", customerAddress.getCustomerId());
        entityWrapper.eq("STATUS", StatusDict.START.getCode());
        entityWrapper.eq("IS_DELETE", 0);
        List<CustomerAddress> list = customerAddressMapper.selectList(entityWrapper);
        return list;
    }
}
