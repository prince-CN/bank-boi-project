import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { accountAPI, transactionAPI } from '../services/api';
import './Dashboard.css';

function Transactions() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [accounts, setAccounts] = useState([]);
    const [transactions, setTransactions] = useState([]);
    const [selectedAccount, setSelectedAccount] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchAccounts();
    }, [user]);

    useEffect(() => {
        if (selectedAccount) {
            fetchTransactions(selectedAccount);
        }
    }, [selectedAccount]);

    const fetchAccounts = async () => {
        try {
            setLoading(true);
            const res = await accountAPI.getByUserId(user.id);
            const accountsData = res.data || [];
            setAccounts(accountsData);

            if (accountsData.length > 0) {
                setSelectedAccount(accountsData[0].accountNumber);
            }
        } catch (err) {
            console.error('Error fetching accounts:', err);
        } finally {
            setLoading(false);
        }
    };

    const fetchTransactions = async (accountNumber) => {
        try {
            const res = await transactionAPI.getHistory(accountNumber);
            setTransactions(res.data || []);
        } catch (err) {
            console.error('Error fetching transactions:', err);
            setTransactions([]);
        }
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const getStatusColor = (status) => {
        switch (status?.toUpperCase()) {
            case 'SUCCESS':
            case 'COMPLETED':
                return '#10b981';
            case 'PENDING':
                return '#f59e0b';
            case 'FAILED':
                return '#ef4444';
            default:
                return '#6b7280';
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleString('en-IN', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    if (loading) {
        return <div className="loading">Loading transactions...</div>;
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
                <button onClick={() => navigate('/dashboard')} className="nav-btn">📊 Dashboard</button>
                <button onClick={() => navigate('/accounts')} className="nav-btn">💳 Accounts</button>
                <button onClick={() => navigate('/transfer')} className="nav-btn">💸 Send Money</button>
                <button onClick={() => navigate('/transactions')} className="nav-btn active">📈 Transactions</button>
            </div>

            <div className="transactions-page">
                <div className="page-header">
                    <h2>📈 Transaction History</h2>
                    {accounts.length > 0 && (
                        <select
                            value={selectedAccount}
                            onChange={(e) => setSelectedAccount(e.target.value)}
                            className="account-selector"
                        >
                            {accounts.map(account => (
                                <option key={account.id} value={account.accountNumber}>
                                    {account.accountNumber} - {account.accountType}
                                </option>
                            ))}
                        </select>
                    )}
                </div>

                {accounts.length === 0 ? (
                    <div className="empty-state">
                        <p>No accounts yet. Create an account first to view transactions.</p>
                        <button onClick={() => navigate('/accounts')} className="btn-primary">
                            ➕ Create Account
                        </button>
                    </div>
                ) : transactions.length === 0 ? (
                    <div className="empty-state">
                        <p>No transactions yet for this account.</p>
                        <button onClick={() => navigate('/transfer')} className="btn-primary">
                            💸 Send Money
                        </button>
                    </div>
                ) : (
                    <div className="transactions-table">
                        <table>
                            <thead>
                                <tr>
                                    <th>📅 Date & Time</th>
                                    <th>🔖 Transaction ID</th>
                                    <th>📤 From Account</th>
                                    <th>📥 To Account</th>
                                    <th>💰 Amount</th>
                                    <th>📊 Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                {transactions.map(tx => (
                                    <tr key={tx.id}>
                                        <td>{formatDate(tx.timestamp || tx.createdAt)}</td>
                                        <td className="transaction-id">{tx.transactionId || tx.id}</td>
                                        <td>{tx.fromAccount}</td>
                                        <td>{tx.toAccount}</td>
                                        <td className={tx.fromAccount === selectedAccount ? 'debit' : 'credit'}>
                                            {tx.fromAccount === selectedAccount ? '-' : '+'}
                                            ₹{Number(tx.amount).toFixed(2)}
                                        </td>
                                        <td>
                                            <span
                                                className="status-badge"
                                                style={{ backgroundColor: getStatusColor(tx.status) }}
                                            >
                                                {tx.status || 'COMPLETED'}
                                            </span>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Transactions;
