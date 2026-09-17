import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('vat_user');
    return saved ? JSON.parse(saved) : null;
  });

  const login = async (username, password) => {
    const res = await authService.login(username, password);
    if (res && res.data) {
      setUser(res.data);
      localStorage.setItem('vat_user', JSON.stringify(res.data));
      return res.data;
    }
  };

  const logout = async () => {
    if (user?.id) {
      try {
        await authService.logout(user.id);
      } catch (ignored) {}
    }
    setUser(null);
    localStorage.removeItem('vat_user');
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
