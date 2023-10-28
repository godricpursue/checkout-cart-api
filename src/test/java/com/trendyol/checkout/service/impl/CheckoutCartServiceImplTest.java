package com.trendyol.checkout.service.impl;

import com.trendyol.checkout.dto.AddItemDTO;
import com.trendyol.checkout.dto.ResponseDTO;
import com.trendyol.checkout.entity.*;
import com.trendyol.checkout.mapper.ItemMapper;
import com.trendyol.checkout.repository.CheckoutCartRepository;
import com.trendyol.checkout.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CheckoutCartServiceImplTest {

    @InjectMocks
    private CheckoutCartServiceImpl checkoutCartService;

    @Mock
    private CheckoutCartRepository checkoutCartRepository;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        CheckoutCart checkoutCart = new CheckoutCart();
        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);
    }

    @Test
    public void testAddItem_DefaultItemSuccess() {
        AddItemDTO itemDTO = new AddItemDTO();
        itemDTO.setItemId(10);
        itemDTO.setCategoryId(3738);
        itemDTO.setSellerId(2326);
        itemDTO.setPrice(260);
        itemDTO.setQuantity(3);

        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.SUCCESS, response.isResult());
        assertEquals(CheckoutCart.SUCCESS_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_DigitalItemSuccess() {
        AddItemDTO itemDTO = new AddItemDTO();
        itemDTO.setItemId(10);
        itemDTO.setCategoryId(DigitalItem.DIGITAL_ITEM_CATEGORY_ID);
        itemDTO.setSellerId(2326);
        itemDTO.setPrice(260);
        itemDTO.setQuantity(3);

        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.SUCCESS, response.isResult());
        assertEquals(CheckoutCart.SUCCESS_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_MaxCartValueExceeded() {

        AddItemDTO itemDTO = new AddItemDTO(12, 3738, 2326, 500001, 2);

        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(CheckoutCart.MAX_CART_VALUE_ERROR_MESSAGE, response.getMessage());
    }

    @Test
    public void testAddItem_MaxUniqueItemCountExceeded() {
        CheckoutCart checkoutCart = new CheckoutCart();
        List<Item> mockItems = createMockItems(0, CheckoutCart.MAX_UNIQUE_ITEM_COUNT,2);
        checkoutCart.setItems(mockItems);

        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        AddItemDTO itemDTO = new AddItemDTO(12, 5623, 1289, 300, 2);
        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(CheckoutCart.MAX_UNIQUE_ITEM_COUNT_ERROR_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_MaxQuantityPerCartExceeded() {
        CheckoutCart checkoutCart = new CheckoutCart();

        List<Item> mockItems = createMockItems(1, CheckoutCart.MAX_UNIQUE_ITEM_COUNT,3);
        checkoutCart.setItems(mockItems);

        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        AddItemDTO itemDTO = new AddItemDTO(12, 5623, 1289, 300, 5);
        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(CheckoutCart.MAX_QUANTITY_PER_CART_ERROR_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_VasItemWrongEndpoint() {
        CheckoutCart checkoutCart = new CheckoutCart();
        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        AddItemDTO itemDTO = new AddItemDTO(12, 5623, 5003, 300, 5);
        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(VasItem.VAS_ITEM_WRONG_ENDPOINT_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_DigitalCartHasDefaultItem() {
        CheckoutCart checkoutCart = new CheckoutCart();
        AddItemDTO mockItemDTO = new AddItemDTO(12, 5623, 1289, 300, 5);
        Item mockItem = ItemMapper.dtoToItem(mockItemDTO);
        checkoutCart.getItems().add(mockItem);

        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        AddItemDTO itemDTO = new AddItemDTO(12, DigitalItem.DIGITAL_ITEM_CATEGORY_ID, 1289, 300, 2);
        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(DigitalItem.HAS_DEFAULT_ITEM_ERROR_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_DigitalItemQuantityPerUnitExceeded() {
        CheckoutCart checkoutCart = new CheckoutCart();
        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        AddItemDTO itemDTO = new AddItemDTO(12, DigitalItem.DIGITAL_ITEM_CATEGORY_ID, 1289, 300, DigitalItem.DIGITAL_ITEM_QUANTITY_PER_INPUT + 1);
        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(DigitalItem.QUANTITY_ERROR_PER_INPUT_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_DefaultCartHasDigitaltItem() {
        CheckoutCart checkoutCart = new CheckoutCart();
        AddItemDTO mockItemDTO = new AddItemDTO(12, DigitalItem.DIGITAL_ITEM_CATEGORY_ID, 1289, 300, 5);
        Item mockItem = ItemMapper.dtoToItem(mockItemDTO);
        checkoutCart.getItems().add(mockItem);

        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        AddItemDTO itemDTO = new AddItemDTO(12, 6723, 1289, 300, 2);
        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(DefaultItem.HAS_DIGITAL_ITEM_ERROR_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_DefaultItemQuantityPerUnitExceeded() {
        CheckoutCart checkoutCart = new CheckoutCart();
        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        AddItemDTO itemDTO = new AddItemDTO(12, 4672, 1289, 300, Item.ITEM_QUANTITY_PER_INPUT + 1);
        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(DefaultItem.QUANTITY_ERROR_PER_INPUT_MESSAGE, response.getMessage());
    }
    private List<Item> createMockItems(int range, int count, int quantity){
        return IntStream.range(range, count)
                .mapToObj(i -> {
                    Item item = new DefaultItem();
                    item.setItemId(i + 1);
                    item.setCategoryId(i + 3738);
                    item.setSellerId(i + 2326);
                    item.setPrice(i*10 + 260);
                    item.setQuantity(quantity);
                    return item;
                })
                .collect(Collectors.toList());
    }
}


