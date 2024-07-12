package org.jeecg.modules.voucher.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.voucher.entity.Voucher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 填制凭证
 * @Author: jeecg-boot
 * @Date:   2023-05-06
 * @Version: V1.0
 */
@Mapper
public interface VoucherMapper extends BaseMapper<Voucher> {

}
