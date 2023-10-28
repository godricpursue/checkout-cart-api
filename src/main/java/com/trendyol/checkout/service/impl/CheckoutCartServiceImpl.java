package com.trendyol.checkout.service.impl;

import com.trendyol.checkout.dto.AddItemDTO;
import com.trendyol.checkout.dto.AddVasItemDTO;
import com.trendyol.checkout.dto.RemoveItemDTO;
import com.trendyol.checkout.dto.ResponseDTO;
import com.trendyol.checkout.entity.*;
import com.trendyol.checkout.mapper.ItemMapper;
import com.trendyol.checkout.mapper.VasItemMapper;
import com.trendyol.checkout.model.Promotion;
import com.trendyol.checkout.repository.CheckoutCartRepository;
import com.trendyol.checkout.repository.ItemRepository;
import com.trendyol.checkout.repository.VasItemRepository;
import com.trendyol.checkout.service.CheckoutCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Override
    public ResponseDTO addItem(AddItemDTO itemDTO) {
        CheckoutCart checkoutCart = getCheckoutCart(true);
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
        item.setCheckoutCart(checkoutCart);
        checkoutCart.getItems().add(item);
        updateCheckoutCartWithNewItem(checkoutCart);

        return new ResponseDTO(ResponseDTO.SUCCESS, CheckoutCart.SUCCESS_MESSAGE);
    }
    @Override
    public ResponseDTO addVasItem(AddVasItemDTO vasItemDTO) {
        CheckoutCart checkoutCart = getCheckoutCart(false);
        if (checkoutCart == null) {
            return new ResponseDTO(ResponseDTO.FAILED, CheckoutCart.CART_NOT_FOUND_ERROR_MESSAGE);
        }

        if (checkoutCart.getItems().isEmpty()) {
            return new ResponseDTO(ResponseDTO.FAILED, VasItem.NO_ITEMS_IN_THE_CART_TO_ATTACH_A_VAS_ITEM_MESSAGE);
        }

        DefaultItem defaultItem = fetchDefaultItemFromCart(checkoutCart, vasItemDTO.getItemId());
        if (defaultItem == null) {
            return new ResponseDTO(ResponseDTO.FAILED, DefaultItem.DEFAULT_ITEM_NOT_FOUND_MESSAGE);
        }

        if (!isVasItemApplicableToDefaultItem(defaultItem)) {
            return new ResponseDTO(ResponseDTO.FAILED, VasItem.VAS_ITEM_CATEGORY_ERROR_MESSAGE);
        }
        if (hasNoVasItemSpace(defaultItem)) {
            return new ResponseDTO(ResponseDTO.FAILED, DefaultItem.EXCEEDED_VASITEM_MESSAGE);
        }
        VasItem vasItem = createVasItemFromDTO(vasItemDTO, defaultItem);
        if(!isValidVasItem(vasItem, defaultItem)) {
            return new ResponseDTO(ResponseDTO.FAILED, VasItem.INVALID_VAS_ITEM_DETAILS_MESSAGE);
        }
        defaultItem.getVasItems().add(vasItem);
        updateCheckoutCartWithNewItem(checkoutCart);
        return new ResponseDTO(ResponseDTO.SUCCESS, VasItem.SUCCESS_MESSAGE);
    }
    @Override
    public ResponseDTO removeItem(RemoveItemDTO removeItemDTO) {
        CheckoutCart checkoutCart = getCheckoutCart(false);
        if (checkoutCart == null) {
            return new ResponseDTO(ResponseDTO.FAILED, CheckoutCart.CART_NOT_FOUND_ERROR_MESSAGE);
        }

        Item item = fetchItemFromCart(checkoutCart, removeItemDTO.getItemId());

        if (item == null) {
            return new ResponseDTO(ResponseDTO.FAILED, Item.ITEM_NOT_FOUND_MESSAGE);
        }

        checkoutCart.getItems().remove(item);
        itemRepository.delete(item);
        updateCheckoutCartWithNewItem(checkoutCart);
        return new ResponseDTO(ResponseDTO.SUCCESS, CheckoutCart.REMOVAL_SUCCESS_MESSAGE);
    }

    // RemoveItem method helper methods
    private Item fetchItemFromCart(CheckoutCart checkoutCart, int itemId) {
        return checkoutCart.getItems().stream()
                .filter(i -> i.getItemId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    // VasItem method helper methods
    private DefaultItem fetchDefaultItemFromCart(CheckoutCart checkoutCart, int itemId) {
        Item item = fetchItemFromCart(checkoutCart, itemId);
        if (item instanceof DefaultItem) {
            return (DefaultItem) item;
        }
        return null;
    }
    private boolean isVasItemApplicableToDefaultItem(DefaultItem defaultItem) {
        return VasItem.applicableCategories.contains(defaultItem.getCategoryId());
    }
    private boolean hasNoVasItemSpace(DefaultItem defaultItem) {
        return defaultItem.getVasItems().size() >= DefaultItem.VAS_ITEM_SPACE_LIMIT;
    }
    private VasItem createVasItemFromDTO(AddVasItemDTO vasItemDTO, DefaultItem defaultItem) {
        VasItem vasItem = VasItemMapper.dtoToVasItem(vasItemDTO);

        vasItem.setDefaultItem(defaultItem);
        vasItem.setCheckoutCart(defaultItem.getCheckoutCart());

        return vasItem;
    }
    private boolean isValidVasItem(VasItem vasItem, DefaultItem defaultItem) {
        return vasItem.getCategoryId() == VasItem.VAS_ITEM_CATEGORY_ID
                && vasItem.getSellerId() == VasItem.VAS_ITEM_SELLER_ID
                && vasItem.getPrice() <= defaultItem.getPrice();
    }



    // AddItem method helper methods
    private void updateCheckoutCartWithNewItem(CheckoutCart checkoutCart) {

        double totalPrice = CheckoutCart.getTotalCartValue(checkoutCart);
        checkoutCart.setTotalPrice(totalPrice);

        Promotion appliedPromotion = Promotion.applyPromotion(checkoutCart);
        checkoutCart.setAppliedPromotionId(appliedPromotion.getPromotionId());
        checkoutCart.setTotalDiscount(appliedPromotion.getDiscount());

        double newTotalPrice = CheckoutCart.getTotalCartValue(checkoutCart) - appliedPromotion.getDiscount();

        checkoutCart.setTotalPrice(newTotalPrice);

        checkoutCartRepository.save(checkoutCart);
    }
    private CheckoutCart getCheckoutCart(boolean createIfNotFound) {
        return checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)
                .orElse(createIfNotFound ? new CheckoutCart() : null);
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
