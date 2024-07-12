package org.jeecg.modules.initial.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.initial.entity.Merchant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 客商管理
 * @Author: jeecg-boot
 * @Date:   2023-03-27
 * @Version: V1.0
 */
@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {

}
