# AIQUA Testing Grocery Shop

A small grocery shopping app for Android and iOS. A Shopper browses a Catalog of Products, adds them to a Cart and places an Order. The app exists as a realistic host for testing the AIQUA SDK.

## Language

### Catalog

**Catalog**:
The full set of Products for sale, organised into Categories.
_Avoid_: Menu, inventory, store, product list

**Category**:
A label on a Product, such as Vegetables or Meat. Products with the same Category appear together under one header.
_Avoid_: Section, aisle, department

**Product**:
One thing for sale in the Catalog, with a name, an emoji image and a Price. It is sold in whole units as-is, and the Shopper cannot customise it (no size, weight or options).
_Avoid_: Item, SKU, goods

**Price**:
The cost of one unit of a Product, in whole Japanese yen (JPY).
_Avoid_: Cost, amount

### Buying

**Shopper**:
The person using the app to buy groceries.
_Avoid_: Buyer, customer

**Cart**:
The Products the Shopper currently intends to buy, held as Cart Lines.
_Avoid_: Basket, bag, trolley

**Cart Line**:
One Product in the Cart, plus how many units of it the Shopper wants (1 to 9). A Product with no units is not in the Cart at all.
_Avoid_: Item, cart item, entry, line item

**Cart Total**:
The sum of Price × quantity across all Cart Lines. There are no fees, tax or delivery charges.
_Avoid_: Subtotal, grand total

**Order**:
A confirmed purchase of everything in the Cart, created when the Shopper taps Buy. Placing an Order takes the ordered units out of the Cart.
_Avoid_: Purchase, checkout, transaction

**Order ID**:
A short code that uniquely identifies an Order, such as `ORD-482913`. It is not shown to the Shopper; it identifies the Order to AIQUA.
_Avoid_: Order number, reference, receipt number
