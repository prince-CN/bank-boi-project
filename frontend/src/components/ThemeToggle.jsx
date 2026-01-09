import React from 'react';
import { useTheme } from '../context/ThemeContext';
import './ThemeToggle.css';

const ThemeToggle = () => {
    const { theme, toggleTheme } = useTheme();

    return (
        <div
            className="theme-toggle"
            onClick={toggleTheme}
            data-theme={theme}
            title={`Switch to ${theme === 'light' ? 'dark' : 'light'} mode`}
        >
            <div className="theme-toggle-slider"></div>
        </div>
    );
};

export default ThemeToggle;
