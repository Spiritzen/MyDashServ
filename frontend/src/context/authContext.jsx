import { createContext, useContext, useMemo, useState, useEffect } from "react";

const AuthCtx = createContext(null);
// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => useContext(AuthCtx);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);   // { email, fullName, roles: [...] }
  const [token, setToken] = useState(null); // string

  useEffect(() => {
    const t = localStorage.getItem("auth_token");
    const u = localStorage.getItem("auth_user");
    if (t && u) { setToken(t); setUser(JSON.parse(u)); }
  }, []);

  const login = (nextUser, nextToken) => {
    setUser(nextUser); setToken(nextToken);
    localStorage.setItem("auth_token", nextToken);
    localStorage.setItem("auth_user", JSON.stringify(nextUser));
  };

  const logout = () => {
    setUser(null); setToken(null);
    localStorage.removeItem("auth_token");
    localStorage.removeItem("auth_user");
  };

  const isAdmin = !!user?.roles?.includes("ROLE_ADMIN");

  const value = useMemo(() => ({ user, token, isAdmin, login, logout }), [user, token, isAdmin]);
  return <AuthCtx.Provider value={value}>{children}</AuthCtx.Provider>;
}
