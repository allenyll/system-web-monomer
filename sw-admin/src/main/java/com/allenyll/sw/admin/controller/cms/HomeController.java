package com.allenyll.sw.admin.controller.cms;

import com.alibaba.fastjson.JSON;
import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.constants.CacheKeys;
import com.allenyll.sw.common.entity.market.Ad;
import com.allenyll.sw.common.entity.market.Message;
import com.allenyll.sw.common.entity.product.CategoryTree;
import com.allenyll.sw.common.entity.product.Goods;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.Result;
import com.allenyll.sw.core.cache.util.CacheUtil;
import com.allenyll.sw.system.service.market.IAdService;
import com.allenyll.sw.system.service.market.IMessageService;
import com.allenyll.sw.system.service.product.ICategoryService;
import com.allenyll.sw.system.service.product.IGoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Api(value = "小程序首页", tags = "小程序首页")
@RestController
@RequestMapping("wx/home")
public class HomeController {
    
    @Autowired
    private IMessageService messageService;
    
    @Autowired
    private IAdService adService;
    
    @Autowired
    private IGoodsService goodsService;
    
    @Autowired
    private ICategoryService categoryService;
    
    @Autowired
    private CacheUtil cacheUtil;
    
    @ApiOperation("【小程序接口】微信小程序首页")
    @RequestMapping("/index")
    public Result index(@CurrentUser(isFull = true) User user, @RequestBody Map<String, Object> params, HttpServletRequest request) {
        Result result = new Result();
        log.info("【请求开始】访问首页,请求参数,用户信息:{}", JSON.toJSONString(user));

        Map<String, Object> data = null;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {
            // 查看缓存存在缓存数据
            data = cacheUtil.getBean(CacheKeys.WX_INDEX, Map.class);
            if (data != null) {
                log.info("从缓存读取到首页数据");
                result.setData(data);
                return result;
            } else {
                data = new HashMap<>();
            }
            Callable<List<Message>> message = () -> messageService.getMessageList(params);
            Callable<List<Ad>> ad = () -> adService.getAdList(params);
            params.put("goodsType", "new");
            Callable<List<Goods>> newGoods = () -> goodsService.getGoodsListByType(params);
            params.put("goodsType", "hot");
            Callable<List<Goods>> hotGoods = () -> goodsService.getGoodsListByType(params);
            Callable<List<CategoryTree>> category = () -> categoryService.tree("");
            FutureTask<List<Message>> messageTask = new FutureTask<>(message);
            FutureTask<List<Ad>> adTask = new FutureTask<>(ad);
            FutureTask<List<Goods>> newGoodsTask = new FutureTask<>(newGoods);
            FutureTask<List<Goods>> hotGoodsTask = new FutureTask<>(hotGoods);
            FutureTask<List<CategoryTree>> categoryTask = new FutureTask<>(category);
            executorService.submit(messageTask);
            executorService.submit(adTask);
            executorService.submit(newGoodsTask);
            executorService.submit(hotGoodsTask);
            executorService.submit(categoryTask);
            List<Message> messageList = messageTask.get();
            List<Ad> adList = adTask.get();
            List<Goods> newGoodsList = newGoodsTask.get();
            List<Goods> hotGoodsList = hotGoodsTask.get();
            List<CategoryTree> categoryList = categoryTask.get();
            data.put("messageList", messageList);
            data.put("adList", adList);
            data.put("newGoodsList", newGoodsList);
            data.put("hotGoodsList", hotGoodsList);
            data.put("categoryList", categoryList);
            cacheUtil.setBean(CacheKeys.WX_INDEX, data, 6 * 60 * 60);
            executorService.shutdown();
        } catch (Exception e) {
            log.error("小程序首页初始化失败：" + e.getMessage());
            e.printStackTrace();
        }
        result.setData(data);
        return result;
    }
}
