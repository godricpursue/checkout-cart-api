package com.trendyol.checkout.service.impl;

import com.trendyol.checkout.dto.AddItemDTO;
import com.trendyol.checkout.dto.ResponseDTO;
import com.trendyol.checkout.entity.*;
import com.trendyol.checkout.model.Promotion;
import com.trendyol.checkout.repository.CheckoutCartRepository;
import com.trendyol.checkout.repository.ItemRepository;
import com.trendyol.checkout.service.CheckoutCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckoutCartServiceImpl implements CheckoutCartService {
    private final CheckoutCartRepository checkoutCartRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public CheckoutCartServiceImpl(CheckoutCartRepository checkoutCartRepository, ItemRepository itemRepository) {
        this.checkoutCartRepository = checkoutCartRepository;
        this.itemRepository = itemRepository;
    }

    public ResponseDTO addItem(AddItemDTO itemDTO) {
        CheckoutCart checkoutCart = checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE).orElse(new CheckoutCart());

        // Constraints
        if (checkoutCart.getTotalPrice() + itemDTO.getPrice() * itemDTO.getQuantity() > CheckoutCart.MAX_CART_VALUE) {
            return new ResponseDTO(ResponseDTO.FAILED, CheckoutCart.MAX_CART_VALUE_ERROR_MESSAGE);
        }

        if (checkoutCart.getItems().size() >= CheckoutCart.MAX_UNIQUE_ITEM_COUNT) {
            return new ResponseDTO(ResponseDTO.FAILED, CheckoutCart.MAX_UNIQUE_ITEM_COUNT_ERROR_MESSAGE);
        }

        if (checkoutCart.getItems().stream().mapToInt(Item::getQuantity).sum() + itemDTO.getQuantity() > CheckoutCart.MAX_QUANTITY_PER_CART) {
            return new ResponseDTO(ResponseDTO.FAILED, CheckoutCart.MAX_QUANTITY_PER_CART_ERROR_MESSAGE);
        }


        Item item;

        if(itemDTO.getSellerId() == VasItem.VAS_ITEM_SELLER_ID || itemDTO.getCategoryId() == VasItem.VAS_ITEM_CATEGORY_ID){
            return new ResponseDTO(ResponseDTO.FAILED, VasItem.VAS_ITEM_WRONG_ENDPOINT_MESSAGE);
        } else if (itemDTO.getCategoryId() == DigitalItem.DIGITAL_ITEM_CATEGORY_ID) {
            if (checkoutCart.getItems().stream().anyMatch(cartItem -> (cartItem instanceof DefaultItem))) {
                return new ResponseDTO(ResponseDTO.FAILED, DigitalItem.HAS_DEFAULT_ITEM_ERROR_MESSAGE);
            }
            if (itemDTO.getQuantity() > DigitalItem.DIGITAL_ITEM_QUANTITY_PER_INPUT) {
                return new ResponseDTO(ResponseDTO.FAILED, DigitalItem.QUANTITY_ERROR_PER_INPUT_MESSAGE);
            }
            item = new DigitalItem();
        } else {
            if (checkoutCart.getItems().stream().anyMatch(cartItem -> cartItem instanceof DigitalItem)) {
                return new ResponseDTO(ResponseDTO.FAILED, DefaultItem.HAS_DIGITAL_ITEM_ERROR_MESSAGE);
            }
            if (itemDTO.getQuantity() > Item.ITEM_QUANTITY_PER_INPUT) {
                return new ResponseDTO(ResponseDTO.FAILED, Item.QUANTITY_ERROR_PER_INPUT_MESSAGE);
            }
            item = new DefaultItem();
        }

        item.setItemId(itemDTO.getItemId());
        item.setCategoryId(itemDTO.getCategoryId());
        item.setSellerId(itemDTO.getSellerId());
        item.setPrice(itemDTO.getPrice());
        item.setQuantity(itemDTO.getQuantity());

        item.setCheckoutCart(checkoutCart);

        checkoutCart.getItems().add(item);
        Promotion appliedPromotion = Promotion.applyPromotion(checkoutCart);
        checkoutCart.setAppliedPromotionId(appliedPromotion.getPromotionId());
        checkoutCart.setTotalDiscount(appliedPromotion.getDiscount());
        checkoutCart.setTotalPrice(checkoutCart.getTotalPrice() + item.getPrice() * item.getQuantity() - appliedPromotion.getDiscount());

        checkoutCartRepository.save(checkoutCart);

        return new ResponseDTO(ResponseDTO.SUCCESS, "Item added to checkoutCart.");
    }

}
