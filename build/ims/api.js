/**
 * API Helper - Handles all communication with Java backend servlets
 */

const API = {
  BASE_URL: window.location.origin + (() => {
    const segments = window.location.pathname.split('/').filter(Boolean);
    return segments.length > 0 ? '/' + segments[0] : '';
  })(),

  /**
   * Make async API request
   */
  async request(endpoint, method = 'GET', data = null) {
    const url = this.BASE_URL + '/api/' + endpoint;
    
    let options = {
      method: method,
      credentials: 'include',  // Include session cookies
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    };

    if (data) {
      options.body = new URLSearchParams(data).toString();
    }

    try {
      const response = await fetch(url, options);
      return await response.json();
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  },

  /**
   * GET request
   */
  get(endpoint) {
    return this.request(endpoint, 'GET');
  },

  /**
   * POST request
   */
  post(endpoint, data) {
    return this.request(endpoint, 'POST', data);
  },

  /**
   * DELETE request
   */
  delete(endpoint, id) {
    return this.request(endpoint, 'DELETE', { id: id });
  },

  // Product APIs
  product: {
    getAll: () => API.get('product'),
    add: (data) => API.post('product', data),
    delete: (id) => API.delete('product', id)
  },

  // Customer APIs
  customer: {
    getAll: () => API.get('customer'),
    add: (data) => API.post('customer', data),
    delete: (id) => API.delete('customer', id)
  },

  // Supplier APIs
  supplier: {
    getAll: () => API.get('supplier'),
    add: (data) => API.post('supplier', data),
    delete: (id) => API.delete('supplier', id)
  },

  // Payment APIs
  payment: {
    getAll: () => API.get('payment'),
    add: (data) => API.post('payment', data),
    delete: (id) => API.delete('payment', id)
  },

  // Invoice APIs
  invoice: {
    getAll: () => API.get('invoice'),
    add: (data) => API.post('invoice', data)
  },

  // Login API
  login: (username, password, shopId) => 
    API.post('login', { username, password, shopId }),

  // Signup API
  signup: (userData) => API.post('signup', userData),

  // Check session
  checkSession: () => API.get('login')
};

/**
 * Utility: Redirect to login if not authenticated
 */
async function checkAuth() {
  try {
    const result = await API.checkSession();
    if (!result.loggedIn) {
      window.location.href = 'index.html';
    }
    return result.shopId;
  } catch (error) {
    window.location.href = 'index.html';
  }
}

/**
 * Utility: Show notification
 */
function showNotification(message, type = 'info') {
  const notification = document.createElement('div');
  notification.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 15px 20px;
    background: ${type === 'error' ? '#dc3545' : type === 'success' ? '#28a745' : '#17a2b8'};
    color: white;
    border-radius: 4px;
    z-index: 9999;
  `;
  notification.textContent = message;
  document.body.appendChild(notification);
  
  setTimeout(() => notification.remove(), 3000);
}
