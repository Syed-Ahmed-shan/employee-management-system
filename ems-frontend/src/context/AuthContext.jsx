import { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    const employeeId = localStorage.getItem('employeeId');
    const firstLogin = localStorage.getItem('firstLogin') === 'true';
    return token ? { token, username, role, employeeId, firstLogin } : null;
  });

  const loginUser = (data) => {
    localStorage.setItem('token', data.token);
    localStorage.setItem('username', data.username);
    localStorage.setItem('role', data.role);
    localStorage.setItem('firstLogin', data.firstLogin);
    if (data.employeeId) localStorage.setItem('employeeId', data.employeeId);
    setUser(data);
  };

  const updateFirstLogin = (val) => {
    localStorage.setItem('firstLogin', val);
    setUser(prev => ({ ...prev, firstLogin: val }));
  };

  const logoutUser = () => {
    localStorage.clear();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loginUser, logoutUser, updateFirstLogin }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
