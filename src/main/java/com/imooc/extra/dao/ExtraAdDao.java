package com.imooc.extra.dao;

import com.imooc.extra.entity.ExtraAd;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1>ExtraAd Dao 接口定义</h1>
 * Created by izzy.
 */
public interface ExtraAdDao extends JpaRepository<ExtraAd, Long> {
}
