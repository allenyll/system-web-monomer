package com.allenyll.sw.system.service.pay.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.constants.CacheKeys;
import com.allenyll.sw.common.constants.WxConstants;
import com.allenyll.sw.common.entity.order.Order;
import com.allenyll.sw.common.entity.order.OrderDetail;
import com.allenyll.sw.common.entity.pay.Transaction;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.enums.dict.OrderStatusDict;
import com.allenyll.sw.common.enums.dict.OrderTradeDict;
import com.allenyll.sw.common.enums.dict.PayTypeDict;
import com.allenyll.sw.common.util.*;
import com.allenyll.sw.core.cache.util.CacheUtil;
import com.allenyll.sw.core.properties.WxProperties;
import com.allenyll.sw.system.service.order.impl.OrderDetailServiceImpl;
import com.allenyll.sw.system.service.order.impl.OrderServiceImpl;
import com.allenyll.sw.system.service.pay.IWxPaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;


/**
 * @Description:  微信小程序支付业务
 * @Author:       allenyll
 * @Date:         2020/11/23 上f午11:55
 * @Version:      1.0
 */
@Slf4j
@Service("wxPaymentService")
public class WxPaymentServiceImpl implements IWxPaymentService {

    private static final String NOTIFY_URL = "https://www.allenyll.com/system-web/pay/payCallback";
    
    private static final String PAY_NOTIFY_URL = "https://localhost:8443/pay/payNotify";

    @Autowired
    WxProperties wxProperties;

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderDetailServiceImpl orderDetailService;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private WxPayService wxService;


    @Override
    public Result createOrder(User user, HttpServletRequest request, HttpServletResponse response) {
        Result result = new Result();
        WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = new WxPayUnifiedOrderRequest();
        //接受参数(openid)
        String openid = request.getParameter("openid");
        String mode = request.getParameter("mode");
        String currentOpenId = cacheUtil.get(CacheKeys.WX_CURRENT_OPENID + "_" + mode + "_" + openid, String.class);
        if (!openid.equals(currentOpenId)) {
            result.fail(user.getUserName() + "当前用户与登录用户不匹配");
            return result;
        }

        Long orderId = Long.parseLong(request.getParameter("orderId"));

        Order order = orderService.getById(orderId);
        if (order == null || order.getId() == null) {
            result.fail("订单关联失败，请联系管理员");
            return result;
        }

        if (OrderStatusDict.CANCEL.getCode() == order.getOrderStatus() || OrderStatusDict.CLOSE.getCode() == order.getOrderStatus() ||
                OrderStatusDict.UN_EFFECT.getCode() == order.getOrderStatus()
        ) {
            result.fail("订单已取消或者已失效或者已关闭");
            return result;
        }

        if (OrderStatusDict.PAY.getCode() == order.getOrderStatus()) {
            result.fail("订单已支付，请不要重复操作");
            return result;
        }

        //接受参数(金额)
        String amount = request.getParameter("amount");
        //用户ID
        Long customerId = Long.parseLong(request.getParameter("customerId"));
        //支付来源
        String payName = request.getParameter("payName");
        //支付备注
        String remark = request.getParameter("remark");
        //接口调用总金额单位为分换算一下(测试金额改成1,单位为分则是0.01,根据自己业务场景判断是转换成float类型还是int类型)
        BigDecimal bigAmount = new BigDecimal("0.02").multiply(new BigDecimal("100"));
        int amountFen = bigAmount.intValue();
        //设置随机字符串
        String nonceStr = StringUtil.getRandomString(11);
        //设置商户订单号
        String outTradeNo =  StringUtil.getRandomString(11);

        Map<String, Object> data = new HashMap<>();
        //订单的商品
        data.put("orderId", orderId);
        String body = "SNU ";
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailList(data);
        if (CollectionUtil.isNotEmpty(orderDetails)) {
            for (OrderDetail goodsVo : orderDetails) {
                body = body + goodsVo.getGoodsName() + "、";
            }
            if (body.length() > 0) {
                body = body.substring(0, body.length() - 1);
            }
        }

        wxPayUnifiedOrderRequest.setBody(body);
        wxPayUnifiedOrderRequest.setOutTradeNo(outTradeNo);
        wxPayUnifiedOrderRequest.setTotalFee(amountFen);
        wxPayUnifiedOrderRequest.setSpbillCreateIp(IPUtil.getIpAddr(request, response));
        wxPayUnifiedOrderRequest.setNotifyUrl(PAY_NOTIFY_URL);
        wxPayUnifiedOrderRequest.setTradeType("JSAPI");
        wxPayUnifiedOrderRequest.setOpenid(openid);
        wxPayUnifiedOrderRequest.setNonceStr(nonceStr);
        WxPayMpOrderResult wxPayMpOrderResult;

        try {
            wxPayMpOrderResult = wxService.createOrder(wxPayUnifiedOrderRequest);
        } catch (WxPayException e) {
            log.error("微信支付失败！订单号：{}，原因：{}", order.getOrderNo(), e.getMessage());
            result.fail(e.getMessage());
            return result;
        }

        Transaction transaction = createTransactionInfo(order, outTradeNo, customerId, orderId, payName, amount, remark);

        Map map = BeanUtil.beanToMap(wxPayMpOrderResult);
        data.putAll(map);
        data.put("transactionId", transaction.getId());
        log.info("微信统一下单返回：{}", JSON.toJSONString(map));
        result.setData(data);

        return result;
    }

    /**
     * 创建支付信息对象
     */
    private Transaction createTransactionInfo(Order order, String outTradeNo, Long customerId, Long orderId, String payName, 
                                              String amount, String remark) {
        Transaction transaction = new Transaction();
        transaction.setId(SnowflakeIdWorker.generateId());
        transaction.setTransactionNo(outTradeNo);
        transaction.setCustomerId(customerId);
        transaction.setIntegral(0);
        transaction.setOrderId(orderId);
        transaction.setSource(payName);
        transaction.setTransactionTime(DateUtil.getCurrentDateTime());
        transaction.setAmount(new BigDecimal(amount));
        transaction.setStatus(OrderTradeDict.START.getCode());
        transaction.setPayChannel(PayTypeDict.WECHAT.getCode());
        transaction.setRemark(remark);
        transaction.setIsDelete(0);
        transaction.setAddTime(DateUtil.getCurrentDateTime());
        transaction.setAddUser(customerId);
        transaction.setUpdateTime(DateUtil.getCurrentDateTime());
        transaction.setUpdateUser(customerId);
        //插入Dao
        transactionService.save(transaction);

        order.setTradeNo(transaction.getTransactionNo());
        orderService.updateById(order);
        
        return transaction;
    }

    @SneakyThrows
    @Override
    public String payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        WxPayOrderNotifyResult result = wxService.parseOrderNotifyResult(xmlResult);
        log.info("支付成功回调返回：{}", JSON.toJSONString(result));
        // 判断支付是否成功
        if ("SUCCESS".equals(result.getReturnCode())) {
            // 加入自己处理订单的业务逻辑，需要判断订单是否已经支付过，否则可能会重复调用
            String orderNo = result.getOutTradeNo();
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("ORDER_NO", orderNo);
            Order order = orderService.getOne(queryWrapper);
            if (order == null) {
                return WxPayNotifyResponse.fail("支付回调失败，未查询到订到");
            }
            if (OrderStatusDict.START.getCode() == order.getOrderStatus()) {
                QueryWrapper<Transaction> transactionQueryWrapper = new QueryWrapper<>();
                transactionQueryWrapper.eq("ORDER_ID", order.getId());
                transactionQueryWrapper.eq("IS_DELETE", 0);
                Transaction transaction = transactionService.getOne(transactionQueryWrapper);
                orderService.updateOrder(order, transaction);
            }
        }
        return WxPayNotifyResponse.success("处理成功!");
    }

    @Override
    public Result<Map<String, Object>> createUnifiedOrder(User user, HttpServletRequest request, HttpServletResponse response) {
        //设置最终返回对象
        Result<Map<String, Object>> result = new Result<>();
        Map<String, Object> data = new HashMap<>();
        //接受参数(openid)
        String openid = request.getParameter("openid");
        String mode = request.getParameter("mode");
        String currentOpenId = cacheUtil.get(CacheKeys.WX_CURRENT_OPENID + "_" + mode + "_" + openid, String.class);
        if (!openid.equals(currentOpenId)) {
            result.fail("当前用户与登录用户不匹配");
            return result;
        }

        Long orderId = Long.parseLong(request.getParameter("orderId"));

        Order order = orderService.getById(orderId);
        if (order == null || order.getId() == null) {
            result.fail("订单关联失败，请联系管理员");
            return result;
        }

        if (OrderStatusDict.CANCEL.getCode() == order.getOrderStatus() || OrderStatusDict.CLOSE.getCode() == order.getOrderStatus() ||
                OrderStatusDict.UN_EFFECT.getCode() == order.getOrderStatus()
        ) {
            result.fail("订单已取消或者已失效或者已关闭");
            return result;
        }

        if (OrderStatusDict.PAY.getCode() == order.getOrderStatus()) {
            result.fail("订单已支付，请不要重复操作");
            return result;
        }


        //接受参数(金额)
        String amount = request.getParameter("amount");
        //用户ID
        Long customerId = Long.parseLong(request.getParameter("customerId"));
        //支付来源
        String payName = request.getParameter("payName");
        //支付备注
        String remark = request.getParameter("remark");

        //接口调用总金额单位为分换算一下(测试金额改成1,单位为分则是0.01,根据自己业务场景判断是转换成float类型还是int类型)
        BigDecimal bigAmount = new BigDecimal("0.02").multiply(new BigDecimal("100"));
        int amountFen = bigAmount.intValue();
        //创建hashmap(用户获得签名)
        SortedMap<String, String> paraMap = new TreeMap<>();
        //设置body变量 (支付成功显示在微信支付 商品详情中)
        String body = "SNU";
        //设置随机字符串
        String nonceStr = StringUtil.getRandomString(11);
        //设置商户订单号
        String outTradeNo =  StringUtil.getRandomString(11);


        //设置请求参数(小程序ID)
        paraMap.put("appid", wxProperties.getAppId());
        //设置请求参数(商户号)
        paraMap.put("mch_id", wxProperties.getMchId());
        //设置请求参数(随机字符串)
        paraMap.put("nonce_str", nonceStr);

        //订单的商品
        data.put("orderId", orderId);
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailList(data);
        if (CollectionUtil.isNotEmpty(orderDetails)) {
            for (OrderDetail goodsVo : orderDetails) {
                body = body + goodsVo.getGoodsName() + "、";
            }
            if (body.length() > 0) {
                body = body.substring(0, body.length() - 1);
            }
        }

        //设置请求参数(商品描述)
        paraMap.put("body", body);
        //设置请求参数(商户订单号)
        paraMap.put("out_trade_no", outTradeNo);
        //设置请求参数(总金额)
        paraMap.put("total_fee", String.valueOf(amountFen));
        //设置请求参数(终端IP)
        paraMap.put("spbill_create_ip", IPUtil.getIpAddr(request, response));
        //设置请求参数(通知地址)
        paraMap.put("notify_url", NOTIFY_URL);
        //设置请求参数(交易类型)
        paraMap.put("trade_type", "JSAPI");
        //设置请求参数(openid)(在接口文档中 该参数 是否必填项 但是一定要注意 如果交易类型设置成'JSAPI'则必须传入openid)
        paraMap.put("openid", openid);
        //调用逻辑传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        String stringA = StringUtil.formatUrlMap(paraMap, false, false);
        //第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。(签名)
        String sign = MD5Util.md5Password(stringA+"&key="+wxProperties.getKey()).toUpperCase();
        paraMap.put("sign", sign);
        //将参数 编写XML格式
        String xmlData = XmlUtil.GetMapToXML(paraMap);
        try {
            //发送请求(POST)(获得数据包ID)(这有个注意的地方 如果不转码成ISO8859-1则会告诉你body不是UTF8编码 就算你改成UTF8编码也一样不好使 所以修改成ISO8859-1)
            Map<String,String> resultUn = XmlUtil.doXMLParse(HttpUtils.getRemotePortData(wxProperties.getOrderUrl(), new String(xmlData.getBytes(), "ISO8859-1")));

            if (resultUn == null) {
                result.fail("统一下单失败");
                return result;
            }

            // 响应报文
            String returnCode = MapUtil.getString(resultUn, "return_code");
            String returnMsg = MapUtil.getString(resultUn, "return_msg");

            log.info("微信统一下单返回状态：{},返回消息：{}", returnCode, returnMsg);

            if (WxConstants.FAIL.equalsIgnoreCase(returnCode)) {
                result.fail("支付失败: " + returnMsg);
                return result;
            }

            //应该创建 支付表数据

            //创建支付信息对象
            Transaction transaction = new Transaction();
            transaction.setId(SnowflakeIdWorker.generateId());
            transaction.setTransactionNo(outTradeNo);
            transaction.setCustomerId(customerId);
            transaction.setIntegral(0);
            transaction.setOrderId(orderId);
            transaction.setSource(payName);
            transaction.setTransactionTime(DateUtil.getCurrentDateTime());
            transaction.setAmount(new BigDecimal(amount));
            transaction.setStatus(OrderTradeDict.START.getCode());
            transaction.setPayChannel(PayTypeDict.WECHAT.getCode());
            transaction.setRemark(remark);
            transaction.setIsDelete(0);
            transaction.setAddTime(DateUtil.getCurrentDateTime());
            transaction.setAddUser(customerId);
            transaction.setUpdateTime(DateUtil.getCurrentDateTime());
            transaction.setUpdateUser(customerId);
            //插入Dao
            transactionService.save(transaction);

            order.setTradeNo(transaction.getTransactionNo());
            orderService.updateById(order);

            log.info("微信 统一下单 接口调用成功 并且新增支付信息成功");
            data.put("transactionId", transaction.getId());
            data.put("prepayId", resultUn.get("prepay_id"));
            data.put("outTradeNo", paraMap.get("out_trade_no"));

            sign(data);
            result.setData(data);
            return result;

        } catch (UnsupportedEncodingException e) {
            log.info("微信 统一下单 异常："+e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log.info("微信 统一下单 异常："+e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result queryOrder(CurrentUser currentUser, Long orderId, Long transactionId) {
        Result result = new Result();
        if (orderId == null || transactionId == null) {
            result.fail("订单不存在");
            return result;
        }

        Order order = orderService.getById(orderId);
        //设置随机字符串
        String nonceStr = StringUtil.getRandomString(11);

        //创建hashmap(用户获得签名)
        SortedMap<String, String> paraMap = new TreeMap<String, String>();
        //设置(小程序ID)(这块一定要是大写)
        paraMap.put("appid", wxProperties.getAppId());
        // 商家账号。
        paraMap.put("mch_id", wxProperties.getMchId());
        //设置(随机串)
        paraMap.put("nonce_str", nonceStr);
        // 商户订单编号
        paraMap.put("out_trade_no", order.getTradeNo());


        //调用逻辑传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        String stringA = StringUtil.formatUrlMap(paraMap, false, false);
        //第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。(签名)
        String sign = MD5Util.md5Password(stringA+"&key="+wxProperties.getKey()).toUpperCase();
        paraMap.put("sign", sign);
        //将参数 编写XML格式
        String xmlData = XmlUtil.GetMapToXML(paraMap);

        //发送请求(POST)(获得数据包ID)(这有个注意的地方 如果不转码成ISO8859-1则会告诉你body不是UTF8编码 就算你改成UTF8编码也一样不好使 所以修改成ISO8859-1)
        Map<String,String> resultUn = null;
        try {
            resultUn = XmlUtil.doXMLParse(HttpUtils.getRemotePortData(wxProperties.getOrderQuery(), new String(xmlData.getBytes(), "ISO8859-1")));
        } catch (Exception e) {
            log.error("查询订单失败：" + e.getMessage());
            result.fail("查询订单失败");
            return result;
        }

        if (resultUn == null) {
            result.fail("查询订单失败");
            return result;
        }

        // 响应报文
        String returnCode = MapUtil.getString(resultUn, "return_code");
        String returnMsg = MapUtil.getString(resultUn, "return_msg");

        log.info("微信查询订单返回状态：{},返回消息：{}", returnCode, returnMsg);

        if (WxConstants.FAIL.equalsIgnoreCase(returnCode)) {
            result.fail("支付失败: " + returnMsg);
            return result;
        }

        String errorCode = MapUtil.getString(resultUn, "err_code");
        String errorCodeDes = MapUtil.getString(resultUn, "err_code_des");

        if (WxConstants.FAIL.equalsIgnoreCase(errorCode)) {
            result.fail("支付失败: " + errorCodeDes);
            return result;
        }

        String tradeState = MapUtil.getString(resultUn, "trade_state");

        // 支付成功
        if (WxConstants.SUCCESS.equals(tradeState)) {
            // 更新订单状态，更新支付交易状态
            QueryWrapper<Transaction> entityWrapper = new QueryWrapper<>();
            entityWrapper.eq("IS_DELETE", 0);
            entityWrapper.eq("ID", transactionId);
            Transaction transaction = transactionService.getOne(entityWrapper);
            if(transaction != null){
                transaction.setStatus(OrderTradeDict.COMPLETE.getCode());
                transactionService.updateById(transaction);
                // 更新订单状态
                orderService.updateOrder(order, transaction);
            }
            return result;
        } else if (WxConstants.USERPAYING.equals(tradeState)) {
            // 重试查询三次，查询都在支付中，返回失败
            Integer num = cacheUtil.get(CacheKeys.WX_CACHE_ZONE + CacheKeys.ORDER_PAY + orderId, Integer.class);
            if (num == null) {
                cacheUtil.set(CacheKeys.WX_CACHE_ZONE + CacheKeys.ORDER_PAY + orderId, 1);
                this.queryOrder(currentUser, orderId, transactionId);
            } else if (num <= 3) {
                cacheUtil.remove(CacheKeys.WX_CACHE_ZONE + CacheKeys.ORDER_PAY + orderId);
                cacheUtil.set(CacheKeys.WX_CACHE_ZONE + CacheKeys.ORDER_PAY + orderId, ++num);
                this.queryOrder(currentUser, orderId, transactionId);
            } else {
                result.fail("查询订单失败, error: " + tradeState);
                return result;
            }
        } else {
            result.fail("查询订单失败, error: " + tradeState);
            return result;
        }

        result.fail("未知错误原因，联系客服");
        return result;
    }

    @Override
    public DataResponse paySign(HttpServletRequest request, HttpServletResponse response) {
        log.info("微信 支付接口生成签名 方法开始");
        //实例化返回对象
        Map<String, Object> result = new HashMap<>();

        //获得参数(微信统一下单接口生成的prepay_id )
        String prepayId = request.getParameter("prepayId");
        //创建 时间戳
        String timeStamp = Long.valueOf(System.currentTimeMillis()).toString();
        //创建 随机串
        String nonceStr = StringUtil.getRandomString(11);
        //创建 MD5
        String signType = "MD5";

        //创建hashmap(用户获得签名)
        SortedMap<String, String> paraMap = new TreeMap<String, String>();
        //设置(小程序ID)(这块一定要是大写)
        paraMap.put("appId", wxProperties.getAppId());
        //设置(时间戳)
        paraMap.put("timeStamp", timeStamp);
        //设置(随机串)
        paraMap.put("nonceStr", nonceStr);
        //设置(数据包)
        paraMap.put("package", "prepay_id="+prepayId);
        //设置(签名方式)
        paraMap.put("signType", signType);


        //调用逻辑传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        String stringA = StringUtil.formatUrlMap(paraMap, false, false);
        //第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。(签名)
        String sign = MD5Util.md5Password(stringA+"&key="+wxProperties.getKey()).toUpperCase();

        if(StringUtil.isNotEmpty(sign)){
            //返回签名信息
            result.put("sign", sign);
            //返回随机串(这个随机串是新创建的)
            result.put("nonceStr", nonceStr);
            //返回时间戳
            result.put("timeStamp", timeStamp);
            //返回数据包
            result.put("package", "prepay_id="+prepayId);

            log.info("微信 支付接口生成签名 设置返回值");
        }
        log.info("微信 支付接口生成签名 方法结束");
        return DataResponse.success(result);
    }

    /**
     * 签名
     * @param result
     */
    private void sign(Map<String, Object> result) {
        log.info("微信 支付接口生成签名 方法开始");
        //获得参数(微信统一下单接口生成的prepay_id )
        String prepayId = MapUtil.getString(result, "prepayId");
        //创建 时间戳
        String timeStamp = Long.valueOf(System.currentTimeMillis()).toString();
        //创建 随机串
        String nonceStr = StringUtil.getRandomString(11);
        //创建 MD5
        String signType = "MD5";

        //创建hashmap(用户获得签名)
        SortedMap<String, String> paraMap = new TreeMap<String, String>();
        //设置(小程序ID)(这块一定要是大写)
        paraMap.put("appId", wxProperties.getAppId());
        //设置(时间戳)
        paraMap.put("timeStamp", timeStamp);
        //设置(随机串)
        paraMap.put("nonceStr", nonceStr);
        //设置(数据包)
        paraMap.put("package", "prepay_id="+prepayId);
        //设置(签名方式)
        paraMap.put("signType", signType);


        //调用逻辑传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        String stringA = StringUtil.formatUrlMap(paraMap, false, false);
        //第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。(签名)
        String sign = MD5Util.md5Password(stringA+"&key="+wxProperties.getKey()).toUpperCase();

        if(StringUtil.isNotEmpty(sign)){
            //返回签名信息
            result.put("sign", sign);
            //返回随机串(这个随机串是新创建的)
            result.put("nonceStr", nonceStr);
            //返回时间戳
            result.put("timeStamp", timeStamp);
            //返回数据包
            result.put("package", "prepay_id="+prepayId);
            log.info("微信 支付接口生成签名 设置返回值: {}", result);
        }
        log.info("微信 支付接口生成签名 方法结束");
    }
}
