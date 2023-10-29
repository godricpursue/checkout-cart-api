# Checkout Cart API
## Overview
This API manages the operations for a checkout cart. It provides functionalities to add items, remove items, reset cart, and display the contents of the cart. Moreover, it incorporates rules around different item types and applies promotions based on specific conditions.

## Prerequisites:
Ensure you have Docker installed on your machine.

## How to run:
Navigate to the root directory of the project and run the following command:

```bash
docker compose up
```

You can check ``http://localhost:8080/swagger-ui/index.html#/`` for the Swagger UI.
## Cart Specifications:
- Can contain up to 10 unique items (excluding VasItem).
- The cart cannot exceed 30 products in total.
- Total value cannot exceed 500,000 TL after promotion.
## Item Specifications:
- Items can be added or removed from the cart.
- All items can be reset.
- Maximum quantity for a single item is 10.
- Items in the cart have seller and category IDs. 
- The types of items include:
  - VasItem
  - DefaultItem
  - DigitalItem
## DigitalItem:
 - Only digital items can be added to a cart defined as a DigitalItem.
 - Examples: steam cards, donation cards.
 - Maximum quantity is 5.
 - Identified by CategoryID 7889.
## DefaultItem:
  - Traditional e-commerce items (e.g., t-shirts, phones, detergents).
  - A DefaultItem can have a maximum of 3 VasItems.
  ## VasItem (Value Added Service Item):
  - Represents services, not physical products.
  - Can only be added as a sub-item to Furniture (CategoryID: 1001) and Electronics (CategoryID: 3004).
  - VasItem has a CategoryID of 3242 and a seller ID of 5003.
  ## Promotion:
  - Types:
    - SameSellerPromotion: 10% discount if all items have the same seller (PromotionID = 9909).
    - CategoryPromotion: 5% discount for items with CategoryID 3003 (PromotionID = 5676).
    - TotalPricePromotion: Discounts based on the total cart price. (PromotionID = 1232)
 
    Only one promotion can be applied at a time.

## Endpoints:
### 1. Add Item to Cart
   __URL:__ /carts/item\
   __Method:__ POST\
   __Input:__

```json
{
"itemId": "10",
"categoryId": "1001",
"sellerId": "5223",
"price": "100",
"quantity": "1"
}
```
__Output:__

```json
{
"result": true,
"message": "Item added to checkoutCart."
}
```
### 2. Add VasItem to Default Item in Cart
   __URL:__ /carts/vasItem\
   __Method:__ POST\
   __Input:__

```json
{
"itemId": "10",
"vasItemId": "4585",
"categoryId": "3242",
"sellerId": "5003",
"price": "50",
"quantity": "1"
}
```
__Output:__

```json
{
"result": true,
"message": "VasItem added to DefaultItem."
}
```
### 3. Remove Item from Cart
   __URL:__ /carts/item/{itemId}\
   __Method:__ DELETE

### 4. Reset the Cart
   __URL:__ /carts/reset\
   __Method:__ POST

### 5. Display Cart Contents
   __URL:__ /carts/display\
   __Method:__ GET

## Implementation:
The service layer (CheckoutCartServiceImpl) communicates with the repositories (CheckoutCartRepository, ItemRepository, VasItemRepository) to perform CRUD operations on the cart and items. The controller (CheckoutCartController) provides RESTful endpoints and manages the HTTP responses based on the operations performed by the service layer.

For more details on the implementation, please refer to the provided codebase.

## Notes:
Ensure you've set up your database configurations properly in your application.properties or equivalent config files.
Always validate the input data before performing operations.
Ensure exception handling mechanisms are in place for smoother user experience.