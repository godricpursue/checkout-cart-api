package com.trendyol.checkout.service.impl;

import com.trendyol.checkout.dto.AddItemDTO;
import com.trendyol.checkout.dto.AddVasItemDTO;
import com.trendyol.checkout.dto.RemoveItemDTO;
import com.trendyol.checkout.dto.ResponseDTO;
import com.trendyol.checkout.entity.*;
import com.trendyol.checkout.mapper.ItemMapper;
import com.trendyol.checkout.repository.CheckoutCartRepository;
import com.trendyol.checkout.repository.ItemRepository;
import com.trendyol.checkout.service.CheckoutCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class CheckoutCartServiceImplAddItemTest {

    @InjectMocks
    private CheckoutCartServiceImpl checkoutCartService;

    @Mock
    private CheckoutCartRepository checkoutCartRepository;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        CheckoutCart checkoutCart = new CheckoutCart();
        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);
    }

    @Test
    public void testAddItem_DefaultItemSuccess() {
        AddItemDTO itemDTO = new AddItemDTO(10, 3738, 2326, 260, 3,null);

        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.SUCCESS, response.isResult());
        assertEquals(CheckoutCart.SUCCESS_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_DigitalItemSuccess() {
        AddItemDTO itemDTO = new AddItemDTO(10, DigitalItem.DIGITAL_ITEM_CATEGORY_ID, 2326, 260, 3,null);

        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.SUCCESS, response.isResult());
        assertEquals(CheckoutCart.SUCCESS_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_MaxCartValueExceeded() {

        AddItemDTO itemDTO = new AddItemDTO(12, 3738, 2326, 500001, 2,null);

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

        AddItemDTO itemDTO = new AddItemDTO(12, 5623, 1289, 300, 2,null);
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

        AddItemDTO itemDTO = new AddItemDTO(12, 5623, 1289, 300, 5,null);
        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(CheckoutCart.MAX_QUANTITY_PER_CART_ERROR_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_VasItemWrongEndpoint() {
        CheckoutCart checkoutCart = new CheckoutCart();
        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        AddItemDTO itemDTO = new AddItemDTO(12, 5623, 5003, 300, 5,null);
        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(VasItem.VAS_ITEM_WRONG_ENDPOINT_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_DigitalCartHasDefaultItem() {
        CheckoutCart checkoutCart = new CheckoutCart();
        AddItemDTO mockItemDTO = new AddItemDTO(12, 5623, 1289, 300, 5,null);
        Item mockItem = ItemMapper.dtoToItem(mockItemDTO);
        checkoutCart.getItems().add(mockItem);

        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        AddItemDTO itemDTO = new AddItemDTO(12, DigitalItem.DIGITAL_ITEM_CATEGORY_ID, 1289, 300, 2,null);
        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(DigitalItem.HAS_DEFAULT_ITEM_ERROR_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_DigitalItemQuantityPerUnitExceeded() {
        CheckoutCart checkoutCart = new CheckoutCart();
        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        AddItemDTO itemDTO = new AddItemDTO(12, DigitalItem.DIGITAL_ITEM_CATEGORY_ID, 1289, 300, DigitalItem.DIGITAL_ITEM_QUANTITY_PER_INPUT + 1,null);
        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(DigitalItem.QUANTITY_ERROR_PER_INPUT_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_DefaultCartHasDigitalItem() {
        CheckoutCart checkoutCart = new CheckoutCart();
        AddItemDTO mockItemDTO = new AddItemDTO(12, DigitalItem.DIGITAL_ITEM_CATEGORY_ID, 1289, 300, 5,null);
        Item mockItem = ItemMapper.dtoToItem(mockItemDTO);
        checkoutCart.getItems().add(mockItem);

        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        AddItemDTO itemDTO = new AddItemDTO(12, 6723, 1289, 300, 2,null);
        ResponseDTO response = checkoutCartService.addItem(itemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(DefaultItem.HAS_DIGITAL_ITEM_ERROR_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddItem_DefaultItemQuantityPerUnitExceeded() {
        CheckoutCart checkoutCart = new CheckoutCart();
        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        AddItemDTO itemDTO = new AddItemDTO(12, 4672, 1289, 300, Item.ITEM_QUANTITY_PER_INPUT + 1,null);
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
class CheckoutCartServiceImplAddVasItemTest {
    @InjectMocks
    private CheckoutCartServiceImpl checkoutCartService;

    @Mock
    private CheckoutCartRepository checkoutCartRepository;

    @Mock
    private ItemRepository itemRepository;

    private CheckoutCart checkoutCart;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        checkoutCart = new CheckoutCart();
        AddItemDTO defaultItemDto = new AddItemDTO(10, VasItem.FURNITURE_CATEGORY_ID, 5050, 300, 1,null);
        Item defaultItem = ItemMapper.dtoToItem(defaultItemDto);
        if (defaultItem instanceof DefaultItem) {
            ((DefaultItem) defaultItem).setVasItems(new ArrayList<>());
        }
        checkoutCart.getItems().add(defaultItem);
    }
    @Test
    public void testAddVasItem_Success() {
        when(checkoutCartRepository.findById(anyString())).thenReturn(Optional.of(checkoutCart));

        AddVasItemDTO vasItemDTO = new AddVasItemDTO(10,3030, VasItem.VAS_ITEM_CATEGORY_ID, VasItem.VAS_ITEM_SELLER_ID, 100, 1);
        ResponseDTO response = checkoutCartService.addVasItem(vasItemDTO);

        assertEquals(ResponseDTO.SUCCESS, response.isResult());
        assertEquals(VasItem.SUCCESS_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddVasItem_DefaultItemNotFound() {
        when(checkoutCartRepository.findById(anyString())).thenReturn(Optional.of(checkoutCart));

        AddVasItemDTO vasItemDTO = new AddVasItemDTO(11,3030, VasItem.VAS_ITEM_CATEGORY_ID, VasItem.VAS_ITEM_SELLER_ID, 100, 1);
        ResponseDTO response = checkoutCartService.addVasItem(vasItemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(DefaultItem.DEFAULT_ITEM_NOT_FOUND_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddVasItem_CartNotFound() {
        when(checkoutCartRepository.findById(anyString())).thenReturn(Optional.empty());

        AddVasItemDTO addVasItemDTO = new AddVasItemDTO();
        ResponseDTO response = checkoutCartService.addVasItem(addVasItemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(CheckoutCart.CART_NOT_FOUND_ERROR_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddVasItem_EmptyCart() {

        checkoutCart.getItems().clear();
        when(checkoutCartRepository.findById(anyString())).thenReturn(Optional.of(checkoutCart));

        AddVasItemDTO addVasItemDTO = new AddVasItemDTO();
        ResponseDTO response = checkoutCartService.addVasItem(addVasItemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(VasItem.NO_ITEMS_IN_THE_CART_TO_ATTACH_A_VAS_ITEM_MESSAGE, response.getMessage());
    }

    @Test
    public void testAddVasItem_PriceHigherThanDefaultItem() {
        when(checkoutCartRepository.findById(anyString())).thenReturn(Optional.of(checkoutCart));

        AddVasItemDTO vasItemDTO = new AddVasItemDTO(10,3030, VasItem.VAS_ITEM_CATEGORY_ID, VasItem.VAS_ITEM_SELLER_ID, 500, 1);
        ResponseDTO response = checkoutCartService.addVasItem(vasItemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(VasItem.INVALID_VAS_ITEM_DETAILS_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddVasItem_InvalidVasItem() {
        when(checkoutCartRepository.findById(anyString())).thenReturn(Optional.of(checkoutCart));

        AddVasItemDTO vasItemDTO = new AddVasItemDTO(10,3030, 3221, VasItem.VAS_ITEM_SELLER_ID, 100, 1);
        ResponseDTO response = checkoutCartService.addVasItem(vasItemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(VasItem.INVALID_VAS_ITEM_DETAILS_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddVasItem_InvalidAppliedCategory(){
        when(checkoutCartRepository.findById(anyString())).thenReturn(Optional.of(checkoutCart));
        AddItemDTO defaultItemDto2 = new AddItemDTO(11, 2002, 5050, 250, 1,null);
        Item defaultItem2 = ItemMapper.dtoToItem(defaultItemDto2);
        checkoutCart.getItems().add(defaultItem2);

        AddVasItemDTO vasItemDTO = new AddVasItemDTO(11,3030, VasItem.VAS_ITEM_CATEGORY_ID, VasItem.VAS_ITEM_SELLER_ID, 100, 1);
        ResponseDTO response = checkoutCartService.addVasItem(vasItemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(VasItem.VAS_ITEM_CATEGORY_ERROR_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddVasItem_ExceededLimit(){
        when(checkoutCartRepository.findById(anyString())).thenReturn(Optional.of(checkoutCart));

        for (int i = 0; i <= 2; i++) {
            AddVasItemDTO vasItemDTO = new AddVasItemDTO(10, 3030+i, VasItem.VAS_ITEM_CATEGORY_ID, VasItem.VAS_ITEM_SELLER_ID, 100.0, 1);
            checkoutCartService.addVasItem(vasItemDTO);
        }

        AddVasItemDTO vasItemDTO = new AddVasItemDTO(10,2345, VasItem.VAS_ITEM_CATEGORY_ID, VasItem.VAS_ITEM_SELLER_ID, 100, 1);
        ResponseDTO response = checkoutCartService.addVasItem(vasItemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(DefaultItem.EXCEEDED_VASITEM_MESSAGE, response.getMessage());
    }
    @Test
    public void testAddVasItem_MaxValueForCartExceeded(){
        when(checkoutCartRepository.findById(anyString())).thenReturn(Optional.of(checkoutCart));
        AddItemDTO defaultItemDto2 = new AddItemDTO(11, VasItem.FURNITURE_CATEGORY_ID, 2789, 250, 1,null);
        Item defaultItem2 = ItemMapper.dtoToItem(defaultItemDto2);
        checkoutCart.getItems().add(defaultItem2);

        AddVasItemDTO vasItemDTO = new AddVasItemDTO(11,3030, VasItem.VAS_ITEM_CATEGORY_ID, VasItem.VAS_ITEM_SELLER_ID, 100, 11);
        ResponseDTO response = checkoutCartService.addVasItem(vasItemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(Item.QUANTITY_ERROR_PER_INPUT_MESSAGE, response.getMessage());
    }
}
class CheckoutCartServiceImplRemoveItemTest {

    @InjectMocks
    private CheckoutCartServiceImpl checkoutCartService;

    @Mock
    private CheckoutCartRepository checkoutCartRepository;

    @Mock
    private ItemRepository itemRepository;  // Mock the ItemRepository

    CheckoutCart checkoutCart;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        checkoutCart = new CheckoutCart();
        AddItemDTO defaultItemDto = new AddItemDTO(10, VasItem.FURNITURE_CATEGORY_ID, 2020, 300, 1,null);
        Item item = ItemMapper.dtoToItem(defaultItemDto);
        checkoutCart.getItems().add(item);
    }

    @Test
    public void testRemoveItem_SuccessfulRemoval() {
        when(checkoutCartRepository.findById(anyString())).thenReturn(Optional.of(checkoutCart));
        doNothing().when(itemRepository).delete(any(Item.class));

        RemoveItemDTO removeItemDTO = new RemoveItemDTO(10);
        ResponseDTO response = checkoutCartService.removeItem(removeItemDTO);

        assertEquals(ResponseDTO.SUCCESS, response.isResult());
        assertEquals(CheckoutCart.REMOVAL_SUCCESS_MESSAGE, response.getMessage());
    }
    @Test
    public void testRemoveItem_NoCart() {
        Mockito.when(checkoutCartRepository.findOne(Mockito.any())).thenReturn(null);

        ResponseDTO response = checkoutCartService.removeItem(new RemoveItemDTO(10));

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(CheckoutCart.CART_NOT_FOUND_ERROR_MESSAGE, response.getMessage());
    }
    @Test
    public void testRemoveItem_NoItemInCart() {
        when(checkoutCartRepository.findById(anyString())).thenReturn(Optional.of(checkoutCart));
        doNothing().when(itemRepository).delete(any(Item.class));

        RemoveItemDTO removeItemDTO = new RemoveItemDTO(11);
        ResponseDTO response = checkoutCartService.removeItem(removeItemDTO);

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(Item.ITEM_NOT_FOUND_MESSAGE, response.getMessage());
    }
}
class CheckoutCartServiceImplResetCartTest{
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
    }

    @Test
    public void testResetCart_SuccessfulReset() {
        CheckoutCart checkoutCart = new CheckoutCart();
        List<Item> mockItems = createMockItems(0, 5,2);
        checkoutCart.setItems(mockItems);

        when(checkoutCartRepository.findById(CheckoutCart.CART_REFERANCE)).thenReturn(Optional.of(checkoutCart));
        when(checkoutCartRepository.save(any(CheckoutCart.class))).thenReturn(checkoutCart);

        ResponseDTO response = checkoutCartService.resetCart();

        assertEquals(ResponseDTO.SUCCESS, response.isResult());
        assertEquals(CheckoutCart.RESET_SUCCESS_MESSAGE, response.getMessage());
    }
    @Test
    public void testResetCart_NoCart() {
        Mockito.when(checkoutCartRepository.findOne(Mockito.any())).thenReturn(null);

        ResponseDTO response = checkoutCartService.resetCart();

        assertEquals(ResponseDTO.FAILED, response.isResult());
        assertEquals(CheckoutCart.CART_NOT_FOUND_ERROR_MESSAGE, response.getMessage());
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

