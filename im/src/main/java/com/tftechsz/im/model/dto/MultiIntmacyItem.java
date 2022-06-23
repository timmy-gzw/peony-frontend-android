package com.tftechsz.im.model.dto;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.tftechsz.common.entity.IntimacyDto;

/**
 * 包 名 : com.tftechsz.im.api
 * 描 述 : TODO
 */
public class MultiIntmacyItem implements MultiItemEntity {
    private final IntimacyDto.IntimacyConfigDTO dto;

    public IntimacyDto.IntimacyConfigDTO getDto() {
        return dto;
    }


    public MultiIntmacyItem(IntimacyDto.IntimacyConfigDTO dto) {
        this.dto = dto;
    }

    @Override
    public int getItemType() {
        return dto.status;
    }


}
