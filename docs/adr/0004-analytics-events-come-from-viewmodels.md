# Analytics events come from ViewModels

AIQUA events (Product added, Order placed, Order failed, Home content loaded, …) are sent from the ViewModels, not
from the Repositories. A Repository is the single place a change happens, but it doesn't know which screen the Shopper
was on or what they tapped. The ViewModel does, and that context is what makes an event useful. The cost is that a
shared action, such as adding to the Cart from Home and from Cart, has to be tracked in each ViewModel that offers it.
