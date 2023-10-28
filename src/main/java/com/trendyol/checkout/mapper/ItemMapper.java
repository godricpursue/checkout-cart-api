package com.trendyol.checkout.mapper;


import com.trendyol.checkout.dto.AddItemDTO;
import com.trendyol.checkout.dto.AddVasItemDTO;
import com.trendyol.checkout.entity.DefaultItem;
import com.trendyol.checkout.entity.DigitalItem;
import com.trendyol.checkout.entity.Item;
import com.trendyol.checkout.entity.VasItem;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static Item dtoToItem(AddItemDTO addItemDTO) {
        Item item;

        if (addItemDTO.getCategoryId() == DigitalItem.DIGITAL_ITEM_CATEGORY_ID) {
            item = new DigitalItem();
        } else {
            item = new DefaultItem();
        }

        item.setItemId(addItemDTO.getItemId());
        item.setCategoryId(addItemDTO.getCategoryId());
        item.setSellerId(addItemDTO.getSellerId());
        item.setPrice(addItemDTO.getPrice());
        item.setQuantity(addItemDTO.getQuantity());

        return item;
    }

    public static AddItemDTO itemToDto(Item item) {
        AddItemDTO addItemDTO = new AddItemDTO();
        addItemDTO.setItemId(item.getItemId());
        addItemDTO.setCategoryId(item.getCategoryId());
        addItemDTO.setSellerId(item.getSellerId());
        addItemDTO.setPrice(item.getPrice());
        addItemDTO.setQuantity(item.getQuantity());

        if (item instanceof DefaultItem defaultItem) {
            if (defaultItem.getVasItems() != null && !defaultItem.getVasItems().isEmpty()) {
                List<AddVasItemDTO> vasItemDTOs = defaultItem.getVasItems().stream()
                        .map(VasItemMapper::vasItemToDto)
                        .collect(Collectors.toList());
                addItemDTO.setVasItems(vasItemDTOs);
            }
        }

        return addItemDTO;
    }
}
