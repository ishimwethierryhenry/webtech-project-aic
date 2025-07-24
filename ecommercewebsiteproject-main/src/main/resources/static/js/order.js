// // // order.js - Cart and Order Management System
// //
// // // Global variables
// // let cart = [];
// // let selectedCustomer = null;
// //
// // // DOM Ready Handler
// // document.addEventListener('DOMContentLoaded', () => {
// //     // Initialize cart UI
// //     updateCartUI();
// //
// //     // Set up event listeners
// //     document.getElementById('cartButton')?.addEventListener('click', openCart);
// //     document.getElementById('cartOverlay')?.addEventListener('click', closeCart);
// //     document.getElementById('placeOrderBtn')?.addEventListener('click', placeOrder);
// //
// //     // Product search functionality
// //     const productSearch = document.getElementById('productSearch');
// //     if (productSearch) {
// //         productSearch.addEventListener('input', filterProducts);
// //     }
// //
// //     // Initialize product cards
// //     initProductCards();
// // });
// //
// // // Initialize product cards with event listeners
// // function initProductCards() {
// //     document.querySelectorAll('.product-card').forEach(card => {
// //         const productId = card.getAttribute('data-id');
// //         const productName = card.querySelector('.card-title').textContent;
// //         const price = parseFloat(card.querySelector('.price-value').textContent);
// //
// //         const addButton = card.querySelector('.add-to-cart-btn');
// //         if (addButton) {
// //             addButton.addEventListener('click', () => {
// //                 addToCart(productId, productName, price);
// //             });
// //         }
// //     });
// // }
// //
// // // Open cart sidebar
// // function openCart() {
// //     document.getElementById('cartSidebar').classList.add('active');
// //     document.getElementById('cartOverlay').classList.add('active');
// // }
// //
// // // Close cart sidebar
// // function closeCart() {
// //     document.getElementById('cartSidebar').classList.remove('active');
// //     document.getElementById('cartOverlay').classList.remove('active');
// // }
// //
// // // Add product to cart
// // function addToCart(id, name, price) {
// //     // Check if product already in cart
// //     const existingItem = cart.find(item => item.id === id);
// //     if (existingItem) {
// //         existingItem.quantity += 1;
// //     } else {
// //         cart.push({
// //             id: id,
// //             name: name,
// //             price: price,
// //             quantity: 1
// //         });
// //     }
// //
// //     updateCartUI();
// //     openCart();
// // }
// //
// // // Remove item from cart
// // function removeFromCart(id) {
// //     cart = cart.filter(item => item.id !== id);
// //     updateCartUI();
// // }
// //
// // // Update quantity in cart
// // function updateQuantity(id, quantity) {
// //     const item = cart.find(item => item.id === id);
// //     if (item) {
// //         item.quantity = parseInt(quantity);
// //         if (item.quantity < 1) {
// //             removeFromCart(id);
// //         } else {
// //             updateCartUI();
// //         }
// //     }
// // }
// //
// // // Select customer for order
// // function selectCustomer(id, name) {
// //     selectedCustomer = { id, name };
// //     document.getElementById('orderCustomer').value = name;
// // }
// //
// // // Update cart UI
// // function updateCartUI() {
// //     // Update cart badge
// //     const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
// //     const cartBadge = document.getElementById('cartBadge');
// //     if (cartBadge) cartBadge.textContent = totalItems;
// //
// //     // Update cart items
// //     const cartItemsEl = document.getElementById('cartItems');
// //     if (!cartItemsEl) return;
// //
// //     if (cart.length === 0) {
// //         cartItemsEl.innerHTML = '<div class="alert alert-info">No items in cart</div>';
// //     } else {
// //         let html = '';
// //         let subtotal = 0;
// //
// //         cart.forEach(item => {
// //             const itemTotal = item.price * item.quantity;
// //             subtotal += itemTotal;
// //
// //             html += `
// //             <div class="cart-item">
// //                 <div class="d-flex justify-content-between">
// //                     <h6>${item.name}</h6>
// //                     <button class="btn btn-sm btn-link text-danger" onclick="removeFromCart('${item.id}')">
// //                         <i class="bi bi-trash"></i>
// //                     </button>
// //                 </div>
// //                 <div class="d-flex justify-content-between align-items-center">
// //                     <span>$${item.price.toFixed(2)}</span>
// //                     <div>
// //                         <input type="number" min="1" value="${item.quantity}"
// //                                class="form-control form-control-sm"
// //                                style="width: 70px;"
// //                                onchange="updateQuantity('${item.id}', this.value)">
// //                     </div>
// //                     <span class="fw-bold">$${itemTotal.toFixed(2)}</span>
// //                 </div>
// //             </div>
// //             `;
// //         });
// //
// //         cartItemsEl.innerHTML = html;
// //     }
// //
// //     // Update totals
// //     const shipping = 5.99;
// //     const tax = subtotal * 0.08;
// //     const total = subtotal + shipping + tax;
// //
// //     const subtotalEl = document.getElementById('subtotal');
// //     const taxEl = document.getElementById('tax');
// //     const totalEl = document.getElementById('total');
// //
// //     if (subtotalEl) subtotalEl.textContent = `$${subtotal.toFixed(2)}`;
// //     if (taxEl) taxEl.textContent = `$${tax.toFixed(2)}`;
// //     if (totalEl) totalEl.textContent = `$${total.toFixed(2)}`;
// // }
// //
// // // Place order
// // function placeOrder() {
// //     if (!selectedCustomer) {
// //         alert('Please select a customer');
// //         return;
// //     }
// //
// //     if (cart.length === 0) {
// //         alert('Please add items to your order');
// //         return;
// //     }
// //
// //     // Create order data
// //     const orderData = {
// //         customerId: selectedCustomer.id,
// //         shippingAddress: document.getElementById('shippingAddress').value,
// //         items: cart.map(item => ({
// //             productId: item.id,
// //             quantity: item.quantity,
// //             price: item.price
// //         }))
// //     };
// //
// //     // Send order to server
// //     fetch('/order/create', {
// //         method: 'POST',
// //         headers: {
// //             'Content-Type': 'application/json'
// //         },
// //         body: JSON.stringify(orderData)
// //     })
// //         .then(response => {
// //             if (response.ok) {
// //                 return response.json();
// //             }
// //             throw new Error('Order placement failed');
// //         })
// //         .then(data => {
// //             // Show success message
// //             const successModal = new bootstrap.Modal(document.getElementById('successModal'));
// //             successModal.show();
// //
// //             // Reset cart
// //             cart = [];
// //             selectedCustomer = null;
// //             document.getElementById('orderCustomer').value = '';
// //             document.getElementById('shippingAddress').value = '';
// //             updateCartUI();
// //             closeCart();
// //         })
// //         .catch(error => {
// //             console.error('Error:', error);
// //             alert('Error placing order: ' + error.message);
// //         });
// // }
// //
// // // Filter products
// // function filterProducts() {
// //     const searchTerm = this.value.toLowerCase();
// //     const productCards = document.querySelectorAll('.product-card');
// //
// //     productCards.forEach(card => {
// //         const name = card.querySelector('.card-title').textContent.toLowerCase();
// //         if (name.includes(searchTerm)) {
// //             card.parentElement.style.display = 'block';
// //         } else {
// //             card.parentElement.style.display = 'none';
// //         }
// //     });
// // }
// //
// // // Make functions available globally for HTML onclick attributes
// // window.addToCart = addToCart;
// // window.removeFromCart = removeFromCart;
// // window.updateQuantity = updateQuantity;
// // window.selectCustomer = selectCustomer;
// // window.openCart = openCart;
// // window.closeCart = closeCart;
// // order.js - Cart and Order Management System (No ID Version)
//
// // Global variables
// let cart = [];
// let selectedCustomer = null;
//
// // DOM Ready Handler
// document.addEventListener('DOMContentLoaded', () => {
//     updateCartUI();
//
//     document.getElementById('cartButton')?.addEventListener('click', openCart);
//     document.getElementById('cartOverlay')?.addEventListener('click', closeCart);
//     document.getElementById('placeOrderBtn')?.addEventListener('click', placeOrder);
//
//     const productSearch = document.getElementById('productSearch');
//     if (productSearch) {
//         productSearch.addEventListener('input', filterProducts);
//     }
//
//     initProductCards();
// });
//
// // Initialize product cards with event listeners
// function initProductCards() {
//     document.querySelectorAll('.product-card').forEach(card => {
//         const productName = card.querySelector('.card-title').textContent;
//         const price = parseFloat(card.querySelector('.price-value').textContent);
//
//         const addButton = card.querySelector('.add-to-cart-btn');
//         if (addButton) {
//             addButton.addEventListener('click', () => {
//                 addToCart(productName, price);
//             });
//         }
//     });
// }
//
// // Open cart sidebar
// function openCart() {
//     document.getElementById('cartSidebar').classList.add('active');
//     document.getElementById('cartOverlay').classList.add('active');
// }
//
// // Close cart sidebar
// function closeCart() {
//     document.getElementById('cartSidebar').classList.remove('active');
//     document.getElementById('cartOverlay').classList.remove('active');
// }
//
// // Add product to cart
// function addToCart(name, price) {
//     const existingItem = cart.find(item => item.name === name);
//     if (existingItem) {
//         existingItem.quantity += 1;
//     } else {
//         cart.push({ name, price, quantity: 1 });
//     }
//
//     updateCartUI();
//     openCart();
// }
//
// // Remove item from cart
// function removeFromCartByName(name) {
//     cart = cart.filter(item => item.name !== name);
//     updateCartUI();
// }
//
// // Update quantity in cart
// function updateQuantityByName(name, quantity) {
//     const item = cart.find(item => item.name === name);
//     if (item) {
//         item.quantity = parseInt(quantity);
//         if (item.quantity < 1) {
//             removeFromCartByName(name);
//         } else {
//             updateCartUI();
//         }
//     }
// }
//
// // Select customer for order
// function selectCustomer(id, name) {
//     selectedCustomer = { id, name };
//     document.getElementById('orderCustomer').value = name;
// }
//
// // Update cart UI
// function updateCartUI() {
//     const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
//     const cartBadge = document.getElementById('cartBadge');
//     if (cartBadge) cartBadge.textContent = totalItems;
//
//     const cartItemsEl = document.getElementById('cartItems');
//     if (!cartItemsEl) return;
//
//     if (cart.length === 0) {
//         cartItemsEl.innerHTML = '<div class="alert alert-info">No items in cart</div>';
//     } else {
//         let html = '';
//         let subtotal = 0;
//
//         cart.forEach(item => {
//             const itemTotal = item.price * item.quantity;
//             subtotal += itemTotal;
//
//             html += `
//             <div class="cart-item">
//                 <div class="d-flex justify-content-between">
//                     <h6>${item.name}</h6>
//                     <button class="btn btn-sm btn-link text-danger" onclick="removeFromCartByName('${item.name}')">
//                         <i class="bi bi-trash"></i>
//                     </button>
//                 </div>
//                 <div class="d-flex justify-content-between align-items-center">
//                     <span>$${item.price.toFixed(2)}</span>
//                     <div>
//                         <input type="number" min="1" value="${item.quantity}"
//                                class="form-control form-control-sm"
//                                style="width: 70px;"
//                                onchange="updateQuantityByName('${item.name}', this.value)">
//                     </div>
//                     <span class="fw-bold">$${itemTotal.toFixed(2)}</span>
//                 </div>
//             </div>
//             `;
//         });
//
//         cartItemsEl.innerHTML = html;
//     }
//
//     const shipping = 5.99;
//     const tax = subtotal * 0.08;
//     const total = subtotal + shipping + tax;
//
//     document.getElementById('subtotal').textContent = `$${subtotal.toFixed(2)}`;
//     document.getElementById('tax').textContent = `$${tax.toFixed(2)}`;
//     document.getElementById('total').textContent = `$${total.toFixed(2)}`;
// }
//
// // Place order
// function placeOrder() {
//     if (!selectedCustomer) {
//         alert('Please select a customer');
//         return;
//     }
//
//     if (cart.length === 0) {
//         alert('Please add items to your order');
//         return;
//     }
//
//     const orderData = {
//         customerId: selectedCustomer.id,
//         shippingAddress: document.getElementById('shippingAddress').value,
//         items: cart.map(item => ({
//             name: item.name,
//             quantity: item.quantity,
//             price: item.price
//         }))
//     };
//
//     fetch('/order/create', {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         body: JSON.stringify(orderData)
//     })
//         .then(response => {
//             if (response.ok) return response.json();
//             throw new Error('Order placement failed');
//         })
//         .then(() => {
//             const successModal = new bootstrap.Modal(document.getElementById('successModal'));
//             successModal.show();
//
//             cart = [];
//             selectedCustomer = null;
//             document.getElementById('orderCustomer').value = '';
//             document.getElementById('shippingAddress').value = '';
//             updateCartUI();
//             closeCart();
//         })
//         .catch(error => {
//             console.error('Error:', error);
//             alert('Error placing order: ' + error.message);
//         });
// }
//
// // Filter products
// function filterProducts() {
//     const searchTerm = this.value.toLowerCase();
//     const productCards = document.querySelectorAll('.product-card');
//
//     productCards.forEach(card => {
//         const name = card.querySelector('.card-title').textContent.toLowerCase();
//         card.parentElement.style.display = name.includes(searchTerm) ? 'block' : 'none';
//     });
// }
//
// // Expose global functions
// window.addToCart = addToCart;
// window.removeFromCartByName = removeFromCartByName;
// window.updateQuantityByName = updateQuantityByName;
// window.selectCustomer = selectCustomer;
// window.openCart = openCart;
// window.closeCart = closeCart;
