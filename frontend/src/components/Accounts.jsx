import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { accountAPI } from '../services/api';
import './Dashboard.css';

function Accounts() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showCreateForm, setShowCreateForm] = useState(false);
    const [newAccount, setNewAccount] = useState({
        accountType: 'SAVINGS',
        balance: 0
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        fetchAccounts();
    }, [user]);

    const fetchAccounts = async () => {
        try {
            setLoading(true);
            const res = await accountAPI.getByUserId(user.id);
            setAccounts(res.data || []);
        } catch (err) {
            console.error('Error fetching accounts:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateAccount = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            await accountAPI.create({
                userId: user.id,
                ...newAccount
            });
            setShowCreateForm(false);
            setNewAccount({ accountType: 'SAVINGS', balance: 0 });
            setSuccess('Account created successfully! 🎉');
            fetchAccounts();
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create account');
        }
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    if (loading) {
        return <div className="loading">Loading your accounts...</div>;
    }

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
                <button onClick={() => navigate('/dashboard')} className="nav-btn">Dashboard</button>
                <button onClick={() => navigate('/accounts')} className="nav-btn active">Accounts</button>
                <button onClick={() => navigate('/transfer')} className="nav-btn">Send Money</button>
                <button onClick={() => navigate('/transactions')} className="nav-btn">Transactions</button>
            </div>

            <div className="accounts-page">
                <div className="page-header">
                    <h2>My Bank Accounts</h2>
                    <button
                        onClick={() => setShowCreateForm(!showCreateForm)}
                        className="btn-primary"
                    >
                        {showCreateForm ? 'Cancel' : 'Create New Account'}
                    </button>
                </div>

                {error && <div className="error-message" style={{ marginTop: '16px' }}>{error}</div>}
                {success && <div className="success-message" style={{ marginTop: '16px' }}>{success}</div>}

                {showCreateForm && (
                    <div className="create-account-form">
                        <h3>Create New Account</h3>
                        <form onSubmit={handleCreateAccount}>
                            <div className="input-group">
                                <label>Account Type</label>
                                <select
                                    value={newAccount.accountType}
                                    onChange={(e) => setNewAccount({ ...newAccount, accountType: e.target.value })}
                                    required
                                >
                                    <option value="SAVINGS">💰 Savings Account</option>
                                    <option value="CURRENT">💼 Current Account</option>
                                </select>
                            </div>
                            <div className="input-group">
                                <label>Initial Balance (₹)</label>
                                <input
                                    type="number"
                                    min="0"
                                    step="0.01"
                                    value={newAccount.balance}
                                    onChange={(e) => setNewAccount({ ...newAccount, balance: parseFloat(e.target.value) })}
                                    required
                                    placeholder="Enter initial deposit amount"
                                />
                            </div>
                            <button type="submit" className="btn-primary">Create Account</button>
                        </form>
                    </div>
                )}

                <div className="accounts-grid">
                    {accounts.length === 0 ? (
                        <div className="empty-state">
                            <p>No accounts yet. Create your first account to get started!</p>
                        </div>
                    ) : (
                        accounts.map(account => (
                            <div key={account.id} className="account-card">
                                <div className="account-header">
                                    <h3>{account.accountType === 'SAVINGS' ? '💰' : '💼'} {account.accountType} Account</h3>
                                    <span className={`status ${account.status?.toLowerCase()}`}>
                                        {account.status || 'ACTIVE'}
                                    </span>
                                </div>
                                <div className="account-details">
                                    <p className="account-number">
                                        <strong>Account Number:</strong> {account.accountNumber}
                                    </p>
                                    <p className="account-balance">
                                        <strong>Balance:</strong>
                                        <span className="balance-amount">₹{Number(account.balance).toFixed(2)}</span>
                                    </p>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div >
    );
}

export default Accounts;
