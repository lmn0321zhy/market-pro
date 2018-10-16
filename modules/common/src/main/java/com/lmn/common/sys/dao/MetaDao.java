package com.lmn.common.sys.dao;


import com.lmn.common.base.CrudDao;
import com.lmn.common.sys.entity.Meta;
import com.lmn.common.sys.entity.MetaContent;

import java.util.List;
import java.util.Map;

/**
 * 媒体数据DAO接口
 */
public interface MetaDao extends CrudDao<Meta> {
    /**
     * 插入媒体与内容关联数据
     *
     * @param metaContents
     * @return
     */
    public int insertMetaContent(List<MetaContent> metaContents);


    /**
     * 删除媒体关联数据
     *
     * @param metaContent
     * @return
     */
    public int deleteMetaContent(MetaContent metaContent);


    /**
     * 根据文件hash获取文件
     *
     * @param hash
     * @return
     */
    public Meta getByHash(String hash);

    /**
     * 获取媒体内容表映射关系
     *
     * @param metaContent
     * @return
     */
    public MetaContent getMetaContent(MetaContent metaContent);

    /**
     * 获取媒体内容表映射关系列表
     *
     * @param metaContent
     * @return
     */
    public List<MetaContent> findMetaContentList(MetaContent metaContent);


    /**
     * 根据用户id查找媒体数据
     *
     * @param meta
     * @return
     */
    public List<Meta> findMetaByUserId(Meta meta);

    /**
     * 由媒体id与用户id获取关联数据
     *
     * @param meta
     * @return
     */
    public Meta getUserMeta(Meta meta);

    /**
     * 删除媒体与用户关联数据
     *
     * @param meta
     * @return
     */
    public int deleteUserMeta(Meta meta);

    /**
     * 插入媒体与用户关联数据
     *
     * @param meta
     * @return
     */
    public int insertUserMeta(Meta meta);

    /**
     * 查询文物资源文档
     */
    public List<Map> findListByOffice(Map map);

    /**
     * 查询文物资源文档
     */
    public List<Map> findListByStation(Map map);

    /*
    * 查询文档的文件列表
    * */
    public List<Map> findMetaListByDocument(Map map);

}