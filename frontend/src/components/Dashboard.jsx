import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { accountAPI, transactionAPI, notificationAPI } from '../services/api';
import ThemeToggle from './ThemeToggle';
import './Dashboard.css';

function Dashboard() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [accounts, setAccounts] = useState([]);
    const [recentTransactions, setRecentTransactions] = useState([]);
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [stats, setStats] = useState({
        totalBalance: 0,
        accountCount: 0,
        transactionCount: 0
    });

    useEffect(() => {
        fetchDashboardData();
    }, [user]);

    const fetchDashboardData = async () => {
        try {
            setLoading(true);

            // Fetch user accounts
            const accountsRes = await accountAPI.getByUserId(user.id);
            setAccounts(accountsRes.data || []);

            // Calculate stats
            const totalBalance = (accountsRes.data || []).reduce((sum, acc) => sum + Number(acc.balance), 0);
            setStats({
                totalBalance,
                accountCount: (accountsRes.data || []).length,
                transactionCount: 0
            });

            // Fetch recent transactions (if accounts exist)
            if (accountsRes.data && accountsRes.data.length > 0) {
                const firstAccount = accountsRes.data[0].accountNumber;
                try {
                    const txRes = await transactionAPI.getHistory(firstAccount);
                    setRecentTransactions((txRes.data || []).slice(0, 5));
                    setStats(prev => ({ ...prev, transactionCount: (txRes.data || []).length }));
                } catch (err) {
                    console.log('No transactions yet');
                }

                // Fetch notifications
                try {
                    const notifRes = await notificationAPI.getByRecipient(firstAccount);
                    setNotifications((notifRes.data || []).slice(0, 5));
                } catch (err) {
                    console.log('No notifications yet');
                }
            }
        } catch (error) {
            console.error('Error fetching dashboard data:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    if (loading) {
        return <div className="loading">Loading dashboard...</div>;
    }

    return (
        <div className="dashboard">
            <header className="dashboard-header">
                <h1>
                    BOI Digital Bank
                </h1>
                <div className="user-info">
                    <span>Welcome, {user.username}!</span>
                    <ThemeToggle />
                    <button onClick={handleLogout} className="btn-logout">Logout</button>
                </div>
            </header>

            <div className="dashboard-nav">
                <button onClick={() => navigate('/dashboard')} className="nav-btn active">Dashboard</button>
                <button onClick={() => navigate('/accounts')} className="nav-btn">Accounts</button>
                <button onClick={() => navigate('/transfer')} className="nav-btn">Send Money</button>
                <button onClick={() => navigate('/transactions')} className="nav-btn">Transactions</button>
            </div>

            <div className="stats-grid">
                <div className="stat-card">
                    <h3>Total Balance</h3>
                    <p className="stat-value">₹{stats.totalBalance.toFixed(2)}</p>
                </div>
                <div className="stat-card">
                    <h3>My Accounts</h3>
                    <p className="stat-value">{stats.accountCount}</p>
                </div>
                <div className="stat-card">
                    <h3>Transactions</h3>
                    <p className="stat-value">{stats.transactionCount}</p>
                </div>
            </div>

            <div className="dashboard-grid">
                <div className="dashboard-section">
                    <h2>My Accounts</h2>
                    {accounts.length === 0 ? (
                        <div className="empty-state">
                            <p>No accounts yet</p>
                            <button onClick={() => navigate('/accounts')} className="btn-primary">
                                ➕ Create Account
                            </button>
                        </div>
                    ) : (
                        <div className="accounts-list">
                            {accounts.map(account => (
                                <div key={account.id} className="account-item">
                                    <div>
                                        <strong>{account.accountNumber}</strong>
                                        <span className="account-type">{account.accountType}</span>
                                    </div>
                                    <div className="account-balance">₹{Number(account.balance).toFixed(2)}</div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                <div className="dashboard-section">
                    <h2>📈 Recent Transactions</h2>
                    {recentTransactions.length === 0 ? (
                        <div className="empty-state">
                            <p>No transactions yet</p>
                        </div>
                    ) : (
                        <div className="transactions-list">
                            {recentTransactions.map(tx => (
                                <div key={tx.id} className="transaction-item">
                                    <div>
                                        <strong>{tx.fromAccount} → {tx.toAccount}</strong>
                                        <span className="tx-status">{tx.status}</span>
                                    </div>
                                    <div className="tx-amount">₹{Number(tx.amount).toFixed(2)}</div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                <div className="dashboard-section">
                    <h2>🔔 Notifications</h2>
                    {notifications.length === 0 ? (
                        <div className="empty-state">
                            <p>No notifications</p>
                        </div>
                    ) : (
                        <div className="notifications-list">
                            {notifications.map(notif => (
                                <div key={notif.id} className={`notification-item ${notif.type.toLowerCase()}`}>
                                    <span className="notif-type">{notif.type}</span>
                                    <p>{notif.message}</p>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Dashboard;
