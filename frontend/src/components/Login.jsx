import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';
import './Auth.css';

function Login() {
    const navigate = useNavigate();
    const { login } = useAuth();
    const [formData, setFormData] = useState({
        username: '',
        password: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await authAPI.login(formData);
            login(response.data);
            navigate('/dashboard');
        } catch (err) {
            const errorMessage = err.response?.data?.message
                || err.response?.data?.error
                || (typeof err.response?.data === 'string' ? err.response.data : null)
                || err.message
                || 'Login failed. Please check your credentials.';
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            {/* Left Side - Branding */}
            <div className="auth-branding">
                <img
                    src="/boi-logo.png"
                    alt="BOI Digital Bank"
                    className="auth-logo"
                />
                <h1>BOI DIGITAL BANK</h1>
                <p className="tagline">Secure. Smart. Seamless Banking Experience</p>

                <div className="features">
                    <div className="feature-item">
                        <div className="feature-icon">🔒</div>
                        <div className="feature-text">
                            <h3>Bank-Grade Security</h3>
                            <p>Military-grade encryption</p>
                        </div>
                    </div>
                    <div className="feature-item">
                        <div className="feature-icon">⚡</div>
                        <div className="feature-text">
                            <h3>Instant Transfers</h3>
                            <p>24/7 real-time transactions</p>
                        </div>
                    </div>
                    <div className="feature-item">
                        <div className="feature-icon">📱</div>
                        <div className="feature-text">
                            <h3>Mobile Banking</h3>
                            <p>Access anywhere, anytime</p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Right Side - Login Form */}
            <div className="auth-form-section">
                <div className="auth-card">
                    <div className="auth-card-header">
                        <h2>Welcome Back!</h2>
                        <p>Sign in to your account to continue</p>
                    </div>

                    {error && (
                        <div className="auth-message error">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label>Username</label>
                            <input
                                type="text"
                                placeholder="Enter your username"
                                value={formData.username}
                                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Password</label>
                            <input
                                type="password"
                                placeholder="Enter your password"
                                value={formData.password}
                                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            className="submit-btn"
                            disabled={loading}
                        >
                            {loading ? 'Signing In...' : 'Sign In'}
                        </button>
                    </form>

                    <div className="auth-link">
                        Don't have an account? <Link to="/signup">Sign Up</Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Login;
