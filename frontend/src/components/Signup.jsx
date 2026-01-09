import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';
import './Auth.css';

function Signup() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            setLoading(false);
            return;
        }

        if (formData.password.length < 6) {
            setError('Password must be at least 6 characters');
            setLoading(false);
            return;
        }

        try {
            const { confirmPassword, ...signupData } = formData;
            await authAPI.signup(signupData);

            alert('Signup successful! Please login with your credentials.');
            navigate('/login');
        } catch (err) {
            const errorMessage = err.response?.data?.message
                || err.response?.data?.error
                || (typeof err.response?.data === 'string' ? err.response.data : null)
                || err.message
                || 'Signup failed. Please try again.';
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
                <p className="tagline">Join Millions of Happy Customers Worldwide</p>

                <div className="features">
                    <div className="feature-item">
                        <div className="feature-icon">💳</div>
                        <div className="feature-text">
                            <h3>Zero Fees</h3>
                            <p>No hidden charges</p>
                        </div>
                    </div>
                    <div className="feature-item">
                        <div className="feature-icon">🎁</div>
                        <div className="feature-text">
                            <h3>Welcome Bonus</h3>
                            <p>Get rewards on signup</p>
                        </div>
                    </div>
                    <div className="feature-item">
                        <div className="feature-icon">🌍</div>
                        <div className="feature-text">
                            <h3>Global Access</h3>
                            <p>Bank from anywhere</p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Right Side - Signup Form */}
            <div className="auth-form-section">
                <div className="auth-card">
                    <div className="auth-card-header">
                        <h2>Create Account</h2>
                        <p>Start your banking journey with us</p>
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
                                placeholder="Choose a username"
                                value={formData.username}
                                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Email</label>
                            <input
                                type="email"
                                placeholder="Enter your email"
                                value={formData.email}
                                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Password</label>
                            <input
                                type="password"
                                placeholder="Create a strong password"
                                value={formData.password}
                                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Confirm Password</label>
                            <input
                                type="password"
                                placeholder="Re-enter your password"
                                value={formData.confirmPassword}
                                onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            className="submit-btn"
                            disabled={loading}
                        >
                            {loading ? 'Creating Account...' : 'Create Account'}
                        </button>
                    </form>

                    <div className="auth-link">
                        Already have an account? <Link to="/login">Sign In</Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Signup;
