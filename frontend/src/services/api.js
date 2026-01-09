import axios from 'axios';

// API Gateway Base URL - Single Entry Point for All Services
// Using relative path for Vite proxy in development
const API_GATEWAY_URL = '/api';

// Axios instance with default configuration
const apiClient = axios.create({
    baseURL: API_GATEWAY_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add request interceptor to attach JWT token
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add response interceptor for error handling
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            // Handle unauthorized - redirect to login
            localStorage.removeItem('token');
            window.location.href = '/';
        }
        return Promise.reject(error);
    }
);

const api = {
    // Auth Service Routes (via Gateway: /api/auth/*)
    auth: {
        register: (data) => apiClient.post('/auth/register', data),
        signup: (data) => apiClient.post('/auth/signup', data),
        login: (data) => apiClient.post('/auth/login', data),
        logout: () => {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            return Promise.resolve();
        },
        getCurrentUser: () => apiClient.get('/auth/me'),
    },

    // Account Service Routes (via Gateway: /api/accounts/*)
    accounts: {
        create: (data) => apiClient.post('/accounts/create', data),
        getByUserId: (userId) => apiClient.get(`/accounts/user/${userId}`),
        getByNumber: (accountNumber) => apiClient.get(`/accounts/number/${accountNumber}`),
        getAll: () => apiClient.get('/accounts/all'),
    },

    // Transaction Service Routes (via Gateway: /api/transactions/*)
    transactions: {
        create: (data) => apiClient.post('/transactions', data),
        getById: (id) => apiClient.get(`/transactions/${id}`),
        getHistory: (accountNumber) => apiClient.get(`/transactions/history/${accountNumber}`),
        getPending: () => apiClient.get('/transactions/pending'),
    },

    // Wallet Service Routes (via Gateway: /api/wallets/*)
    wallets: {
        create: (accountNumber, initialBalance = 0) =>
            apiClient.post(`/wallets/create?accountNumber=${accountNumber}&initialBalance=${initialBalance}`),
        getByAccount: (accountNumber) => apiClient.get(`/wallets/${accountNumber}`),
        getBalance: (accountNumber) => apiClient.get(`/wallets/${accountNumber}/balance`),
    },

    // Notification Service Routes (via Gateway: /api/notifications/*)
    notifications: {
        getAll: () => apiClient.get('/notifications/all'),
        getByRecipient: (recipient) => apiClient.get(`/notifications/recipient/${recipient}`),
        getByTransaction: (transactionId) => apiClient.get(`/notifications/transaction/${transactionId}`),
    },

    // Fraud Service Routes (via Gateway: /api/fraud/*)
    fraud: {
        getFlagged: () => apiClient.get('/fraud/flagged'),
        getByAccount: (accountNumber) => apiClient.get(`/fraud/account/${accountNumber}`),
        getAll: () => apiClient.get('/fraud/all'),
    },
};

// Named exports for individual modules
export const authAPI = api.auth;
export const accountAPI = api.accounts;
export const transactionAPI = api.transactions;
export const walletAPI = api.wallets;
export const notificationAPI = api.notifications;
export const fraudAPI = api.fraud;

// Default export
export default api;
