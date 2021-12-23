package com.allenyll.sw.system.service.product.impl;

import com.allenyll.sw.common.enums.dict.*;
import com.allenyll.sw.core.cache.util.CacheUtil;
import com.allenyll.sw.system.service.file.impl.FileServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.product.GoodsMapper;
import com.allenyll.sw.system.service.product.IGoodsService;
import com.allenyll.sw.common.dto.GoodsQueryDto;
import com.allenyll.sw.common.dto.GoodsResult;
import com.allenyll.sw.common.entity.product.*;
import com.allenyll.sw.common.entity.system.File;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 商品基本信息表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-03-21 10:51:24
 */
@Service("goodsService")
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsServiceImpl.class);
  
    protected static final String DEFAULT_URL = "https://system-web-1257935390.cos.ap-chengdu.myqcloud.com/images/no.jpeg";

    @Autowired
    GoodsFullReduceServiceImpl goodsFullReduceService;
    
    @Autowired
    GoodsLadderServiceImpl goodsLadderService;

    @Autowired
    SkuServiceImpl skuService;

    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    protected CacheUtil cacheUtil;

    @Autowired
    SpecsServiceImpl specsService;

    @Autowired
    SpecOptionServiceImpl specOptionService;

    @Autowired
    BrandServiceImpl brandService;

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    GoodsMapper goodsMapper;

    /**
     * 创建商品
     * @param goodsParam
     * @param user
     * @return
     * @throws Exception
     */
    public int createGoods(GoodsParam goodsParam, User user) throws Exception {
        Goods goods = goodsParam;
        if (StringUtil.isEmpty(goodsParam.getSaleTime())) {
            goodsParam.setSaleTime(DateUtil.getCurrentDateTime());
        }
        Long userId = user.getId();
        goods.setIsDelete(0);
        goods.setAddTime(DateUtil.getCurrentDateTime());
        goods.setAddUser(userId);
        goods.setUpdateTime(DateUtil.getCurrentDateTime());
        goods.setUpdateUser(userId);
        goods.setWarningStock(2);
        int num = goodsMapper.insert(goods);
        String promotionType = goodsParam.getPromotionType();
        // 无优惠不保存优惠信息
        if(!"SW2001".equals(promotionType)){
            insertRelateList(goodsFullReduceService, goodsParam.getGoodsFullReduceList(), goodsParam.getId());
            insertRelateList(goodsLadderService, goodsParam.getGoodsLadderList(), goodsParam.getId());
        }
        int stock = insertSkuStock(goodsParam.getSkuStockList(), goodsParam.getPrice(), goodsParam.getId(), user);
        Goods newGoods = goodsMapper.selectById(goodsParam.getId());
        if (newGoods != null) {
            newGoods.setStock(stock);
            goodsMapper.updateById(newGoods);
        }
        return num;
    }

    /**
     * 更新商品
     * @param goodsParam
     * @param user
     * @return
     */
    public int updateGoods(GoodsParam goodsParam, User user) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String promotionType = goodsParam.getPromotionType();
        // 更新商品图片
        List<Map<String, String>> fileList = goodsParam.getSelectSkuPics();
        map.put("FILE_TYPE", FileDict.GOODS.getCode());
        map.put("FK_ID", goodsParam.getId());
        map.put("fileList", fileList);
        fileService.updateFile(map);
        // 无优惠不保存优惠信息
        if(!"SW2001".equals(promotionType)){
            // 先删除优惠信息在新增
            deleteGoodsFullList(goodsParam);
            insertRelateList(goodsFullReduceService, goodsParam.getGoodsFullReduceList(), goodsParam.getId());
            deleteGoodsLadderList(goodsParam);
            insertRelateList(goodsLadderService, goodsParam.getGoodsLadderList(), goodsParam.getId());
        }
        deleteSkuStock(goodsParam);
        int stock = insertSkuStock(goodsParam.getSkuStockList(), goodsParam.getPrice(), goodsParam.getId(), user);
        Goods goods = goodsParam;
        goods.setStock(stock);
        goods.setUpdateTime(DateUtil.getCurrentDateTime());
        goods.setUpdateUser(user.getId());
        goodsMapper.updateById(goods);
        return 1;
    }

    /**
     * 删除sku信息
     * @param goodsParam
     */
    private void deleteSkuStock(GoodsParam goodsParam) {
        QueryWrapper<Sku> entityWrapper = new QueryWrapper<>();
        entityWrapper.eq("GOODS_ID", goodsParam.getId());
        skuService.remove(entityWrapper);
    }

    /**
     * 删除满减优惠
     * @param goodsParam
     */
    private void deleteGoodsFullList(GoodsParam goodsParam) {
        QueryWrapper<GoodsFullReduce> entityWrapper = new QueryWrapper<>();
        entityWrapper.eq("GOODS_ID", goodsParam.getId());
        goodsFullReduceService.remove(entityWrapper);
    }

    /**
     * 删除阶梯优惠
     * @param goodsParam
     */
    private void deleteGoodsLadderList(GoodsParam goodsParam) {
        QueryWrapper<GoodsLadder> entityWrapper = new QueryWrapper<>();
        entityWrapper.eq("GOODS_ID", goodsParam.getId());
        goodsLadderService.remove(entityWrapper);
    }

    /**
     * 获取商品信息
     * @param goods
     * @return
     * @throws Exception
     */
    public Map<String, Object> getGoodsInfo(Goods goods) throws Exception {
        Map<String, Object> result = new HashMap<>();
        GoodsParam goodsParam = new GoodsParam();
        BeanUtil.fatherToChild(goods, goodsParam);

        // 获取父级分类
        Category category = categoryService.getById(goodsParam.getCategoryId());
        goodsParam.setParentCategoryId(category.getParentId());
        Category specCategory = categoryService.getById(goodsParam.getSpecCategoryId());
        goodsParam.setParentSpecCategoryId(specCategory.getParentId());
        goodsParam.setCategoryName(specCategory.getCategoryName());

        String promotionType = goods.getPromotionType();
        if(!"SW2001".equals(promotionType)){
            QueryWrapper<GoodsFullReduce> wrapper = new QueryWrapper<>();
            wrapper.eq("GOODS_ID", goodsParam.getId());
            List<GoodsFullReduce> goodsFullReduces = goodsFullReduceService.list(wrapper);
            if(CollectionUtil.isNotEmpty(goodsFullReduces)) {
                for(GoodsFullReduce fullReduce: goodsFullReduces){
                    fullReduce.setDefault(true);
                }
            }
            goodsParam.setGoodsFullReduceList(goodsFullReduces);
            QueryWrapper<GoodsLadder> goodsLadderEntityWrapper = new QueryWrapper<>();
            goodsLadderEntityWrapper.eq("GOODS_ID", goodsParam.getId());
            List<GoodsLadder> goodsLadders = goodsLadderService.list(goodsLadderEntityWrapper);
            if(CollectionUtil.isNotEmpty(goodsLadders)) {
                for(GoodsLadder goodsLadder: goodsLadders){
                    goodsLadder.setDefault(true);
                }
            }
            goodsParam.setGoodsLadderList(goodsLadders);

        }

        // 获取SKU
        QueryWrapper<Sku> wrapper = new QueryWrapper<>();
        wrapper.eq("GOODS_ID", goodsParam.getId());
        List<Sku> list = skuService.list(wrapper);
        goodsParam.setSkuStockList(list);
        List<Map<String, Object>> specValueList = new ArrayList<>();
        List<Map<String, Object>> specsList = dealSpecs(list, goodsParam, specValueList);
        LOGGER.debug("specsList: {}",specsList);
        goodsParam.setSpecsList(specsList);
        goodsParam.setSkuStockMapList(specValueList);

        // 品牌信息
        Brand brand = brandService.getById(goodsParam.getBrandId());
        if(brand == null){
            brand.setBrandName("品牌失效");
        }
        goodsParam.setBrand(brand);
        result.put("obj", goodsParam);

        return result;
    }

    /**
     * 处理配置的规则和sku
     * @param list
     * @param goodsParam
     * @param specValueList
     * @return
     */
    private List<Map<String, Object>> dealSpecs(List<Sku> list, GoodsParam goodsParam, List<Map<String, Object>> specValueList) {
        List<Map<String, Object>> specList = new ArrayList<>();
        List<String> specNames = new ArrayList<>();
        List<String> specOptionIds = new ArrayList<>();
        List<Map<String, Object>> specOptionList = null;
        if(CollectionUtil.isEmpty(list)) {
            return specList;
        }
        for(Sku sku:list){
            Map<String, Object> map;
            map = sku.toMap();
            String specValue = sku.getSpecValue();
            String[] specValues = specValue.split(";");
            if(specValues.length > 1) {
                for(int i = 0; i < specValues.length;i++){
                    Set<String> values = new HashSet<>();
                    String _val = specValues[i].substring(1, specValues[i].length() - 1);
                    String[] split = _val.split(",");
                    // specOptionId
                    String specOptionId = split[0];
                    values.add(specOptionId);
                    String value = split[1];
                    map.put("value"+i, value);
                    Map<String, Object> spec = specsService.getSpecs(specOptionId);
                    if(spec == null && spec.isEmpty()) {
                        continue;
                    }
                    String name = MapUtil.getString(spec, "specName");
                    String specId = MapUtil.getString(spec, "specId");
                    String id = name + "_" + value + "_" +specOptionId;
                    if(!specOptionIds.contains(id)){
                        specOptionList = new ArrayList<>();
                        if(CollectionUtil.isNotEmpty(specList)){
                            for(Map<String, Object> _spec:specList){
                                String _name = MapUtil.getString(_spec, "specName");
                                if(name.equals(_name)){
                                    specOptionList = (List<Map<String, Object>>) _spec.get("specOptionList");
                                    specOptionList.get(0).put("active", false);
                                }
                            }
                        }
                        specOptionIds.add(id);
                        Map specOptionMap = new HashMap();
                        specOptionMap.put("id", specOptionId);
                        specOptionMap.put("name", value);
                        specOptionMap.put("active", false);
                        specOptionMap.put("specId", specId);
                        specOptionMap.put("specName", name);
                        specOptionList.add(specOptionMap);
                        sortOption(specOptionList);
                        specOptionList.get(0).put("active", true);
                        spec.put("specOptionList", specOptionList);
                    }
                    if(!specNames.contains(name)){
                        QueryWrapper<SpecOption> entityWrapper = new QueryWrapper<>();
                        entityWrapper.eq("IS_DELETE", 0);
                        entityWrapper.eq("SPECS_ID", MapUtil.getLong(spec, "specId"));
                        List<SpecOption> specOptions = specOptionService.list(entityWrapper);
                        if(CollectionUtil.isNotEmpty(specOptions)){
                            spec.put("specOptions", specOptions);
                        }
                        spec.put("values", values);
                        specList.add(spec);
                        specNames.add(name);
                    }else{
                        for(Map<String, Object> _map:specList){
                            String _name = MapUtil.getString(_map, "specName");
                            if(name.equals(_name)){
                                Set<String> set = (Set<String>) _map.get("values");
                                set.add(specOptionId);
                            }
                        }
                    }
                }
            }
            specValueList.add(map);
        }
        return specList;
    }

    private void sortOption(List<Map<String, Object>> specOptionList) {
        if(CollectionUtil.isEmpty(specOptionList)){
            return;
        }
        Collections.sort(specOptionList, (o1, o2) -> {
            String name1 = MapUtil.getString(o1, "name"); //name1是从你list里面拿出来的一个
            String name2 = MapUtil.getString(o2, "name"); //name1是从你list里面拿出来的第二个name
            if(name1.compareTo(name2) < 0){
                return -1;
            }else if(name1.compareTo(name2) > 0){
                return 1;
            }else{
                return 0;
            }
        });
    }

    private void insertRelateList(ServiceImpl service, List list, Long id) throws Exception {
        if(CollectionUtil.isEmpty(list)){
            return;
        }
        for(Object data:list){
            Method defaultMethod = data.getClass().getMethod("isDefault");
            Object isDefault = defaultMethod.invoke(data);
            if(isDefault.equals(true)) {
                Method method = data.getClass().getMethod("setGoodsId", String.class);
                method.invoke(data, id);
                Method setIdMethod = data.getClass().getMethod("setId", String.class);
                setIdMethod.invoke(data, SnowflakeIdWorker.generateId());
                service.save(data);
            }
        }
    }

    /**
     * 保存SKU
     * @param skuStockList
     * @param price
     * @param pkGoodsId
     */
    private int insertSkuStock(List<Sku> skuStockList, BigDecimal price, Long pkGoodsId, User user) {
        Long userId =  user.getId();
        int stock = 0;
        if(CollectionUtil.isEmpty(skuStockList)){
            return stock;
        }
        for(Sku sku:skuStockList) {
            if(StringUtil.isEmpty(sku.getSkuStock())) {
                sku.setSkuStock(0);
            }
            if(StringUtil.isEmpty(sku.getWarnStock())) {
                sku.setWarnStock(1);
            }
            if(StringUtil.isEmpty(sku.getSkuPrice())) {
                sku.setSkuPrice(price);
            }
            stock += sku.getSkuStock();
            dealSkuCode(sku, pkGoodsId);
            sku.setId(SnowflakeIdWorker.generateId());
            sku.setGoodsId(pkGoodsId);
            sku.setSkuStatus(StatusDict.START.getCode());
            sku.setIsDelete(0);
            sku.setAddTime(DateUtil.getCurrentDateTime());
            sku.setAddUser(userId);
            sku.setUpdateTime(DateUtil.getCurrentDateTime());
            sku.setUpdateUser(userId);
            skuService.save(sku);
        }
        return stock;
    }

    /**
     * 生成SKU编码
     * @param sku
     */
    private void dealSkuCode(Sku sku, Long pkGoodsId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String no = StringUtil.getOrderNo(pkGoodsId.toString(), sdf);
        sku.setSkuCode(no);
    }

    private static void test(Object data, String id) throws Exception {
        Method defaultMethod = data.getClass().getMethod("isDefault");
        Object isDefaut = defaultMethod.invoke(data);
        if(isDefaut.equals(false)){
            System.out.println(isDefaut);
        }
    }


    public static void main(String[] args) throws Exception {



        /*GoodsLadder goodsLadder = new GoodsLadder();
        goodsLadder.setDefault(true);
        test(goodsLadder, "111");
        System.out.println(goodsLadder);*/
        /*String result="";
        Random random=new Random();
        for(int i=0;i<3;i++){
            result+=random.nextInt(10);
        }
        System.out.println(result);


        String a="c51e741b745e3da0a51a97892e06e7aa";
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(a);
        String numStr = m.replaceAll("").trim();
        if (numStr.length() > 19){
            numStr = numStr.substring(0, 18);
        }
        System.out.println(String.format("%04d", Long.parseLong(numStr)).substring(0,4));*/


       /* List<Map<String, Object>> set =  new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("sss","ssss");
        map.put("qqq","ssss");
        map.put("qq1","ssss");
        map.put("223","ssss");
        set.add(map);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("sss","ssss");
        map2.put("qqq","ssss");
        map2.put("qq1","ssss");
        map2.put("223","ssss");
        //set.add(map2);

        System.out.println(map.equals(map2));*/
      /* String s1 = "36";
       String s2 = "37";
       System.out.println(s1.compareTo(s2));*/

        List<Map<String, Object>> set =  new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("name","36");
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name","37");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name","38");
        Map<String, Object> map3 = new HashMap<>();
        map3.put("name","39");
        set.add(map3);
        set.add(map1);
        set.add(map);
        set.add(map2);
        System.out.println(set);
        new GoodsServiceImpl().sortOption(set);
        System.out.println(set);
    }

    @Override
    public void setFile(Goods goods) {
        QueryWrapper<File> fileEntityWrapper = new QueryWrapper<>();
        fileEntityWrapper.eq("FILE_TYPE", FileDict.GOODS.getCode());
        fileEntityWrapper.eq("IS_DELETE", 0);
        fileEntityWrapper.eq("FK_ID", goods.getId());
        List<File> sysFiles = fileService.list(fileEntityWrapper);
        if(CollectionUtil.isNotEmpty(sysFiles)){
            goods.setFileList(sysFiles);
            goods.setFileUrl(sysFiles.get(0).getFileUrl());
        }else{
            goods.setFileUrl(DEFAULT_URL);
        }
    }

    @Override
    public Result<GoodsResult> getGoodsListByCondition(GoodsQueryDto goodsQueryDto) {
        GoodsResult goodsResult = new GoodsResult();
        int page = goodsQueryDto.getPage();
        int limit = goodsQueryDto.getLimit();
        int totalPage = 0;
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        String sort = goodsQueryDto.getSort();
        if ("default".equals(sort) || "category".equals(sort)) {
            // 综合排序处理
            sort = "GOODS_SEQ";
        }

        if (StringUtil.isNotEmpty(goodsQueryDto.getKeyword())) {
            wrapper.and(_wrapper -> _wrapper.like("GOODS_NAME", goodsQueryDto.getKeyword()).or().like("GOODS_CODE", goodsQueryDto.getKeyword()));
        }
        if (StringUtil.isNotEmpty(goodsQueryDto.getCategoryId())) {
            wrapper.eq("CATEGORY_ID", goodsQueryDto.getCategoryId());
        }
        if (StringUtil.isNotEmpty(goodsQueryDto.getBrandId())) {
            wrapper.eq("BRAND_ID", goodsQueryDto.getBrandId());
        }
        if (StringUtil.isNotEmpty(goodsQueryDto.getStatus())) {
            wrapper.eq("STATUS", goodsQueryDto.getStatus());
        } else {
            wrapper.eq("STATUS", SaleOrNotDict.SALE.getCode());
        }
        if (StringUtil.isNotEmpty(goodsQueryDto.getIsUsed())) {
            wrapper.eq("IS_USED", goodsQueryDto.getIsUsed());
        } else {
            wrapper.eq("IS_USED", StatusDict.START.getCode());
        }
        if (StringUtil.isNotEmpty(goodsQueryDto.getYear())) {
            wrapper.gt("SALE_TIME", goodsQueryDto.getYear()+"-01-01 00:00:00");
        }
        if (StringUtil.isNotEmpty(goodsQueryDto.getUnit())) {
            wrapper.eq("UNIT", goodsQueryDto.getUnit());
        }
        if (StringUtil.isNotEmpty(goodsQueryDto.getSeason())) {
            wrapper.eq("SEASON", goodsQueryDto.getUnit());
        }

        String order = goodsQueryDto.getOrder();
        boolean isAsc;
        if ("asc".endsWith(order)) {
            isAsc = true;
        } else {
            isAsc = false;
        }
        wrapper.orderBy(true, isAsc, sort);

        int total = goodsMapper.selectCount(wrapper);
        Page<Goods> pages = goodsMapper.selectPage(new Page<>(page, limit), wrapper);
        List<Goods> list = pages.getRecords();
        if(CollectionUtil.isNotEmpty(list)){
            for (Goods goods: list){
                setFile(goods);
            }
        }

        int num = total%limit;
        if(num == 0){
            totalPage = total/limit;
        }else{
            totalPage = total/limit + 1;
        }

        goodsResult.setCurrentPage(page);
        goodsResult.setTotalPage(totalPage);
        goodsResult.setGoodsList(list);

        Result<GoodsResult> resultResult = new Result<>();
        resultResult.setData(goodsResult);
        return resultResult;
    }

    @Override
    public Result<GoodsResult> getStock(GoodsQueryDto goodsQueryDto) {
        GoodsResult goodsResult;
        Result<GoodsResult> result = new Result<>();
        goodsResult = goodsMapper.getStock(goodsQueryDto);
        if (goodsResult != null) {
            BigDecimal cost = goodsResult.getTotalCost().divide(new BigDecimal(10000)).setScale(2, BigDecimal.ROUND_HALF_UP);
            goodsResult.setCost(cost);
            int warnNum = goodsMapper.getWarnStock(goodsQueryDto);
            goodsResult.setTotalWarnStock(warnNum);
        }
        result.setData(goodsResult);
        return result;
    }

    @Override
    public int deleteGoods(User user, Long id) {
        Goods goods = goodsMapper.selectById(id);
        if (goods != null) {
            goods.setIsDelete(1);
            goods.setUpdateTime(DateUtil.getCurrentDateTime());
            goods.setUpdateUser(user.getId());
            goodsMapper.updateById(goods);
        }
        return 0;
    }

    @Override
    public Result<GoodsResult> importGoods(GoodsQueryDto goodsQueryDto, User user) {
        LOGGER.info("需要导入的商品集合：{}", goodsQueryDto);
        List<GoodsParam> goodsList = goodsQueryDto.getGoodsList();
        if (CollectionUtil.isEmpty(goodsList)) {
            LOGGER.info("需要导入的商品集合为空");
            return new Result<>();
        }
        goodsList.stream().forEach(goodsParam -> {
            goodsParam.setId(SnowflakeIdWorker.generateId());
            goodsParam.setBrandId(758418558159491072L);
            goodsParam.setCategoryId(758780717586518016L);
            goodsParam.setSpecCategoryId(758780717586518016L);
            goodsParam.setGoodsBrief(goodsParam.getGoodsName());
            goodsParam.setUnit("SW1610");
            goodsParam.setStatus(SaleOrNotDict.SALE.getCode());
            goodsParam.setIsUsed(StatusDict.START.getCode());
            goodsParam.setIsSpec(IsOrNoDict.NO.getCode());
            goodsParam.setIsBest(IsOrNoDict.NO.getCode());
            goodsParam.setIsHot(IsOrNoDict.NO.getCode());
            goodsParam.setIsNew(IsOrNoDict.YES.getCode());
            goodsParam.setIsRecom(IsOrNoDict.NO.getCode());
            goodsParam.setPromotionType(PromotionDict.NO.getCode());
            goodsParam.setSaleNum(0);
            goodsParam.setKeywords(goodsParam.getGoodsName()+","+goodsParam.getGoodsCode());
            String color = goodsParam.getColor();
            List<String> colorList = Arrays.asList(color.split(","));
            String size = goodsParam.getSize();
            List<String> sizeList = Arrays.asList(size.split(","));
            List<List<String>> specList= new ArrayList<>();
            specList.add(colorList);
            specList.add(sizeList);
            List<Sku> skuList = new ArrayList<>();
            dealWidthSpecs(specList, skuList, 0, "", user);
            goodsParam.setSkuStockList(skuList);
            try {
                createGoods(goodsParam, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Result<GoodsResult> result = new Result<>();
        result.setMessage("导入成功");
        return result;

    }

    @Override
    public DataResponse getGoodsList(Map<String, Object> params) {
        String keyword = MapUtil.getString(params, "keyword");
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        if(StringUtil.isNotEmpty(keyword)){
            wrapper.and(_wrapper -> _wrapper.like("GOODS_NAME", keyword).or().like("GOODS_CODE", keyword));
        }
        List<Goods> list = goodsMapper.selectList(wrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return DataResponse.success(result);
    }

    @Override
    public DataResponse getGoodsListByType(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String goodsType = MapUtil.getString(params, "goodsType");
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("IS_USED", StatusDict.START.getCode());
        if (StringUtil.isEmpty(goodsType)) {
            result.put("goodsList", new ArrayList<>());
            return DataResponse.success(result);
        }
        if ("new".equals(goodsType)) {
            wrapper.eq("IS_NEW", IsOrNoDict.YES.getCode());
        } else if ("hot".equals(goodsType)) {
            wrapper.eq("IS_HOT", IsOrNoDict.YES.getCode());
        } else if ("recommend".equals(goodsType)) {
            wrapper.eq("IS_RECOM", IsOrNoDict.YES.getCode());
        } else if ("best".equals(goodsType)) {
            wrapper.eq("IS_BEST", IsOrNoDict.YES.getCode());
        }
        List<Goods> list = goodsMapper.selectList(wrapper);
        if(CollectionUtil.isNotEmpty(list)){
            for(Goods goods:list){
                setFile(goods);
            }
        }
        result.put("goodsList", list);
        return DataResponse.success(result);
    }

    @Override
    public DataResponse updateLabel(User user, Map<String, Object> params) {
        LOGGER.debug("保存参数：{}", params);
        Map<String, Object> result = new HashMap<>();
        Long goodsId = MapUtil.getLong(params, "id");
        String label = MapUtil.getString(params, "label");
        String status = MapUtil.getString(params, "status");
        Goods goods = goodsMapper.selectById(goodsId);
        if(goods == null){
            return DataResponse.fail("更新失败, 商品不存在");
        }
        if("isUsed".equals(label)){
            goods.setIsUsed(status);
        }else if("isRecom".equals(label)){
            goods.setIsRecom(status);
        }else if("isSpec".equals(label)){
            goods.setIsSpec(status);
        }else if("isBest".equals(label)){
            goods.setIsBest(status);
        }else if("isHot".equals(label)){
            goods.setIsHot(status);
        }else if("isNew".equals(label)){
            goods.setIsNew(status);
        }

        Long userId = user.getId();

        goods.setUpdateTime(DateUtil.getCurrentDateTime());
        goods.setUpdateUser(userId);
        int flag = goodsMapper.updateById(goods);

        if(flag == 0){
            return DataResponse.fail("更新商品状态失败");
        }

        return DataResponse.success(result);
    }

    @Override
    public DataResponse getGoodsByCategory(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int totalPage = 0;
        int page = MapUtil.getIntValue(params, "page");
        int limit = MapUtil.getIntValue(params, "limit");
        String id = MapUtil.getMapValue(params, "categoryId");
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("CATEGORY_ID", id);

        int total = goodsMapper.selectCount(wrapper);
        Page<Goods> pages = goodsMapper.selectPage(new Page<>(page, limit), wrapper);
        List<Goods> list = pages.getRecords();
        if(CollectionUtil.isNotEmpty(list)){
            for (Goods goods: list){
                setFile(goods);
            }
        }

        if(total%limit == 0){
            totalPage = total/limit;
        }else{
            totalPage = total/limit + 1;
        }

        result.put("currentPage", page);
        result.put("totalPage", totalPage);
        result.put("goods", list);

        return DataResponse.success(result);
    }

    private void dealWidthSpecs(List<List<String>> specList, List<Sku> skuList, int index, String specOption, User user) {
        // 集合数量大于1
        if (index < specList.size() -1 ) {
            // 大于一个集合，且第一个集合为空
            if (CollectionUtil.isEmpty(specList.get(index))) {
                dealWidthSpecs(specList, skuList, index + 1, specOption, user);
            } else {
                for (int i = 0; i < specList.get(index).size() - 1; i++ ) {
                    String specValue = dealSpecOption(specList, i, index, specOption, user);
                    dealWidthSpecs(specList, skuList, index + 1, specValue, user);
                }
            }
        } else if (index == specList.size() - 1 ) {
            if (CollectionUtil.isEmpty(specList.get(index))) {
                Sku sku = new Sku();
                sku.setSkuStock(5);
                sku.setSpecValue(specOption);
                skuList.add(sku);
            } else {
                for (int i = 0; i < specList.get(index).size() - 1; i++ ) {
                    String specValue = dealSpecOption(specList, i, index, specOption, user);
                    Sku sku = new Sku();
                    sku.setSkuStock(5);
                    sku.setSpecValue(specValue);
                    skuList.add(sku);
                }
            }
        }
    }

    private String dealSpecOption(List<List<String>> specList,int i, int index,String specOption, User user) {
        Long specId = Long.parseLong(specList.get(index).get(specList.get(index).size() - 1));
        StringBuffer specValue = new StringBuffer();
        String name = specList.get(index).get(i);
        QueryWrapper<SpecOption> specsQueryWrapper = new QueryWrapper<>();
        specsQueryWrapper.eq("NAME", name);
        SpecOption option = specOptionService.getOne(specsQueryWrapper);
        if (option == null) {
            option = new SpecOption();
            option.setId(SnowflakeIdWorker.generateId());
            option.setName(name);
            option.setCode(specOptionService.getMaxCode(specId.toString()));
            option.setSpecsId(specId);
            option.setIsDelete(0);
            option.setAddUser(user.getAddUser());
            option.setAddTime(DateUtil.getCurrentDateTime());
            option.setUpdateUser(user.getUpdateUser());
            option.setUpdateTime(DateUtil.getCurrentDateTime());
            specOptionService.save(option);
        }
        if (StringUtil.isEmpty(specOption)) {
            specValue.append(specOption).append("[").append(option.getId()).append(",").append(name).append("]");
        } else {
            specValue.append(specOption).append(";[").append(option.getId()).append(",").append(name).append("]");
        }
        return specValue.toString();
    }
}
