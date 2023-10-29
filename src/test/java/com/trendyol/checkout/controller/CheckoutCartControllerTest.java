package com.trendyol.checkout.controller;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.trendyol.checkout.dto.*;
import com.trendyol.checkout.entity.CheckoutCart;
import com.trendyol.checkout.entity.Item;
import com.trendyol.checkout.entity.VasItem;
import com.trendyol.checkout.mapper.CartMapper;
import com.trendyol.checkout.mapper.ItemMapper;
import com.trendyol.checkout.service.CheckoutCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
class CheckoutCartControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CheckoutCartService checkoutCartService;
    private CheckoutCart checkoutCart;
    private AddItemDTO addItemDTO;
    private AddVasItemDTO addVasItemDTO;

    @BeforeEach
    void setUp() {
    }

    @Test
    void addItem() throws Exception {
        addItemDTO = new AddItemDTO(10, 3003, 6,290,1,null);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(addItemDTO);

        when(checkoutCartService.addItem(addItemDTO)).thenReturn(new ResponseDTO(ResponseDTO.SUCCESS, CheckoutCart.SUCCESS_MESSAGE));
        this.mockMvc.perform(post("/carts/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void addVasItem() throws Exception {
        checkoutCart = new CheckoutCart();

        AddItemDTO defaultItemDto = new AddItemDTO(11, 2002, 5050, 250, 1,null);
        Item defaultItem = ItemMapper.dtoToItem(defaultItemDto);
        checkoutCart.getItems().add(defaultItem);

        AddVasItemDTO vasItemDTO = new AddVasItemDTO(11,3030, VasItem.VAS_ITEM_CATEGORY_ID, VasItem.VAS_ITEM_SELLER_ID, 100, 1);
        when(checkoutCartService.addVasItem(vasItemDTO)).thenReturn(new ResponseDTO(ResponseDTO.SUCCESS, VasItem.SUCCESS_MESSAGE));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(vasItemDTO);

        this.mockMvc.perform(post("/carts/vasItem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void removeItem() throws Exception {
        checkoutCart = new CheckoutCart();

        AddItemDTO defaultItemDto2 = new AddItemDTO(11, 2002, 5050, 250, 1,null);
        Item defaultItem = ItemMapper.dtoToItem(defaultItemDto2);
        checkoutCart.getItems().add(defaultItem);

        RemoveItemDTO removeItemDTO = new RemoveItemDTO(11);
        when(checkoutCartService.removeItem(removeItemDTO)).thenReturn(new ResponseDTO(ResponseDTO.SUCCESS, CheckoutCart.REMOVAL_SUCCESS_MESSAGE));

        this.mockMvc.perform(delete("/carts/item/11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void resetCart() throws Exception {
        checkoutCart = new CheckoutCart();

        AddItemDTO defaultItemDto = new AddItemDTO(11, 2002, 5050, 250, 1,null);
        Item defaultItem = ItemMapper.dtoToItem(defaultItemDto);
        checkoutCart.getItems().add(defaultItem);

        AddItemDTO defaultItemDto2 = new AddItemDTO(12, 2002, 5050, 300, 1,null);
        Item defaultItem2 = ItemMapper.dtoToItem(defaultItemDto2);
        checkoutCart.getItems().add(defaultItem2);

        when(checkoutCartService.resetCart()).thenReturn(new ResponseDTO(ResponseDTO.SUCCESS, CheckoutCart.RESET_SUCCESS_MESSAGE));

        this.mockMvc.perform(post("/carts/reset")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void displayCart() throws Exception {
        CheckoutCart checkoutCart = new CheckoutCart();

        AddItemDTO defaultItemDto = new AddItemDTO(11, 2002, 5050, 250, 1,null);
        Item defaultItem = ItemMapper.dtoToItem(defaultItemDto);
        checkoutCart.getItems().add(defaultItem);

        CartDetailsDTO cartDetailsDTO = CartMapper.cartToDTO(checkoutCart);
        when(checkoutCartService.displayCart()).thenReturn(new CartResponseDTO(ResponseDTO.SUCCESS, cartDetailsDTO));

        this.mockMvc.perform(get("/carts/display")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new CartResponseDTO(ResponseDTO.SUCCESS, cartDetailsDTO))));
    }

}