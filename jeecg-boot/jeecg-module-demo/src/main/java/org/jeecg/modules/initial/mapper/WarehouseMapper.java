package org.jeecg.modules.initial.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.initial.entity.Warehouse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 仓库档案
 * @Author: jeecg-boot
 * @Date:   2023-04-29
 * @Version: V1.0
 */
@Mapper
public interface WarehouseMapper extends BaseMapper<Warehouse> {

}
