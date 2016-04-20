package org.domeos.framework.api.biz.base.impl;

import org.domeos.framework.api.biz.base.BaseBiz;
import org.domeos.framework.api.model.resource.Resource;
import org.domeos.framework.engine.exception.DaoConvertingException;
import org.domeos.framework.engine.exception.DaoException;
import org.domeos.framework.engine.mapper.RowMapper;
import org.domeos.framework.engine.model.RowMapperDao;
import org.domeos.framework.engine.model.RowModelBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sparkchen on 16/4/5.
 */

@Service("baseBiz")
public class BaseBizImpl implements BaseBiz {

    @Autowired
    RowMapper mapper;

    public <T extends RowModelBase> T checkResult(RowMapperDao dao, Class<T> clazz) {
        if (dao == null ) {
            return null;
        }
        if (dao.isRemoved()) {
            return null;
        }
        T result = dao.toModel(clazz);
        if (result.getId() <= 0) {
            throw new DaoConvertingException("Get MySQL Data failed! id=" + result.getId()
                + ", getContent=" + dao);
        }
        return result;
    }

    @Override
    public <T extends RowModelBase> T getById(String tableName, int id, Class<T> clazz) {
        RowMapperDao dao = null;
        try {
            dao = mapper.getById(tableName, id);
            if (dao == null ) {
                return null;
            }
            if (dao.isRemoved()) {
                return null;
            }
            T result = dao.toModel(clazz);
            if (result.getId() <= 0) {
                throw new DaoConvertingException("Get MySQL Data failed! tableName=" + tableName
                    + ", getContent=" + dao);
            }

            return result;
        } catch (Exception e) {
            throw new DaoConvertingException("Get MySQL Data failed! tableName=" + tableName
                + ", getContent=" + dao, e);
        }

    }


    @Override
    public <T extends RowModelBase> T getByName(String tableName, String name, Class<T> clazz) {
        RowMapperDao dao = null;
        try {
            dao = mapper.getByName(tableName, name);
            if (dao == null ) {
                return null;
            }
            if (dao.isRemoved()) {
                return null;
            }
            T result = dao.toModel(clazz);
            if (result.getId() <= 0) {
                throw new DaoConvertingException("Get MySQL Data failed! tableName=" + tableName
                    + ", getContent=" + dao);
            }
            return result;
        } catch (Exception e) {
            throw new DaoConvertingException("Get MySQL Data failed! tableName=" + tableName
                + ", getContent=" + dao, e);
        }

    }

    @Override
    public <T extends RowModelBase> List<T> getList(String tableName, Class<T> clazz) {
        try {
            List<RowMapperDao> list = mapper.getList(tableName);
            List<T> result = new ArrayList<>();
            for (RowMapperDao dao: list) {
                result.add(dao.toModel(clazz));
            }
            return result;
        }catch (Exception e) {
            throw new DaoConvertingException("Get MySQL Data failed! tableName=" + tableName, e);
        }
    }

    @Override
    public <T extends RowModelBase> List<T> getListByName(String tableName, String name, Class<T> clazz) {
        try {
            List<RowMapperDao> list = mapper.getListByName(tableName, name);
            List<T> result = new ArrayList<>();
            for (RowMapperDao dao: list) {
                result.add(dao.toModel(clazz));
            }
            return result;
        }catch (Exception e) {
            throw new DaoConvertingException("Get MySQL Data failed! tableName=" + tableName + ", name=" + name, e );
        }
    }

    @Override
    public <T extends RowModelBase> List<T> getListByReousrce(String tableName, List<Resource> resourceList, Class<T> clazz) {
        try {
            if (resourceList == null || resourceList.size() == 0) {
                return new ArrayList<>();
            }
            StringBuilder builder = new StringBuilder();
            builder.append(" ( ");
            for (int i = 0; i < resourceList.size(); i++) {
                builder.append(resourceList.get(i).getResourceId());
                if (i != resourceList.size() - 1) {
                    builder.append(" , ");
                }
            }
            builder.append(") ");
            List<RowMapperDao> list = mapper.getByIdList(tableName, builder.toString());
            List<T> result = new ArrayList<>();
            for (RowMapperDao dao: list) {
                result.add(dao.toModel(clazz));
            }
            return result;
        }catch (Exception e) {
            throw new DaoConvertingException("Get MySQL Data failed! tableName=" + tableName
                + ", resourceList=" + resourceList, e );
        }
    }

    @Override
    public <T extends RowModelBase> List<T> getWholeTable(String tableName, Class<T> clazz) {
        try {
            List<RowMapperDao> list = mapper.getList(tableName);
            List<T> result = new ArrayList<>();
            for (RowMapperDao dao: list) {
                result.add(dao.toModel(clazz));
            }
            return result;
        }catch (Exception e) {
            throw new DaoConvertingException("Get MySQL Data failed! tableName=" + tableName, e);
        }
    }

    @Override
    public void removeById(String tableName, int id) {
        mapper.removeRowById(tableName, id, System.currentTimeMillis());
    }

    @Override
    public void updateRow(String tableName, RowModelBase rowModelBase) throws DaoException {
        try {
            mapper.updateRow(tableName, new RowMapperDao(rowModelBase));
        } catch (Exception e) {
            throw new DaoException(e);
        }

    }

    @Override
    public void insertRow(String tableName, RowModelBase rowModelBase) throws DaoException{
        if (rowModelBase.getCreateTime() == 0) {
            rowModelBase.setCreateTime(System.currentTimeMillis());
        }
        try {
//            if (getMoreColumn(rowModelBase.getClass()).length() > 0) {
//                throw new DaoException("More Columns are detected, need to Override insertRowForProject for class:"
//                    + rowModelBase.getClass().getName());
//
//            }
            RowMapperDao dao = new RowMapperDao(rowModelBase);
            mapper.insertRow(tableName, dao);
            rowModelBase.setId(dao.getId());
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void updateState(String tableName, String state, int id) {
        mapper.updateState(tableName, state, id);
    }

    @Override
    public String getState(String tableName, int id) {
        return mapper.getStateById(tableName, id);
    }

    @Override
    public String getNameById(String tableName, int id) {
        return mapper.getNameById(tableName, id);
    }
//
//    public <T extends RowModelBase> String getMoreColumn(Class<T> clazz) throws Exception {
//        T tmp = clazz.newInstance();
//        if (tmp.excludeForJSON().size() > RowModelBase.toExclude.size()) {
//            // more columns are standalone
//            Set<String> cols = new HashSet<>();
//            cols.addAll(tmp.excludeForJSON());
//            cols.removeAll(RowModelBase.toExclude);
//            StringBuilder builder = new StringBuilder();
//            for (String str : cols) {
//                builder.append(", " ).append(str);
//            }
//            return builder.append(" ").toString();
//        }
//        return "";
//    }
}
