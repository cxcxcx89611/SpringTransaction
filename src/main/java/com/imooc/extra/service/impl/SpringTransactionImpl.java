package com.imooc.extra.service.impl;

import com.imooc.extra.dao.ExtraAdDao;
import com.imooc.extra.entity.ExtraAd;
import com.imooc.extra.exception.CustomException;
import com.imooc.extra.service.ISpringTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * <h1>Spring Transactional 测试接口实现</h1>
 * Created by izzy.
 */
@Slf4j
@Service
public class SpringTransactionImpl implements ISpringTransaction {

    /** ExtraAd Dao */
    private final ExtraAdDao extraAdDao;

    @Autowired
    public SpringTransactionImpl(ExtraAdDao extraAdDao) {
        this.extraAdDao = extraAdDao;
    }

    /**
     * <h2>捕捉异常, 导致不能回滚</h2>
     * */
    @Override
    @Transactional
    public void CatchExceptionCanNotRollback() {

        try {
            extraAdDao.save(new ExtraAd("izzy"));
            throw new RuntimeException();
        } catch (Exception ex) {
            ex.printStackTrace();
            // 手动标记回滚
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    /**
     * <h2>捕捉异常并转换异常, 导致不能回滚</h2>
     * */
    @Override
    @Transactional
    public void NotRuntimeExceptionCanNotRollback() throws CustomException {

        try {
            extraAdDao.save(new ExtraAd("izzy"));
            throw new RuntimeException();
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        }
    }

    /**
     * <h2>RuntimeException 异常可以回滚</h2>
     * */
    @Override
    @Transactional
    public void RuntimeExceptionCanRollback() {

        extraAdDao.save(new ExtraAd("izzy"));
        throw new RuntimeException();
    }

    /**
     * <h2>指定异常, 可以回滚</h2>
     * */
    @Override
    @Transactional(rollbackFor = {CustomException.class})
    public void AssignExceptionCanRollback() throws CustomException {

        try {
            extraAdDao.save(new ExtraAd("izzy"));
            throw new RuntimeException();
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * <h2>在 private 方法上标注 transactional, 事务无效</h2>
     * */
    @Transactional
    public void oneSaveMethod() {

        extraAdDao.save(new ExtraAd("izzy"));
    }

    /**
     * <h2>Rollback Only</h2>
     * org.springframework.transaction.UnexpectedRollbackException:
     * Transaction silently rolled back because it has been marked as rollback-only
     * 有任何一个transaction  异常 则整个 回滚   传播机制 合成transaction
     * */
    @Override
    @Transactional
    public void RollbackOnlyCanRollback() throws Exception {

        oneSaveMethod();

        try {
            extraAdDao.save(new ExtraAd());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
            // 若此处throw不抛出异常， 则rollbackonlycanrollback  无法捕捉异常 会 自动产生 transaction 处理的异常 造成没有加入新数据 而不是roll back生效
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * <h2>在private方法上标注transactional, 事务无效</h2>
     * */
    @Transactional
    public void anotherOneSaveMethod() {

        extraAdDao.save(new ExtraAd("izzy"));
        throw new RuntimeException();
    }

    /**
     * <h2>同一个类中, 一个不标注事务的方法去调用 transactional 的方法, 事务会失效</h2>
     * */
    @Override
//    @Transactional
    public void NonTransactionalCanNotRollback() {

        anotherOneSaveMethod();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
