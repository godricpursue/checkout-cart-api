package com.trendyol.checkout.service.impl;

import com.trendyol.checkout.dto.AddItemDTO;
import com.trendyol.checkout.dto.AddVasItemDTO;
import com.trendyol.checkout.dto.ResponseDTO;
import com.trendyol.checkout.entity.*;
import com.trendyol.checkout.mapper.ItemMapper;
import com.trendyol.checkout.model.Promotion;
import com.trendyol.checkout.repository.CheckoutCartRepository;
import com.trendyol.checkout.repository.ItemRepository;
import com.trendyol.checkout.repository.VasItemRepository;
import com.trendyol.checkout.service.CheckoutCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CheckoutCartServiceImpl implements CheckoutCartService {

    private final CheckoutCartRepository checkoutCartRepository;
    private final ItemRepository itemRepository;
    private final VasItemRepository vasItemRepository;

    @Autowired
    public CheckoutCartServiceImpl(CheckoutCartRepository checkoutCartRepository, ItemRepository itemRepository, VasItemRepository vasItemRepository) {
        this.checkoutCartRepository = checkoutCartRepository;
        this.itemRepository = itemRepository;
        this.vasItemRepository = vasItemRepository;
    }

    public ResponseDTO addItem(AddItemDTO itemDTO) {
        CheckoutCart checkoutCart = getCheckoutCart();
        ResponseDTO validationResult = validateCartConstraints(checkoutCart, itemDTO);

        if (validationResult != null) {
            return validationResult;
        }

        if (isVasItem(itemDTO)) {
            return new ResponseDTO(ResponseDTO.FAILED, VasItem.VAS_ITEM_WRONG_ENDPOINT_MESSAGE);
        }

        if (isDigitalItem(itemDTO)) {
            if (hasDefaultItem(checkoutCart)) {
                return new ResponseDTO(ResponseDTO.FAILED, DigitalItem.HAS_DEFAULT_ITEM_ERROR_MESSAGE);
            }
            if (isQuantityInvalidForDigitalItem(itemDTO)) {
                return new ResponseDTO(ResponseDTO.FAILED, DigitalItem.QUANTITY_ERROR_PER_INPUT_MESSAGE);
            }
        } else {
            if (hasDigitalItem(checkoutCart)) {
                return new ResponseDTO(ResponseDTO.FAILED, DefaultItem.HAS_DIGITAL_ITEM_ERROR_MESSAGE);
            }
            if (isQuantityInvalidForDefaultItem(itemDTO)) {
                return new ResponseDTO(ResponseDTO.FAILED, Item.QUANTITY_ERROR_PER_INPUT_MESSAGE);
            }
        }

        Item item = ItemMapper.dtoToItem(itemDTO);
        updateCheckoutCartWithNewItem(checkoutCart, item);

        return new ResponseDTO(ResponseDTO.SUCCESS, CheckoutCart.SUCCESS_MESSAGE);
    }

    public ResponseDTO addVasItem(AddVasItemDTO vasItemDTO) {
        CheckoutCart checkoutCart = checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE).orElse(null);
        if (checkoutCart == null) {
            return new ResponseDTO(ResponseDTO.FAILED, "No cart found.");
        }

        if (checkoutCart.getItems().isEmpty()) {
            return new ResponseDTO(ResponseDTO.FAILED, "No items in the cart to attach a VasItem.");
        }

        Optional<Item> optionalDefaultItem = checkoutCart.getItems().stream()
                .filter(i -> i.getItemId().equals(vasItemDTO.getItemId()))
                .findFirst();

        if (optionalDefaultItem.isEmpty()) {
            return new ResponseDTO(ResponseDTO.FAILED, "Default item not found.");
        }

        DefaultItem defaultItem = (DefaultItem) optionalDefaultItem.get();

        if (!Arrays.asList(1001, 3004).contains(defaultItem.getCategoryId())) {
            return new ResponseDTO(ResponseDTO.FAILED, "VasItem can only be added to Furniture or Electronics.");
        }

        if (defaultItem.getVasItems().size() >= 3) {
            return new ResponseDTO(ResponseDTO.FAILED, "A DefaultItem can have a maximum of 3 VasItems.");
        }

        VasItem vasItem = new VasItem();
        vasItem.setItemId(vasItemDTO.getItemId());
        vasItem.setVasItemId(vasItemDTO.getVasItemId());
        vasItem.setCategoryId(vasItemDTO.getCategoryId());
        vasItem.setSellerId(vasItemDTO.getSellerId());
        vasItem.setPrice(vasItemDTO.getPrice());
        vasItem.setQuantity(vasItemDTO.getQuantity());

        vasItem.setDefaultItem(defaultItem);
        vasItem.setCheckoutCart(defaultItem.getCheckoutCart());

        if (vasItem.getCategoryId() != 3242 || vasItem.getSellerId() != 5003) {
            return new ResponseDTO(ResponseDTO.FAILED, "Invalid VasItem details.");
        }
        if (vasItem.getPrice() > defaultItem.getPrice()) {
            return new ResponseDTO(ResponseDTO.FAILED, "VasItem price cannot be higher than the DefaultItem price.");
        }

        defaultItem.getVasItems().add(vasItem);

        checkoutCartRepository.save(checkoutCart);

        return new ResponseDTO(ResponseDTO.SUCCESS, "VasItem added to DefaultItem.");
    }

    private void updateCheckoutCartWithNewItem(CheckoutCart checkoutCart, Item item) {
        item.setCheckoutCart(checkoutCart);
        checkoutCart.getItems().add(item);

        Promotion appliedPromotion = Promotion.applyPromotion(checkoutCart);
        checkoutCart.setAppliedPromotionId(appliedPromotion.getPromotionId());
        checkoutCart.setTotalDiscount(appliedPromotion.getDiscount());

        double newTotalPrice = checkoutCart.getTotalPrice() + item.getPrice() * item.getQuantity() - appliedPromotion.getDiscount();
        checkoutCart.setTotalPrice(newTotalPrice);

        checkoutCartRepository.save(checkoutCart);
    }
    private CheckoutCart getCheckoutCart() {
        return checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)
                .orElse(new CheckoutCart());
    }
    private ResponseDTO validateCartConstraints(CheckoutCart checkoutCart, AddItemDTO itemDTO) {
        if (isCartOverValueLimit(checkoutCart, itemDTO)) {
            return new ResponseDTO(ResponseDTO.FAILED, CheckoutCart.MAX_CART_VALUE_ERROR_MESSAGE);
        }

        if (isCartAtMaxUniqueItems(checkoutCart)) {
            return new ResponseDTO(ResponseDTO.FAILED, CheckoutCart.MAX_UNIQUE_ITEM_COUNT_ERROR_MESSAGE);
        }

        if (isCartOverTotalQuantityLimit(checkoutCart, itemDTO)) {
            return new ResponseDTO(ResponseDTO.FAILED, CheckoutCart.MAX_QUANTITY_PER_CART_ERROR_MESSAGE);
        }
        return null;
    }
    private boolean isCartOverValueLimit(CheckoutCart cart, AddItemDTO itemDTO) {
        double newTotal = cart.getTotalPrice() + itemDTO.getPrice() * itemDTO.getQuantity();
        return newTotal > CheckoutCart.MAX_CART_VALUE;
    }
    private boolean isCartAtMaxUniqueItems(CheckoutCart cart) {
        return cart.getItems().size() >= CheckoutCart.MAX_UNIQUE_ITEM_COUNT;
    }
    private boolean isCartOverTotalQuantityLimit(CheckoutCart cart, AddItemDTO itemDTO) {
        int totalQuantity = cart.getItems().stream().mapToInt(Item::getQuantity).sum() + itemDTO.getQuantity();
        return totalQuantity > CheckoutCart.MAX_QUANTITY_PER_CART;
    }
    private boolean isVasItem(AddItemDTO itemDTO) {
        return itemDTO.getSellerId() == VasItem.VAS_ITEM_SELLER_ID || itemDTO.getCategoryId() == VasItem.VAS_ITEM_CATEGORY_ID;
    }
    private boolean isDigitalItem(AddItemDTO itemDTO) {
        return itemDTO.getCategoryId() == DigitalItem.DIGITAL_ITEM_CATEGORY_ID;
    }
    private boolean hasDefaultItem(CheckoutCart cart) {
        return cart.getItems().stream().anyMatch(item -> item instanceof DefaultItem);
    }
    private boolean isQuantityInvalidForDigitalItem(AddItemDTO itemDTO) {
        return itemDTO.getQuantity() > DigitalItem.DIGITAL_ITEM_QUANTITY_PER_INPUT;
    }
    private boolean hasDigitalItem(CheckoutCart cart) {
        return cart.getItems().stream().anyMatch(item -> item instanceof DigitalItem);
    }
    private boolean isQuantityInvalidForDefaultItem(AddItemDTO itemDTO) {
        return itemDTO.getQuantity() > Item.ITEM_QUANTITY_PER_INPUT;
    }

}
