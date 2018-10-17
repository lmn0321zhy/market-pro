package com.lmn.common.sys.service;

import com.github.pagehelper.PageInfo;
import com.lmn.common.base.CrudService;
import com.lmn.common.base.IUser;
import com.lmn.common.security.ShiroUtils;
import com.lmn.common.dao.MetaDao;
import com.lmn.common.sys.entity.Meta;
import com.google.common.collect.Lists;
import com.lmn.common.sys.entity.MetaContent;
import com.lmn.common.ui.FileInfo;
import com.lmn.common.ui.ManageMsg;
import com.lmn.common.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 媒体数据Service
 */
@Service
public class MetaService extends CrudService<MetaDao, Meta> {

    public Meta get(String id) {
        return super.get(id);
    }

    public List<Meta> findList(Meta meta) {
        return super.findList(meta);
    }


    public PageInfo<Meta> findPage(HttpServletRequest request, Meta meta) {
        return super.findPage(request, meta);
    }

    public void save(Meta meta) {
        super.save(meta);

        IUser user = ShiroUtils.getUser();
        meta.setUser(user);

        //保存媒体用户信息关联表
        if (dao.getUserMeta(meta) == null) {
            dao.insertUserMeta(meta);
        }
    }

    public void delete(Meta meta) {
        super.delete(meta);
    }

    public MetaContent getMetaContent(MetaContent metaContent) {
        return dao.getMetaContent(metaContent);
    }

    public List<MetaContent> findMetaContentList(MetaContent metaContent) {
        return dao.findMetaContentList(metaContent);
    }

    public void insertMetaContent(List<MetaContent> metaContents) {
        //过滤已经插入的关系
        for (int i = 0; i < metaContents.size(); i++) {
            MetaContent metaContent = metaContents.get(i);
            if (getMetaContent(metaContent) != null) {
                metaContents.remove(metaContent);
            }
        }
        if (metaContents != null && metaContents.size() > 0)
            dao.insertMetaContent(metaContents);
    }

    public void deleteMetaContent(MetaContent metaContent) {
        dao.deleteMetaContent(metaContent);
    }


    public List<Meta> findByContentId(String contentId) {
        return findByContentId(contentId, null);
    }

    public Meta getByHash(String hash) {
        return dao.getByHash(hash);
    }

    public List<Meta> findByContentId(String contentId, String ext) {
        if (StringUtils.isBlank(contentId)) return Lists.newArrayList();
        Meta meta = new Meta();
        MetaContent metaContent = new MetaContent();
        metaContent.setContentId(contentId);
        meta.setMetaContent(metaContent);
        meta.setExt(ext);
        return dao.findList(meta);
    }


    public PageInfo<Meta> findByUserPage(HttpServletRequest request, Meta meta) {
        IUser user = ShiroUtils.getUser();
        if (ShiroUtils.isAdmin()) {
            return findPage(request, meta);
        } else {
            meta.setUser(user);
            return findPage(request, meta);
        }
    }

    public ManageMsg findManageMeta(String contentId) {
        ManageMsg manageMsg = new ManageMsg();
        Meta meta = new Meta();
        //当前页面为新加时返回当前用户所有已经插入的媒体列表
        if (StringUtils.isBlank(contentId)) {
            meta.setUser(ShiroUtils.getUser());
        } else {
            MetaContent metaContent = new MetaContent();
            metaContent.setContentId(contentId);
            meta.setMetaContent(metaContent);
        }
        List<Meta> metas = dao.findList(meta);

        List<FileInfo> files = Lists.newArrayList();

        for (Meta m : metas) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setId(m.getId());
            fileInfo.setSize(m.getSize());
            fileInfo.setType(m.getExt());
            fileInfo.setDatetime(m.getCreateDate());
            fileInfo.setName(m.getName());
            fileInfo.setUrl(m.getPath());
            files.add(fileInfo);
        }

        manageMsg.setFile_list(files);
        manageMsg.setTotal_count(files.size());

        return manageMsg;
    }

    public List<Map> findListByOffice(Map map) {
        return dao.findListByOffice(map);
    }

    public List<Map> findListByStation(Map map) {
        return dao.findListByStation(map);
    }

    public List<Map> findMetaListByDocument(Map map) {
        return dao.findMetaListByDocument(map);
    }
}