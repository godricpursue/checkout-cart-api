package com.trendyol.checkout.mapper;

import com.trendyol.checkout.dto.AddVasItemDTO;
import com.trendyol.checkout.entity.VasItem;

public class VasItemMapper {

    public static VasItem dtoToVasItem(AddVasItemDTO vasItemDTO) {
        VasItem vasItem = new VasItem();
        vasItem.setVasItemId(vasItemDTO.getVasItemId());
        vasItem.setCategoryId(vasItemDTO.getCategoryId());
        vasItem.setSellerId(vasItemDTO.getSellerId());
        vasItem.setPrice(vasItemDTO.getPrice());
        vasItem.setQuantity(vasItemDTO.getQuantity());
        return vasItem;
    }

    public AddVasItemDTO vasItemToDto(VasItem vasItem) {
        AddVasItemDTO vasItemDTO = new AddVasItemDTO();
        vasItemDTO.setVasItemId(vasItem.getVasItemId());
        vasItemDTO.setCategoryId(vasItem.getCategoryId());
        vasItemDTO.setSellerId(vasItem.getSellerId());
        vasItemDTO.setPrice(vasItem.getPrice());
        vasItemDTO.setQuantity(vasItem.getQuantity());
        return vasItemDTO;
    }
}
