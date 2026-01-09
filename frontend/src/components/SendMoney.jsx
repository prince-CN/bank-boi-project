import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { transactionAPI } from '../services/api';
import './Dashboard.css';

function SendMoney() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        fromAccount: '',
        toAccount: '',
        amount: '',
        description: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
        setError('');
        setSuccess('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        try {
            const response = await transactionAPI.create({
                ...formData,
                amount: parseFloat(formData.amount)
            });
            setSuccess(`✅ Transaction successful! ID: ${response.data.id}`);
            setTimeout(() => navigate('/transactions'), 2000);
        } catch (err) {
            setError(err.response?.data?.message || 'Transaction failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div className="dashboard">
            <header className="dashboard-header">
                <h1>🏦 BOI Digital Bank</h1>
                <div className="user-info">
                    <span>Welcome, {user.username}!</span>
                    <button onClick={handleLogout} className="btn-logout">Logout</button>
                </div>
            </header>

            <div className="dashboard-nav">
                <button onClick={() => navigate('/dashboard')} className="nav-btn">📊 Dashboard</button>
                <button onClick={() => navigate('/accounts')} className="nav-btn">💳 Accounts</button>
                <button onClick={() => navigate('/transfer')} className="nav-btn active">💸 Send Money</button>
                <button onClick={() => navigate('/transactions')} className="nav-btn">📈 Transactions</button>
            </div>

            <div className="send-money-container">
                <div className="send-money-card">
                    <h2>💸 Send Money</h2>
                    <p className="send-money-subtitle">Transfer funds instantly to any account</p>

                    {error && <div className="error-message">{error}</div>}
                    {success && <div className="success-message">{success}</div>}

                    <form onSubmit={handleSubmit} className="send-money-form">
                        <div className="input-group">
                            <label>📤 From Account</label>
                            <input
                                type="text"
                                name="fromAccount"
                                value={formData.fromAccount}
                                onChange={handleChange}
                                required
                                placeholder="Enter your account number (e.g., ACC001)"
                            />
                        </div>

                        <div className="input-group">
                            <label>📥 To Account</label>
                            <input
                                type="text"
                                name="toAccount"
                                value={formData.toAccount}
                                onChange={handleChange}
                                required
                                placeholder="Enter recipient account number"
                            />
                        </div>

                        <div className="input-group">
                            <label>💰 Amount (₹)</label>
                            <input
                                type="number"
                                name="amount"
                                value={formData.amount}
                                onChange={handleChange}
                                required
                                min="1"
                                step="0.01"
                                placeholder="Enter amount"
                            />
                        </div>

                        <div className="input-group">
                            <label>📝 Description (Optional)</label>
                            <textarea
                                name="description"
                                value={formData.description}
                                onChange={handleChange}
                                placeholder="Add a note about this transaction"
                                rows="3"
                            />
                        </div>

                        <button type="submit" className="btn-primary btn-large" disabled={loading}>
                            {loading ? '⏳ Processing...' : '💸 Send Money'}
                        </button>
                    </form>
                </div>

                <div className="transaction-info-card">
                    <h3>ℹ️ Transaction Information</h3>
                    <ul>
                        <li>⚡ Transactions are processed instantly</li>
                        <li>🔔 You'll receive a confirmation notification</li>
                        <li>🛡️ Fraud detection automatically checks high-value transfers</li>
                        <li>💵 Minimum amount: ₹1</li>
                        <li>🔒 Bank-grade security for all transactions</li>
                    </ul>
                </div>
            </div>
        </div>
    );
}

export default SendMoney;
